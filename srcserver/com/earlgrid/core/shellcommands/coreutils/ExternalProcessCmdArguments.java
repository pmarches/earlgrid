package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class ExternalProcessCmdArguments extends BaseCmdArguments<ExternalProcessCmdArguments> {
  boolean programHasHeader=false;
  String[] programAndArgs;

  @Override
  public BaseCmdSpecification<ExternalProcessCmdArguments> newCmdSpecification() {
    return new ExternalProcessCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    programAndArgs=new String[argumentsArray.length+1];
    programAndArgs[0]=commandString;
    System.arraycopy(argumentsArray, 0, programAndArgs, 1, argumentsArray.length);
  }

}
