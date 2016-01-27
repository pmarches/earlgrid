package com.earlgrid.remoting;

import java.io.IOException;

public class SSHRemotingClient extends RemotingClient {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(SSHRemotingClient.class);
  private RemoteSSHHostConnection sshconnection;

  public SSHRemotingClient(RemoteHostConfiguration remoteHostConfiguration, InteractiveFormHandler interactiveFormHandler) throws Exception {
    super(interactiveFormHandler);
    sshconnection = new RemoteSSHHostConnection();
    boolean enableRemoteServerDebugging=false;
    if(enableRemoteServerDebugging){
      log.warn("Enabling JVM debugging on the remote host");
      remoteHostConfiguration.setJVMOptions("-agentlib:jdwp=transport=dt_socket,address=172.17.42.1:8000,server=n,suspend=y");
    }
    sshconnection.connect(remoteHostConfiguration);
    connectWith(sshconnection.getEndPoint());
  }

  @Override
  public void shutdownLocalEnd() throws IOException {
    super.shutdownLocalEnd();
    sshconnection.close();
  }

  @Override
  public String getName() {
    return sshconnection.sshSession.getHost();
  }

}
