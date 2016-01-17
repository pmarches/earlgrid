package com.earlgrid.ui.parts;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.core.sessionmodel.TabularOutput;
import com.earlgrid.ui.model.TabularOutputDataProvider;
import com.earlgrid.ui.standalone.ApplicationMain;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TerminalEditor extends Composite {
  public static final int VERTICAL_SPACING_BETWEEN_COMMANDS = 1;

  final Color greenColor = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
  final Color redColor = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED);
  final Color blackColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
  final Color greyColor = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);

  ScrolledComposite scrollComposite;
  private Composite terminalOutputComposite;
  private Font monospaceFont;

  private Text commandLineTxt;

  ApplicationMain app;

  public TerminalEditor(Composite parent, ApplicationMain app) {
    super(parent, SWT.NONE);
    this.app=app;
    createPartControl(parent);
  }

  public void createPartControl(Composite parent) {
    monospaceFont = new Font(parent.getDisplay(), new FontData("Monospace", 12, SWT.NONE));
    FillLayout fillLayout = (FillLayout) parent.getLayout();
    fillLayout.type = SWT.VERTICAL;

    Composite topComposite=new Composite(parent, SWT.NONE);
    topComposite.setLayout(new FormLayout());
    
    scrollComposite = new ScrolledComposite(topComposite, SWT.V_SCROLL);
    scrollComposite.setAlwaysShowScrollBars(true);
    scrollComposite.setExpandHorizontal(true);
    scrollComposite.setExpandVertical(true);
    scrollComposite.setMinWidth(topComposite.getSize().y);
    FormData scrollCompositeLayoutData=new FormData();
    scrollCompositeLayoutData.top=new FormAttachment(0);
    scrollCompositeLayoutData.left=new FormAttachment(0);
    scrollCompositeLayoutData.right=new FormAttachment(100);
    scrollComposite.setLayoutData(scrollCompositeLayoutData);
    
    terminalOutputComposite = new Composite(scrollComposite, SWT.NONE);
    terminalOutputComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_CYAN));
    GridLayout terminalCompositeLayout=new GridLayout(1, false);
    terminalCompositeLayout.verticalSpacing=VERTICAL_SPACING_BETWEEN_COMMANDS;
    terminalCompositeLayout.marginWidth=0;
    terminalCompositeLayout.marginHeight=0;
    terminalOutputComposite.setLayout(terminalCompositeLayout);
    
    Composite emptyPadding=new Composite(terminalOutputComposite, SWT.NONE);
    GridData emptyPaddingLayoutData=new GridData(SWT.FILL, SWT.FILL, true, true);
    emptyPaddingLayoutData.heightHint=Integer.MAX_VALUE;
    emptyPadding.setLayoutData(emptyPaddingLayoutData);
    
    scrollComposite.setContent(terminalOutputComposite);
    for(ExecutionHistoryRecord executionHistoryRecord : app.getSessionModel().getHistory()){
      createOutputRow(terminalOutputComposite, executionHistoryRecord);
    }
    scrollComposite.setOrigin(0, Integer.MAX_VALUE);
    
    commandLineTxt = new Text(topComposite, SWT.BORDER);
    FormData commandLineTxtLayoutData=new FormData();
    commandLineTxtLayoutData.left=new FormAttachment(scrollComposite, 0, SWT.LEFT);
    commandLineTxtLayoutData.right=new FormAttachment(scrollComposite, 0, SWT.RIGHT);
    commandLineTxtLayoutData.bottom=new FormAttachment(100);
    commandLineTxt.setLayoutData(commandLineTxtLayoutData);
