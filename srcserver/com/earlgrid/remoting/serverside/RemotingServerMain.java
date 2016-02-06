package com.earlgrid.remoting.serverside;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

import com.earlgrid.core.serverside.EarlGridPb.PbExceptionSpecification;
import com.earlgrid.core.serverside.EarlGridPb.PbExecutableCode;
import com.earlgrid.core.serverside.EarlGridPb.PbFileSystemManagement;
import com.earlgrid.core.serverside.EarlGridPb.PbFileSystemManagement.PbOperation;
import com.earlgrid.core.serverside.EarlGridPb.PbFileSystemObjectSpecification;
import com.earlgrid.core.serverside.EarlGridPb.PbInteractiveForm;
import com.earlgrid.core.serverside.EarlGridPb.PbPathSpecification;
import com.earlgrid.core.serverside.EarlGridPb.PbRemoting;
import com.earlgrid.core.serverside.EarlGridPb.PbRemoveTaskFromHistory;
import com.earlgrid.core.serverside.EarlGridPb.PbSessionModelChange;
import com.earlgrid.core.serverside.EarlGridPb.PbSessionModelChange.PbTaskState;
import com.earlgrid.core.serverside.EarlGridPb.PbTopLevel;
import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.core.session.ServerSideShellSession;
import com.earlgrid.core.sessionmodel.ChangeCurrentWorkingDirectorySessionModelEvent;
import com.earlgrid.core.sessionmodel.RemoveTaskFromHistorySessionModelEvent;
import com.earlgrid.core.sessionmodel.SessionModelChangeObserver;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TaskCreatedStatus;
import com.earlgrid.core.sessionmodel.TaskExitStatus;

public class RemotingServerMain implements IOConnectionMessageHandler, SessionModelChangeObserver {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(RemotingServerMain.class);
  private RemotingClassLoader classLoader;
  private ServerSideShellSession session;
  public IOConnection connectionToClient;

  public RemotingServerMain(IOEndPoint controllingEndPoint) {
    connectionToClient=new IOConnection(controllingEndPoint, "ServerSide", this);
    session=new ServerSideShellSession(this);
    classLoader=new RemotingClassLoader(controllingEndPoint);
  }
  
  @Override
  public void onNotificationOrRequestReceived(PbTopLevel topLevelMsgRcv) {
    try {
      if(topLevelMsgRcv.hasRemoting()){
        handleRemotingMessage(topLevelMsgRcv);
      }
      if(topLevelMsgRcv.hasUserAction()){
        session.handleCommandLineInvocation(topLevelMsgRcv);
      }
      if(topLevelMsgRcv.hasFileSystemManagement()){
        handleFilesystemMessage(topLevelMsgRcv);
      }
      if(topLevelMsgRcv.hasClientSideInteractiveForm()){
        handleClientSideUserInteraction(topLevelMsgRcv);
      }
    } catch (Exception e) {
      if(topLevelMsgRcv.getMessageType()==PbTopLevel.MessageType.REQUEST){
        PbTopLevel.Builder topMsgResponse = connectionToClient.createResponseForRequest(topLevelMsgRcv);
        topMsgResponse.setExceptionOccured(convertExceptionToExceptionSpecification(e));
        connectionToClient.queueResponse(topMsgResponse);
      }
    }
  }

  @Override
  public void onIOExceptionOccured(Exception e) {
    log.error("onIOExceptionOccured", e);
  }
  
 
  private PbExceptionSpecification.Builder convertExceptionToExceptionSpecification(Exception e) {
    e.printStackTrace();
    PbExceptionSpecification.Builder exception=PbExceptionSpecification.newBuilder();
    exception.setExceptionClass(e.getClass().getName());
    if(e.getMessage()!=null){
      exception.setMessage(e.getMessage());
    }
    exception.setStackTrace(Arrays.asList(e.getStackTrace()).toString());
    return exception;
  }

