package com.earlgrid.core.shellcommands;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.remoting.TestClient;

public class DelayCmdSpecificationTest {
  @Test
  public void testDelay() throws Exception {
    TestClient testClient=new TestClient();
    
    ExecutionHistoryRecord mockWho=testClient.execute("mock who");
    ExecutionHistoryRecord delayedRecord=testClient.execute("history 0|delay");
    assertEquals(mockWho.getOut(), delayedRecord.getOut());
    assertEquals("pmarches", delayedRecord.out.getRow(0).getCellAtColumn(0));

    testClient.close();
  }
  
}
