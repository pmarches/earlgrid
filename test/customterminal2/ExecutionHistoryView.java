package com.earlgrid.ui.standalone.customterminal2;

import com.earlgrid.core.session.ExecutionHistory;
import com.earlgrid.core.session.ExecutionHistoryRecord;

public class ExecutionHistoryView {
  private ExecutionHistory backingHistory;
  int[] numberOfRowsPerComputation;
  int[] computationNumber;
  
  public ExecutionHistoryView(ExecutionHistory backingHistory) {
    this.backingHistory=backingHistory;

    computationNumber=new int[backingHistory.size()];
    numberOfRowsPerComputation=new int[backingHistory.size()];
    int i=0;
    for(ExecutionHistoryRecord exec:backingHistory){
      computationNumber[i]=exec.taskId;
      numberOfRowsPerComputation[i]=exec.getOut().getRowCount()+1;
      i++;
    }
  }
  
//  public String getLogicalRowContent(int rowIndex){
//    int cumulativeNumberOfRows=0;
//    for(int i=0; i<numberOfRowsPerComputation.length; i++){
//      cumulativeNumberOfRows+=numberOfRowsPerComputation[i];
//    }
//    String line=backingHistory.get(commandIndex).command;
//    return line;
//  }

  public int getRowCount() {
    int numberOfRows=0;
    for(ExecutionHistoryRecord exec:backingHistory){
      numberOfRows+=getNumberOfRowsForRecord(exec);
    }
    return numberOfRows;
  }

  private int getNumberOfRowsForRecord(ExecutionHistoryRecord exec) {
    int numberOfRowsForThisCommand=exec.out.getRowCount()+1;
    return Math.min(20, numberOfRowsForThisCommand);
  }

  public ExecutionHistoryRecord[] getExecutionRecords(int startingRowNumber, int numberOfRows) {
    //Find the first record that has one of the rows fitting the input range
    int cumulativeNumberOfRows=0;
    int indexOfFirstRecordToReturn=0;
    for(; indexOfFirstRecordToReturn<numberOfRowsPerComputation.length; indexOfFirstRecordToReturn++){
      if(cumulativeNumberOfRows>=startingRowNumber){
        break;
      }
      cumulativeNumberOfRows+=numberOfRowsPerComputation[indexOfFirstRecordToReturn];
    }

    cumulativeNumberOfRows=0;
    int indexOfLastRecordToReturn=indexOfFirstRecordToReturn;
    for(; indexOfLastRecordToReturn<numberOfRowsPerComputation.length; indexOfLastRecordToReturn++){
      if(cumulativeNumberOfRows>=numberOfRows){
        break;
      }
      cumulativeNumberOfRows+=numberOfRowsPerComputation[indexOfLastRecordToReturn];
    }

    int numberOfRecordsToReturn=indexOfLastRecordToReturn-indexOfFirstRecordToReturn;
    ExecutionHistoryRecord[] recordsThatFit=new ExecutionHistoryRecord[numberOfRecordsToReturn];
    for(int i=0; i<numberOfRecordsToReturn; i++){
      int recordNumber=computationNumber[indexOfFirstRecordToReturn+i];
      recordsThatFit[i]=backingHistory.get(recordNumber);
    }
    return recordsThatFit;
  }
}
