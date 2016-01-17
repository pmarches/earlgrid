package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class ClipboardCmdArguments extends BaseCmdArguments<ClipboardCmdArguments> {
  @Override
  public BaseCmdSpecification<ClipboardCmdArguments> newCmdSpecification() {
    return new ClipboardCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    for (int i = 0; i < argumentsArray.length; i++) {
      String arg = argumentsArray[i];
        throw new Exception("Unknown argument '"+arg+"'");
    }
  }
}
