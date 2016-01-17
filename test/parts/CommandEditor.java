package com.earlgrid.ui.parts;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.ui.standalone.ApplicationMain;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class CommandEditor extends Composite {

  public static final String ID = "com.earlgrid.parts.CommandEditor"; //$NON-NLS-1$

  private Table tabularErrorTable;
  private Table tabularLogTable;
  private Text commandLineTxt;

  private ExecutionHistoryRecord currentlySelectedExecutionRecord;
  private NatTable terminalGrid;
  private DataLayer terminalDataLayer;

  private ApplicationMain app;

  public CommandEditor(Composite parent, ApplicationMain app) {
    super(parent, SWT.NONE);
    this.app=app;
    createPartControl(parent);
  }
  
  private IConfigLabelAccumulator configLabelAccumulator=new IConfigLabelAccumulator() {
    @Override
    public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
      String cell = currentlySelectedExecutionRecord.out.rows.get(rowPosition).getCellAtColumn(columnPosition);
    }
  };

  public void createPartControl(Composite parent) {
    final Font monospaceFont = new Font(parent.getDisplay(), new FontData("Monospace", 12, SWT.NONE));
    final Color greenColor = parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN);
    final Color redColor = parent.getDisplay().getSystemColor(SWT.COLOR_DARK_RED);
    final Color blackColor = parent.getDisplay().getSystemColor(SWT.COLOR_BLACK);
    final Color greyColor = parent.getDisplay().getSystemColor(SWT.COLOR_GRAY);

    FillLayout fillLayout = (FillLayout) parent.getLayout();
    fillLayout.type = SWT.VERTICAL;

    Composite terminalComposite = new Composite(parent, SWT.NONE);
    terminalComposite.setLayout(new FormLayout());
    terminalComposite.setBackground(blackColor);
    
    TabFolder terminalPane = new TabFolder(terminalComposite, SWT.BOTTOM);
    terminalPane.setBackground(blackColor);
    FormData terminalLayoutData=new FormData();
    terminalLayoutData.top=new FormAttachment(0);
    terminalLayoutData.left=new FormAttachment(0);
    terminalLayoutData.right=new FormAttachment(100);
    terminalPane.setLayoutData(terminalLayoutData);
    
    TabItem outTabItem = new TabItem(terminalPane, SWT.NONE);
    outTabItem.setText("out");
//    terminalDataLayer = new DataLayer(currentlySelectedExecutionRecordDataProvider);

    SelectionLayer selectionLayer=new SelectionLayer(terminalDataLayer);
    ViewportLayer viewPortLayer=new ViewportLayer(selectionLayer);
    viewPortLayer.setRegionName(GridRegion.BODY);
    ColumnHeaderLayer columnHeaderLayer=new ColumnHeaderLayer(new DataLayer(columnDataProvider), viewPortLayer, selectionLayer);
    CompositeLayer compositeLayer = new CompositeLayer(1, 2);
    compositeLayer.setChildLayer(GridRegion.COLUMN_HEADER, columnHeaderLayer, 0, 0);
    compositeLayer.setChildLayer(GridRegion.BODY, viewPortLayer, 0, 1);
    compositeLayer.setConfigLabelAccumulatorForRegion(GridRegion.BODY, configLabelAccumulator);
    terminalGrid=new NatTable(terminalPane, compositeLayer);
    terminalGrid.setTheme(new EarlGridTheme());
    terminalGrid.setBackground(blackColor);
    outTabItem.setControl(terminalGrid);
    
    TabItem errTabItem = new TabItem(terminalPane, SWT.NONE);
    errTabItem.setText("err");
    tabularErrorTable = new Table(terminalPane, SWT.VIRTUAL | SWT.HIDE_SELECTION);
    tabularErrorTable.setHeaderVisible(false);
    tabularErrorTable.setLinesVisible(true);
//    tabularErrorTable.addListener(SWT.SetData, tabularErrorDataHandler);
    tabularErrorTable.setFont(monospaceFont);
    tabularErrorTable.setBackground(redColor);
    tabularErrorTable.setForeground(greyColor);
    errTabItem.setControl(tabularErrorTable);
    
    TabItem logTabItem = new TabItem(terminalPane, SWT.NONE);
    logTabItem.setText("log");
    tabularLogTable = new Table(terminalPane, SWT.VIRTUAL | SWT.HIDE_SELECTION);
    tabularLogTable.setHeaderVisible(false);
    tabularLogTable.setLinesVisible(true);
