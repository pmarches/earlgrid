package com.earlgrid.ui.standalone.customterminal;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultSelectionStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle.LineStyleEnum;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.core.session.ExecutionHistoryRecord.TaskExecutionState;
import com.earlgrid.core.sessionmodel.ChangeCurrentWorkingDirectorySessionModelEvent;
import com.earlgrid.core.sessionmodel.RemoveTaskFromHistorySessionModelEvent;
import com.earlgrid.core.sessionmodel.SessionModelChangeObserver;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TaskCreatedStatus;
import com.earlgrid.core.sessionmodel.TaskExitStatus;
import com.earlgrid.ui.standalone.TerminalActionWindow;
import com.earlgrid.ui.standalone.ResourceCache;
import com.patrikdufresne.fontawesome.FontAwesome;

public class TaskWidget extends Composite implements SessionModelChangeObserver {
  private ExecutionHistoryRecord execRecord;
  private Label taskStateIcon;
  private DataLayer bodyDataLayer;
  protected boolean allowGridFocus=true;
  private NatTable natTable;
  public static final int NB_PIXELS_PER_ROW=20;

  public TaskWidget(Composite parent, int style, ExecutionHistoryRecord record) {
    super(parent, style);
    this.execRecord=record;
    
    GridLayout tightLayout=new GridLayout(4, false);
    tightLayout.marginHeight=0;
    tightLayout.marginWidth=0;
    tightLayout.marginRight=10; //FIXME This margin might be OS dependant. We need it on linux to avoid having the taskState icon be partially hidden by the scrollbar
    tightLayout.horizontalSpacing=5;
    tightLayout.verticalSpacing=0;
    setLayout(tightLayout);

    TerminalActionWindow.configureLookOfControlFromParent(this);

    Label promptLabel=new Label(this, SWT.NONE);
    promptLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
    promptLabel.setText(">");
    TerminalActionWindow.configureLookOfControlFromParent(promptLabel);

    //Using a regular text field will create a border that cannot be hidden
    StyledText taskCommandLine=new StyledText(this, SWT.READ_ONLY);
    taskCommandLine.setEditable(false);
    taskCommandLine.setText(record.userEditedCommand);
    TerminalActionWindow.configureLookOfControlFromParent(taskCommandLine);
    taskCommandLine.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    taskCommandLine.addFocusListener(forceFocusToParent);
    taskCommandLine.setForeground(getDisplay().getSystemColor(SWT.COLOR_GREEN));
    
    final int FONTAWESOME_FONT_SIZE=12;
    Label collapseIcon=new Label(this, SWT.NONE);
    collapseIcon.setFont(FontAwesome.getFont(FONTAWESOME_FONT_SIZE));
    collapseIcon.setText(FontAwesome.minus_square_o);
    collapseIcon.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

    taskStateIcon=new Label(this, SWT.NONE);
    taskStateIcon.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
    taskStateIcon.setFont(FontAwesome.getFont(FONTAWESOME_FONT_SIZE));
    taskStateIcon.setText(FontAwesome.close);
//    taskStateIcon.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
    updateStateIcon();

    createOutputGrid();
  }

  private void updateStateIcon() {
    if(isDisposed()){
      return;
    }
    getDisplay().asyncExec(new Runnable() {
      @Override
      public void run() {
        if(isDisposed()){
          return;
        }
        
        if(execRecord.getState()==TaskExecutionState.RUNNING){
          taskStateIcon.setText(FontAwesome.stop);
//          taskStateIcon.setImage(ResourceCache.getInstance().CLOSE_ICON_RED);
        }
        else{
          taskStateIcon.setText(FontAwesome.close);
//          taskStateIcon.setImage(ResourceCache.getInstance().CLOSE_ICON);
        }
      }
    });
  }
  
  @Override
  public Point computeSize(int wHint, int hHint, boolean changed) {
    //FIXME
    int nbRowsToDraw=Math.min(20, execRecord.out.getRowCount())+1;
    nbRowsToDraw=execRecord.out.getRowCount()+1;
    
    Point preferedSize = super.computeSize(wHint, hHint, changed);
    preferedSize.y=nbRowsToDraw*NB_PIXELS_PER_ROW;
//    System.out.println("wHint="+wHint+" hHint="+hHint+" preferedSize="+preferedSize+" task="+execRecord.taskId);
    return preferedSize;
  }
  
