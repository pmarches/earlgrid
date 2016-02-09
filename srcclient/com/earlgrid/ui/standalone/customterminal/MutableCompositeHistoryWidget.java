package com.earlgrid.ui.standalone.customterminal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Slider;

import com.earlgrid.core.sessionmodel.ChangeCurrentWorkingDirectorySessionModelEvent;
import com.earlgrid.core.sessionmodel.RemoveTaskFromHistorySessionModelEvent;
import com.earlgrid.core.sessionmodel.SessionModelChangeObserver;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TaskCreatedStatus;
import com.earlgrid.core.sessionmodel.TaskExitStatus;
import com.earlgrid.ui.standalone.ApplicationMain;
import com.earlgrid.ui.standalone.TerminalActionWindow;

public class MutableCompositeHistoryWidget extends Composite implements SessionModelChangeObserver {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(MutableCompositeHistoryWidget.class);
  Slider slider;
  Composite taskContent;
  LineBasedPanel lineBasedPanel=new LineBasedPanel(); //FIXME Move this to the sessionModel?
  LineBasedViewport lineBasedViewPort=new LineBasedViewport(lineBasedPanel, 0);

  public MutableCompositeHistoryWidget(Composite parent, int style) {
    super(parent, style);
    TerminalActionWindow.configureLookOfControlFromParent(this);
    TerminalActionWindow.configureTightGridLayout(this, 2, false);
    TerminalActionWindow.configureLookOfControlFromParent(this);

    taskContent=new Composite(this, SWT.NONE);
    TerminalActionWindow.configureLookOfControlFromParent(taskContent);
    taskContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    taskContent.addFocusListener(onFocusListener);

    slider=new Slider(this, SWT.VERTICAL);
    slider.setValues(0, 0, 0, 10, 1, 10);
    slider.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
    slider.addSelectionListener(onSliderChanged);
    slider.addFocusListener(onSliderFocus);

    taskContent.addControlListener(onResizeOrMoved);
    ApplicationMain.getInstance().getSessionModel().addDelayedObserver(this);
  }

  public void refreshAllTaskWidgets(){
    if(isDisposed()){
      return;
    }
    getDisplay().asyncExec(new Runnable() {
      @Override
      public void run() {
        if(isDisposed()){
          return;
        }
        lineBasedViewPort.scrollTopOfViewPortToPanelLine(slider.getSelection());

        taskContent.setRedraw(false);
        for(Control c : taskContent.getChildren()){
          ApplicationMain.getInstance().getSessionModel().removeDelayedObserver((SessionModelChangeObserver) c);
          c.dispose();
        }

        PositionedLineBasedArea[] visibleAreas=lineBasedViewPort.getPositionedVisibleAreas();
        if(visibleAreas.length!=0){
          //The first area might be partially visible
          PositionedLineBasedArea firstArea=visibleAreas[0];
          int y=firstArea.topOffsetOfArea*TaskWidget.NB_PIXELS_PER_ROW;
          log.debug("lineBasedViewPort="+lineBasedViewPort.toString());
          for(PositionedLineBasedArea area : visibleAreas){
            TaskWidget taskWidget=new TaskWidget(taskContent, SWT.NONE, area.area.getRecord());
            taskWidget.setAllowFocus(false);
            TerminalActionWindow.configureLookOfControlFromParent(taskWidget);
            int widgetHeight=area.area.getNumberOfLines()*TaskWidget.NB_PIXELS_PER_ROW;
            taskWidget.setBounds(0, y, getClientArea().width, widgetHeight);
            y+=widgetHeight;
            log.debug("taskWidget bounds:"+taskWidget.getBounds());
            
            ApplicationMain.getInstance().getSessionModel().addDelayedObserver(taskWidget);
          }
        }
        
        taskContent.setRedraw(true);
      }
    });
  }

  private int getNumberOfDisplayableRows() {
    return getClientArea().height/TaskWidget.NB_PIXELS_PER_ROW;
  }

  private ControlListener onResizeOrMoved=new ControlAdapter() {
    @Override
    public void controlResized(ControlEvent e) {
      lineBasedViewPort.setSize(getNumberOfDisplayableRows());
      refreshAllTaskWidgets();
    }
  };
  
  private FocusListener onSliderFocus=new FocusAdapter() {
    @Override
    public void focusGained(FocusEvent e) {
      taskContent.setFocus();
    }
  };

  private SelectionListener onSliderChanged=new SelectionAdapter(){
    @Override
    public void widgetSelected(SelectionEvent e) {
      refreshAllTaskWidgets();
    }
  };
  
  private FocusListener onFocusListener=new FocusAdapter() {
    public void focusGained(org.eclipse.swt.events.FocusEvent e) {
      ApplicationMain.getInstance().mainWindow.terminalWindow.inputArea.setFocus();
    };
  };

  public void setFocusOnTask(int taskIdToFocus) {
    for(Control c : taskContent.getChildren()){
      TaskWidget taskWidget = (TaskWidget) c;
      if(taskWidget.getTaskId()==taskIdToFocus){
        taskWidget.setAllowFocus(true);
        taskWidget.setFocus();
        taskWidget.setAllowFocus(false);
      }
    }
    
  }

  @Override
  public void onUpstreamTaskCreated(TaskCreatedStatus taskCreated) {
    updateSliderMaximum();
    lineBasedPanel.occupyArea(new LineBasedArea(ApplicationMain.getInstance().getSessionModel().getHistory().get(taskCreated.getTaskId())));
    refreshAllTaskWidgets();
  }

  private void updateSliderMaximum() {
    getDisplay().asyncExec(() -> {
      int nbLinesWithContent=lineBasedPanel.getNumberOfOccupiedLines();
      slider.setMaximum(Math.max(0, nbLinesWithContent-lineBasedViewPort.nbOfVisibleLinesInViewport));
      slider.setSelection(slider.getMaximum());
      log.debug("sel={} max={}", slider.getSelection(), slider.getMaximum());
    });
  }

  @Override
  public void onUpstreamTaskFinished(TaskExitStatus exitStatus) {
  }

  @Override
  public void onUpstreamColumnHeader(TabularOutputColumnHeader columnHeader) {
  }

  @Override
  public void onUpstreamOutputRow(TabularOutputRow outputRow) {
    updateSliderMaximum();
    refreshAllTaskWidgets();
  }

  @Override
  public void onChangeCurrentWorkingDirectory(ChangeCurrentWorkingDirectorySessionModelEvent changeWorkingDirectoryEvent) {
  }

  @Override
  public void onRemoveAllTasksFromHistory(RemoveTaskFromHistorySessionModelEvent event) {
    lineBasedPanel.freeAllAreas();
    refreshAllTaskWidgets();
  }
}
