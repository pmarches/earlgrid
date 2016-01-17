package com.earlgrid.core.sessionmodel;

import java.util.Arrays;
import java.util.List;

import com.earlgrid.core.serverside.EarlGridPb.PbTabularColumnHeaders;
import com.earlgrid.core.sessionmodel.TabularOutput.ColumnType;

public class TabularOutputColumnHeader extends SessionModelChangeEvent {
  TabularOutputColumn[] columns;

  public TabularOutputColumnHeader(int taskId, TabularOutputColumnHeader columnHeader) {
    super(taskId);
    setColumnHeaders(columnHeader.columns);
  }

  public TabularOutputColumnHeader(int taskId, TabularOutputColumn... columns) {
    super(taskId);
    setColumnHeaders(columns);
  }

  public void setColumnHeaders(TabularOutputColumn... columnDesc) {
    if(columnDesc==null){
      columns=null;
    }
    else{
      columns=Arrays.copyOf(columnDesc, columnDesc.length);
    }
  }

  public TabularOutputColumnHeader setColumnHeaders(String... columnNames) {
    TabularOutputColumn[] columns=new TabularOutputColumn[columnNames.length];
    for(int i=0; i<columnNames.length; i++){
      if(columnNames[i].contains(" ")){
        throw new RuntimeException("Column names with spaces not allowed '"+columnNames[i]+"'");
      }
      columns[i]=new TabularOutputColumn(columnNames[i], ColumnType.STRING);
    }
    setColumnHeaders(columns);
    return this;
  }

  public int size() {
    return columns.length;
  }

  public TabularOutputColumn get(int i) {
    return columns[i];
  }

  public TabularOutputColumn getColumn(String columnName) {
    for(TabularOutputColumn col : columns){
      if(col.name.equals(columnName)){
        return col;
      }
    }
    return null;
  }
  
  public int[] resolveColumnNamesToColumnIndexes(List<String> columnNamesToResolve, TabularOutputColumnHeader tabularOutputColumns) {
    int[] columnIndices=new int[columnNamesToResolve.size()];
    for(int i=0; i<columnNamesToResolve.size(); i++){
      String columnNameToResolve=columnNamesToResolve.get(i);
      columnIndices[i]=-1;
      for(int j=0; j<columns.length; j++){
        if(columns[j].name.equals(columnNameToResolve)){
          columnIndices[i]=j;
          break;
        }
      }
      if(columnIndices[i]<0){
        throw new RuntimeException("Failed to resolve column name '"+columnNameToResolve+"' amongst "+columnNamesToResolve.toString());
      }
    }
    return columnIndices;
  }

  @Override
  public String toString() {
    return Arrays.asList(columns).toString();
  }

  public PbTabularColumnHeaders.Builder toProtoBuf() {
    PbTabularColumnHeaders.Builder pb=PbTabularColumnHeaders.newBuilder();
    for (TabularOutputColumn col : columns) {
      pb.addColumnName(col.name);
    }
    return pb;
  }

  public static TabularOutputColumnHeader fromProtoBuf(int taskId, PbTabularColumnHeaders pbHeaders) {
    TabularOutputColumnHeader header=new TabularOutputColumnHeader(taskId);
    header.columns=new TabularOutputColumn[pbHeaders.getColumnNameCount()];
    for(int i=0; i<header.columns.length; i++){
      header.columns[i]=new TabularOutputColumn(pbHeaders.getColumnName(i), ColumnType.STRING);
    }
    return header;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(columns);
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
    TabularOutputColumnHeader other = (TabularOutputColumnHeader) obj;
    if (!Arrays.equals(columns, other.columns))
      return false;
    return true;
  }

  public String[] getColumnNames() {
    String[] ret=new String[columns.length];
    for (int i = 0; i < ret.length; i++) {
      ret[i]=columns[i].name;
    }
    return ret;
  }
}
