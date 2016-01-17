package com.earlgrid.core.shellcommands.coreutils;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;


public class LsCmdArguments extends BaseCmdArguments<LsCmdArguments> {
  boolean showAllFiles = false;
  boolean longListing = false;
  boolean verbose=false;
  Path directoryToList;

  @Override
  public BaseCmdSpecification<LsCmdArguments> newCmdSpecification() {
    return new LsCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    for(int i=0; i<argumentsArray.length; i++){
      String arg=argumentsArray[i];
      if("-l".equals(arg)){
        longListing=true;
      }
      else if("-a".equals(arg)){
        showAllFiles=true;
      }
      else if("-v".equals(arg)){
        verbose=true;
      }
      else{
        directoryToList=FileSystems.getDefault().getPath(arg);
      }
    }
  }
}
