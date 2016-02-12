package com.earlgrid.core.shellcommands.coreutils;

import java.io.File;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import com.earlgrid.core.sessionmodel.CmdBeginStatus;
import com.earlgrid.core.sessionmodel.CmdExitStatus;
import com.earlgrid.core.sessionmodel.TabularOutputColumn;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class WriteCmdSpecification extends BaseCmdSpecification<WriteCmdArguments> {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(WriteCmdSpecification.class);
  static final String OUTPUT_TABLE_NAME = "OUTPUT";
  static final String OUTPUT_ORDER_IDX = "OUTPUT_ORDER_IDX";
  SqlJetDb db;
  int rowCounter=0;
  
  
  @Override
  public void onUpstreamCommandBegun(CmdBeginStatus parentCommandBegun) throws Exception {
    File outputFile=new File(args.outputFilePath);
    outputFile.delete();
    db=SqlJetDb.open(outputFile, true);
    db.getOptions().setAutovacuum(true);
    db.beginTransaction(SqlJetTransactionMode.WRITE);
    createSchema();
  }
  
  private void createSchema() throws SqlJetException {
    db.getOptions().setUserVersion(1);
//    db.createTable("CREATE TABLE "+OUTPUT_TABLE_NAME+"(OID INT NOT NULL) ");
//    db.createTable("CREATE TABLE "+OUTPUT_CELL_TABLE_NAME+"(OID INT NOT NULL, COLUMN INT, CELL_CONTENT STRING) ");
  }

  @Override
  public void onUpstreamCommandFinished(CmdExitStatus input) throws Exception {
    db.createIndex("CREATE INDEX "+OUTPUT_ORDER_IDX+" ON "+OUTPUT_TABLE_NAME+"(OUTPUT_ROW_ORDERING)");
    db.commit();
    db.close();
    db=null;
    super.onUpstreamCommandFinished(input);
  }
  
  @Override
  public void onUpstreamCommandColumnHeader(TabularOutputColumnHeader parentColumnHeader) throws Exception {
    //FIXME Putting the header names directly in the SQL table forces us to forbid certain column names
    StringBuffer createSQL=new StringBuffer("CREATE TABLE "+OUTPUT_TABLE_NAME+"(OUTPUT_ROW_ORDERING INT,");
    for(int i=0; i<parentColumnHeader.size(); i++){
      TabularOutputColumn column = parentColumnHeader.get(i);
      createSQL.append(column.name);
      createSQL.append(" STRING,");
    }
    createSQL.setCharAt(createSQL.length()-1, ')');
    log.debug(createSQL);
    db.createTable(createSQL.toString());
  }
  
  @Override
  public void onUpstreamCommandOutputRow(TabularOutputRow outputRow) throws Exception {
    ISqlJetTable outputTable = db.getTable(OUTPUT_TABLE_NAME);
    Object[] rowContent=new Object[outputRow.size()+1];
    rowContent[0]=rowCounter;
    for(int i=0; i<outputRow.size(); i++){
      rowContent[i+1]=outputRow.getCellAtColumn(i);
    }
    outputTable.insert(rowContent);
    
    rowCounter++;
  }
}
