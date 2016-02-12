package com.earlgrid.core.shellcommands;

public class TabCmdArguments extends BaseCmdArguments<TabCmdArguments> {
  boolean hasHeaderRow=false;
  String regexDelimiterStr="\\s+";
  String columnWidthStr;
  boolean wellAligned;

  @Override
  public BaseCmdSpecification<TabCmdArguments> newCmdSpecification() {
    return new TabCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    for (int i = 0; i < argumentsArray.length; i++) {
      String arg = argumentsArray[i];
      if("-w".equals(arg)){
        arg=argumentsArray[++i];
        columnWidthStr=arg;
      }
      else if("-r".equals(arg)){
        arg=argumentsArray[++i];
        regexDelimiterStr=arg;
      }
      else if("-h".equals(arg)){
        hasHeaderRow=true;
      }
      else if("-a".equals(arg)){
        wellAligned=true;
      }
      else{
        throw new Exception("Invalid argument '"+arg+"'");
      }
    }

  }

}
