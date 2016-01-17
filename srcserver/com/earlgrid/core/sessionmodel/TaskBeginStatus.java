package com.earlgrid.core.sessionmodel;


public class TaskBeginStatus extends SessionModelChangeEvent {
  public String userEditedCommandLine;
  
  public TaskBeginStatus(int taskId, String userEditedCommand) {
    super(taskId);
    this.userEditedCommandLine=userEditedCommand;
  }

  public String getUserEditedCommandLine() {
    return userEditedCommandLine;
  }

}
