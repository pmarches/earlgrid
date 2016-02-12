package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class PerrorCmdArguments extends BaseCmdArguments<PerrorCmdArguments> {
  String[] errorMessageToPrint;
  
  @Override
  public BaseCmdSpecification<PerrorCmdArguments> newCmdSpecification() {
    return new PerrorCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    errorMessageToPrint=argumentsArray;
  }

}
