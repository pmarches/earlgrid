package com.earlgrid.core.shellcommands;


public class HistoryCmdArguments extends BaseCmdArguments<HistoryCmdArguments> {
  Integer historyIndex;

  @Override
  public BaseCmdSpecification<HistoryCmdArguments> newCmdSpecification() {
    return new HistoryCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    if(argumentsArray.length!=1){
      throw new Exception("Expected 1 argument");
    }
    historyIndex=Integer.parseInt(argumentsArray[0]);
  }

}
