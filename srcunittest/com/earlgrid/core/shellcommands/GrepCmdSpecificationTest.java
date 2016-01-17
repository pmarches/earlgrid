package com.earlgrid.core.shellcommands;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.remoting.TestClient;

public class GrepCmdSpecificationTest {
  @Test
  public void testPerformComputation() throws Exception {
    TestClient client=new TestClient();
    ExecutionHistoryRecord mockResult = client.execute("mock output 4 100");

    ExecutionHistoryRecord simpleGrep = client.execute("history 0 | grep cell-A-0.1");
    assertEquals("result was "+simpleGrep.getOut().toString(), 10, simpleGrep.getOut().getRowCount());

    ExecutionHistoryRecord invertedGrep = client.execute("history 0 | grep -v cell-A-0.1 | grep -v 'cell-.*-0.3'");
    assertEquals("result was "+invertedGrep.getOut().toString(), 80, invertedGrep.getOut().getRowCount());
    
    ExecutionHistoryRecord regularGrep = client.execute("history 0 | grep 'cell-A-0.1' | grep cell-.*-001");
    assertEquals("result was "+regularGrep.getOut().toString(), 1, regularGrep.getOut().getRowCount());
    
    client.close();
  }

}
