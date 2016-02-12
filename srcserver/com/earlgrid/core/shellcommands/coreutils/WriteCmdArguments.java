package com.earlgrid.core.shellcommands.coreutils;

import java.util.Arrays;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

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
