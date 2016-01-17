package com.earlgrid.remoting.serverside;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.earlgrid.remoting.RemoteHostConfiguration;
import com.earlgrid.remoting.RemotingClient;
import com.earlgrid.remoting.SSHRemotingClient;
import com.earlgrid.remoting.UserCredentials;

public class RemoteSSHHostConnectionTest {
  @Test
  public void test() throws Exception {
    UserCredentials pkCredentials=new UserCredentials("root", new File("unittest/testSSHKey_rsa"));
    RemoteHostConfiguration conf=new RemoteHostConfiguration("localhost", 8022, pkCredentials);
    RemotingClient client = new SSHRemotingClient(conf, null);
    for(int i=0;i<100; i++){
      assertTrue(client.ping());
    }
    client.shutdownRemoteEnd();
  }

}
