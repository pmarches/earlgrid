package com.earlgrid.core.shellcommands;

import java.util.Hashtable;

import com.earlgrid.core.session.ServerSideShellSession;
import com.earlgrid.core.shellcommands.coreutils.CSVCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.CdCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.ClearCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.ClipboardCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.CutCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.DelayCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.ExitCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.ExternalProcessCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.GrepCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.HistoryCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.LsCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.PerrorCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.PromptCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.PwdCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.ReadCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.SeqCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.SessionCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.SortCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.TabCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.WcCmdArguments;
import com.earlgrid.core.shellcommands.coreutils.WriteCmdArguments;
import com.earlgrid.core.shellcommands.tjoin.TJoinCmdArguments;
import com.earlgrid.core.shellparser.ResolvedSingleCommand;

public class CommandRegistry {
  Hashtable<String, Class<? extends BaseCmdArguments<? extends BaseCmdArguments>>> commandToSpecification=new Hashtable<>();
  
  public CommandRegistry() {
    //discoverCommandsWithAnnotation();
    //readCommandsFromConfigurationFile();
    loadBuiltInCommands();
  }

  private void loadBuiltInCommands() {
    commandToSpecification.put("cd", CdCmdArguments.class);
    commandToSpecification.put("cut", CutCmdArguments.class); //Replaced by Tjoin?
    commandToSpecification.put("clear", ClearCmdArguments.class);
    commandToSpecification.put("clipboard", ClipboardCmdArguments.class);
    commandToSpecification.put("csv", CSVCmdArguments.class);
//    commandToSpecification.put("system", ExternalProcessCmdSpecification.class);
    commandToSpecification.put("grep", GrepCmdArguments.class);
    commandToSpecification.put("delay", DelayCmdArguments.class);
    commandToSpecification.put("exit", ExitCmdArguments.class);
    commandToSpecification.put("history", HistoryCmdArguments.class);
    commandToSpecification.put("tjoin", TJoinCmdArguments.class);
    commandToSpecification.put("ls", LsCmdArguments.class);
    commandToSpecification.put("mock", MockCmdArguments.class); //FIXME This one should be in a a installable package
    commandToSpecification.put("perror", PerrorCmdArguments.class);
    commandToSpecification.put("prompt", PromptCmdArguments.class);
    commandToSpecification.put("pwd", PwdCmdArguments.class);
    commandToSpecification.put("read", ReadCmdArguments.class);
    commandToSpecification.put("seq", SeqCmdArguments.class);
    commandToSpecification.put("session", SessionCmdArguments.class);
    commandToSpecification.put("sort", SortCmdArguments.class);
    commandToSpecification.put("tab", TabCmdArguments.class);
    commandToSpecification.put("wc", WcCmdArguments.class);
    commandToSpecification.put("write", WriteCmdArguments.class);
//    commandToSpecification.put("xargs", XargsCmdSpecification.class);
  }
  
  public BaseCmdSpecification resolveCommand(ServerSideShellSession session, ResolvedSingleCommand resolvedSingleCommand, int taskId) throws Exception {
    String commandName=resolvedSingleCommand.getCommandName();
    Class<? extends BaseCmdArguments> cmdClass=commandToSpecification.get(commandName);
    if(cmdClass==null){
      cmdClass=ExternalProcessCmdArguments.class;
//      throw new Exception("Command '"+commandName+"' was not found");
    }
    BaseCmdArguments cmdArgument=cmdClass.newInstance();
    cmdArgument.parseArguments(resolvedSingleCommand.getCommandName(), resolvedSingleCommand.getArgumentsArray());
    
    BaseCmdSpecification cmd=cmdArgument.newCmdSpecification();
    cmd.bindSession(session);
    cmd.bindCmdArguments(cmdArgument, taskId);
    return cmd;
  }
}
