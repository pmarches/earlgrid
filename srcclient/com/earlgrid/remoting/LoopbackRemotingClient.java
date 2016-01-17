package com.earlgrid.remoting;

import java.io.IOException;

import com.earlgrid.remoting.serverside.IOEndPoint;
import com.earlgrid.remoting.serverside.RemotingServerMain;

public class LoopbackRemotingClient extends RemotingClient {
  public RemotingServerMain server;

  public LoopbackRemotingClient(InteractiveFormHandler interactiveFormHandler) throws IOException {
    super(interactiveFormHandler);
    IOEndPoint[] pipedEndPoints=IOEndPoint.createEndPointConnectedByPipes();
    server=new RemotingServerMain(pipedEndPoints[0]);
    connectWith(pipedEndPoints[1]);
  }

  @Override
  public void shutdownRemoteEnd() throws IOException {
    super.shutdownRemoteEnd();
  }

  @Override
  public String getName() {
    return "local";
  }
}
