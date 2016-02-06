package com.earlgrid.core.shellcommands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.remoting.TestClient;

public class ExternalProcessCmdSpecificationTest {
  @Test
  public void testPerformComputation() throws Exception {
    org.junit.Assume.assumeTrue(System.getProperty("os.name").endsWith("Linux"));

    TestClient testClient=new TestClient();
    testClient.requestCommandExecution("mock who");
  
    ExecutionHistoryRecord mockComputation = testClient.getSessionModel().getHistory().get(1);
    ExecutionHistoryRecord execCatRecord = testClient.requestCommandExecution("history 0 | exec cat");
    assertEquals("result was "+execCatRecord.getOut().toString(), mockComputation.out, execCatRecord.getOut());

    ExecutionHistoryRecord execLsRecord = testClient.requestCommandExecution("history 0 | exec 'ls -l'");
    assertTrue("result was "+execLsRecord.getOut().toString(), execLsRecord.getOut().getRowCount()>10);
    testClient.close();
  }

}
