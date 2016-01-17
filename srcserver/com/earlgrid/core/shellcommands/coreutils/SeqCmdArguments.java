package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class SeqCmdArguments extends BaseCmdArguments<SeqCmdArguments> {
  int start;
  int end;
  int step=1;
  
  @Override
  public BaseCmdSpecification<SeqCmdArguments> newCmdSpecification() {
    return new SeqCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    if(argumentsArray.length<2){
      throw new Exception("Invalid number of arguments, expected at least 2, but got "+argumentsArray.length);
    }
    else if(argumentsArray.length>3){
      throw new Exception("Invalid number of arguments, expected at most 3, but got "+argumentsArray.length);
    }
    
    start=Integer.parseInt(argumentsArray[0]);
    end=Integer.parseInt(argumentsArray[1]);
    if(argumentsArray.length==3){
      step=Integer.parseInt(argumentsArray[2]);
    }
  }

}
