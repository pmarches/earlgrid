package com.earlgrid.core.sessionmodel;

import java.util.Iterator;

public class TabularOutputColumnRangeList implements Iterable<Integer> {
  DiscreteIntegerRangeList columnRange=new DiscreteIntegerRangeList();
  
  public static TabularOutputColumnRangeList newFromString(String startColNameStr, String endColNameStr) {
    if(endColNameStr==null){
      endColNameStr=startColNameStr;
    }
    int startIndex=convertColumnNameToIndex(startColNameStr);
    int endIndex=convertColumnNameToIndex(endColNameStr);
    TabularOutputColumnRangeList colRange=new TabularOutputColumnRangeList();
    colRange.columnRange.unionThis(new DiscreteIntegerRange(startIndex, endIndex));
    return colRange;
  }
  
  //0  = A
  //25 = Z
  //26 = AA
  //27 = AB
  static public String convertIndexToColumnName(int columnIndex){
    StringBuilder columnName=new StringBuilder();
    while(columnIndex>0){
      int remainder=columnIndex%26;
      columnIndex/=26;
      columnName.append('A'+remainder);
    }
    return columnName.toString();
  }
  
  static public int convertColumnNameToIndex(String columnName){
    int accumulator=0;
    for(int i=0; i<columnName.length(); i++){
      char colChar=columnName.charAt(i);
      accumulator*=26;
      accumulator+=(colChar-'A'+1);
    }
    return accumulator-1;
  }
  
  @Override
  public String toString() {
    return super.toString();
  }

  public static TabularOutputColumnRangeList newZeroToInfinity() {
    TabularOutputColumnRangeList newRangeList=new TabularOutputColumnRangeList();
    newRangeList.columnRange.unionThis(new DiscreteIntegerRange(0, Integer.MAX_VALUE));
    return newRangeList;
  }

  public TabularOutputColumnRangeList unionThis(TabularOutputColumnRangeList other) {
    columnRange.unionThis(other.columnRange);
    return this;
  }

  public TabularOutputColumnRangeList minusThis(TabularOutputColumnRangeList other) {
    columnRange.minusThis(other.columnRange);
    return this;
  }

  @Override
  public Iterator<Integer> iterator() {
    return columnRange.iterator();
  }

}