  private void handleClientSideUserInteraction(PbTopLevel topMsgRequest) {
    PbInteractiveForm clientSideInteractiveForm = topMsgRequest.getClientSideInteractiveForm();
    log.warn("The server received a user interaction request. This should normaly go to the client");
  }

  private void handleFilesystemMessage(PbTopLevel topMsgRequest) throws Exception {
    PbFileSystemManagement fsMgtReq=topMsgRequest.getFileSystemManagement();
    PbOperation operationRequested = fsMgtReq.getOperation();
    if(PbOperation.LIST==operationRequested){
      handleListFile(topMsgRequest, fsMgtReq.getPath());
    }
    else if(PbOperation.FIND==operationRequested){
      handleFindFile(topMsgRequest, fsMgtReq);
    }
    else if(PbOperation.CWD==operationRequested){
      handleGetCurrentWorkingDirectory(topMsgRequest, fsMgtReq);
    }
  }

  private void handleGetCurrentWorkingDirectory(PbTopLevel topMsgRequest, PbFileSystemManagement fsMgtReq) throws Exception {
    final PbPathSpecification.Builder pathSpecification=PbPathSpecification.newBuilder();
    for(Path pathElement : session.getSessionModel().getCurrentWorkingDirectory()){
      pathSpecification.addPathElement(pathElement.toString());
    }
    final PbFileSystemManagement.Builder fsMgtResponse=PbFileSystemManagement.newBuilder();
    fsMgtResponse.setPath(pathSpecification);

    PbTopLevel.Builder topMsgResponse = connectionToClient.createResponseForRequest(topMsgRequest);
    topMsgResponse.setFileSystemManagement(fsMgtResponse);
    connectionToClient.queueResponse(topMsgResponse);
  }

  private void handleFindFile(PbTopLevel topMsgRequest, PbFileSystemManagement findReq) throws Exception {
    final FileSelectionPredicate fileSelectionPredicate = convertExecutableBytesToInstance(findReq.getFindPredicate());
    final Path startDirectory=convertPathSpecificationToPath(findReq.getPath());

    final PbFileSystemManagement.Builder fsMgtResponse=PbFileSystemManagement.newBuilder();
    SimpleFileVisitor<Path> visitorBackedBySelectionPredicate=new SimpleFileVisitor<Path>(){
      @Override
      public FileVisitResult visitFile(Path pathOfFile, BasicFileAttributes attributesOfFile) throws IOException {
        if(fileSelectionPredicate.includeInResult(pathOfFile, attributesOfFile)){
          PbFileSystemObjectSpecification.Builder fileSpec=PbFileSystemObjectSpecification.newBuilder();
          fileSpec.setPath(convertFileToPathSpecification(pathOfFile));
          fileSpec.setSizeInBytes(attributesOfFile.size());
          fsMgtResponse.addFileListing(fileSpec);
        }
        
        if(attributesOfFile.isDirectory()){
          if(fileSelectionPredicate.visitChildren(pathOfFile, attributesOfFile)){
            return FileVisitResult.CONTINUE;
          }
          return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
      }
      
      @Override
      public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        log.error("Failed to visit "+file+" because "+exc);
        return FileVisitResult.SKIP_SUBTREE;
      }
    };
    Files.walkFileTree(startDirectory, visitorBackedBySelectionPredicate);

    //TODO We should send the responses from within the FileVisitor
    PbTopLevel.Builder topMsgResponse = connectionToClient.createResponseForRequest(topMsgRequest);
    topMsgResponse.setFileSystemManagement(fsMgtResponse);
    connectionToClient.queueResponse(topMsgResponse);
  }

  private FileSelectionPredicate convertExecutableBytesToInstance(PbExecutableCode findPredicate) throws IOException, ClassNotFoundException {
    ByteArrayInputStream bais=new ByteArrayInputStream(findPredicate.getExecutableBytes().toByteArray());
    ObjectInputStream ois=new ObjectInputStream(bais);
    final FileSelectionPredicate fileSelectionPredicate=(FileSelectionPredicate) ois.readObject();
    ois.close();
    return fileSelectionPredicate;
  }

