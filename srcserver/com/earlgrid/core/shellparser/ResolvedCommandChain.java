package com.earlgrid.core.shellparser;

import java.util.ArrayList;

public class ResolvedCommandChain {
  public ArrayList<ResolvedSingleCommand> pipedCommands=new ArrayList<>();
  private String userEditedCommand;
  
  public ResolvedCommandChain(String userEditedCommand) {
    this.userEditedCommand=userEditedCommand;
  }

  @Override
  public String toString() {
    StringBuffer sb=new StringBuffer();
    for(ResolvedSingleCommand cmd:pipedCommands){
      sb.append(cmd.toString());
      sb.append("|");
    }
    sb.deleteCharAt(sb.length()-1);
    return sb.toString();
  }

  public int numberOfCommands() {
    return pipedCommands.size();
  }

  public String getUserEditedCommand() {
    return userEditedCommand;
  }
}
