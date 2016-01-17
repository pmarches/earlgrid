package com.earlgrid.core.session;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ExecutionHistory implements Iterable<ExecutionHistoryRecord> {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(ExecutionHistory.class);
  TreeMap<Integer, ExecutionHistoryRecord> executionRecords=new TreeMap<>();
  
  public ExecutionHistory() {
  }

  public void appendHistoryRecord(ExecutionHistoryRecord newExecutionRecord) {
    log.debug("Adding executionHistoryrecord "+newExecutionRecord);
    executionRecords.put(newExecutionRecord.taskId, newExecutionRecord);
  }

  public boolean containsKey(Integer historyNumber) {
    return executionRecords.containsKey(historyNumber);
  }


  public ExecutionHistoryRecord get(Integer historyNumber) {
    return executionRecords.get(historyNumber);
  }

  public ExecutionHistoryRecord getLastExecutionRecord() {
    Entry<Integer, ExecutionHistoryRecord> entry = executionRecords.lastEntry();
    if(entry==null){
      return null;
    }
    return entry.getValue();
  }

  @Override
  public Iterator<ExecutionHistoryRecord> iterator() {
    return executionRecords.values().iterator();
  }

  public ExecutionHistoryRecord getHistoryRecordBefore(int startingPoint) {
    Entry<Integer, ExecutionHistoryRecord> entryFound = executionRecords.floorEntry(startingPoint-1);
    if(entryFound==null){
      return null;
    }
    return entryFound.getValue();
  }

  public ExecutionHistoryRecord getHistoryRecordAfter(int startingPoint) {
    Entry<Integer, ExecutionHistoryRecord> entryFound = executionRecords.ceilingEntry(startingPoint+1);
    if(entryFound==null){
      return null;
    }
    return entryFound.getValue();
  }

  public void removeExecutionRecord(int executionIndex) {
    executionRecords.remove(executionIndex);
  }

  public int size() {
    return executionRecords.size();
  }

  public ExecutionHistoryRecord getHistoryRecordFromLast(int offset) {
    if(offset<0){
      throw new IndexOutOfBoundsException("The offset "+offset+" needs to be greater than 0");
    }
    Iterator<Integer> it = executionRecords.descendingKeySet().iterator();
    while(offset>0){
      if(it.hasNext()==false){
        return null;
      }
      it.next();
      offset--;
    }
    return executionRecords.get(it.next());
  }

  public void removeAll() {
    executionRecords.clear();
  }
  
  

}
