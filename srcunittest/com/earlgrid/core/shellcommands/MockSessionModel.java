package com.earlgrid.core.shellcommands;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.core.sessionmodel.SessionModel;
import com.earlgrid.core.sessionmodel.TabularOutputColumn;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutput.ColumnType;

public class MockSessionModel extends SessionModel {
  private MockSessionModel() {
    super(null);
  }

  public static SessionModel create() {
    MockSessionModel model=new MockSessionModel();
    model.appendMockTask(
        "Size/Month/Day/Year/FileName",
        "11/Feb/01/2011/File1.txt", 
        "12/Mar/02/2012/File2.zip",
        "13/Apr/03/2013/File3.dat",
        "14/Jun/04/2014/Dir4"
    );
    model.appendMockTask(
        "OID",
        "0000001", 
        "1231231",
        "2342342",
        "2555555"
    );
    return model;
  }

  private void appendMockTask(String columnNamesStr, Object... rows) {
    final String DELIMITER="/";
    int taskId=getNextTaskId();
    ExecutionHistoryRecord newExecutionRecord=new ExecutionHistoryRecord(taskId, taskId, "MockTask"+taskId);
    String[] columnNames=columnNamesStr.split(DELIMITER);
    TabularOutputColumn[] columns=new TabularOutputColumn[columnNames.length];
    for (int i = 0; i < columnNames.length; i++) {
      columns[i]=new TabularOutputColumn(columnNames[i], ColumnType.STRING);
    }
    newExecutionRecord.out.columnHeader=new TabularOutputColumnHeader(taskId, columns);
    
    for(Object row:rows){
      String[] cells=((String) row).split(DELIMITER);
      newExecutionRecord.out.addRow(taskId, cells);
    }
    appendHistoryRecord(newExecutionRecord);
  }

}
