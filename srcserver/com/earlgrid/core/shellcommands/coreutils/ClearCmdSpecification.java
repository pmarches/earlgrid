package com.earlgrid.core.shellcommands.coreutils;
import com.earlgrid.core.sessionmodel.RemoveTaskFromHistorySessionModelEvent;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class ClearCmdSpecification extends BaseCmdSpecification<ClearCmdArguments> {
  @Override
  protected void onThisCommandExecute() throws Exception {
    session.getSessionModel().onRemoveAllTasksFromHistory(new RemoveTaskFromHistorySessionModelEvent(getTaskId()));
  }
}
