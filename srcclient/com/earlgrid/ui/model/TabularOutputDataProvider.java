package com.earlgrid.ui.model;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import com.earlgrid.core.sessionmodel.TabularOutput;


public class TabularOutputDataProvider implements IDataProvider {
  TabularOutput backingOutput;
  
  public TabularOutputDataProvider(TabularOutput out) {
    this.backingOutput=out;
  }

  @Override
  public Object getDataValue(int columnIndex, int rowIndex) {
    return backingOutput.rows.get(rowIndex).getCellAtColumn(columnIndex);
  }

  @Override
  public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public int getColumnCount() {
    return backingOutput.getColumnHeader().size();
  }

  @Override
  public int getRowCount() {
    return backingOutput.rows.size();
  }

}
