package com.earlgrid.core.shellcommands;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.core.sessionmodel.TabularOutput;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class MockCmdSpecification extends BaseCmdSpecification<MockCmdArguments> {
  @Override
  public void onThisCommandExecute() throws Exception {
    if(MockCmdArguments.WHO.equals(args.subCommand)){
      createMockWho();
    }
    else if(MockCmdArguments.UNIXFIND.equals(args.subCommand)){
      createMockUnixFind();
    }
    else if(MockCmdArguments.MANY.equals(args.subCommand)){
      createMockUnixFind();
    }
    else if(MockCmdArguments.LS.equals(args.subCommand)){
      createMockLs();
    }
    else if(MockCmdArguments.OUTPUT.equals(args.subCommand)){
      ExecutionHistoryRecord newTask=createMockExecutionRecord(taskId, args.numberOfColumn, args.numberOfRows);
      emitTask(newTask);
    }
    else if(MockCmdArguments.JSON.equals(args.subCommand)){
      createJSON();
    }
  }
  
  private void emitTask(ExecutionHistoryRecord newTask) throws InterruptedException {
    emitOutput(newTask.getOut());
  }

  private void createJSON() {
    
  }

  private void createMockLs() throws InterruptedException {
    ExecutionHistoryRecord comp=new ExecutionHistoryRecord(taskId, -1, "ls");
    TabularOutput out=comp.getOut();
    out.getColumnHeader().setColumnHeaders("A");
    out.addRow(taskId, "drwxrwxr-x 3 pmarches pmarches      4096 May  6 14:58 dir1");
    out.addRow(taskId, "drwxrwxr-x 3 pmarches pmarches      4096 May  7 15:18 dir2");
    out.addRow(taskId, "-rw-r--r-- 1 pmarches pmarches 328324809 May  7 12:48 file1.zip");
    //  out.addRow(taskId, "total 320644");
    emitOutput(out);
  }

  private void createMockUnixFind() throws InterruptedException {
    ExecutionHistoryRecord comp=new ExecutionHistoryRecord(taskId, -1, "find /tmp -size +0");
    TabularOutput out=comp.getOut();
    out.getColumnHeader().setColumnHeaders("A");
    out.addRow(taskId, "10354704   20 drwxr-xr-x   4 pmarches pmarches    20480 May  8 17:16 .");
    out.addRow(taskId, "10364647   16 -rw-r-----   1 pmarches pmarches     9068 Jan  9 15:16 ./File1.xlsx");
    out.addRow(taskId, "10364569 403464 -rw-r-----   1 pmarches pmarches 413138944 Dec 21 19:19 ./File2-wheezy-amd64.iso");
    out.addRow(taskId, "10364584 27200 -rw-rw-r--   1 pmarches pmarches 27845884 Aug 29  2014 ./10-13-2010--flight-test.wmv");
    out.addRow(taskId, "10358189 3348 -rwxrwxr-x   1 pmarches pmarches  3424952 Sep 20  2013 ./file3.old");
    out.addRow(taskId, "10358367  448 -rw-rw-r--   1 pmarches pmarches   453908 Dec 20  2013 ./JavaEWAH-0.7.9.zip");
    out.addRow(taskId, "10361887  208 -rw-rw-r--   1 pmarches pmarches   206776 Mar 13  2014 ./File4.pdf");
    out.addRow(taskId, "10364567  316 -rw-rw-r--   1 pmarches pmarches   318352 Aug 26  2014 ./asdasd\\ 23d32f\\ d23f23f\\ X\\ asd.exe");
    emitOutput(out);
  }

  private void createMockWho() throws InterruptedException {
    ExecutionHistoryRecord comp=new ExecutionHistoryRecord(taskId, -1, "who");
    TabularOutput out=comp.getOut();
    out.getColumnHeader().setColumnHeaders("Username", "Login", "What", "Host");
    out.addRow(taskId, "pmarches", "Wed Apr 22 16:54:13 PDT 2015", "who", "laptop");
    out.addRow(taskId, "root", "Wed Apr 15 00:54:00 PDT 2015", "EarlGrid", "laptop");
    out.addRow(taskId, "luser1", "Thu Apr 23 10:19:01 PDT 2015", "vi", "term1");
    emitOutput(out);
  }

  static public ExecutionHistoryRecord createMockExecutionRecord(int taskId, int nbColumn, int nbRows) {
    ExecutionHistoryRecord newTask=new ExecutionHistoryRecord(taskId, -1, "mockComputation");
    TabularOutput newOutput = newTask.getOut();
    String[] columnHeaders=new String[nbColumn];
    for(int columnIndex=0; columnIndex<nbColumn; columnIndex++){
      char columnLetter=(char) ('A'+columnIndex);
      columnHeaders[columnIndex]=String.format("%s", columnLetter);
    }
    newOutput.getColumnHeader().setColumnHeaders(columnHeaders);

    for(int rowIndex=0; rowIndex<nbRows; rowIndex++){
      String[] rowContent=new String[nbColumn];
      for(int columnIndex=0; columnIndex<nbColumn; columnIndex++){
        char columnLetter=(char) ('A'+columnIndex);
        rowContent[columnIndex]=String.format("cell-%s-%03d", columnLetter, rowIndex);
      }
      newOutput.addRow(taskId, rowContent);
    }
    return newTask;
  }
  
  public static void addRowsToRecord(ExecutionHistoryRecord record, int nbRowsToAdd) {
    int nbColumn=record.getOut().getColumnHeader().size();
    for(int rowIndex=0; rowIndex<nbRowsToAdd; rowIndex++){
      String[] rowContent=new String[nbColumn];
      for(int columnIndex=0; columnIndex<nbColumn; columnIndex++){
        char columnLetter=(char) ('A'+columnIndex);
        rowContent[columnIndex]=String.format("cell-%s-%03d", columnLetter, rowIndex);
      }
      record.out.addRow(record.taskId, rowContent);
    }
  }

//
//  public ExecutionHistoryRecord createMockLs(int taskId) throws Exception {
//    ensureTaskHasBeenConsumedSequentially();
//
//    ExecutionHistoryRecord comp=new ExecutionHistoryRecord(taskId, "ls");
//    TabularOutput out=comp.getOut();
//    out.getColumnHeader().setColumnHeaders("A");
//    out.addRow(taskId, "drwxrwxr-x 3 pmarches pmarches      4096 May  6 14:58 dir1");
//    out.addRow(taskId, "drwxrwxr-x 3 pmarches pmarches      4096 May  7 15:18 dir2");
//    out.addRow(taskId, "-rw-r--r-- 1 pmarches pmarches 328324809 May  7 12:48 file1.zip");
////    out.addRow(taskId, "total 320644");
//    client.server.getSession().getSessionModel().appendHistoryRecord(comp);
//
//    return returnLastRecivedTask();
//  }
//
//  public ExecutionHistoryRecord createMockFind(int taskId) throws Exception {
//    ensureTaskHasBeenConsumedSequentially();
//
//    client.server.getSession().getSessionModel().appendHistoryRecord(comp);
//
//    return returnLastRecivedTask();
//  }
//
  public void createManyComputations() throws Exception {
    int executionIndex=1;
    for(int i=0; i<100; i++){
      createMockUnixFind();
      createMockLs();
      createMockWho();
    }
  }
//
//  /**
//   * Helper methond when running the tests under windows
//   * @param outputFile
//   * @return
//   */
//  public static String convertToUnixPath(File outputFile) {
//    String unixStylePath=outputFile.toString().replaceAll("\\\\", "/");
//    return unixStylePath;
//  }


}
