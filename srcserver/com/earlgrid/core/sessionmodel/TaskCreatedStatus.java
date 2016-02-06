package com.earlgrid.core.sessionmodel;

public class TaskCreatedStatus {
  protected int taskId;
  protected int requestIdThatCreatedThisTask;
  protected String userEditedCommandLine;
  
  public TaskCreatedStatus(int taskId, int requestIdThatCreatedThisTask, String userEditedCommand){
    this.taskId=taskId;
    this.requestIdThatCreatedThisTask=requestIdThatCreatedThisTask;
    this.userEditedCommandLine=userEditedCommand;
  }

  public String getUserEditedCommandLine() {
    return userEditedCommandLine;
  }

  public int getTaskId() {
    return taskId;
  }
  
  public int getRequestIdThatCreatedThisTask(){
    return requestIdThatCreatedThisTask;
  }
}
