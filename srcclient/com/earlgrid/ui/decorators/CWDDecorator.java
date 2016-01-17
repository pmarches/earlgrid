package com.earlgrid.ui.decorators;

import com.earlgrid.core.sessionmodel.ChangeCurrentWorkingDirectorySessionModelEvent;
import com.earlgrid.core.sessionmodel.RemoveTaskFromHistorySessionModelEvent;
import com.earlgrid.core.sessionmodel.SessionModelChangeObserver;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TaskBeginStatus;
import com.earlgrid.core.sessionmodel.TaskExitStatus;
import com.earlgrid.ui.standalone.ApplicationMainWindow;
import com.earlgrid.ui.standalone.CommandLineInputArea;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CWDDecorator extends CommandLineInputAreaDecorator implements SessionModelChangeObserver {
  private final CommandLineInputArea commandLineInputArea;
  Label currentWorkindDirectoryLabel;
  
  public CWDDecorator(CommandLineInputArea commandLineInputArea, Composite contributionArea) {
    this.commandLineInputArea = commandLineInputArea;
    currentWorkindDirectoryLabel=new Label(contributionArea, SWT.NONE);
    ApplicationMainWindow.configureLookOfControlFromParent(currentWorkindDirectoryLabel);
    currentWorkindDirectoryLabel.addPaintListener(x -> scheduleUpdateOfCWDLabel() );
    scheduleUpdateOfCWDLabel();
  }

  private void scheduleUpdateOfCWDLabel() {
    commandLineInputArea.getDisplay().asyncExec(()-> {
      String promptText=String.format("%s>", this.commandLineInputArea.app.client.getSessionModel().getCurrentWorkingDirectory().toString());
      currentWorkindDirectoryLabel.setText(promptText);
      currentWorkindDirectoryLabel.getParent().layout(); //The size of the label changes, maybe we should just make the label bigger?
    } );
  }

  @Override
  public void onUpstreamTaskBegin(TaskBeginStatus commandBegun) {
  }

  @Override
  public void onUpstreamTaskFinished(TaskExitStatus exitStatus) {
  }

  @Override
  public void onUpstreamColumnHeader(TabularOutputColumnHeader columnHeader) {
  }

  @Override
  public void onUpstreamOutputRow(TabularOutputRow outputRow) {
  }

  @Override
  public void onChangeCurrentWorkingDirectory(ChangeCurrentWorkingDirectorySessionModelEvent changeWorkingDirectoryEvent) {
    scheduleUpdateOfCWDLabel();
  }

  @Override
  public void onRemoveAllTasksFromHistory(RemoveTaskFromHistorySessionModelEvent event) {
  }
}
