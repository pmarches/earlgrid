package com.earlgrid.core.session;

import com.earlgrid.core.sessionmodel.TabularOutput;

public class ExecutionHistoryRecord {
  public String userEditedCommand;
  public TabularOutput out;
  public int taskId;
  public int requestIdThatCreatedThisTask;

  public enum TaskExecutionState {RUNNING, STOPPED, SUSPENDED};
  TaskExecutionState state=TaskExecutionState.RUNNING;

  protected ExecutionHistoryRecord() {
  }

  public ExecutionHistoryRecord(int taskId, int requestIdThatCreatedThisTask, String command) {
    super();
    this.requestIdThatCreatedThisTask=requestIdThatCreatedThisTask;
    this.taskId=taskId;
    this.userEditedCommand = command;
    out=new TabularOutput(taskId);
  }

  public TabularOutput getOut() {
    return out;
  }

  
  public TaskExecutionState getState() {
    return state;
  }
  public void setState(TaskExecutionState newState) {
    state=newState;
  }

  @Override
  public String toString() {
    return String.format("TaskId=%d command=%s", taskId, userEditedCommand);
  }

  public int getRequestIdThatCreatedThisTask() {
    return requestIdThatCreatedThisTask;
  }
}
