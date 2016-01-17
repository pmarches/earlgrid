package com.earlgrid.core.shellcommands;

public class CSVCmdArguments extends BaseCmdArguments<CSVCmdArguments> {
  String inputCSVFilePath;
  String outputCSVFilePath;
  boolean hasHeader=false;

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    for (int i = 0; i < argumentsArray.length; i++) {
      String arg = argumentsArray[i];
      if("-i".equals(arg)){
        arg=argumentsArray[++i];
        inputCSVFilePath=arg;
      }
      else if("-o".equals(arg)){
        arg=argumentsArray[++i];
        outputCSVFilePath=arg;
      }
      else if("-h".equals(arg)){
        hasHeader=true;
      }
    }
  }

  @Override
  public BaseCmdSpecification<CSVCmdArguments> newCmdSpecification() {
    return new CSVCmdSpecification();
  }

}
