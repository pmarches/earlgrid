package com.earlgrid.core.sessionmodel;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

public class DiscreteIntegerRangeListTest {
  @Test
  public void testIterator() {
    DiscreteIntegerRangeList range1=DiscreteIntegerRangeList.newFromRangeListString("10,1-3,101-102,15,100");
    Iterator<Integer> it1 = range1.iterator();
    assertTrue(it1.hasNext());
    assertEquals(1, it1.next().intValue());
    assertEquals(2, it1.next().intValue());
    assertEquals(3, it1.next().intValue());
    assertEquals(10, it1.next().intValue());
    assertEquals(15, it1.next().intValue());
    assertEquals(100, it1.next().intValue());
    assertEquals(101, it1.next().intValue());
    assertEquals(102, it1.next().intValue());
    assertFalse(it1.hasNext());
  }

  @Test
  public void testMinus() {
    DiscreteIntegerRangeList base=DiscreteIntegerRangeList.newFromRangeListString("10-20,22,24-26,28");
    DiscreteIntegerRangeList eleven=DiscreteIntegerRangeList.newFromRangeListString("11");
    DiscreteIntegerRangeList twentyEight=DiscreteIntegerRangeList.newFromRangeListString("28");
    DiscreteIntegerRangeList twenty=DiscreteIntegerRangeList.newFromRangeListString("20");
    assertEquals("10,12-20,22,24-26,28", base.minus(eleven).toString());
    assertEquals("10-20,22,24-26", base.minus(twentyEight).toString());
    assertEquals("10-19,22,24-26,28", base.minus(twenty).toString());
    assertEquals("", base.minus(base).toString());
    
    assertEquals(twentyEight, base.minus(DiscreteIntegerRangeList.newFromRangeListString("2-26")));
    assertEquals(base, base.minus(DiscreteIntegerRangeList.newFromRangeListString("")));
    assertEquals("10-20,22", base.minus(DiscreteIntegerRangeList.newFromRangeListString("24-30")).toString());
    
    assertEquals("0,3-7", DiscreteIntegerRangeList.newFromRangeListString("0-7").minus(DiscreteIntegerRangeList.newFromRangeListString("1-2")).toString());
  }
  
  @Test
  public void testUnion() {
    final String ODD_STR="21,23,25,27,29";
    DiscreteIntegerRangeList odd=DiscreteIntegerRangeList.newFromRangeListString(ODD_STR);
    assertEquals(ODD_STR, odd.toString());
    
    DiscreteIntegerRangeList even=DiscreteIntegerRangeList.newFromRangeListString("20,22,24,26,28,30");
    DiscreteIntegerRangeList twentyToTirthy=DiscreteIntegerRangeList.newFromRangeListString("20-30");
    assertEquals(twentyToTirthy, odd.union(even));

    DiscreteIntegerRangeList mixed=DiscreteIntegerRangeList.newFromRangeListString("20-22,24,25,26,28-30");
    assertEquals("20-22,24-26,28-30", mixed.toString());
    assertEquals(twentyToTirthy, odd.union(mixed));

    DiscreteIntegerRangeList outliers=DiscreteIntegerRangeList.newFromRangeListString("1-3,100");
    assertEquals("1-3,20-30,100", twentyToTirthy.union(outliers).toString());

    assertEquals(odd, odd.union(new DiscreteIntegerRangeList()));
  }

}
