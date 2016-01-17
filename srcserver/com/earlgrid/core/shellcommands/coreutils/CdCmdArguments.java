package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;


public class CdCmdArguments extends BaseCmdArguments<CdCmdArguments> {
  public String[] paths;

  @Override
  public BaseCmdSpecification<CdCmdArguments> newCmdSpecification() {
    return new CdCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    if(argumentsArray.length>1){
      throw new Exception("Unexpected number of arguments to the cd command");
    }
    paths=argumentsArray;
  }

}
