package com.earlgrid.core.shellparser;

import java.util.ArrayList;
import java.util.List;

import com.earlgrid.core.session.SessionEnvironmentVariables;
import com.earlgrid.core.shellparser.UnResolvedArgument.ArgumentKind;

public class CommandLineResolver {
  protected SessionEnvironmentVariables sessionVariables;

  public CommandLineResolver(SessionEnvironmentVariables sessionVariables) {
    this.sessionVariables=sessionVariables;
  }

  public List<ResolvedCommandChain> resolve(List<UnResolvedCommandChain> unResolvedCommands) {
    ArrayList<ResolvedCommandChain> allResolvedcmd=new ArrayList<>();
    
    for(UnResolvedCommandChain unResolvedCmd: unResolvedCommands){
      allResolvedcmd.add(resolve(unResolvedCmd));
    }
    return allResolvedcmd;
  }

  public ResolvedCommandChain resolve(UnResolvedCommandChain unResolvedCommand) {
    ResolvedCommandChain resolvedCommand=new ResolvedCommandChain(unResolvedCommand.toString());
    for(UnResolvedSingleCommand cmd:unResolvedCommand.pipedCommands){
      resolvedCommand.pipedCommands.add(resolve(cmd));
    }
    return resolvedCommand;
  }

  protected ResolvedSingleCommand resolve(UnResolvedSingleCommand unresolvedCmd) {
    expandVariables(unresolvedCmd);
    mergeContiguousOrdinaryArguments(unresolvedCmd);
    return convertSimplifiedUnResolvedCommandToResolvedCommand(unresolvedCmd);
  }

  protected ResolvedSingleCommand convertSimplifiedUnResolvedCommandToResolvedCommand(UnResolvedSingleCommand unresolvedCmd) {
    ResolvedSingleCommand resolvedCmd=new ResolvedSingleCommand();
    for(UnResolvedArgument arg : unresolvedCmd.arguments){
      if(arg.kind!=ArgumentKind.WHITESPACE){
        resolvedCmd.cmdAndArguments.add(new ResolvedArgument(arg.argumentStr));
      }
    }
    return resolvedCmd;
  }

  protected void expandVariables(UnResolvedSingleCommand unresolvedCmd) {
    for(UnResolvedArgument arg : unresolvedCmd.arguments){
      if(arg.kind!=ArgumentKind.ENVIRONMENT_VARIABLE){
        continue;
      }
      String replacementValue=sessionVariables.get(arg.argumentStr);
      arg.kind=ArgumentKind.ORDINARY;
      if(replacementValue!=null){
        arg.argumentStr=replacementValue;
      }
      else{
        arg.argumentStr="";
      }
    }
  }

  protected void mergeContiguousOrdinaryArguments(UnResolvedSingleCommand unresolvedCmd) {
    if(unresolvedCmd.arguments.size()<=1){
      return;
    }
    
    UnResolvedArgument previousArg=unresolvedCmd.arguments.get(0);
    for(int i=1; i<unresolvedCmd.arguments.size(); i++){
      UnResolvedArgument arg=unresolvedCmd.arguments.get(i);
      if(previousArg.kind==ArgumentKind.ORDINARY && arg.kind==ArgumentKind.ORDINARY){
        previousArg.argumentStr=previousArg.argumentStr+arg.argumentStr;
        unresolvedCmd.arguments.remove(i);
        i--;
      }
      else{
        previousArg=arg;
      }
    }
  }
}
