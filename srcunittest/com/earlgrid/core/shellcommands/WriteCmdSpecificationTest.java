package com.earlgrid.core.shellcommands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.remoting.TestClient;

public class WriteCmdSpecificationTest {
  @Test
  public void testWriteCmd() throws Exception{
    TestClient testClient=new TestClient();
    ExecutionHistoryRecord mockHistoryRecord=testClient.requestCommandExecution("mock who");
    
    File outputFile=File.createTempFile(getClass().getSimpleName(), null);
    outputFile.delete();
    outputFile.deleteOnExit();
    String unixStylePath = TestClient.convertToUnixPath(outputFile);
    
    ExecutionHistoryRecord writeOutput = testClient.requestCommandExecution("history 0 | write "+unixStylePath);
    assertTrue(outputFile.length()>3040);
    assertEquals(0, writeOutput.getOut().getRowCount());
    
    ExecutionHistoryRecord readFromFile = testClient.requestCommandExecution("read "+unixStylePath);
    assertEquals(mockHistoryRecord.getOut(), readFromFile.getOut());
    testClient.close();
  }

}
