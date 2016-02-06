package com.earlgrid.core.sessionmodel;

public interface SessionModelChangeObserver {
  void onUpstreamTaskCreated(TaskCreatedStatus taskCreated);
  void onUpstreamTaskFinished(TaskExitStatus exitStatus);
  void onUpstreamColumnHeader(TabularOutputColumnHeader columnHeader);
  void onUpstreamOutputRow(TabularOutputRow outputRow);
  void onChangeCurrentWorkingDirectory(ChangeCurrentWorkingDirectorySessionModelEvent changeWorkingDirectoryEvent);
  void onRemoveAllTasksFromHistory(RemoveTaskFromHistorySessionModelEvent event);
}
