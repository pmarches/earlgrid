package com.earlgrid.core.shellcommands;


import com.earlgrid.core.execution.PipelineTask;
import com.earlgrid.core.session.ServerSideShellSession;
import com.earlgrid.core.sessionmodel.CmdBeginStatus;
import com.earlgrid.core.sessionmodel.CmdExitStatus;
import com.earlgrid.core.sessionmodel.SessionModelChangeEvent;
import com.earlgrid.core.sessionmodel.TabularOutput;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;

abstract public class BaseCmdSpecification<T> extends PipelineTask {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(BaseCmdSpecification.class);
  protected ServerSideShellSession session;
  protected T args;
  protected int taskId;
  volatile boolean keepReceivingUpstreamEvents=true;
  
  protected void onThisCommandExecute() throws Exception {
    if(inputQueue==null){
      return;
    }
    while(keepReceivingUpstreamEvents){
      if(Thread.interrupted()){ //TODO This might not even be needed, the .take() function should throw InterruptedException
        keepReceivingUpstreamEvents=false;
      }
      SessionModelChangeEvent inputEvent = inputQueue.take();
      if(inputEvent instanceof TabularOutputRow){
        onUpstreamCommandOutputRow((TabularOutputRow) inputEvent);
      }
      else if(inputEvent instanceof TabularOutputColumnHeader){
        onUpstreamCommandColumnHeader((TabularOutputColumnHeader) inputEvent);
      }
      else if(inputEvent instanceof CmdBeginStatus){
        onUpstreamCommandBegun((CmdBeginStatus) inputEvent);
      }
      else if(inputEvent instanceof CmdExitStatus){
        onUpstreamCommandFinished((CmdExitStatus) inputEvent);
        keepReceivingUpstreamEvents=false;
      }
    }
  }

  public void validateCmdArgumentsBeforeExecution() throws Exception {
  }


  @Override
  public void run() {
    try {
//      Thread.currentThread().setName("command Thread "+getClass().getSimpleName());
      emit(new CmdBeginStatus(taskId));
      onThisCommandExecute();
    } catch(InterruptedException e){
      log.debug("Interrupted wile command was running, evgerything is fine.");
    }
    catch (Throwable e) {
      log.fatal("Exception occured while command was running", e);
    }
    finally{
      try {
        emit(new CmdExitStatus(taskId));
      } catch (InterruptedException e) {
        log.debug("Interrupted wile command was finishing", e);
      }
    }
  }
  
  
  public void bindSession(ServerSideShellSession session) {
    this.session=session;
  }

  public void bindCmdArguments(T cmdArgument, int taskId) {
    this.args=cmdArgument;
    this.taskId=taskId;
  }
  
  public int getTaskId(){
    return taskId;
  }

  protected void emitOutput(TabularOutput recalledOut) throws InterruptedException {
    if(recalledOut!=null){
      emit(new TabularOutputColumnHeader(taskId, recalledOut.getColumnHeader()));
      for(int i=0; i<recalledOut.getRowCount(); i++){
        checkForInterruption();
        emit(new TabularOutputRow(taskId, recalledOut.getRow(i).getAllCells()));
      }
    }
  }
  
  protected void checkForInterruption() throws InterruptedException {
    if(Thread.interrupted()){
      throw new InterruptedException("Interupted in checkForInterruption()");
    }
  }

  public void stopThisCommand() {
//    throw new InterruptedException(); //Does not allow distinction between Ctrl-C and normal termination
    keepReceivingUpstreamEvents=false;
  }

  
  //These are to be overriden by subclasses
  public void onUpstreamCommandBegun(CmdBeginStatus upstreamCommandBegun) throws Exception {
    emit(upstreamCommandBegun);
  }

  public void onUpstreamCommandFinished(CmdExitStatus upstreamCommandExitStatus) throws Exception {
  }

  public void onUpstreamCommandColumnHeader(TabularOutputColumnHeader upstreamColumnHeader) throws Exception {
    emit(upstreamColumnHeader);
  }

  public void onUpstreamCommandOutputRow(TabularOutputRow upstreamRow) throws Exception {
    emit(upstreamRow);
  }

}
