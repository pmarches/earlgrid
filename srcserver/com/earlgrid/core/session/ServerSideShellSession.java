package com.earlgrid.core.session;

import org.eclipse.swt.graphics.Point;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.earlgrid.core.execution.PipelineExecutor;
import com.earlgrid.core.serverside.EarlGridPb.PbClientClipboard;
import com.earlgrid.core.serverside.EarlGridPb.PbCommandLineUserAction.UserRequestedActionKind;
import com.earlgrid.core.serverside.EarlGridPb.PbInteractiveForm;
import com.earlgrid.core.serverside.EarlGridPb.PbTopLevel;
import com.earlgrid.core.sessionmodel.SessionModel;
import com.earlgrid.core.sessionmodel.TaskCreatedStatus;
import com.earlgrid.core.shellcommands.CommandRegistry;
import com.earlgrid.core.shellparser.CommandLineInvocation;
import com.earlgrid.core.shellparser.CommandLineResolver;
import com.earlgrid.core.shellparser.ResolvedCommandChain;
import com.earlgrid.core.shellparser.ShellCommandLineParser;
import com.earlgrid.core.shellparser.UnResolvedCommandChain;
import com.earlgrid.remoting.TopLevelResponseFuture;
import com.earlgrid.remoting.serverside.RemotingServerMain;
import com.earlgrid.remoting.serverside.ServerSideHasShutDownException;

public class ServerSideShellSession {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(ServerSideShellSession.class);
  ShellCommandLineParser commandLineParser=new ShellCommandLineParser();
  CommandLineResolver envVariableResolver;
  CommandRegistry cmdRegistry=new CommandRegistry();
  RemotingServerMain remotingServerMain;
  SessionModel serverSideSessionModel;
  
  public ServerSideShellSession(RemotingServerMain remotingServerMain) {
    this.remotingServerMain=remotingServerMain;
    serverSideSessionModel=new SessionModel(remotingServerMain);
    envVariableResolver=new CommandLineResolver(getSessionModel().sessionVariables);
  }

  public void executeTaskSync(ResolvedCommandChain commandchain) {
    log.info("executeTaskSync "+commandchain);
    PipelineExecutor executor=new PipelineExecutor(ServerSideShellSession.this, commandchain.getTaskId());
    executor.execute(commandchain);
  }

  public List<ResolvedCommandChain> parse(CommandLineInvocation userInvocation) {
    List<UnResolvedCommandChain> unresolved = commandLineParser.parseUserEditedCommand(userInvocation);
    return envVariableResolver.resolve(unresolved);
  }

  public List<ResolvedCommandChain> parse(String commandStringToParse) {
    CommandLineInvocation executeInvocation=new CommandLineInvocation(commandStringToParse);
    return parse(executeInvocation);
  }

  
  /**
   * The client should not parse the command line by itself, it needs the server to validate it. Hence, the client will
   * have to send incomplete command lines to the server on every keystroke for validation. The server will reply
   * with either a syntax highlight, help message, completion information etc.
   * @return 
   */
  public void handleCommandLineInvocation(PbTopLevel topLevelMsgRcv) {
    CommandLineInvocation userAction=CommandLineInvocation.newFromProtoBuf(topLevelMsgRcv.getUserAction());
    if(userAction.whatWasinvoked==UserRequestedActionKind.EXECUTE){
      List<UnResolvedCommandChain> parsedScript = commandLineParser.parseUserEditedCommand(userAction);
      List<ResolvedCommandChain> resolvedCmdChains = envVariableResolver.resolve(parsedScript);
      if(resolvedCmdChains.size()!=1){
        log.error("We do not support multiple tasks yet"); //TODO
      }

      ResolvedCommandChain cmdChain=resolvedCmdChains.get(0);
      cmdChain.setTaskId(getSessionModel().getNextTaskId());
      TaskCreatedStatus taskCreated=new TaskCreatedStatus(cmdChain.getTaskId(), topLevelMsgRcv.getRequestId(), cmdChain.getUserEditedCommand());
      serverSideSessionModel.onUpstreamTaskCreated(taskCreated);
      executeTaskSync(cmdChain);
    }
    else if(userAction.whatWasinvoked==UserRequestedActionKind.AUTO_COMPLETE){
      log.error(userAction.whatWasinvoked+" not implemented.");
    }
    else{
      log.error(userAction.whatWasinvoked+" not implemented.");
    }
  }

  public CommandRegistry getCommandRegistry() {
    return cmdRegistry;
  }

  public Point getScreenSizeInChar() {
    return null;
  }

  public void terminateSession() throws ServerSideHasShutDownException {
    remotingServerMain.stop();
//    throw new ServerSideHasShutDownException();
  }

  public SessionModel getSessionModel() {
    return serverSideSessionModel;
  }

  public PbInteractiveForm requestUserInteraction(PbInteractiveForm.Builder userInteractionMsg) throws InterruptedException, ExecutionException {
    PbTopLevel.Builder topLevel=PbTopLevel.newBuilder().setClientSideInteractiveForm(userInteractionMsg);
    TopLevelResponseFuture topResponse = remotingServerMain.connectionToClient.queueRequest(topLevel);
    return topResponse.get().getClientSideInteractiveForm();
  }

  public PbClientClipboard requestClientClipboard(PbClientClipboard.Builder clipboardRequest) throws InterruptedException, ExecutionException {
    PbTopLevel.Builder topLevel=PbTopLevel.newBuilder().setClientClipboard(clipboardRequest);
    TopLevelResponseFuture topResponse = remotingServerMain.connectionToClient.queueRequest(topLevel);
    return topResponse.get().getClientClipboard();
  }

}
