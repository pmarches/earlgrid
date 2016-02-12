package com.earlgrid.core.shellcommands.tjoin;

import com.earlgrid.core.session.ExecutionHistory;
import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.core.sessionmodel.SessionModel;
import com.earlgrid.core.sessionmodel.TabularOutput;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.shellcommands.tjoin.TJoinExpresssion.ExpressionType;

public class TJoinExpressionEvaluator {
  protected SessionModel model;

  public TJoinExpressionEvaluator(SessionModel model) {
    this.model=model;
  }

  public TabularOutputRow evaluateOneRow(TJoinExpresssion expressionToEvaluate, int expressionEvaluatedAtRowIndex) {
    String rowContent="";
    TabularOutputRow newRow=new TabularOutputRow(model.getNextTaskId(), rowContent);
    return newRow;
  }

//  public String evaluate(TJoinExpresssion expression){
//    if(expression.type==ExpressionType.CONSTANT){
//      return constantExpression;
//    }
//    
//    String expressionPathStr=null;
//    String[] expressionPath=expressionPathStr.split(".");
//    resolveColumnExpresssion(executionHistory, expressionPath);
//    return null;
//  }
//
//  protected void resolveColumnExpresssion(ExecutionHistory executionHistory, String[] expressionPath) {
//    Integer historyNumber=Integer.parseInt(expressionPath[0]);
//    ExecutionHistoryRecord historyRecord = executionHistory.get(historyNumber);
//    if("out".equals(expressionPath[1])==false){
//      throw new RuntimeException("Referencing outputs other than out is not yet implemented");
//    }
//    
//    TabularOutput output = historyRecord.getOut();
//    columnExpression=output.columnHeader.getColumn(expressionPath[2]);
//    if(columnExpression==null){
//      throw new RuntimeException("Column name "+expressionPath[2]+" not found");
//    }
//  }

}
