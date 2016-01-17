package com.earlgrid.core.shellcommands;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.remoting.TestClient;

public class CutCmdSpecificationTest {

  @Test
  public void testPerformComputation() throws Exception {
    TestClient testClient=new TestClient();
    testClient.execute("mock output 4 100");
    
    ExecutionHistoryRecord onlyAColumn = testClient.execute("history 0 | cut -f A ");
    assertEquals("result was "+onlyAColumn.getOut().toString(), 100, onlyAColumn.getOut().getRowCount());
    assertEquals("result was "+onlyAColumn.getOut().toString(), "A", onlyAColumn.getOut().getColumnHeader().get(0).toString());

    ExecutionHistoryRecord otherColumnThanC = testClient.execute("history 0 | cut -v -f 'C'");
    assertArrayEquals("result was "+otherColumnThanC.getOut().toString(), "A,B,D".split(","), otherColumnThanC.getOut().getColumnHeadersAsString());
    
    ExecutionHistoryRecord nonExistantColumn = testClient.execute("history 0 | cut -f TTT ");
    assertEquals("result was "+nonExistantColumn.getOut().toString(), 0, nonExistantColumn.getOut().getRowCount());
    assertEquals("result was "+nonExistantColumn.getOut().toString(), 0, nonExistantColumn.getOut().getColumnHeader().size());
    testClient.close();
  }
}
