package com.earlgrid.core.execution;

import com.earlgrid.core.sessionmodel.CmdBeginStatus;
import com.earlgrid.core.sessionmodel.CmdExitStatus;
import com.earlgrid.core.sessionmodel.SessionModelChangeObserver;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TaskBeginStatus;
import com.earlgrid.core.sessionmodel.TaskExitStatus;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class ForwardSessionModelChanges extends BaseCmdSpecification {
  private SessionModelChangeObserver forwardToSessionModelChangeReceiver;
  private String userEditedCommand;
  
  public ForwardSessionModelChanges(String userEditedCommand, SessionModelChangeObserver forwardToSessionModelChangeReceiver, int taskId) {
    this.userEditedCommand=userEditedCommand;
    super.taskId=taskId;
    this.forwardToSessionModelChangeReceiver=forwardToSessionModelChangeReceiver;
  }

  @Override
  public void onUpstreamCommandColumnHeader(TabularOutputColumnHeader parentColumnHeader) throws Exception {
    forwardToSessionModelChangeReceiver.onUpstreamColumnHeader(parentColumnHeader);
  }

  @Override
  public void onUpstreamCommandBegun(CmdBeginStatus parentCommandBegun) throws Exception {
    forwardToSessionModelChangeReceiver.onUpstreamTaskBegin(new TaskBeginStatus(getTaskId(), userEditedCommand));
  }

  @Override
  public void onUpstreamCommandFinished(CmdExitStatus input) throws Exception {
    forwardToSessionModelChangeReceiver.onUpstreamTaskFinished(new TaskExitStatus(getTaskId()));
  }

  @Override
  public void onUpstreamCommandOutputRow(TabularOutputRow outputElement) throws Exception {
    forwardToSessionModelChangeReceiver.onUpstreamOutputRow(outputElement);
  }
}
