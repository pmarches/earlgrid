package com.earlgrid.core.sessionmodel;

public class TabularOutputAddress {
  protected int taskId;
  protected String outputName;
  protected String columnName; //Range?
  protected int rowIndex; //Range?
  
  protected TabularOutputAddress() {
  }
  
  public static TabularOutputAddress fromString(String addressStr){
    TabularOutputAddress newAddress=new TabularOutputAddress();
    return newAddress;
  }
}
