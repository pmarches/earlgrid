package com.earlgrid.core.sessionmodel;

import java.util.Iterator;
import java.util.stream.Collectors;

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
  //28 = AC
  static public String convertIndexToColumnName(int columnIndex){
    StringBuilder columnName=new StringBuilder();
    columnIndex++;
    while(columnIndex>0){
      int remainder=columnIndex%26;
      columnName.insert(0, (char) ('A'+remainder-1));
      columnIndex=(columnIndex-remainder)/26;
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
  
  protected static String convertIntegerRangeToColumNameRange(DiscreteIntegerRange integerRange){
    if(integerRange.start==integerRange.end){
      return convertIndexToColumnName(integerRange.start);
    }
    return convertIndexToColumnName(integerRange.start)+":"+convertIndexToColumnName(integerRange.end);
  }
  
  @Override
  public String toString() {
    return columnRange.ranges.stream().map(TabularOutputColumnRangeList::convertIntegerRangeToColumNameRange).collect(Collectors.joining(","));
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

  public TabularOutputColumnRangeList intersectionThis(DiscreteIntegerRangeList other) {
    columnRange.intersectionThis(other);
    return this;
  }

  public TabularOutputColumnRangeList intersectionThis(DiscreteIntegerRange other) {
    columnRange.intersectionThis(other);
    return this;
  }

}
