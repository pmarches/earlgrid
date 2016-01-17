package com.earlgrid.core.shellcommands;

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
