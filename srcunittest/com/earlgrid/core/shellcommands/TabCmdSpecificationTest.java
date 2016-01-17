package com.earlgrid.core.shellcommands;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.core.sessionmodel.TabularOutput;
import com.earlgrid.remoting.TestClient;

public class TabCmdSpecificationTest {
  @Test
  public void testPerformComputation() throws Exception {
    TestClient testClient=new TestClient();
    testClient.execute("mock ls");

    ExecutionHistoryRecord execLsRecord = testClient.execute("history 0 | tab");
    TabularOutput out = execLsRecord.getOut();
    assertEquals("result was "+execLsRecord.getOut().toString(), 3, out.getRowCount());
    assertArrayEquals(new String[]{"drwxrwxr-x", "3", "pmarches", "pmarches", "4096", "May", "6", "14:58", "dir1"}, out.getRow(0).getAllCells());

//    ExecutionHistoryRecord execLsRecord = testClient.execute(session.parse("exec 'ls -l' | tab -h -w 10,3,9,9,9,12,* "));
//    assertTrue("result was "+execLsRecord.getOut().toString(), execLsRecord.getOut().getRowCount()>10);
    testClient.close();
  }

}
