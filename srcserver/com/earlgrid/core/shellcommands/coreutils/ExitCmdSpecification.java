package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class ExitCmdSpecification extends BaseCmdSpecification<ExitCmdArguments> {
  @Override
  public void onThisCommandExecute() throws Exception {
    session.terminateSession();
  }
}
