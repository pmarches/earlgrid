package com.earlgrid.core.sessionmodel;


public abstract class SessionModelChangeEvent {
  protected int taskId;

  public SessionModelChangeEvent(int taskId) {
    this.taskId=taskId;
  }
  
  public int getTaskId(){
    return taskId;
  }
}
