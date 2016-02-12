package com.earlgrid.core.shellcommands.coreutils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Pattern;

import com.earlgrid.core.sessionmodel.CmdBeginStatus;
import com.earlgrid.core.sessionmodel.CmdExitStatus;
import com.earlgrid.core.sessionmodel.TabularOutput;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class TabCmdSpecification extends BaseCmdSpecification<TabCmdArguments> {
  protected TabularOutput outputCollector;

  @Override
  public void onUpstreamCommandBegun(CmdBeginStatus parentCommandBegun) throws Exception {
    outputCollector=new TabularOutput(taskId);
  }
  
  @Override
  public void onUpstreamCommandColumnHeader(TabularOutputColumnHeader parentColumnHeader) {
    outputCollector.columnHeader=parentColumnHeader;
  }
  
  @Override
  public void onUpstreamCommandOutputRow(TabularOutputRow rowFromParent) throws Exception {
    outputCollector.rows.add(rowFromParent);
  }
  
  @Override
  public void onUpstreamCommandFinished(CmdExitStatus input) throws Exception {
    int numberOfColumns;
    LinkedList<String[]> resultingLines=new LinkedList<>();
    if(args.columnWidthStr!=null){
      numberOfColumns=tabulateOnColumnWidth(resultingLines);
    }
    else if(args.wellAligned){
      numberOfColumns=tabulateOnWellAlignedWhiteSpaceDelimiter(resultingLines);
    }
    else {
      numberOfColumns=tabulateOnRegexDelimiter(resultingLines);
    }

    String[] generatedColumnNames=new String[numberOfColumns];
    for(int i=0; i<generatedColumnNames.length; i++){
      generatedColumnNames[i]=String.format("%c", 'A'+i);
    }

    if(args.hasHeaderRow){
      String[] inputRowToBeUsedAsHeader = resultingLines.removeFirst();
      for(int i=0; i<inputRowToBeUsedAsHeader.length; i++){
        if(inputRowToBeUsedAsHeader[i]!=null){
          generatedColumnNames[i]=inputRowToBeUsedAsHeader[i];
        }
      }
    }

    emit(new TabularOutputColumnHeader(taskId).setColumnHeaders(generatedColumnNames));
    for(String[] splitLine : resultingLines){
      emit(new TabularOutputRow(taskId, splitLine));
    }
  }

  private int tabulateOnWellAlignedWhiteSpaceDelimiter(LinkedList<String[]> resultingLines) throws Exception {
    int numberOfColumns;
    //Auto detect the columns that have the same value for each character column
    char[] referenceLine=null;
    ArrayList<Integer> columnDelimiterCandidates=new ArrayList<>();

    int nbInputRowsToBeRead=outputCollector.getRowCount();
    int inputRowIndex=0;
    if(args.hasHeaderRow){
      inputRowIndex++;
    }
    for(; inputRowIndex<nbInputRowsToBeRead; inputRowIndex++){
      String[] inputRow=outputCollector.getRow(inputRowIndex).getAllCells();
      if(inputRow.length!=1){
        throw new Exception("Input row does not have exactly 1 column, but "+inputRow.length);
      }
      String line=inputRow[0];
      if(referenceLine==null){
        referenceLine=line.toCharArray();
        columnDelimiterCandidates.ensureCapacity(referenceLine.length);
        for(int i=0; i<referenceLine.length; i++){
          if(Character.isWhitespace(referenceLine[i])){
            columnDelimiterCandidates.add(i);
          }
        }
      }
      else{
        for(int columnCandidateIndex=0; columnCandidateIndex<columnDelimiterCandidates.size(); columnCandidateIndex++){
          int delimiterPosition=columnDelimiterCandidates.get(columnCandidateIndex);
          if(delimiterPosition>=line.length() || referenceLine[delimiterPosition]!=line.charAt(delimiterPosition)){
            columnDelimiterCandidates.remove(columnCandidateIndex);
            columnCandidateIndex--;
          }
        }
      }
    }
    mergeAdjacentOffsets(columnDelimiterCandidates);

    inputRowIndex=0;
    for(; inputRowIndex<nbInputRowsToBeRead; inputRowIndex++){
      String[] inputRow=outputCollector.getRow(inputRowIndex).getAllCells();
      String[] outputRowStr=splitStringByOffsetList(inputRow[0], columnDelimiterCandidates);
      resultingLines.add(outputRowStr);
    }
    numberOfColumns=columnDelimiterCandidates.size()+1;
    return numberOfColumns;
  }

  private void mergeAdjacentOffsets(ArrayList<Integer> columnDelimiterCandidates) {
    for(int i=0; i<columnDelimiterCandidates.size()-1; i++){
      if(columnDelimiterCandidates.get(i)+1==columnDelimiterCandidates.get(i+1)){
        columnDelimiterCandidates.remove(i);
        i--;
      }
    }
  }

  protected String[] splitStringByOffsetList(String line, ArrayList<Integer> offsetList) {
    String[] splitLine=new String[offsetList.size()+1];
    int previousOffset=0;
    int i=0;
    for(; i<offsetList.size(); i++){
      int offset=offsetList.get(i);
      if(offset>line.length()){
        offset=line.length();
      }
      splitLine[i]=line.substring(previousOffset, offset).trim();
      previousOffset=offset; //TODO Skip the delimiter?
      if(previousOffset>=line.length()){
        return splitLine;
      }
    }
    splitLine[i]=line.substring(previousOffset, line.length()).trim();
    return splitLine;
  }

  protected int tabulateOnColumnWidth(LinkedList<String[]> resultingLines) throws Exception {
    int wildCardColumnIndex=-1;
    String[] columnWidthsStr=args.columnWidthStr.split(",");
    int sumOfAllColumnWidth=0;
    int[] columnWidth=new int[columnWidthsStr.length];
    for(int i=0; i<columnWidthsStr.length; i++){
      try {
        columnWidth[i]=Integer.parseInt(columnWidthsStr[i]);
        sumOfAllColumnWidth+=columnWidth[i];
      } catch (NumberFormatException e) {
        wildCardColumnIndex=i;
      }
    }

    int nbInputRowsToBeRead=outputCollector.getRowCount();
    for(int i=0; i<nbInputRowsToBeRead; i++){
      String[] inputRow=outputCollector.getRow(i).getAllCells();
      if(inputRow.length!=1){
        throw new Exception("Input row does not have exactly 1 column, but "+inputRow.length);
      }
      String line=inputRow[0];
      if(wildCardColumnIndex!=-1){
        columnWidth[wildCardColumnIndex]=line.length()-sumOfAllColumnWidth;
      }
      String[] outputRowStr=splitStringByColumnWidth(line, columnWidth);
      resultingLines.add(outputRowStr);
    }
    return columnWidth.length;
  }

  protected String[] splitStringByColumnWidth(String line, int[] columnWidth) {
    String[] splitString=new String[columnWidth.length];
    int beginIndex=0;
    for(int i=0; i<columnWidth.length; i++){
      int endIndex=beginIndex+columnWidth[i];
      if(endIndex>line.length()){
        splitString[i]=line.substring(beginIndex, line.length());
        break;
      }
      else{
        splitString[i]=line.substring(beginIndex, endIndex);
      }
      beginIndex=endIndex;
    }
    return splitString;
  }

  protected int tabulateOnRegexDelimiter(LinkedList<String[]> resultingLines) throws Exception {
    Pattern rowRegex=Pattern.compile(args.regexDelimiterStr);

    int maxNumberOfColumns=0;
    int nbInputRowsToBeRead=outputCollector.getRowCount();
    for(int i=0; i<nbInputRowsToBeRead; i++){
      String[] inputRow=outputCollector.getRow(i).getAllCells();
      if(inputRow.length!=1){
        throw new Exception("Input row does not have exactly 1 column, but "+inputRow.length);
      }
      String[] outputRowStr=rowRegex.split(inputRow[0]);
      maxNumberOfColumns=Math.max(maxNumberOfColumns, outputRowStr.length);
      resultingLines.add(outputRowStr);
    }
    return maxNumberOfColumns;
  }

}
