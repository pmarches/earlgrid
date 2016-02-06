package com.earlgrid.core.execution;

import com.earlgrid.core.sessionmodel.CmdBeginStatus;
import com.earlgrid.core.sessionmodel.CmdExitStatus;
import com.earlgrid.core.sessionmodel.SessionModelChangeObserver;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TaskExitStatus;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class ForwardSessionModelChanges extends BaseCmdSpecification<Object> {
  private SessionModelChangeObserver forwardToSessionModelChangeReceiver;
  
  public ForwardSessionModelChanges(SessionModelChangeObserver forwardToSessionModelChangeReceiver, int taskId) {
    super.taskId=taskId;
    this.forwardToSessionModelChangeReceiver=forwardToSessionModelChangeReceiver;
  }

  @Override
  public void onUpstreamCommandColumnHeader(TabularOutputColumnHeader parentColumnHeader) throws Exception {
    forwardToSessionModelChangeReceiver.onUpstreamColumnHeader(parentColumnHeader);
  }

  @Override
  public void onUpstreamCommandBegun(CmdBeginStatus parentCommandBegun) throws Exception {
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
