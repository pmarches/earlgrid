package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

//usage: session <set|get|ls|rm> name
public class SessionCmdArguments extends BaseCmdArguments<SessionCmdArguments> {
  enum SESSION_SUB_COMMAND { LS, GET, SET, RM };
  SESSION_SUB_COMMAND subCommand;
  public String keyName;
  
  @Override
  public BaseCmdSpecification<SessionCmdArguments> newCmdSpecification() {
    return new SessionCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    this.subCommand=SESSION_SUB_COMMAND.valueOf(argumentsArray[0].toUpperCase());
    if(argumentsArray.length>1){
      keyName=argumentsArray[1];
    }
  }

}
