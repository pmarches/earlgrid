package com.earlgrid.ui.standalone.customterminal;

import com.earlgrid.core.session.ExecutionHistoryRecord;

public class LineBasedArea {
  protected ExecutionHistoryRecord record;
  
  public LineBasedArea(ExecutionHistoryRecord record) {
    this.record=record;
  }
  
  public int getNumberOfLines(){
    return record.getOut().getRowCount()+1;
  }

  public ExecutionHistoryRecord getRecord() {
    return record;
  }
}
