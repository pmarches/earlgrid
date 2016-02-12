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
  private AtomicInteger requestIdCounter=new AtomicInteger();
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
    if(responseMsg.hasRequestId()==false){
      throw new RuntimeException("The response "+responseMsg+" is missing it's source requestId");
    }
    responseMsg.setMessageType(PbTopLevel.MessageType.NOTIFICATION); //FIXME This line might not be needed, the type might already havebeen set.
    ioThread.queueMessage(responseMsg.build());
  }

  public int nextRequestId() {
    return requestIdCounter.incrementAndGet();
  }
  
  public TopLevelResponseFuture queueRequest(PbTopLevel.Builder request){
    int thisRequestId=nextRequestId();
    request.setMessageType(PbTopLevel.MessageType.REQUEST);
    request.setRequestId(thisRequestId);
    TopLevelResponseFuture responseFuture = new TopLevelResponseFuture(thisRequestId);
    this.requestsAwaitingCompletion.put(thisRequestId, responseFuture);

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

      if(topLevelMsg.hasRequestHasCompleted() && topLevelMsg.getRequestHasCompleted()){
        TopLevelResponseFuture responseFuture=requestsAwaitingCompletion.remove(topLevelMsg.getRequestId());
        if(responseFuture==null){
          log.error("received a response about requestId="+topLevelMsg.getRequestId()+" that we have no record of sending.");
          return;
        }
        if(topLevelMsg.hasExceptionOccured()){
          responseFuture.completeExceptionally(convertExceptionSpecificationToException(topLevelMsg.getExceptionOccured()));
        }
        else{
          responseFuture.complete(topLevelMsg);
        }
      }
    }
    else if(topLevelMsg.getMessageType()==PbTopLevel.MessageType.REQUEST){
      //TODO Use Threadpool here?
      new Thread() {
        public void run() {
          setName("IOConnection REQUEST handling Thread");
          messageHandler.onNotificationOrRequestReceived(topLevelMsg);
        }
      }.start();
    }
    else{
      log.error("Unhandeled message "+topLevelMsg);
    }
  }
  
  public PbTopLevel.Builder createResponseForRequest(PbTopLevel topMsgRequest) {
    return createResponseForRequest(topMsgRequest.getRequestId());
  }

  public PbTopLevel.Builder createResponseForRequest(int requestIdOfRequest) {
    PbTopLevel.Builder topMsgResponse=PbTopLevel.newBuilder();
    topMsgResponse.setMessageType(PbTopLevel.MessageType.NOTIFICATION);
    topMsgResponse.setRequestId(requestIdOfRequest);
    topMsgResponse.setRequestHasCompleted(true);
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
