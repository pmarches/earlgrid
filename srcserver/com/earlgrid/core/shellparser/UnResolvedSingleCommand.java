package com.earlgrid.core.shellparser;

import java.util.List;

public class UnResolvedSingleCommand {
  List<UnResolvedArgument> arguments;

  public UnResolvedSingleCommand(List<UnResolvedArgument> arg0) {
    this.arguments=arg0;
  }

  @Override
  public String toString() {
    StringBuffer sb=new StringBuffer();
    for(UnResolvedArgument arg: arguments){
      sb.append(arg.toString());
    }
    return sb.toString();
  }
}
