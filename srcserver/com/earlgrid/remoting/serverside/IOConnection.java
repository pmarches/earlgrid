package com.earlgrid.remoting.serverside;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.earlgrid.core.serverside.EarlGridPb.PbExceptionSpecification;
import com.earlgrid.core.serverside.EarlGridPb.PbTopLevel;
import com.earlgrid.remoting.TopLevelResponseFuture;

/**
 * Represents a connection to the remote end, responsible for completing the pending futures when a response is received.
 */
public class IOConnection {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(IOConnection.class);
  IOThread ioThread;
  private AtomicInteger messageIdCounter=new AtomicInteger();
  ConcurrentHashMap<Integer, TopLevelResponseFuture> requestsAwaitingCompletion=new ConcurrentHashMap<>();
  private IOConnectionMessageHandler messageHandler;

  public IOConnection(IOEndPoint endpoint, String sideLabel, IOConnectionMessageHandler requestHandler) {
    this.messageHandler=requestHandler;
    ioThread=new IOThread(endpoint, sideLabel, this);
    ioThread.start();
  }

  public void queueNotification(PbTopLevel.Builder notificationMsg){
    notificationMsg.setMessageType(PbTopLevel.MessageType.NOTIFICATION);
    ioThread.queueMessage(notificationMsg.build());
  }

  public void queueResponse(PbTopLevel.Builder responseMsg){
    responseMsg.setMessageType(PbTopLevel.MessageType.RESPONSE);
    ioThread.queueMessage(responseMsg.build());
  }

  public TopLevelResponseFuture queueRequest(PbTopLevel.Builder request){
    int thisMessageId=messageIdCounter.incrementAndGet();
    request.setMessageType(PbTopLevel.MessageType.REQUEST);
    request.setRequestId(thisMessageId);
    TopLevelResponseFuture responseFuture = new TopLevelResponseFuture(thisMessageId);
    this.requestsAwaitingCompletion.put(thisMessageId, responseFuture);

    ioThread.queueMessage(request.build());
    return responseFuture;
  }

  private Throwable convertExceptionSpecificationToException(PbExceptionSpecification exceptionSpec) {
    try {
      Class exceptionClass = Class.forName(exceptionSpec.getExceptionClass());
      if(Throwable.class.isAssignableFrom(exceptionClass)==false){
        throw new ClassNotFoundException();
      }

      Constructor<Throwable> stringMessageCtor = exceptionClass.getConstructor(String.class);
      if(stringMessageCtor==null){
        throw new Exception();
      }

      String message="";
      if(exceptionSpec.hasMessage()){
        message=exceptionSpec.getMessage();
      }
      Throwable remoteException=stringMessageCtor.newInstance(message);
      return remoteException;
    } catch (Exception e) {
      return new Exception("Error occurred while trying to convert the exception specification '"+exceptionSpec.getExceptionClass()+"' to a java Exception");
    }
  }

  private void completeAllPendingResponsesExceptionally(Exception e) {
    for(TopLevelResponseFuture pendingResponse : requestsAwaitingCompletion.values()){
      pendingResponse.completeExceptionally(e);
    }
  }

  public void onMessageReceived(PbTopLevel topLevelMsg) {
    if(topLevelMsg.hasMessageType()==false){
      log.error("Ignoring received message without a message type "+topLevelMsg);
      return;
    }

    if(topLevelMsg.getMessageType()==PbTopLevel.MessageType.NOTIFICATION){
      messageHandler.onNotificationOrRequestReceived(topLevelMsg);
    }
    else if(topLevelMsg.getMessageType()==PbTopLevel.MessageType.REQUEST){
      //TODO Use Threadpool here?
      new Thread(()-> messageHandler.onNotificationOrRequestReceived(topLevelMsg)).start();
    }
    else if(topLevelMsg.getMessageType()==PbTopLevel.MessageType.RESPONSE){
      TopLevelResponseFuture responseFuture=requestsAwaitingCompletion.remove(topLevelMsg.getRequestId());
      if(responseFuture==null){
        log.error("received a response about messageId="+topLevelMsg.getRequestId()+" that we have no record of sending.");
        return;
      }
      if(topLevelMsg.hasExceptionOccured()){
        responseFuture.completeExceptionally(convertExceptionSpecificationToException(topLevelMsg.getExceptionOccured()));
      }
      else{
        responseFuture.complete(topLevelMsg);
      }
    }
    else{
      log.error("Unhandeled message "+topLevelMsg);
    }
  }
  
  public PbTopLevel.Builder createResponseForRequest(PbTopLevel topMsgRequest) {
    PbTopLevel.Builder topMsgResponse=PbTopLevel.newBuilder();
    topMsgResponse.setMessageType(PbTopLevel.MessageType.RESPONSE);
    topMsgResponse.setRequestId(topMsgRequest.getRequestId());
    return topMsgResponse;
  }


  public void onIOExceptionOccured(Exception e) {
    completeAllPendingResponsesExceptionally(e);
  }

  public void stopAndWaitForAllRequestsToComplete() {
    ioThread.stop();
    ioThread.waitForQueuesToEmpty();
  }
}
