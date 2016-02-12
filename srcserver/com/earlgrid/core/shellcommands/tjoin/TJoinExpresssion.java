package com.earlgrid.core.shellcommands.tjoin;

import com.earlgrid.core.sessionmodel.TabularOutputColumn;

public class TJoinExpresssion {
  enum ExpressionType { CONSTANT, COLUMN };
  ExpressionType type;
  String constantExpression;
  TabularOutputColumn columnExpression;
  
  public TJoinExpresssion(String stringExpresssion) {
  }

}
