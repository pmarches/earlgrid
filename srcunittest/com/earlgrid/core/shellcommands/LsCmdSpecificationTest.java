package com.earlgrid.core.shellcommands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.remoting.TestClient;

public class LsCmdSpecificationTest {

  @Test
  public void testExecuteLs() throws Exception {
    TestClient testClient=new TestClient();
    
    ExecutionHistoryRecord ls=testClient.execute("ls -l /tmp");
//    assertEquals(TaskExitStatus.OK_EXIT_STATUS, ls.exitStatus);
    assertEquals("Name", ls.out.getColumnHeader().get(0).name);
    assertEquals("Size", ls.out.getColumnHeader().get(1).name);
    assertTrue(ls.out.getRowCount()>0);

    testClient.close();
  }

}
