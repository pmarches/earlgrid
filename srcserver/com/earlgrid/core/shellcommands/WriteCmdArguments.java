package com.earlgrid.core.shellcommands;

import java.util.Arrays;

public class WriteCmdArguments extends BaseCmdArguments<WriteCmdArguments> {
  String outputFilePath;
  
  @Override
  public BaseCmdSpecification<WriteCmdArguments> newCmdSpecification() {
    return new WriteCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    if(argumentsArray.length!=1){
      throw new Exception("Expected filename but got '"+Arrays.asList(argumentsArray));
    }
    outputFilePath=argumentsArray[0];
  }

}
