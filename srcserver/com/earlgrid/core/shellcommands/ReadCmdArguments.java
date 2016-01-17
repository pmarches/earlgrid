package com.earlgrid.core.shellcommands;

import java.util.Arrays;

public class ReadCmdArguments extends BaseCmdArguments<ReadCmdArguments> {
  String inputFilePath;
  
  @Override
  public BaseCmdSpecification<ReadCmdArguments> newCmdSpecification() {
    return new ReadCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    if(argumentsArray.length!=1){
      throw new Exception("Expected filename but got '"+Arrays.asList(argumentsArray));
    }
    inputFilePath=argumentsArray[0];
  }

}
