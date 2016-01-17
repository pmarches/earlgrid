package com.earlgrid.core.shellcommands;

import java.io.File;
import java.util.List;

import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnDef;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import com.earlgrid.core.sessionmodel.TabularOutputColumn;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TabularOutput.ColumnType;

public class ReadCmdSpecification extends BaseCmdSpecification<ReadCmdArguments> {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(ReadCmdSpecification.class);
  @Override
  public void onThisCommandExecute() throws Exception {
    File inputFile=new File(args.inputFilePath);
    SqlJetDb db = SqlJetDb.open(inputFile, false);
    db.beginTransaction(SqlJetTransactionMode.READ_ONLY);

    ISqlJetTable outputTable = db.getTable(WriteCmdSpecification.OUTPUT_TABLE_NAME);
    List<ISqlJetColumnDef> columnNames=outputTable.getDefinition().getColumns();
    TabularOutputColumn[] columns=new TabularOutputColumn[columnNames.size()-1];
    for(int i=1; i<columnNames.size(); i++){
      columns[i-1]=new TabularOutputColumn(columnNames.get(i).getName(), ColumnType.STRING);
    }
    emit(new TabularOutputColumnHeader(taskId, columns));

    ISqlJetCursor cursor = outputTable.order(WriteCmdSpecification.OUTPUT_ORDER_IDX);
    while(cursor.eof()==false){
      String[] cells=new String[columnNames.size()-1];
      for(int i=1; i<columnNames.size(); i++){
        cells[i-1]=cursor.getString(i);
      }
      emit(new TabularOutputRow(taskId, cells));
      cursor.next();
    }
    db.commit();
    db.close();
  }
  
}
