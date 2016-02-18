package com.earlgrid.core.sessionmodel;

import static org.junit.Assert.*;

import org.junit.Test;

public class TabularOutputSelectionTest {
  @Test
  public void testColumnSelection(){
    assertEquals("0-27", TabularOutputColumnRangeList.newFromString("A", "AB").columnRange.toString());
    assertEquals("25", TabularOutputColumnRangeList.newFromString("Z", "Z").columnRange.toString());
  }
  
  @Test
  public void testNewFromString() {
    TabularOutputSelection sel=TabularOutputSelection.newFromString("+A+C:F-D+10:20-15-U");
    assertEquals("A,C,E:F", sel.getColumnSelection().toString());
    assertEquals("10:14,16:20", sel.getRowSelectionString());

    TabularOutputSelection sel2=TabularOutputSelection.newFromString("+A:H-B:C");
    assertEquals("A,D:H", sel2.getColumnSelection().toString());
  }

}
