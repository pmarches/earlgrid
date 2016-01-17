package com.earlgrid.remoting;

import com.earlgrid.core.serverside.EarlGridPb.PbClientClipboard;
import com.earlgrid.core.serverside.EarlGridPb.PbFormField;
import com.earlgrid.core.serverside.EarlGridPb.PbInteractiveForm;
import com.earlgrid.core.serverside.EarlGridPb.PbTopLevel;
import com.earlgrid.core.serverside.EarlGridPb.PbClientClipboard.ClipboardOperation;
import com.earlgrid.ui.standalone.ApplicationMain;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class InteractiveFormHandler {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(InteractiveFormHandler.class);
  private static final String DEFAULT_TITLE = "Prompt";
  protected ApplicationMain app;
  
  public InteractiveFormHandler(ApplicationMain app) {
    this.app=app;
  }

  public void handleClientSideInteractiveForm(PbTopLevel topLevelMsg) {
    Display.getDefault().asyncExec(()-> {
      createAndShowFormDialog(topLevelMsg);
    });
  }

  protected void createAndShowFormDialog(PbTopLevel topLevelMsg){
    Shell formShell=new Shell(app.mainWindow.getDisplay(), SWT.CLOSE|SWT.APPLICATION_MODAL);
    PbInteractiveForm interactiveFormRequest=topLevelMsg.getClientSideInteractiveForm();
    formShell.setLayout(new GridLayout(2, true));

    String title=DEFAULT_TITLE;
    if(interactiveFormRequest.hasFormTitle()){
      title=interactiveFormRequest.getFormTitle(); 
    }
    formShell.setText(title);
    Composite fieldComposite=new Composite(formShell, SWT.NONE);
    fieldComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
    fieldComposite.setLayout(new GridLayout(2, false));

    Text[] allTextFields=new Text[interactiveFormRequest.getFieldsCount()];
    for(int i=0; i<interactiveFormRequest.getFieldsCount(); i++){
      PbFormField fieldRequest = interactiveFormRequest.getFields(i);
      String fieldLabelStr=String.format("%s%d", "Input", i+1);
      if(fieldRequest.hasFieldLabel()){
        fieldLabelStr=fieldRequest.getFieldLabel();
      }
      Label fieldLabel=new Label(fieldComposite, SWT.NONE);
      fieldLabel.setText(fieldLabelStr);
      fieldLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
      
      Text fieldText=new Text(fieldComposite, SWT.NONE);
      allTextFields[i]=fieldText;
      fieldText.setData(fieldRequest);
      fieldText.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
      if(fieldRequest.hasFieldValue()){
        fieldText.setText(fieldRequest.getFieldValue());
      }
      if(fieldRequest.hasConcealUserInput() && fieldRequest.getConcealUserInput()){
        fieldText.setEchoChar('*');
      }
    }
    
    Button okBtn=new Button(formShell, SWT.PUSH);
    okBtn.addSelectionListener(onOkBtnPressed(formShell, allTextFields, topLevelMsg));
    okBtn.setText("Ok");

    formShell.pack();    
    formShell.open();
  }

  private SelectionListener onOkBtnPressed(Shell formShell, Text[] allTextFields, PbTopLevel topLevelMsg){ return new SelectionAdapter() {
    @Override
    public void widgetSelected(SelectionEvent e) {
      PbInteractiveForm.Builder filledForm=PbInteractiveForm.newBuilder();
      for(int i=0; i<allTextFields.length; i++){
        PbFormField fieldRequest = (PbFormField) allTextFields[i].getData();
        PbFormField.Builder fieldResponse=PbFormField.newBuilder(fieldRequest);
        fieldResponse.setFieldValue(allTextFields[i].getText());
        
        filledForm.addFields(fieldResponse);
      }
      formShell.close();
      
      PbTopLevel.Builder topLevelResponse=app.client.ioConnection.createResponseForRequest(topLevelMsg);
      topLevelResponse.setClientSideInteractiveForm(filledForm);
      app.client.ioConnection.queueResponse(topLevelResponse);
    }
  };
  }

  public void handleClipboard(PbTopLevel topLevelMsg) {
    app.mainWindow.getDisplay().asyncExec(()-> {
      PbClientClipboard clipboardReq = topLevelMsg.getClientClipboard();
      if(clipboardReq.hasOperation()==false){
        log.error("Missing operation on clipboard command");
        return;
      }
      Clipboard swtClipboard=new Clipboard(app.mainWindow.getDisplay());

      if(clipboardReq.getOperation()==ClipboardOperation.READ_CLIPBOARD){
        String clipboardTextData = (String)swtClipboard.getContents(TextTransfer.getInstance());
        if (clipboardTextData != null){
          PbClientClipboard.Builder clipboardResponse=PbClientClipboard.newBuilder();
          clipboardResponse.setClipboardContent(clipboardTextData);
          PbTopLevel.Builder topLevelResponse=app.client.ioConnection.createResponseForRequest(topLevelMsg);
          topLevelResponse.setClientClipboard(clipboardResponse);
          app.client.ioConnection.queueResponse(topLevelResponse);
        }
        else{
          log.error("Non text data not yet implemented");
//          RTFTransfer rtfTransfer = RTFTransfer.getInstance();
//          String rtfData = (String)clipboard.getContents(rtfTransfer, DND.CLIPBOARD);
//          if (rtfData != null) System.out.println("RTF Text is "+rtfData);
        }
      }
      else if(clipboardReq.getOperation()==ClipboardOperation.WRITE_CLIPBOARD){
        Transfer[] dataTypes=new Transfer[]{TextTransfer.getInstance()};
        Object[] data=new Object[]{clipboardReq.getClipboardContent()};
        swtClipboard.setContents(data, dataTypes);
      }
      swtClipboard.dispose();
    });
  }
}
