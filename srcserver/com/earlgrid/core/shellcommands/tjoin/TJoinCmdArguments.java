package com.earlgrid.core.shellcommands.tjoin;

import java.util.ArrayList;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class TJoinCmdArguments extends BaseCmdArguments<TJoinCmdArguments> {
  ArrayList<TJoinExpresssion> expressions; 

  @Override
  public BaseCmdSpecification newCmdSpecification() {
    return new TJoinCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    
  }

}
