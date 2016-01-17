package com.earlgrid.core.execution;

import java.util.concurrent.BlockingQueue;

import com.earlgrid.core.sessionmodel.SessionModelChangeEvent;

public abstract class PipelineTask implements Runnable {

  protected BlockingQueue<SessionModelChangeEvent> inputQueue;
  protected BlockingQueue<SessionModelChangeEvent> outputQueue;
  
  public void bindIOQueue(BlockingQueue<SessionModelChangeEvent> inputQueue, BlockingQueue<SessionModelChangeEvent> outputQueue) {
    this.inputQueue=inputQueue;
    this.outputQueue=outputQueue;
  }

  public void emit(SessionModelChangeEvent content) throws InterruptedException {
    if(content==null){
      throw new NullPointerException();
    }
    
    if(outputQueue!=null){
      outputQueue.put(content);
    }
  }
}
