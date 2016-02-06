package com.earlgrid.core.sessionmodel;


public class SessionModelChangeObservableTimedBuffered implements SessionModelChangeObserver {
  private static final int BUFFER_DELAY_IN_MS = 300;
  SessionModelChangeObserver output;
  TabularOutputRow latestOutputRow=null; //FIXME Might need one per task?
  
  public SessionModelChangeObservableTimedBuffered(SessionModelChangeObserver output){
    this.output=output;
  }

  @Override
  public void onUpstreamTaskCreated(TaskCreatedStatus taskCreated) {
    output.onUpstreamTaskCreated(taskCreated);
  }

  @Override
  public void onUpstreamTaskFinished(TaskExitStatus exitStatus) {
    output.onUpstreamTaskFinished(exitStatus);
  }

  @Override
  public void onUpstreamColumnHeader(TabularOutputColumnHeader columnHeader) {
    output.onUpstreamColumnHeader(columnHeader);
  }

  @Override
  public void onUpstreamOutputRow(TabularOutputRow outputRow) {
    synchronized(this){
      if(latestOutputRow==null){
        latestOutputRow=outputRow;
        new Thread(broadcastLatestOutputRow).start();
      }
      else{
        latestOutputRow=outputRow;
      }
    }
  }

  @Override
  public void onChangeCurrentWorkingDirectory(ChangeCurrentWorkingDirectorySessionModelEvent changeWorkingDirectoryEvent) {
    output.onChangeCurrentWorkingDirectory(changeWorkingDirectoryEvent);
  }
  
  Runnable broadcastLatestOutputRow=new Runnable() {
    @Override
    public void run() {
      try {
        Thread.sleep(BUFFER_DELAY_IN_MS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      TabularOutputRow lastRowNotification;
      synchronized(SessionModelChangeObservableTimedBuffered.this){
        lastRowNotification=latestOutputRow;
        latestOutputRow=null;
      }
      output.onUpstreamOutputRow(lastRowNotification);
    }
  };

  @Override
  public void onRemoveAllTasksFromHistory(RemoveTaskFromHistorySessionModelEvent event) {
    output.onRemoveAllTasksFromHistory(event);
  }

}
