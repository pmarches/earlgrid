package com.earlgrid.core.shellcommands.coreutils;

import java.util.ArrayList;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class CutCmdArguments extends BaseCmdArguments<CutCmdArguments> {
  boolean discardMatches=false;
  ArrayList<String> columnNameFilter=new ArrayList<>();
  String selectionExpression;
  
  @Override
  public BaseCmdSpecification<CutCmdArguments> newCmdSpecification() {
    return new CutCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    for (int i = 0; i < argumentsArray.length; i++) {
      String arg = argumentsArray[i];
      if("-f".equals(arg)){
        arg=argumentsArray[++i];
        columnNameFilter.add(arg);
      }
      else if("-s".equals(arg)){
        arg=argumentsArray[++i];
        selectionExpression=arg;
      }
      else if("-v".equals(arg)){
        discardMatches=true;
      }
      else{
        throw new Exception("Unknown argument '"+arg+"'");
      }
    }
  }
}
