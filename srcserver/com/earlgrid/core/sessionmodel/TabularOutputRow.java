package com.earlgrid.core.sessionmodel;

import java.util.Arrays;

import com.earlgrid.core.serverside.EarlGridPb.PbTabularRow;


public class TabularOutputRow extends SessionModelChangeEvent {
  String[] cells;
  
  public TabularOutputRow(int taskId, String... rowContent) {
    super(taskId);
    cells=new String[rowContent.length];
    System.arraycopy(rowContent, 0, cells, 0, rowContent.length);
  }

  public int size() {
    return cells.length;
  }

  public String getCellAtColumn(int columnIndex){
    if(columnIndex>=cells.length){
      return "";
    }
    return cells[columnIndex];
  }
  
  @Override
  public String toString() {
    return Arrays.asList(cells).toString();
  }

  public PbTabularRow.Builder toProtoBuf() {
    PbTabularRow.Builder rowBuilder=PbTabularRow.newBuilder();
    for(String cell : cells){
      rowBuilder.addCells(cell);
    }
    return rowBuilder;
  }

  public static TabularOutputRow fromProtoBuf(int taskId, PbTabularRow rows) {
    String[] cells=new String[rows.getCellsCount()];
    for(int i=0; i<rows.getCellsCount(); i++){
      cells[i]=rows.getCells(i);
    }
    return new TabularOutputRow(taskId, cells);
  }
  

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(cells);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TabularOutputRow other = (TabularOutputRow) obj;
    if (!Arrays.equals(cells, other.cells))
      return false;
    return true;
  }

  public String[] getAllCells() {
    return cells;
  }

}
