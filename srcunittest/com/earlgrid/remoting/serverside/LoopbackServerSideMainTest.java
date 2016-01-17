package com.earlgrid.remoting.serverside;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.earlgrid.remoting.LoopbackRemotingClient;
import com.earlgrid.remoting.TestClient;

public class LoopbackServerSideMainTest {
  @Test
  public void test() throws Exception {
    TestClient client=new TestClient();
    LoopbackRemotingClient loopBack=new LoopbackRemotingClient(null);
    for(int i=0;i<3; i++){
      assertTrue(loopBack.ping());
    }
    
    client.execute("seq 1 10");
//    assertEquals(TaskExitStatus.OK_EXIT_STATUS, lsOutput.exitStatus);
    loopBack.shutdownRemoteEnd();
  }
}
