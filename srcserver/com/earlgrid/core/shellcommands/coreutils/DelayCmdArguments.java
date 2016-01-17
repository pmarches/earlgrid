package com.earlgrid.core.shellcommands.coreutils;

import java.util.Arrays;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;


public class DelayCmdArguments extends BaseCmdArguments<DelayCmdArguments> {
  int delayInMS=300;
  
  @Override
  public BaseCmdSpecification<DelayCmdArguments> newCmdSpecification() {
    return new DelayCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    if(argumentsArray.length>1){
      throw new Exception("Unexpected number of arguments to the delay command "+Arrays.asList(argumentsArray));
    }
    if(argumentsArray.length==1){
      delayInMS=Integer.parseInt(argumentsArray[0]);
    }
  }

}
