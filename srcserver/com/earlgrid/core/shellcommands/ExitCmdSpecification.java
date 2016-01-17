package com.earlgrid.core.shellcommands;

public class ExitCmdSpecification extends BaseCmdSpecification<ExitCmdArguments> {
  @Override
  public void onThisCommandExecute() throws Exception {
    session.terminateSession();
  }
}
