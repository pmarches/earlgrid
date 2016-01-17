package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class PwdCmdArguments extends BaseCmdArguments<PwdCmdArguments>{
  @Override
  public BaseCmdSpecification<PwdCmdArguments> newCmdSpecification() {
    return new PwdCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {    
    if(argumentsArray.length!=0){
      throw new Exception("The pwd accepts no arguments");
    }
  }

}
