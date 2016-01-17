package com.earlgrid.core.shellcommands.coreutils;

import java.util.Arrays;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class ClearCmdArguments extends BaseCmdArguments<ClearCmdArguments> {
  @Override
  public BaseCmdSpecification<ClearCmdArguments> newCmdSpecification() {
    return new ClearCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    if(argumentsArray.length!=0){
      throw new Exception("Unexpected arguments "+Arrays.asList(argumentsArray));
    }
  }

}