  private void handleListFile(PbTopLevel topMsgRequest, PbPathSpecification pathToList) throws Exception {
    Path absolutePathOfDirectoryToList=convertPathSpecificationToPath(pathToList);
    File directoryToList=absolutePathOfDirectoryToList.toFile();
    if(directoryToList.exists()==false){
      throw new FileNotFoundException(directoryToList.getAbsolutePath());
    }
    if(directoryToList.isDirectory()==false){
      throw new Exception(directoryToList.getAbsolutePath()+" is not a directory");
    }
    PbFileSystemManagement.Builder fsMgtResponse=PbFileSystemManagement.newBuilder();
    for(File childFSObject :directoryToList.listFiles()){
      Path childPathRelativeToBaseDirectory=absolutePathOfDirectoryToList.relativize(childFSObject.toPath());

      PbFileSystemObjectSpecification.Builder childSpec=PbFileSystemObjectSpecification.newBuilder();
      childSpec.setPath(convertFileToPathSpecification(childPathRelativeToBaseDirectory));
      childSpec.setSizeInBytes(childFSObject.length());
      fsMgtResponse.addFileListing(childSpec);
    }
    PbTopLevel.Builder topMsgResponse = connectionToClient.createResponseForRequest(topMsgRequest);
    topMsgResponse.setFileSystemManagement(fsMgtResponse);
    connectionToClient.queueResponse(topMsgResponse);
  }

  private void handleRemotingMessage(PbTopLevel topMsgRequest) throws Exception {
    PbRemoting remotingRequestMsg=topMsgRequest.getRemoting();
    if(remotingRequestMsg.hasLoadTestClass()){
      String classToLoad=remotingRequestMsg.getLoadTestClass();
      Class<?> clazz = classLoader.loadClass(classToLoad);
    }
    if(remotingRequestMsg.hasShutdown() && remotingRequestMsg.getShutdown()){
      stop();
    }
    if(remotingRequestMsg.hasPing() && remotingRequestMsg.getPing()){
      PbTopLevel.Builder topMsgResponse = connectionToClient.createResponseForRequest(topMsgRequest);
      PbRemoting.Builder remotingResponse = PbRemoting.newBuilder().setPong(true);
      topMsgResponse.setRemoting(remotingResponse);
      connectionToClient.queueResponse(topMsgResponse);
    }
  }


  private PbPathSpecification.Builder convertFileToPathSpecification(Path pathToConvert) {
    PbPathSpecification.Builder newPathSpec=PbPathSpecification.newBuilder();
    for(Path pathElement : pathToConvert){
      newPathSpec.addPathElement(pathElement.toString());
    }
    return newPathSpec;
  }

  private Path convertPathSpecificationToPath(PbPathSpecification pathToList) throws Exception {
    if(pathToList.getPathElementCount()==0){
      throw new Exception("Missing path specification");
    }
    FileSystem rootFS = FileSystems.getFileSystem(URI.create("file:///"));
    return rootFS.getPath(File.separator+String.join(File.separator, pathToList.getPathElementList()));
  }

  public static void main(String[] args) {
    try {
      IOEndPoint controllingEndPoint=new IOEndPoint(System.out, System.in, null);
      System.setOut(new PrintStream("/dev/null"));
      System.setIn(new FileInputStream("/dev/null"));
      System.setErr(new PrintStream("/dev/null"));

      new RemotingServerMain(controllingEndPoint);
    } catch (Exception e) {
      //TODO how to report this to the client ?
      e.printStackTrace();
    }
  }

  public void stop() {
    connectionToClient.stopAndWaitForAllRequestsToComplete();
  }

  public ServerSideShellSession getSession() {
    return session;
  }

