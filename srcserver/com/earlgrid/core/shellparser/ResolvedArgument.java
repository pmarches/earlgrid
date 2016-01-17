package com.earlgrid.core.shellparser;

public class ResolvedArgument {

  private String argumentStr;

  public ResolvedArgument(String argumentStr) {
    this.argumentStr=argumentStr;
  }

  @Override
  public String toString() {
    return argumentStr;
  }
}
