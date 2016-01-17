package com.earlgrid.core.sessionmodel;

public class RemoveTaskFromHistorySessionModelEvent extends SessionModelChangeEvent {
  public RemoveTaskFromHistorySessionModelEvent(int taskId) {
    super(taskId);
  }
}
