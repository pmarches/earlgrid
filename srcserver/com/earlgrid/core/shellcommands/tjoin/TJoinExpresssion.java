package com.earlgrid.core.shellcommands.tjoin;

import com.earlgrid.core.session.ExecutionHistory;
import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.core.sessionmodel.TabularOutput;
import com.earlgrid.core.sessionmodel.TabularOutputColumn;

public class TJoinExpresssion {
  enum ExpressionType { CONSTANT, COLUMN };
  ExpressionType type;
  String constantExpression;
  TabularOutputColumn columnExpression;
  
  public String evaluate(ExecutionHistory executionHistory){
    if(type==ExpressionType.CONSTANT){
      return constantExpression;
    }
    
    String expressionPathStr=null;
    String[] expressionPath=expressionPathStr.split(".");
    resolveColumnExpresssion(executionHistory, expressionPath);
    return null;
  }

  protected void resolveColumnExpresssion(ExecutionHistory executionHistory, String[] expressionPath) {
    Integer historyNumber=Integer.parseInt(expressionPath[0]);
    ExecutionHistoryRecord historyRecord = executionHistory.get(historyNumber);
    if("out".equals(expressionPath[1])==false){
      throw new RuntimeException("Referencing outputs other than out is not yet implemented");
    }
    
    TabularOutput output = historyRecord.getOut();
    columnExpression=output.columnHeader.getColumn(expressionPath[2]);
    if(columnExpression==null){
      throw new RuntimeException("Column name "+expressionPath[2]+" not found");
    }
  }
}
