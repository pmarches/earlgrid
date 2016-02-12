package com.earlgrid.core.sessionmodel;

import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Maps;
import org.codehaus.jparsec.functors.Pair;
import org.codehaus.jparsec.functors.Tuple3;

/**
 * +A+C:F-D+10:20-15
 */
public class TabularOutputSelection {
  static Parser<String> IDENTIFIER=Scanners.IDENTIFIER.or(Scanners.DEC_INTEGER).map(Maps.TO_UPPER_CASE);
  static Parser<String> RANGE_DELIMITER=Scanners.isChar(':').source();
  static Parser<String> ADD_OR_MINUS=Scanners.among("+,-").source();

  static Parser<Tuple3<String, String, String>> RANGE=Parsers.tuple(IDENTIFIER, RANGE_DELIMITER.optional(), IDENTIFIER.optional());

  static Map<Tuple3<Integer, String, Integer>, TabularOutputRowRangeList> toRowRange=new Map<Tuple3<Integer,String,Integer>, TabularOutputRowRangeList>() {
    @Override
    public TabularOutputRowRangeList map(Tuple3<Integer, String, Integer> from) {
      return new TabularOutputRowRangeList();
    }
  };

  static Parser<List<Pair<String, Tuple3<String, String, String>>>> EXPRESSSION=Parsers.tuple(ADD_OR_MINUS, RANGE).many();
  
  static Map<List<Pair<String, Tuple3<String, String, String>>>, TabularOutputSelection> toSelection=new Map<List<Pair<String,Tuple3<String,String,String>>>, TabularOutputSelection>() {
    @Override
    public TabularOutputSelection map(List<Pair<String, Tuple3<String, String, String>>> from) {
      TabularOutputSelection selection = new TabularOutputSelection();
      for(Pair<String, Tuple3<String, String, String>> expression: from){
        boolean additive="-".equals(expression.a)==false;
        Tuple3<String, String, String> rangeStr=expression.b;
        if(Character.isAlphabetic(rangeStr.a.charAt(0))){
          TabularOutputColumnRangeList newColRangeToAppend=TabularOutputColumnRangeList.newFromString(rangeStr.a, rangeStr.c);
          selection.appendColumn(additive, newColRangeToAppend);
        }
        else{
          DiscreteIntegerRangeList newRowRangeToAppend=DiscreteIntegerRangeList.newFromRangeString(rangeStr.a, rangeStr.c);
          selection.appendRow(additive, newRowRangeToAppend);
        }
      }
      return selection;
    }
  };

  static Parser<TabularOutputSelection> SELECTION_EXPRESSION=EXPRESSSION.map(toSelection);
  
  public static TabularOutputSelection newFromString(String selectionExpression) {
    TabularOutputSelection expresssion=SELECTION_EXPRESSION.parse(selectionExpression);
    return expresssion;
  }

  protected DiscreteIntegerRangeList summedRowRange;
  protected TabularOutputColumnRangeList summedColRange;

  public DiscreteIntegerRangeList getRowSelection() {
    return summedRowRange;
  }

  public TabularOutputColumnRangeList getColumnSelection() {
    return summedColRange;
  }
  
  protected void appendColumn(boolean additive, TabularOutputColumnRangeList newColRangeToAppend) {
    if(summedColRange==null){
      if(additive){
        summedColRange=new TabularOutputColumnRangeList();
      }
      else{
        summedColRange=TabularOutputColumnRangeList.newZeroToInfinity();
      }
    }

    if(additive){
      summedColRange.unionThis(newColRangeToAppend);
    }
    else{
      summedColRange.minusThis(newColRangeToAppend);
    }
  }

  public void appendRow(boolean additive, DiscreteIntegerRangeList newRowRangeToAppend){
    if(summedRowRange==null){
      if(additive){
        summedRowRange=new DiscreteIntegerRangeList();
      }
      else{
        summedRowRange=DiscreteIntegerRangeList.newZeroToInfinity();
      }
    }

    if(additive){
      summedRowRange.unionThis(newRowRangeToAppend);
    }
    else{
      summedRowRange.minusThis(newRowRangeToAppend);
    }
  }
}