//    commandLineTxt.addFocusListener(commandLineFocusListener);
    commandLineTxt.addKeyListener(new TerminalEditorCommandBoxKeyHandler(this));
    commandLineTxt.setFont(monospaceFont);
    commandLineTxt.setBackground(blackColor);
    commandLineTxt.setForeground(greyColor);
    commandLineTxt.addTraverseListener(preventTraversal);
    scrollCompositeLayoutData.bottom=new FormAttachment(commandLineTxt);

    monospaceFont.dispose();
    monospaceFont=null;
  }

  private void createOutputRow(Composite allOutputComposite, ExecutionHistoryRecord executionHistoryRecord) {
    Composite executionRowComposite = new Composite(allOutputComposite, SWT.BORDER);
    executionRowComposite.setBackgroundMode( SWT.INHERIT_FORCE );
    executionRowComposite.setBackground(greenColor);
    GridLayout executionLayout = new GridLayout(8, false);
    executionLayout.marginHeight=0;
    executionLayout.marginWidth=0;
    executionLayout.horizontalSpacing=0;
    executionLayout.verticalSpacing=0;
    executionLayout.marginRight=3;
    executionRowComposite.setLayout(executionLayout);
    executionRowComposite.setData(executionHistoryRecord);
    executionHistoryRecord.outputComposite=executionRowComposite;

    Label executionReturnCodeLabel=new Label(executionRowComposite, SWT.NONE);
//    if(executionHistoryRecord.exitStatus.getExitMessage()!=null){
//      Image ERROR_IMAGE=new Image(allOutputComposite.getDisplay(), "resources/png/x.png");
//      executionReturnCodeLabel.setImage(ERROR_IMAGE);
//      ERROR_IMAGE.dispose();
//    }
//    else{
      Image ERROR_IMAGE=new Image(allOutputComposite.getDisplay(), "resources/png/x.png");
      executionReturnCodeLabel.setImage(ERROR_IMAGE);
      ERROR_IMAGE.dispose();
//    }
    executionReturnCodeLabel.setLayoutData(new GridData(20, 20));

    Button executionIndexBtn=new Button(executionRowComposite, SWT.PUSH);
    executionIndexBtn.setText(""+executionHistoryRecord.taskId);
    GridData executionIndexLayoutData=new GridData(SWT.LEFT);
    executionIndexBtn.setLayoutData(executionIndexLayoutData);

    Text commandTextLabel=new Text(executionRowComposite, SWT.NONE);
    commandTextLabel.setEditable(false);
    commandTextLabel.setFont(monospaceFont);
    commandTextLabel.setBackground(greenColor);
    commandTextLabel.setForeground(greyColor);
    commandTextLabel.setText(executionHistoryRecord.userEditedCommand);
    GridData commandTextLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    commandTextLabel.setLayoutData(commandTextLayoutData);
    commandTextLayoutData.horizontalIndent=10;

    GridData btnLayoutdata=new GridData(SWT.FILL, SWT.FILL, false, false);
    btnLayoutdata.widthHint=70;
    Button outBtn=new Button(executionRowComposite, SWT.PUSH|SWT.LEFT);
    outBtn.setText("out "+executionHistoryRecord.out.getRowCount());
    outBtn.setLayoutData(btnLayoutdata);
    Button errBtn=new Button(executionRowComposite, SWT.PUSH|SWT.LEFT);
    errBtn.setText("err "+67);
    errBtn.setLayoutData(btnLayoutdata);
    Button logBtn=new Button(executionRowComposite, SWT.PUSH|SWT.LEFT);
    logBtn.setText("log "+66);
    logBtn.setLayoutData(btnLayoutdata);

    Button minimizeOutputBtn=new Button(executionRowComposite, SWT.PUSH);
    minimizeOutputBtn.setText("_");
    GridData minimizeLayoutData = new GridData(SWT.LEFT);
    minimizeLayoutData.horizontalIndent=10;
    minimizeOutputBtn.addSelectionListener(onMinimizeOutput);
    minimizeOutputBtn.setLayoutData(minimizeLayoutData);
    Button closeOutputBtn=new Button(executionRowComposite, SWT.PUSH);
    closeOutputBtn.setText("X");
    closeOutputBtn.setLayoutData(new GridData(SWT.RIGHT));
    closeOutputBtn.addSelectionListener(onRemoveOutput);
    
    int rowHeight=30;
    TabularOutput outputModel = executionHistoryRecord.out;
    TabularOutputDataProvider outputDataProvider = new TabularOutputDataProvider(outputModel);
    if(outputDataProvider.getRowCount()!=0){
      rowHeight+=Math.min(600, 21+(outputDataProvider.getRowCount()*20));
      DataLayer terminalDataLayer = new DataLayer(outputDataProvider);
      for(int i=0; i<outputDataProvider.getColumnCount(); i++){
        int columnWidthInPixel=(outputModel.getColumnHeader().get(i).nbMaxCharacters*10)+2;
        terminalDataLayer.setColumnWidthByPosition(i, columnWidthInPixel);
      }
      SelectionLayer selectionLayer=new SelectionLayer(terminalDataLayer);
      ViewportLayer viewPortLayer=new ViewportLayer(selectionLayer);
      viewPortLayer.setRegionName(GridRegion.BODY);
      ColumnHeaderLayer columnHeaderLayer=new ColumnHeaderLayer(new DataLayer(createColumnDataProvider(outputModel)), viewPortLayer, selectionLayer);
      CompositeLayer headerAndDataCompositeLayer = new CompositeLayer(1, 2);
      headerAndDataCompositeLayer.setChildLayer(GridRegion.COLUMN_HEADER, columnHeaderLayer, 0, 0);
      headerAndDataCompositeLayer.setChildLayer(GridRegion.BODY, viewPortLayer, 0, 1);
      NatTable terminalGrid = new NatTable(executionRowComposite, headerAndDataCompositeLayer);
      terminalGrid.setTheme(new EarlGridTheme());
      terminalGrid.setBackground(blackColor);
      
      GridData terminalGridLayoutData=new GridData(SWT.FILL, SWT.FILL, true, true, 8, 1);
      terminalGrid.setLayoutData(terminalGridLayoutData);
    }

    GridData executionRowLayoutData=new GridData(SWT.FILL, SWT.TOP, true, true);
    executionRowLayoutData.minimumHeight=rowHeight;
    executionRowLayoutData.heightHint=rowHeight;
    executionRowComposite.setLayoutData(executionRowLayoutData);

    int newScrollHeigth=scrollComposite.getMinHeight()+rowHeight+VERTICAL_SPACING_BETWEEN_COMMANDS;
    scrollComposite.getVerticalBar().setMaximum(newScrollHeigth-scrollComposite.getVerticalBar().getThumb());
    scrollComposite.setMinHeight(newScrollHeigth);
  }

  private SelectionListener onMinimizeOutput=new SelectionAdapter() {
     @Override
    public void widgetSelected(SelectionEvent e) {
       try {
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
  };

  private IDataProvider createColumnDataProvider(final TabularOutput out){
    return new IDataProvider() {
      @Override
      public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
      }

      @Override
      public int getRowCount() {
        return 1;
      }

      @Override
      public Object getDataValue(int columnIndex, int rowIndex) {
        return out.columnHeader.get(columnIndex).name;
      }

      @Override
      public int getColumnCount() {
        return out.columnHeader.size();
      }
    };
  }

  void executeCommand(String commandToExecute) throws Exception {
    app.client.executeCommand(commandToExecute);
    scrollComposite.layout(true, true);
    scrollComposite.setOrigin(0, Integer.MAX_VALUE);
  }

  private TraverseListener preventTraversal=new TraverseListener() {
    @Override
    public void keyTraversed(TraverseEvent e) {
      if (e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
        e.doit = false;
      }
    }
  };

  private SelectionListener onRemoveOutput=new SelectionAdapter() {
    @Override
    public void widgetSelected(SelectionEvent e) {
      Button closeButtonSelected=(Button) e.getSource();
      Composite outputRowComposite=closeButtonSelected.getParent();
      ExecutionHistoryRecord executionHistoryRecord=(ExecutionHistoryRecord) outputRowComposite.getData();
      removeExecutionFromHistoryAndTerminal(executionHistoryRecord);
      commandLineTxt.setFocus();
    }

  };

  public void removeExecutionFromHistoryAndTerminal(ExecutionHistoryRecord executionHistoryRecord) {
    app.getSessionModel().getHistory().removeExecutionRecord(executionHistoryRecord.taskId);
    executionHistoryRecord.outputComposite.dispose();
    terminalOutputComposite.pack();
  }
}
