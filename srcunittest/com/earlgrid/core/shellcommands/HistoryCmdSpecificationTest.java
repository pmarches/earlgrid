package com.earlgrid.core.shellcommands;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.remoting.TestClient;

public class HistoryCmdSpecificationTest {
  @Test
  public void testRecallHistory() throws Exception {
    TestClient testClient=new TestClient();
    
    ExecutionHistoryRecord mockWho=testClient.execute("mock who");
    ExecutionHistoryRecord historyRecord=testClient.execute("history 0");
    assertEquals(mockWho.getOut(), historyRecord.getOut());
    assertEquals("pmarches", historyRecord.out.getRow(0).getCellAtColumn(0));

    testClient.close();
  }
  
  @Test
  public void testRecallHistoryAnPipe() throws Exception {
    TestClient testClient=new TestClient();
    testClient.execute("mock who");
    
    ExecutionHistoryRecord historyPipeWcRecord=testClient.execute("history 0|wc");
    assertEquals("3", historyPipeWcRecord.out.getRow(0).getCellAtColumn(0));

    testClient.close();
  }

}
