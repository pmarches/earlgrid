package com.earlgrid.core.sessionmodel;

public class CmdBeginStatus extends SessionModelChangeEvent {
  public CmdBeginStatus(int taskId) {
    super(taskId);
  }
}