//    tabularLogTable.addListener(SWT.SetData, tabularErrorDataHandler);
    tabularLogTable.setFont(monospaceFont);
    tabularLogTable.setBackground(greenColor);
    tabularLogTable.setForeground(greyColor);
    logTabItem.setControl(tabularLogTable);

    terminalPane.setSelection(0);
    
    commandLineTxt = new Text(terminalComposite, SWT.BORDER);
    FormData commandLineTxtLayoutData=new FormData();
    commandLineTxtLayoutData.left=new FormAttachment(terminalPane, 0, SWT.LEFT);
    commandLineTxtLayoutData.right=new FormAttachment(terminalPane, 0, SWT.RIGHT);
    commandLineTxtLayoutData.bottom=new FormAttachment(100);
    commandLineTxt.setLayoutData(commandLineTxtLayoutData);
    commandLineTxt.addFocusListener(commandLineFocusListener);
    commandLineTxt.addKeyListener(commandLineKeyListener);
    commandLineTxt.setFont(monospaceFont);
    commandLineTxt.setBackground(blackColor);
    commandLineTxt.setForeground(greyColor);
    commandLineTxt.addTraverseListener(preventTraversal);
    terminalLayoutData.bottom=new FormAttachment(commandLineTxt);


    monospaceFont.dispose();
  }

  private FocusAdapter commandLineFocusListener = new FocusAdapter() {
    @Override
    public void focusGained(FocusEvent e) {
      commandLineTxt.setSelection(commandLineTxt.getText().length());
    }
  };

  private KeyListener commandLineKeyListener = new KeyAdapter() {
    @Override
    public void keyPressed(KeyEvent keyEvent) {
      try {
        if (isExecuteCommandKeyStroke(keyEvent)) {
          String commandText = commandLineTxt.getText();
          if (commandText.isEmpty() == false) {
            app.client.executeCommand(commandText);
//            commandHistoryTable.setItemCount(interactiveSession.history.size());
//            commandHistoryTable.showItem(commandHistoryTable.getItem(interactiveSession.history.size() - 1));
            commandLineTxt.setText("");
          }
        } else if(isNavigateOutputsKeyStroke(keyEvent) && isArrowKeyStroke(keyEvent)){
//          natOut.setFocus();
//          Event event = new Event();
//          event.type = SWT.KeyUp;
//          event.keyCode = keyEvent.keyCode;
//          event.character = keyEvent.character;
//          event.item=tabularOutputTable;
//          event.widget=tabularOutputTable;
//          event.doit=true;
//          Display.getCurrent().post(event);
//          tabularOutputTable.notifyListeners(event.type, event);
          commandLineTxt.setFocus();

//          if (keyEvent.keyCode == SWT.ARROW_UP || keyEvent.keyCode==SWT.PAGE_UP) {
//            int currentSelection=tabularOutputTable.getSelectionIndex();
//            if(currentSelection==-1){
//              currentSelection=tabularOutputTable.getItemCount()-1;
//            }
//            else{
//              if(currentSelection>0){
//                currentSelection--;
//              }
//            }
//            tabularOutputTable.setSelection(currentSelection);
//          }
//          else if (keyEvent.keyCode == SWT.ARROW_DOWN || keyEvent.keyCode==SWT.PAGE_DOWN) {
//            
//          }
        }
        else if (keyEvent.keyCode == SWT.ARROW_UP) {
          app.getSessionModel().getHistory().getLastExecutionRecord();
//          commandHistoryTable.setSelection(commandHistoryTable.getItemCount() - 1);
//          commandHistoryTable.setFocus();
        }
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }

    private boolean isNavigateOutputsKeyStroke(KeyEvent keyEvent) {
      return (keyEvent.stateMask & SWT.SHIFT) != 0;
    }

    private boolean isArrowKeyStroke(KeyEvent keyEvent) {
      return keyEvent.keyCode==SWT.ARROW_UP || keyEvent.keyCode==SWT.PAGE_UP || keyEvent.keyCode==SWT.ARROW_DOWN || keyEvent.keyCode==SWT.PAGE_DOWN;
    }

    private boolean isExecuteCommandKeyStroke(KeyEvent keyEvent) {
      return keyEvent.keyCode==SWT.CR || keyEvent.keyCode==SWT.KEYPAD_CR;
    }
  };

  private void changeCurrentlyDisplayedExecutionRecord(ExecutionHistoryRecord newExecutionRecord) {
    if(newExecutionRecord.out.rows.isEmpty()){
      //We do not want to erase the old content, but we want to give feedback that the command was executed.
      return;
    }
    boolean needRefresh=currentlySelectedExecutionRecord==null;
    currentlySelectedExecutionRecord=newExecutionRecord;

    if(!needRefresh){
      int percentage=100/(terminalDataLayer.getColumnCount()+1);
      terminalDataLayer.setColumnWidthPercentageByPosition(0, percentage*2);
      for(int i=1; i<terminalDataLayer.getColumnCount(); i++){
        terminalDataLayer.setColumnWidthPercentageByPosition(i, percentage);
      }
    }
    terminalGrid.refresh(); //FIXME: This somewhat works to display the columns the first time a command is run. Especially if the output contains few rows and does not require a scroll bar
    
    int selectedTab=0;
    tabularErrorTable.clearAll();
    int numberOfErrLines=0;
//    if(currentlySelectedExecutionRecord.err!=null){
//      numberOfErrLines=currentlySelectedExecutionRecord.err.rows.size();
//      if(numberOfErrLines>0){
//        selectedTab=1;
//      }
//    }
    TabFolder tabPane=(TabFolder) terminalGrid.getParent();
    tabPane.getItem(1).setText("err "+numberOfErrLines+" lines");
    tabularErrorTable.setItemCount(numberOfErrLines);

    tabularLogTable.clearAll();
    int numberOfLogLines=0;
//    if(currentlySelectedExecutionRecord.log!=null){
//      numberOfLogLines=currentlySelectedExecutionRecord.log.rows.size();
//    }
    tabPane.getItem(2).setText("log-"+numberOfLogLines);
    tabularLogTable.setItemCount(numberOfLogLines);
    
    tabPane.setSelection(selectedTab);
    commandLineTxt.setFocus();
  }

  private TraverseListener preventTraversal=new TraverseListener() {
    @Override
    public void keyTraversed(TraverseEvent e) {
      if (e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
        e.doit = false;
      }
    }
  };

//  private IDataProvider currentlySelectedExecutionRecordDataProvider=new IDataProvider() {
//    @Override
//    public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
//    }
//    
//    @Override
//    public int getRowCount() {
//      if(currentlySelectedExecutionRecord==null){
//        return 0;
//      }
//      return currentlySelectedExecutionRecord.out.getRowCount();
//    }
//    
//    @Override
//    public Object getDataValue(int columnIndex, int rowIndex) {
//      if(currentlySelectedExecutionRecord==null){
//        return null;
//      }
//      return currentlySelectedExecutionRecord.out.getDataValue(columnIndex, rowIndex);
//    }
//    
//    @Override
//    public int getColumnCount() {
//      if(currentlySelectedExecutionRecord==null){
//        return 0;
//      }
//      return currentlySelectedExecutionRecord.out.getColumnCount();
//    }
//  };
//
  private IDataProvider columnDataProvider=new IDataProvider() {
    @Override
    public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
    }
    
    @Override
    public int getRowCount() {
      if(currentlySelectedExecutionRecord==null){
        return 0;
      }
      return 1;
    }
    
    @Override
    public Object getDataValue(int columnIndex, int rowIndex) {
      if(currentlySelectedExecutionRecord==null){
        return "XXXY";
      }
      return currentlySelectedExecutionRecord.out.getColumnHeader().get(columnIndex).name;
    }
    
    @Override
    public int getColumnCount() {
      if(currentlySelectedExecutionRecord==null){
        return 0;
      }
      return currentlySelectedExecutionRecord.out.getColumnHeader().size();
    }
  };

}
