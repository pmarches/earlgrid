package com.earlgrid.core.sessionmodel;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import com.earlgrid.core.session.ExecutionHistory;
import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.core.session.SessionEnvironmentVariables;

//TODO Should keep the server-side model info in a server-side version of the session model. (taskIdCounter)
public class SessionModel implements SessionModelChangeObserver {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(SessionModel.class);
  public Path currentWorkingDirectory;
  protected ExecutionHistory history=new ExecutionHistory();
  public SessionEnvironmentVariables sessionVariables=new SessionEnvironmentVariables();
  private AtomicInteger taskIdCounter=new AtomicInteger();
  SessionModelChangeObserverComposite composite; 
  SessionModelChangeObserver directObserver;
  
  public SessionModel(SessionModelChangeObserver observer) {
    try {
      if(observer!=null){
        directObserver=observer;
        composite=null; //To ensure any addDelayedObserver() method fails 
      }
      else{
        composite=new SessionModelChangeObserverComposite();
        directObserver=new SessionModelChangeObservableTimedBuffered(composite);
      }

      onChangeCurrentWorkingDirectory(new ChangeCurrentWorkingDirectorySessionModelEvent(0, new File(System.getProperty("user.home")).toPath()));
    } catch (Exception e) {
      log.error("SessionModel ctor", e);
    }
  }
  
  public void addDelayedObserver(SessionModelChangeObserver observer) {
    composite.addObserver(observer);
  }
  public void removeDelayedObserver(SessionModelChangeObserver observer) {
    composite.removeObserver(observer);
  }
  
  
  public Path getCurrentWorkingDirectory() {
    return currentWorkingDirectory;
  }

  public ExecutionHistory getHistory() {
    return history;
  }

  public int getNextTaskId() {
    //This is called only when the sessionModel is instanciated on the server-side
    return taskIdCounter.getAndIncrement();
  }

  @Override
  public void onChangeCurrentWorkingDirectory(ChangeCurrentWorkingDirectorySessionModelEvent changeWorkingDirectoryEvent) {
    currentWorkingDirectory=changeWorkingDirectoryEvent.newWorkingDirectory;
    directObserver.onChangeCurrentWorkingDirectory(changeWorkingDirectoryEvent);
  }

  @Override
  public void onRemoveAllTasksFromHistory(RemoveTaskFromHistorySessionModelEvent event) {
    history.removeAll();
    directObserver.onRemoveAllTasksFromHistory(event);
  }

  @Override
  public void onUpstreamColumnHeader(TabularOutputColumnHeader columnHeader) {
    ExecutionHistoryRecord taskRecord=history.get(columnHeader.getTaskId());
    taskRecord.out.columnHeader.setColumnHeaders(columnHeader.columns);
    directObserver.onUpstreamColumnHeader(columnHeader);
  }

  @Override
  public void onUpstreamTaskBegin(TaskBeginStatus commandBegun) {
    history.appendHistoryRecord(new ExecutionHistoryRecord(commandBegun.getTaskId(), commandBegun.getUserEditedCommandLine()));
    directObserver.onUpstreamTaskBegin(commandBegun);
  }

  @Override
  public void onUpstreamTaskFinished(TaskExitStatus exitStatus) {
    ExecutionHistoryRecord taskRecord=history.get(exitStatus.getTaskId());
    if(taskRecord!=null){ //The clear command removed all tasks from the history, including itself. Hence why the taskRecord can be null.
      taskRecord.setState(ExecutionHistoryRecord.TaskExecutionState.STOPPED);
    }
    directObserver.onUpstreamTaskFinished(exitStatus);
  }

  @Override
  public void onUpstreamOutputRow(TabularOutputRow taskOutputRow) {
    ExecutionHistoryRecord taskRecord=history.get(taskOutputRow.getTaskId());
    taskRecord.out.rows.add(taskOutputRow);
    directObserver.onUpstreamOutputRow(taskOutputRow);
  }

  /**
   * Should be called only for testing purposes
   * @param newExecutionRecord
   * @throws Exception
   */
  public void appendHistoryRecord(ExecutionHistoryRecord newExecutionRecord) throws Exception {
    if(getNextTaskId()!=newExecutionRecord.taskId){
      throw new Exception("Mock tasks need to be created in the proper order. You tried adding the mock task with id="+newExecutionRecord.taskId);
    }
    
    onUpstreamTaskBegin(new TaskBeginStatus(newExecutionRecord.taskId, newExecutionRecord.userEditedCommand));
    onUpstreamColumnHeader(newExecutionRecord.out.columnHeader);
    for(TabularOutputRow row : newExecutionRecord.out.rows){
      onUpstreamOutputRow(row);
    }
    onUpstreamTaskFinished(new TaskExitStatus(newExecutionRecord.taskId));
  }

}
