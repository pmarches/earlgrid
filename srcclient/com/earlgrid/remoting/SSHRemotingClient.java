package com.earlgrid.remoting;

import java.io.IOException;

public class SSHRemotingClient extends RemotingClient {
  private RemoteSSHHostConnection sshconnection;

  public SSHRemotingClient(RemoteHostConfiguration remoteHostConfiguration, InteractiveFormHandler interactiveFormHandler) throws Exception {
    super(interactiveFormHandler);
    sshconnection = new RemoteSSHHostConnection();
    // conf.setJVMOptions("-agentlib:jdwp=transport=dt_socket,address=172.17.42.1:8000,server=n,suspend=y");
    sshconnection.connect(remoteHostConfiguration);
    connectWith(sshconnection.getEndPoint());
  }

  @Override
  public void shutdownRemoteEnd() throws IOException {
    super.shutdownRemoteEnd();
    sshconnection.close();
  }

  @Override
  public String getName() {
    return sshconnection.sshSession.getHost();
  }
}
