package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class WcCmdArguments extends BaseCmdArguments<WcCmdArguments> {
  @Override
  public BaseCmdSpecification<WcCmdArguments> newCmdSpecification() {
    return new WcCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
  }

}
