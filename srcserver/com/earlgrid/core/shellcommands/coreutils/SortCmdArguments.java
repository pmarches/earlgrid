package com.earlgrid.core.shellcommands.coreutils;

import java.util.ArrayList;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class SortCmdArguments extends BaseCmdArguments<SortCmdArguments> {
  boolean reverseSort=false;
  ArrayList<String> columnNameSortCriteria=new ArrayList<>();
  boolean caseInsensitive=false;
  boolean reorderColumns=false;

  @Override
  public BaseCmdSpecification<SortCmdArguments> newCmdSpecification() {
    return new SortCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    for(int i=0; i<argumentsArray.length; i++){
      String arg=argumentsArray[i];
      if("-i".equals(arg)){
        caseInsensitive=true;
      }
      else if("-r".equals(arg)){
        reverseSort=true;
      }
      else if("-f".equals(arg)){
        arg=argumentsArray[++i];
        columnNameSortCriteria.add(arg);
      }
      else{
        throw new Exception("Unknown argument '"+arg+"'");
      }
    }
  }

}
