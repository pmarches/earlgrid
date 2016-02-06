package com.earlgrid.remoting.serverside;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.earlgrid.core.serverside.EarlGridPb.PbTopLevel;

/**
 * Responsible for the actual IO with the endpoints. 
 *  - Works asynchronously
 *  - Will notify the IOConnection when a new message arrives.
 *  
 *  
 *  Wrost case scenario:
 *  R1  -> R1
 *  R2  <- R2
 *  R3  -> R3
 *  R3  <- R3
 *  R2  -> R2
 *  R1  <- R1
 * 
 * Principle of operations:
 * 3 types of message exist: Request, Response, Notification. Request have to be completable asynchrnously to avoid deadlocks. 
 * Notifications on the other hand, need to be handled sequentially. Responses Are just a special case of notifications, that indicates
 * the originating request has completed and will no longer be sending notifications.
 * 
 *  Notification 
 *  - are handled by the io thread (readIOThread), so they must be handled rather quickly 
 *  - do not return data to the sender
 *  - May (or may not) be sent in response to a request
 *  - are guaranteed to be processed sequentially
 *   - Can be emited by any thread
 *   - If the notification has the request complete flag, it will complete the Future on the emiter side, by the IOReader thread
 *  
 *  Requests
 *   - Are the initiating the exchange
 *   - Are executed by a new Thread from an threadpool
 *   - The request emiter will receive a Future that will complete when the corresponding response is received
 *  
 */
/**
 * Shutdown scenarios:
 * 1- Client tell the server to shutdown via API
 * 2- Client tells the server to shutdown via a shell command (In this case, it is the server that initiates the shutdown)
 * 3- Remote end crashes (either client or server)
 * 
 * What needs to happen:
 * 1- The initiator needs to mark the connection as being in shutdown mode.
 * 2- The initiator sends a shutdown message to the remote end
 * 3- The initiator waits for the remaining messages to be delivered (there may be background tasks still running?)
 * 4- The receiver receives the shutdown notification
 * 5- The receiver marks the connection as being in shutdown mode
 * 6- Wait for messages to be delivered
 *
 * How to make this happen?
 * A- Have a ConnectionShutdownException to distinguish from normal shutdown and remote endpoint crashing
 * B- 
 */
 
public class IOThread {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(IOThread.class);
  private IOEndPoint endpoint;
  private volatile boolean keepReadingAndWriting=true;
  private volatile boolean disconnectCommandHasBeenSentToRemoteEndPoint=false;
  String threadNamePrefix;
  private IOConnection ioConnection;
  BlockingQueue<PbTopLevel> requestsPendingWrite=new ArrayBlockingQueue<>(10);
  
  public IOThread(IOEndPoint endpoint, String threadNamePrefix, IOConnection ioConnection){
    this.endpoint=endpoint;
    this.threadNamePrefix=threadNamePrefix;
    this.ioConnection=ioConnection;
  }

  public void queueMessage(PbTopLevel topLevelMsg) {
    try {
      if(keepReadingAndWriting==false){
        throw new RuntimeException("This IOThread is closed, cannot queueMessage "+topLevelMsg);
      }
      requestsPendingWrite.put(topLevelMsg);
    } catch (InterruptedException e) {
      log.warn("Interrupted in queueMessage", e);
    }
  }

  Thread readThread=new Thread(){
    @Override
    public void run() {
      try {
        setName(threadNamePrefix+"-reader-"+IOThread.class.getSimpleName());

        while(keepReadingAndWriting){
          PbTopLevel topLevelRead = endpoint.readTopLevel();
          ioConnection.onMessageReceived(topLevelRead);
        }
      } catch (Exception e) {
        if(keepReadingAndWriting){
          log.error("Exception occured while the IOThread was still active", e);
          ioConnection.onIOExceptionOccured(e);
        }
      }
    }
  };

  Thread writeThread=new Thread(){
    @Override
    public void run() {
      try {
        setName(threadNamePrefix+"-writer-"+IOThread.class.getSimpleName());
        while(keepReadingAndWriting){
          try {
            PbTopLevel requestCurrentlyBeingWritten=requestsPendingWrite.take();
            endpoint.writeTopLevel(requestCurrentlyBeingWritten);
          } catch (InterruptedException e) {
            //OK
          }
        }
      } catch (Exception e) {
        log.error("Exception occured in writer thread", e);
        ioConnection.onIOExceptionOccured(e);
      }
    }
  };

  public void start(){
    writeThread.setDaemon(true);
    writeThread.start();
    readThread.start();
  }

  public void stop(){
    keepReadingAndWriting=false;
    writeThread.interrupt();
    readThread.interrupt();
  }
  
  public void waitForQueuesToEmpty(){
    try {
      writeThread.join();
      readThread.join();
    } catch (InterruptedException e) {
//      log.debug("Interrupted in stopAfterAllRequestsHaveCompleted", e);
    }
  }
  
}