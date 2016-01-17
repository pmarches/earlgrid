package com.earlgrid.remoting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutionException;

import com.earlgrid.core.serverside.EarlGridPb.PbCommandLineUserAction;
import com.earlgrid.core.serverside.EarlGridPb.PbExecutableCode;
import com.earlgrid.core.serverside.EarlGridPb.PbFileSystemManagement;
import com.earlgrid.core.serverside.EarlGridPb.PbPathSpecification;
import com.earlgrid.core.serverside.EarlGridPb.PbRemoting;
import com.earlgrid.core.serverside.EarlGridPb.PbSessionModelChange;
import com.earlgrid.core.serverside.EarlGridPb.PbTabularColumnHeaders;
import com.earlgrid.core.serverside.EarlGridPb.PbTabularRow;
import com.earlgrid.core.serverside.EarlGridPb.PbTopLevel;
import com.earlgrid.core.serverside.EarlGridPb.PbFileSystemManagement.PbOperation;
import com.earlgrid.core.serverside.EarlGridPb.PbSessionModelChange.PbTaskState;
import com.earlgrid.core.sessionmodel.ChangeCurrentWorkingDirectorySessionModelEvent;
import com.earlgrid.core.sessionmodel.RemoveTaskFromHistorySessionModelEvent;
import com.earlgrid.core.sessionmodel.SessionModel;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TaskBeginStatus;
import com.earlgrid.core.sessionmodel.TaskExitStatus;
import com.earlgrid.remoting.serverside.FileSelectionPredicate;
import com.earlgrid.remoting.serverside.IOConnection;
import com.earlgrid.remoting.serverside.IOConnectionMessageHandler;
import com.earlgrid.remoting.serverside.IOEndPoint;
import com.google.protobuf.ByteString;

public abstract class RemotingClient implements IOConnectionMessageHandler {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(RemotingClient.class);
  protected IOConnection ioConnection;
  protected SessionModel clientSideSessionModel;
  private InteractiveFormHandler interactiveFormHandler;

  protected RemotingClient(InteractiveFormHandler interactiveFormHandler){
    this.interactiveFormHandler=interactiveFormHandler;
  }
  
  public void connectWith(IOEndPoint ioEndPoint) {
    clientSideSessionModel=new SessionModel(null);
    ioConnection=new IOConnection(ioEndPoint, "ClientIOAccumulator", this);
  }
  
  @Override
  public void onNotificationOrRequestReceived(PbTopLevel topLevelMsg) {
    if(topLevelMsg.hasSessionModelChange()){
      onReceivedSessionModelChange(topLevelMsg);
    }
    if(topLevelMsg.hasClientSideInteractiveForm()){
      interactiveFormHandler.handleClientSideInteractiveForm(topLevelMsg);
    }
    if(topLevelMsg.hasClientClipboard()){
      interactiveFormHandler.handleClipboard(topLevelMsg);
    }
  }

  @Override
  public void onIOExceptionOccured(Exception e) {
    log.error("onIOExceptionOccured", e);
  }

  public void executeCommand(String commandStringToExecute) throws Exception {
    PbCommandLineUserAction.Builder request=PbCommandLineUserAction.newBuilder();
    request.setCommandLine(commandStringToExecute);
    PbTopLevel.Builder executeRequest=PbTopLevel.newBuilder().setUserAction(request);
    sendRequestMessage(executeRequest);
  }

  protected TopLevelResponseFuture sendRequestMessage(PbTopLevel.Builder requestMessage) throws IOException{
    return ioConnection.queueRequest(requestMessage);
  }
  
//  public TopLevelResponseFuture sendFile(Path localFileToSend) throws FileNotFoundException{
//    FileInputStream fileStream=new FileInputStream(localFileToSend.toFile());
//    StreamingDataTransferRequest streamingRequest=new StreamingDataTransferRequest(fileStream);
//    return ioAccumulator.queueRequest(streamingRequest);
//  }

  public void executeClass(String classToExectute) throws IOException {
    PbRemoting.Builder remotingMsg=PbRemoting.newBuilder();
    remotingMsg.setLoadTestClass(classToExectute);
    PbTopLevel.Builder request=PbTopLevel.newBuilder().setRemoting(remotingMsg);
    ioConnection.queueRequest(request);
  }

  public boolean ping() throws IOException, InterruptedException, ExecutionException {
    PbRemoting.Builder request=PbRemoting.newBuilder();
    request.setPing(true);
    PbTopLevel.Builder pingRequest = PbTopLevel.newBuilder().setRemoting(request);
    TopLevelResponseFuture response=sendRequestMessage(pingRequest);
    if(response.get().hasRemoting()==false){
      return false;
    }

    PbRemoting remotingMsg = response.get().getRemoting();
    return remotingMsg.hasPong()&& remotingMsg.getPong();
  }

  public void shutdownRemoteEnd() throws IOException {
    PbRemoting.Builder shutdownMessage=PbRemoting.newBuilder();
    shutdownMessage.setShutdown(true);
    PbTopLevel.Builder shutdownRequest=PbTopLevel.newBuilder().setRemoting(shutdownMessage);
    ioConnection.queueRequest(shutdownRequest);
  }

