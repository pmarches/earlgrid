package com.earlgrid.core.shellcommands;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class MockCmdArguments extends BaseCmdArguments<MockCmdArguments> {
  public String subCommand;
  public int numberOfRows;
  public int numberOfColumn;
  
  public static final String LS = "ls";
  public static final String MANY = "many";
  public static final String OUTPUT = "output";
  public static final String UNIXFIND = "unixfind";
  public static final String WHO = "who";
  public static final String JSON = "json";

  @Override
  public BaseCmdSpecification<MockCmdArguments> newCmdSpecification() {
    return new MockCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    subCommand=argumentsArray[0];
    if(OUTPUT.equals(subCommand)){
      if(argumentsArray.length==3){
        numberOfColumn=Integer.parseInt(argumentsArray[1]);
        numberOfRows=Integer.parseInt(argumentsArray[2]);
      }
      else {
        numberOfColumn=1;
        numberOfRows=Integer.parseInt(argumentsArray[1]);
      }
    }
  }

}
