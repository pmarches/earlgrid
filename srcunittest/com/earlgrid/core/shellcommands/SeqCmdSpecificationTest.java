package com.earlgrid.core.shellcommands;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.remoting.TestClient;

public class SeqCmdSpecificationTest {

  @Test
  public void testExecuteSeq() throws Exception {
    TestClient testClient=new TestClient();
    ExecutionHistoryRecord seqPipeWc=testClient.execute("seq 1 10|wc");
    assertEquals("seq 1 10|wc", seqPipeWc.userEditedCommand);
    assertEquals("10", seqPipeWc.out.getRow(0).getCellAtColumn(0));
    testClient.close();
  }

}
