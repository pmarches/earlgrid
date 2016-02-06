package com.earlgrid.remoting.serverside;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.earlgrid.remoting.RemotingClient;
import com.earlgrid.remoting.TestClient;

public class RemoteSSHHostConnectionTest {
  @Test
  public void test() throws Exception {
    RemotingClient client=TestClient.createNewSSHClient();
    for(int i=0;i<2; i++){
      assertTrue(client.ping());
    }
    client.close();
  }

}
