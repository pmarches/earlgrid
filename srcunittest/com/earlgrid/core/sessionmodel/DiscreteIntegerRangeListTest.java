package com.earlgrid.core.sessionmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.function.BiFunction;

import org.junit.Test;

public class DiscreteIntegerRangeListTest {
  @Test
  public void testIterator() {
    DiscreteIntegerRangeList range1=DiscreteIntegerRangeList.newFromString("10,1-3,101-102,15,100");
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
  public void testIntersection(){
    DiscreteIntegerRangeList splitRangeList=DiscreteIntegerRangeList.newFromString("2-5,7-10");
    assertEquals(DiscreteIntegerRangeList.newEmpty(), splitRangeList.intersection(DiscreteIntegerRange.newFromString("0-1")));
    assertEquals(DiscreteIntegerRangeList.newFromString("2-5,7"), splitRangeList.intersection(DiscreteIntegerRange.newFromString("1-7")));
    assertEquals(splitRangeList, splitRangeList.intersection(DiscreteIntegerRange.newFromString("2-10")));

    assertEquals(DiscreteIntegerRangeList.newFromString("7-10"), splitRangeList.intersection(DiscreteIntegerRange.newFromString("6-12")));
    assertEquals(DiscreteIntegerRangeList.newEmpty(), splitRangeList.intersection(DiscreteIntegerRange.newFromString("1000")));

    assertRangeListEquals("2-5,7", "2-5,7-10", DiscreteIntegerRangeList::intersection, "1-7");
    assertRangeListEquals("", "1-10", DiscreteIntegerRangeList::intersection, "0,11-100");
    assertRangeListEquals("1-2,4-5,7-8,10", "1-10", DiscreteIntegerRangeList::intersection, "1-2,4-5,7-8,10-11");
    assertRangeListEquals("1-5,7-10", "1-10", DiscreteIntegerRangeList::intersection, "1-5,7-10");
  }

  private void assertRangeListEquals(String expected, String base, BiFunction<DiscreteIntegerRangeList, DiscreteIntegerRangeList, DiscreteIntegerRangeList> functionToApply, String other) {
    DiscreteIntegerRangeList baseRangeList=DiscreteIntegerRangeList.newFromString(base);
    DiscreteIntegerRangeList otherRangeList=DiscreteIntegerRangeList.newFromString(other);
    assertEquals(expected, functionToApply.apply(baseRangeList, otherRangeList).toString());
    assertEquals(base, baseRangeList.toString()); //Ensure the rangeList objects have not been modified
    assertEquals(other, otherRangeList.toString()); //Ensure the rangeList objects have not been modified
  }

  @Test
  public void testMinus() {
    final String BASE="10-20,22,24-26,28";
    assertRangeListEquals("10,12-20,22,24-26,28", BASE, DiscreteIntegerRangeList::minus, "11");
    assertRangeListEquals("10-20,22,24-26", BASE, DiscreteIntegerRangeList::minus, "28");
    assertRangeListEquals("10-19,22,24-26,28", BASE, DiscreteIntegerRangeList::minus, "20");
    assertRangeListEquals("", BASE, DiscreteIntegerRangeList::minus, BASE);
    
    assertRangeListEquals("28", BASE, DiscreteIntegerRangeList::minus, "2-26");
    assertRangeListEquals(BASE, BASE, DiscreteIntegerRangeList::minus, "");
    assertRangeListEquals("10-20,22", BASE, DiscreteIntegerRangeList::minus, "24-30");
    
    assertRangeListEquals("0,3-7", "0-7", DiscreteIntegerRangeList::minus, "1-2");
    assertRangeListEquals(BASE, BASE, DiscreteIntegerRangeList::minus, "");
  }
  
  @Test
  public void testUnion() {
    final String odd="21,23,25,27,29";
    final String even="20,22,24,26,28,30";
    
    assertRangeListEquals("20-30", odd, DiscreteIntegerRangeList::union, even);
    DiscreteIntegerRangeList mergeTest=DiscreteIntegerRangeList.newFromString("20-22,24,25,26,28-30");
    assertEquals("20-22,24-26,28-30", mergeTest.toString());
    assertRangeListEquals("20-30", odd, DiscreteIntegerRangeList::union, "20-22,24-26,28-30");

    assertRangeListEquals("1-3,20-30,100", "20-30", DiscreteIntegerRangeList::union, "1-3,100");
    assertRangeListEquals(odd, odd, DiscreteIntegerRangeList::union, "");
    assertRangeListEquals(even, "", DiscreteIntegerRangeList::union, even);
  }

}