  void createOutputGrid(){
    bodyDataLayer = new DataLayer(){
      @Override
      public int getColumnCount() {
        return execRecord.out.getColumnHeader().size();
      }
      @Override
      public int getRowCount() {
        return execRecord.out.getRowCount();
      }
      
      @Override
      public Object getDataValue(int columnIndex, int rowIndex) {
        if(rowIndex>=execRecord.out.getRowCount()){
          return "Nil";
        }
        TabularOutputRow row = execRecord.out.getRow(rowIndex);
        return row.getCellAtColumn(columnIndex);
      }
    };
    bodyDataLayer.setColumnPercentageSizing(true);
    
    SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer);
    ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);
//    viewportLayer.setRegionName(GridRegion.BODY); //This is probably optional since we do not use a GridLayer
    viewportLayer.setHorizontalScrollbarEnabled(true);
//    viewportLayer.setClientAreaProvider(() -> new Rectangle(0, 0, 500, 1000));

    natTable = new NatTable(this, viewportLayer, false);
    natTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
    natTable.addFocusListener(forceFocusToParent);
    natTable.getConfigRegistry().registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new TextPainter(true,true));
    natTable.getConfigRegistry().registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES, false);

    // Setup selection styling
    DefaultSelectionStyleConfiguration selectionStyle = new DefaultSelectionStyleConfiguration();
    selectionStyle.selectionFont = ResourceCache.getInstance().monospaceFont;
    selectionStyle.selectionBgColor = getBackground();
    selectionStyle.selectionFgColor = GUIHelper.COLOR_RED;
    selectionStyle.anchorBorderStyle = new BorderStyle(1, GUIHelper.COLOR_DARK_GRAY, LineStyleEnum.SOLID);
    selectionStyle.anchorBgColor = GUIHelper.getColor(65, 113, 43);
    selectionStyle.selectedHeaderBgColor = GUIHelper.getColor(156, 209, 103);
    selectionLayer.addConfiguration(selectionStyle);
    
    
    Style cellStyle = new Style();
    cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.getBackground());
    cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, this.getForeground());
    cellStyle.setAttributeValue(CellStyleAttributes.FONT, ResourceCache.getInstance().monospaceFont);
    cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    cellStyle.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, VerticalAlignmentEnum.TOP);
    natTable.getConfigRegistry().registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle);

//  cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE,this.borderStyle);
    natTable.setBackground(getBackground()); //This ensures the empty pixels between tasks are not visible. FIXME Maybe this should be fixed instead?
    natTable.configure();
  }

  public void setAllowFocus(boolean allowFocus) {
    this.allowGridFocus = allowFocus;
  }

  @Override
  public boolean setFocus() {
    if(allowGridFocus){
      natTable.setFocus();
      return true;
    }
    return false;
  }
  
  private FocusListener forceFocusToParent=new FocusAdapter() {
    @Override
    public void focusGained(FocusEvent e) {
      if(allowGridFocus==false){
        getParent().forceFocus();
      }
    }
  };

  public int getTaskId() {
    return execRecord.taskId;
  }

  @Override
  public void onUpstreamTaskCreated(TaskCreatedStatus taskCreated) {
    updateStateIcon();
  }

  @Override
  public void onUpstreamTaskFinished(TaskExitStatus exitStatus) {
    updateStateIcon();
  }

  @Override
  public void onUpstreamColumnHeader(TabularOutputColumnHeader columnHeader) {
    //FIXME there are other events that we could call to notify that a row has been added
    natTable.doCommand(new VisualRefreshCommand());
  }

  @Override
  public void onUpstreamOutputRow(TabularOutputRow outputRow) {
    //FIXME there are other events that we could call to notify that a row has been added
    natTable.doCommand(new VisualRefreshCommand());
  }

  @Override
  public void onChangeCurrentWorkingDirectory(ChangeCurrentWorkingDirectorySessionModelEvent changeWorkingDirectoryEvent) {
  }

  @Override
  public void onRemoveAllTasksFromHistory(RemoveTaskFromHistorySessionModelEvent event) {
  }

}
