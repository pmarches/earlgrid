package com.earlgrid.core.sessionmodel;

public class CmdExitStatus extends SessionModelChangeEvent {
  public CmdExitStatus(int taskId) {
    super(taskId);
  }
}
