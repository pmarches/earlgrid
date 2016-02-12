package com.earlgrid.core.shellcommands;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.core.sessionmodel.TabularOutput;
import com.earlgrid.core.shellcommands.tjoin.TJoinExpressionParser;
import com.earlgrid.core.shellcommands.tjoin.TJoinExpresssion;
import com.earlgrid.remoting.TestClient;

public class TJoinCmdSpecificationTest {
  @Test
  public void customTJoinExpressionTest(){
    TJoinExpressionParser parser=new TJoinExpressionParser();

    TJoinExpresssion litteralsAndColumnsExpr=parser.parse("My name is %out.name, I am %out.age at last.");
    TJoinExpresssion joinDatesExpr=parser.parse("%Month-%Day-%Year");
    TJoinExpresssion joinDatesByColIndexExpr=parser.parse("%B-%C-%D");

//    SessionModel mockModel=MockSessionModel.create();
//    TJoinExpressionEvaluator evaluator=new TJoinExpressionEvaluator();
//    TabularOutputRow joinDatesResult=evaluator.evaluateOneRow(joinDatesExpr, 0, mockModel);
//    assertEquals(1, joinDatesResult.getAllCells().length);
//    assertEquals("Feb-01-2011", joinDatesResult.getAllCells()[0]);
//    TabularOutputRow joinDatesByColIndexResult=evaluator.evaluateOneRow(joinDatesByColIndexExpr, 0, mockModel);
//    assertArrayEquals(joinDatesByColIndexResult.getAllCells(), joinDatesResult.getAllCells());
  }
  
//  @Test
  public void testPerformComputation() throws Exception {
    TestClient testClient=new TestClient();
    testClient.requestCommandExecution("mock output 2 5");

    ExecutionHistoryRecord execLsRecord = testClient.requestCommandExecution("history 0 | tjoin ");
    TabularOutput out = execLsRecord.getOut();
    assertEquals("result was "+execLsRecord.getOut().toString(), 3, out.getRowCount());
    assertArrayEquals(new String[]{"drwxrwxr-x", "3", "pmarches", "pmarches", "4096", "May", "6", "14:58", "dir1"}, out.getRow(0).getAllCells());

//    ExecutionHistoryRecord execLsRecord = testClient.execute(session.parse("exec 'ls -l' | tab -h -w 10,3,9,9,9,12,* "));
//    assertTrue("result was "+execLsRecord.getOut().toString(), execLsRecord.getOut().getRowCount()>10);
    testClient.close();
  }

}
