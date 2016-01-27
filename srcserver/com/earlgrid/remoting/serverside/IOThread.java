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
 *  
 * 
 * Principle of operations:
 * 3 types of message exist: Request, Response, Notification.
 * 
 *  Notification 
 *  - are handled by the readIOThread. 
 *  - do not have a return value
 *  - are guaranteed to be processed sequentially
 *  
 *  Requests
 *   - Are executed by a new Thread from an threadpool
 *   - The request emiter will receive a Future that will complete when the corresponding response is received
 *  
 *  Response
 *   - Can be emited by any thread
 *   - Will complete the Future on the emiter side, by the IOReader thread
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