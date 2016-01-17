package com.earlgrid.core.shellparser;

import java.util.ArrayList;

public class ResolvedSingleCommand {
  ArrayList<ResolvedArgument> cmdAndArguments=new ArrayList<>();
  
  public String getCommandName(){
    return cmdAndArguments.get(0).toString();
  }
  
  @Override
  public String toString() {
    StringBuffer sb=new StringBuffer();
    for(ResolvedArgument arg: cmdAndArguments){
      sb.append(arg.toString());
      sb.append(' ');
    }
    if(sb.length()!=0){
      sb.deleteCharAt(sb.length()-1);
    }
    return sb.toString();
  }

  public String[] getArgumentsArray() {
    String[] argsArray=new String[cmdAndArguments.size()-1];
    for(int i=1; i<cmdAndArguments.size(); i++){
      String resolvedArgStr=cmdAndArguments.get(i).toString();
      argsArray[i-1]=resolvedArgStr;
    }
    return argsArray;
  }
}
