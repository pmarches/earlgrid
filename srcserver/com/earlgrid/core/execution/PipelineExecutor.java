package com.earlgrid.core.execution;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.earlgrid.core.session.ServerSideShellSession;
import com.earlgrid.core.sessionmodel.SessionModelChangeEvent;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TaskExitStatus;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;
import com.earlgrid.core.shellparser.ResolvedCommandChain;

public class PipelineExecutor implements ThreadFactory {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(PipelineExecutor.class);
  ThreadGroup threadGroup;
  ServerSideShellSession session;
  int taskId;

  public PipelineExecutor(ServerSideShellSession session, int taskId) {
    this.session=session;
    this.taskId=taskId;
    threadGroup=new ThreadGroup("ThreadGroup for taskId="+taskId);
  }
  
  public void execute(ResolvedCommandChain cmdChain) {
    BaseCmdSpecification<?>[] pipelineTasks;
    BlockingQueue<SessionModelChangeEvent> inputQueue=null;
    try {
      pipelineTasks = new BaseCmdSpecification[cmdChain.pipedCommands.size()];
      for(int i=0; i<pipelineTasks.length; i++){
        BlockingQueue<SessionModelChangeEvent> outputQueue=new ArrayBlockingQueue<>(1);
        BaseCmdSpecification<?> task = session.getCommandRegistry().resolveCommand(session, cmdChain.pipedCommands.get(i), taskId);
        pipelineTasks[i]=task;
        task.bindIOQueue(inputQueue, outputQueue);
        task.validateCmdArgumentsBeforeExecution();
        inputQueue=outputQueue;
      }
    } catch (Exception e) {
      log.error("Exception occured before executing '"+cmdChain+"'", e);
      try {
        session.getSessionModel().onUpstreamOutputRow(new TabularOutputRow(taskId, e.getMessage())); //FIXME Send this to error output
        session.getSessionModel().onUpstreamTaskFinished(new TaskExitStatus(taskId));
      } catch (Exception e1) {
        log.error("Error occured while trying to handle error before execution", e1);
      }
      return;
    }
    
    ExecutorService threadPool = Executors.newFixedThreadPool(cmdChain.numberOfCommands(), r->new Thread(threadGroup, r));
    for(int i=0; i<pipelineTasks.length; i++){
      threadPool.submit(pipelineTasks[i]);
    }
    threadPool.shutdown();

    ForwardSessionModelChanges forwardChangesToSessionModel=new ForwardSessionModelChanges(session.getSessionModel(), taskId);
    forwardChangesToSessionModel.bindIOQueue(inputQueue, null);
    forwardChangesToSessionModel.run();
    
    for(Runnable runaway: threadPool.shutdownNow()){
      log.warn("Runaway "+runaway);
    }
    threadGroup.destroy();
  }

  @Override
  public Thread newThread(Runnable arg0) {
    Thread t=new Thread(threadGroup, arg0);
    t.setName(PipelineExecutor.class.getName()+" executor");
    return t;
  }
}

