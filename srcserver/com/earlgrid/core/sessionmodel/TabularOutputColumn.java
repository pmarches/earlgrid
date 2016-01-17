package com.earlgrid.core.sessionmodel;

import com.earlgrid.core.sessionmodel.TabularOutput.ColumnType;

public class TabularOutputColumn {
  public TabularOutputColumn(String name, ColumnType type) {
    this.name=name;
    this.type=type;
    nbMaxCharacters=name.length();
  }
  public String name;
  public ColumnType type;
  public int nbMaxCharacters;
  
  @Override
  public String toString() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    TabularOutputColumn other = (TabularOutputColumn) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (type != other.type)
      return false;
    return true;
  }
  
  
}