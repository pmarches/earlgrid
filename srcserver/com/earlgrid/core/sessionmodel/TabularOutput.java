package com.earlgrid.core.sessionmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabularOutput {
  public enum ColumnType { //Per column
    STRING,
    NUMBER,
    DATETIME,
  };

  public TabularOutputColumnHeader columnHeader;
  public ArrayList<TabularOutputRow> rows=new ArrayList<>();

  public TabularOutput(int taskId) {
    columnHeader=new TabularOutputColumnHeader(taskId);
  }

  public TabularOutputRow addRow(int taskId, String... cellValue) {
    if(this.columnHeader==null){
      throw new RuntimeException("You must set the column headers before calling newRow()");
    }
    
    if(cellValue.length>this.columnHeader.size()){
      throw new RuntimeException("Too many columns in row. Got "+cellValue.length+" columns but was expecting "+this.columnHeader.size()+" columns");
    }
    TabularOutputRow row=new TabularOutputRow(taskId, cellValue);
    rows.add(row);
    return row;
  }
  
  @Override
  public String toString() {
    StringBuffer ret=new StringBuffer();
    ret.append(Arrays.asList(columnHeader));
    ret.append(System.lineSeparator());

    for(TabularOutputRow row : rows){
      ret.append(Arrays.asList(row));
      ret.append(System.lineSeparator());
    }
    return ret.toString();
  }

  public TabularOutputColumnHeader getColumnHeader() {
    return columnHeader;
  }

  public List<String> getCellsInColumn(int columnIndex) {
    if(columnIndex >= columnHeader.size()){
      throw new RuntimeException("Column index "+columnIndex+" is out of bounds, size is "+columnHeader.size());
    }
    ArrayList<String> ret=new ArrayList<>(rows.size());
    for(TabularOutputRow row : rows){
      ret.add(row.cells[columnIndex]);
    }
    return ret;
  }

  public TabularOutputRow getRow(int rowIndex) {
    return rows.get(rowIndex);
  }

  public String[] getColumnHeadersAsString() {
    String[] headerText=new String[columnHeader.size()];
    for(int i=0; i<headerText.length; i++){
      headerText[i]=columnHeader.get(i).name;
    }
    return headerText;
  }

  public int getRowCount() {
    return rows.size();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((columnHeader == null) ? 0 : columnHeader.hashCode());
    result = prime * result + ((rows == null) ? 0 : rows.hashCode());
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
    TabularOutput other = (TabularOutput) obj;
    if (columnHeader == null) {
      if (other.columnHeader != null)
        return false;
    } else if (!columnHeader.equals(other.columnHeader))
      return false;
    if (rows == null) {
      if (other.rows != null)
        return false;
    } else if (!rows.equals(other.rows))
      return false;
    return true;
  }

}
