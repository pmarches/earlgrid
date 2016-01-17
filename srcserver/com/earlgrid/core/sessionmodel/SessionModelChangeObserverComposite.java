package com.earlgrid.core.sessionmodel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SessionModelChangeObserverComposite implements SessionModelChangeObserver {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(SessionModelChangeObserverComposite.class);
  Set<SessionModelChangeObserver> observers=Collections.synchronizedSet(new HashSet<SessionModelChangeObserver>());
  
  public void addObserver(SessionModelChangeObserver observer) {
    log.debug("Adding observer "+observer);
    observers.add(observer);
  }

  public void removeObserver(SessionModelChangeObserver observer) {
    log.debug("Removing observer "+observer);
    observers.remove(observer);
  }

  @Override
  public void onUpstreamTaskBegin(TaskBeginStatus commandBegun) {
    observers.forEach(o -> o.onUpstreamTaskBegin(commandBegun));
  }

  @Override
  public void onUpstreamTaskFinished(TaskExitStatus exitStatus) {
    observers.forEach(o -> o.onUpstreamTaskFinished(exitStatus));
  }

  @Override
  public void onUpstreamColumnHeader(TabularOutputColumnHeader columnHeader) {
    observers.forEach(o -> o.onUpstreamColumnHeader(columnHeader));
  }

  @Override
  public void onUpstreamOutputRow(TabularOutputRow outputRow) {
    observers.forEach(o -> o.onUpstreamOutputRow(outputRow));
  }

  @Override
  public void onChangeCurrentWorkingDirectory(ChangeCurrentWorkingDirectorySessionModelEvent changeWorkingDirectoryEvent) {
    observers.forEach(o -> o.onChangeCurrentWorkingDirectory(changeWorkingDirectoryEvent));
  }

  @Override
  public void onRemoveAllTasksFromHistory(RemoveTaskFromHistorySessionModelEvent event) {
    observers.forEach(o -> o.onRemoveAllTasksFromHistory(event));
  }

}
