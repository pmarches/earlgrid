package com.earlgrid.core.shellcommands;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.remoting.TestClient;

public class CSVCmdSpecificationTest {

  @Test
  public void testPerformComputation() throws Exception {
    TestClient testClient=new TestClient();
    ExecutionHistoryRecord mockRecord = testClient.execute("mock output 4 100");
    
    File tmpCSVFile=File.createTempFile(getClass().getSimpleName()+"-test", ".csv");
    tmpCSVFile.deleteOnExit();
    ExecutionHistoryRecord csvWrite = testClient.execute("history 0 | csv -o "+TestClient.convertToUnixPath(tmpCSVFile));
    assertEquals(0, csvWrite.getOut().getRowCount());

    ExecutionHistoryRecord csvRead = testClient.execute("csv -h -i "+TestClient.convertToUnixPath(tmpCSVFile));
    assertEquals("result was "+csvRead.getOut().toString(), mockRecord.getOut(), csvRead.getOut());
    testClient.close();
  }
}