  @Override
  public void onUpstreamTaskCreated(TaskCreatedStatus taskCreatedStatus) {
    PbSessionModelChange.Builder sessionModelChangeMsg=PbSessionModelChange.newBuilder();
    sessionModelChangeMsg.setTaskId(taskCreatedStatus.getTaskId());
    sessionModelChangeMsg.setCommandString(taskCreatedStatus.getUserEditedCommandLine());
    sessionModelChangeMsg.setTaskState(PbTaskState.RUNNING);
    PbTopLevel.Builder taskCreatedResponse=PbTopLevel.newBuilder().setSessionModelChange(sessionModelChangeMsg);
    connectionToClient.queueNotification(taskCreatedResponse);
  }

  @Override
  public void onUpstreamTaskFinished(TaskExitStatus taskExitStatus) {
    PbSessionModelChange.Builder sessionModelChange=PbSessionModelChange.newBuilder();
    sessionModelChange.setTaskId(taskExitStatus.getTaskId());
    sessionModelChange.setTaskState(PbTaskState.FINISHED);

    ExecutionHistoryRecord taskHistory = session.getSessionModel().getHistory().get(taskExitStatus.getTaskId());
    int messageIdOfRequest=taskHistory.getRequestIdThatCreatedThisTask();

    PbTopLevel.Builder topLevelMsg=connectionToClient.createResponseForRequest(messageIdOfRequest);
    topLevelMsg.setSessionModelChange(sessionModelChange);
    topLevelMsg.setRequestHasCompleted(true);
    connectionToClient.queueNotification(topLevelMsg);
  }

  @Override
  public void onUpstreamColumnHeader(TabularOutputColumnHeader columnHeader) {
    PbSessionModelChange.Builder sessionModelChange=PbSessionModelChange.newBuilder();
    sessionModelChange.setTaskId(columnHeader.getTaskId());
    sessionModelChange.setHeader(columnHeader.toProtoBuf());
    PbTopLevel.Builder topLevelMsg=PbTopLevel.newBuilder().setSessionModelChange(sessionModelChange);
    connectionToClient.queueNotification(topLevelMsg);
  }

  @Override
  public void onUpstreamOutputRow(TabularOutputRow newRow) {
    PbSessionModelChange.Builder sessionModelChange=PbSessionModelChange.newBuilder();
    sessionModelChange.setTaskId(newRow.getTaskId());
    sessionModelChange.addRow(newRow.toProtoBuf());
    PbTopLevel.Builder topLevelMsg=PbTopLevel.newBuilder().setSessionModelChange(sessionModelChange);
    connectionToClient.queueNotification(topLevelMsg);
  }

  @Override
  public void onChangeCurrentWorkingDirectory(ChangeCurrentWorkingDirectorySessionModelEvent changeWorkingDirectoryEvent) {
    PbSessionModelChange.Builder sessionModelChange=PbSessionModelChange.newBuilder();
    sessionModelChange.setTaskId(changeWorkingDirectoryEvent.getTaskId());
    sessionModelChange.setNewWorkingDirectory(changeWorkingDirectoryEvent.toProtoBuf());
    PbTopLevel.Builder topLevelMsg=PbTopLevel.newBuilder().setSessionModelChange(sessionModelChange);
    connectionToClient.queueNotification(topLevelMsg);
  }

  @Override
  public void onRemoveAllTasksFromHistory(RemoveTaskFromHistorySessionModelEvent event) {
    PbSessionModelChange.Builder sessionModelChange=PbSessionModelChange.newBuilder();
    sessionModelChange.setTaskId(event.getTaskId());
    sessionModelChange.setRemoveTask(PbRemoveTaskFromHistory.newBuilder());
    PbTopLevel.Builder topLevelMsg=PbTopLevel.newBuilder().setSessionModelChange(sessionModelChange);
    connectionToClient.queueNotification(topLevelMsg);
  }
}
