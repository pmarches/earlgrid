package com.earlgrid.core.shellparser;

public class UnResolvedArgument {
  public enum ArgumentKind {
    WHITESPACE,
    ORDINARY,
    ENVIRONMENT_VARIABLE,
    REDIRECTION,
    BACKGROUND,
  };

  String argumentStr;
  ArgumentKind kind;
  
  public UnResolvedArgument(String argumentStr, ArgumentKind kind) {
    this.argumentStr=argumentStr;
    this.kind=kind;
  }

  @Override
  public String toString() {
    return argumentStr;
  }
}
