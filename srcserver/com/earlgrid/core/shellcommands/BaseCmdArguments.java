package com.earlgrid.core.shellcommands;

abstract public class BaseCmdArguments<T> {
  abstract public BaseCmdSpecification<T> newCmdSpecification();
  abstract public void parseArguments(String commandString, String[] argumentsArray) throws Exception;

  protected String getMandatoryArgument(String[] argumentsArray, int argumentIndex, String errorMessage) throws Exception {
    try {
      return argumentsArray[argumentIndex];
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new Exception(errorMessage);
    }
  }
}
