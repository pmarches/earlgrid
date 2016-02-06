package com.earlgrid.core.shellcommands;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.remoting.TestClient;

public class SortCmdSpecificationTest {

  @Test
  public void testPerformComputation() throws Exception {
    TestClient testClient=new TestClient();
    testClient.requestCommandExecution("mock who");
    
    ExecutionHistoryRecord sortedUsernames = testClient.requestCommandExecution("history 0 | sort -f Username ");
    assertEquals("result was "+sortedUsernames.getOut().toString(), "[luser1, pmarches, root]", sortedUsernames.getOut().getCellsInColumn(0).toString());
    assertEquals("result was "+sortedUsernames.getOut().toString(), 3, sortedUsernames.getOut().getRowCount());

    ExecutionHistoryRecord reverseSortedUsernames = testClient.requestCommandExecution("history 0 | sort -r -f Username ");
    assertEquals("result was "+reverseSortedUsernames.getOut().toString(), "[root, pmarches, luser1]", reverseSortedUsernames.getOut().getCellsInColumn(0).toString());
    assertEquals("result was "+reverseSortedUsernames.getOut().toString(), 3, reverseSortedUsernames.getOut().getRowCount());
    testClient.close();
  }
}