  public void shutdownLocalEnd() {
    ioConnection.stopAndWaitForAllRequestsToComplete();
  }

  public PbTopLevel.Builder createListDirectoryMessage(String directoryToList) {
    PbFileSystemManagement.Builder listFileRequest=PbFileSystemManagement.newBuilder();
    listFileRequest.setOperation(PbOperation.LIST);
    listFileRequest.setPath(convertStringPathToPathSpecification(directoryToList));
    return PbTopLevel.newBuilder().setFileSystemManagement(listFileRequest);
  }

  private PbPathSpecification.Builder convertStringPathToPathSpecification(String directoryToList) {
    PbPathSpecification.Builder pathSpec=PbPathSpecification.newBuilder();
    for(String pathElement : directoryToList.split("/")){
      pathSpec.addPathElement(pathElement);
    }
    return pathSpec;
  }
  
  public PbTopLevel.Builder createFileSystemFindMessage(FileSelectionPredicate filePredicate, String startingPath) throws Exception {
    ByteArrayOutputStream baos=new ByteArrayOutputStream();
    ObjectOutputStream oos=new ObjectOutputStream(baos);
    oos.writeObject(filePredicate);
    oos.close();
    ByteString predicateBytes=ByteString.copyFrom(baos.toByteArray());
    PbExecutableCode.Builder predicateCode=PbExecutableCode.newBuilder();
    predicateCode.setExecutableBytes(predicateBytes);

    PbFileSystemManagement.Builder fsMgtReq=PbFileSystemManagement.newBuilder();
    fsMgtReq.setOperation(PbOperation.FIND);
    fsMgtReq.setFindPredicate(predicateCode);
    fsMgtReq.setPath(convertStringPathToPathSpecification(startingPath));
    return PbTopLevel.newBuilder().setFileSystemManagement(fsMgtReq);
  }

  @Deprecated
  public String getCurrentWorkingDirectory() throws Exception {
    PbFileSystemManagement.Builder cwdFReq=PbFileSystemManagement.newBuilder();
    cwdFReq.setOperation(PbOperation.CWD);

    PbTopLevel.Builder cwdRequest = PbTopLevel.newBuilder().setFileSystemManagement(cwdFReq); 
    TopLevelResponseFuture response=sendRequestMessage(cwdRequest);

    if(response.get().hasFileSystemManagement()==false){
      throw new IOException("Did not receive fileSystemManagement message");
    }

    PbFileSystemManagement fsMsgtResponse = response.get().getFileSystemManagement();
    if(fsMsgtResponse.hasPath()==false){
      throw new IOException("fsMgt message did not contain path");
    }
    
    return String.join("/", fsMsgtResponse.getPath().getPathElementList());
  }

  private void onReceivedSessionModelChange(PbTopLevel topLevelMsg) {
    try {
      PbSessionModelChange modelChange = topLevelMsg.getSessionModelChange();
      if(modelChange.hasTaskId()==false){
        log.warn("Discarding "+PbSessionModelChange.class.getSimpleName()+" message because it does not have a taskId "+modelChange);
        return;
      }
      int taskId=modelChange.getTaskId();
      if(modelChange.hasNewWorkingDirectory()){
        clientSideSessionModel.onChangeCurrentWorkingDirectory(ChangeCurrentWorkingDirectorySessionModelEvent.fromProtoBuf(taskId, modelChange.getNewWorkingDirectory()));
      }

      if(modelChange.hasTaskState()){
        PbTaskState taskStateChange = modelChange.getTaskState();
        if(taskStateChange.equals(PbTaskState.RUNNING)){
          clientSideSessionModel.onUpstreamTaskBegin(new TaskBeginStatus(taskId, modelChange.getCommandString()));
        }
        else if(taskStateChange.equals(PbTaskState.FINISHED)){
          clientSideSessionModel.onUpstreamTaskFinished(new TaskExitStatus(taskId));
        }
      }
      
      if(modelChange.hasHeader()){
        PbTabularColumnHeaders headerChange = modelChange.getHeader();
        clientSideSessionModel.onUpstreamColumnHeader(TabularOutputColumnHeader.fromProtoBuf(taskId, headerChange));
      }

      for(int i=0; i<modelChange.getRowCount(); i++){
        PbTabularRow rowChange = modelChange.getRow(i);
        clientSideSessionModel.onUpstreamOutputRow(TabularOutputRow.fromProtoBuf(taskId, rowChange));
      }
      
      if(modelChange.hasRemoveTask()){
        clientSideSessionModel.onRemoveAllTasksFromHistory(new RemoveTaskFromHistorySessionModelEvent(taskId));
      }
    } catch (Exception e) {
      log.error("Exception after receiving message ", e);
    }
  }

  public SessionModel getSessionModel() {
    return clientSideSessionModel;
  }

  public abstract String getName();
}
