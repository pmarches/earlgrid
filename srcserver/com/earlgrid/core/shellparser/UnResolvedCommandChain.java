package com.earlgrid.core.shellparser;

import java.util.ArrayList;
import java.util.List;

public class UnResolvedCommandChain {
  List<UnResolvedSingleCommand> pipedCommands=new ArrayList<>();

  @Override
  public String toString() {
    StringBuffer sb=new StringBuffer();
    for(UnResolvedSingleCommand cmd:pipedCommands){
      sb.append(cmd.toString());
      sb.append("|");
    }
    sb.deleteCharAt(sb.length()-1);
    return sb.toString();
  }

  public int numberOfCommands() {
    return pipedCommands.size();
  }
}
