package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class PromptCmdArguments extends BaseCmdArguments<PromptCmdArguments> {
  public boolean passwordMode;
  public String title;
  public String fieldName;
  
  @Override
  public BaseCmdSpecification<PromptCmdArguments> newCmdSpecification() {
    return new PromptCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    for (int i = 0; i < argumentsArray.length; i++) {
      String arg = argumentsArray[i];
      if("-p".equals(arg)){
        passwordMode=true;        
      }
      else if("-t".equals(arg)){
        i++;
        title=getMandatoryArgument(argumentsArray, i, "The -t flag requires the title as argument");        
      }
      else if("-f".equals(arg)){
        i++;
        fieldName=getMandatoryArgument(argumentsArray, i, "The -f flag requires a field name as argument");        
      }
      else{
        throw new Exception("Unknown argument '"+arg+"'");
      }
    }
  }
}
