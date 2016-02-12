package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class ExitCmdArguments extends BaseCmdArguments<ExitCmdArguments> {
  @Override
  public BaseCmdSpecification<ExitCmdArguments> newCmdSpecification() {
    return new ExitCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
  }
}
