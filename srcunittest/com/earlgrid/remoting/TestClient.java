package com.earlgrid.remoting;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.core.sessionmodel.ChangeCurrentWorkingDirectorySessionModelEvent;
import com.earlgrid.core.sessionmodel.RemoveTaskFromHistorySessionModelEvent;
import com.earlgrid.core.sessionmodel.SessionModelChangeObserver;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TaskBeginStatus;
import com.earlgrid.core.sessionmodel.TaskExitStatus;
import com.earlgrid.remoting.LoopbackRemotingClient;

public class TestClient implements SessionModelChangeObserver, AutoCloseable {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(TestClient.class);
  public LoopbackRemotingClient client;
  CompletableFuture<ExecutionHistoryRecord> taskPromise;
  
  public TestClient() throws IOException {
    client=new LoopbackRemotingClient(null);
  }
  
  public ExecutionHistoryRecord execute(String taskCommandLine) throws Exception{
    taskPromise=new CompletableFuture<>();
    client.executeCommand(taskCommandLine); //FIXME 
    /** FIXME
     * We have a race condition here, the client can trigger a command on the server-side, and the server may 
     * return a finished command very quickly before the client has a change to add a future to some sort of 
     * pending command. Once way to fix this is by introducing a clientSideCookie in when sending the command to
     * the server. Another way is to use the messageId as the key upon which we will wait for the server-side command 
     * to finish.
     */
    return taskPromise.get();
  }
  
  @Override
  public void onUpstreamTaskBegin(TaskBeginStatus commandBegun) {
  }

  @Override
  public void onUpstreamTaskFinished(TaskExitStatus exitStatus) {
    if(taskPromise==null){
      log.info("Task "+exitStatus.getTaskId()+" finished but we do not care");
      return;
    }
    ExecutionHistoryRecord taskExecutionRecord = client.getSessionModel().getHistory().get(exitStatus.getTaskId());
    taskPromise.complete(taskExecutionRecord);
  }

  @Override
  public void onUpstreamColumnHeader(TabularOutputColumnHeader columnHeader) {
  }

  @Override
  public void onUpstreamOutputRow(TabularOutputRow outputRow) {
  }

  @Override
  public void onChangeCurrentWorkingDirectory(ChangeCurrentWorkingDirectorySessionModelEvent changeWorkingDirectoryEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onRemoveAllTasksFromHistory(RemoveTaskFromHistorySessionModelEvent event) {
  }

  @Override
  public void close() throws Exception {
    client.shutdown();
  }
  
  /**
   * Helper methond when running the tests under windows
   * @param outputFile
   * @return
   */
  public static String convertToUnixPath(File outputFile) {
    String unixStylePath=outputFile.toString().replaceAll("\\\\", "/");
    return unixStylePath;
  }

}
