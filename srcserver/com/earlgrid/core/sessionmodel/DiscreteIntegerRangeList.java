package com.earlgrid.core.sessionmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class DiscreteIntegerRangeList implements Iterable<Integer> {
  protected ArrayList<DiscreteIntegerRange> ranges=new ArrayList<>();

  public DiscreteIntegerRangeList(DiscreteIntegerRangeList source) {
    source.ranges.forEach(r-> ranges.add(new DiscreteIntegerRange(r.start, r.end)));
  }

  public DiscreteIntegerRangeList() {
  }

  public static DiscreteIntegerRangeList newEmpty() {
    return new DiscreteIntegerRangeList();
  }

  @Override
  public String toString() {
    return ranges.stream().map(DiscreteIntegerRange::toString).collect(Collectors.joining(","));
  }

  public void unionThis(DiscreteIntegerRangeList rangeToAdd){
    rangeToAdd.ranges.forEach(r-> unionThis(r));
  }

  public DiscreteIntegerRangeList unionThis(DiscreteIntegerRange rangeToAdd){
    if(rangeToAdd.isEmpty()){
      return this;
    }
    int indexWhereThisRangeStarts=Collections.binarySearch(ranges, rangeToAdd);
    if(indexWhereThisRangeStarts>=0){ //We have an existing Range that starts at this position
      unionThisStartingAtRange(rangeToAdd, indexWhereThisRangeStarts);
      return this;
    }

    /**
     * Possible cases: 
     *  - We have a prior range that overlapsOrAdjacent on the newRange
     *  - Both preceding and succeding overlapOrAdjacent on the newRange (this is the same case as the first one)
     *  - Only the succeding range overlapsOrAdjacent on the newRange
     *  - Neither preceding nor succeding overlapOrAdjacent on the newRange
     */
    int insertionPoint=-(indexWhereThisRangeStarts+1);
    if(insertionPoint!=0){ //No need to bother about the prior range if there is none
      DiscreteIntegerRange priorRange=ranges.get(insertionPoint-1);
      if(priorRange.overlapsOrAdjacent(rangeToAdd)){
        unionThisStartingAtRange(rangeToAdd, insertionPoint-1);
        return this;
      }
    }
    
    if(insertionPoint<ranges.size()){ //No need to bother about the succeding range if we will be adding to the last
      DiscreteIntegerRange succedingRange=ranges.get(insertionPoint);
      if(succedingRange.overlapsOrAdjacent(rangeToAdd)){
        unionThisStartingAtRange(rangeToAdd, insertionPoint);
        return this;
      }
    }
    
    //No overlapOrAdjacent on either side
    ranges.add(insertionPoint, rangeToAdd);
    return this;
  }

  protected void unionThisStartingAtRange(DiscreteIntegerRange otherRange, int indexOfFirstAdjacentRange) {
    DiscreteIntegerRange rangeStartingAtSame = ranges.get(indexOfFirstAdjacentRange);
    rangeStartingAtSame.unionThis(otherRange);
    
    int i=indexOfFirstAdjacentRange+1;
    while(i<ranges.size()){
      DiscreteIntegerRange current=ranges.get(i);
      if(current.overlapsOrAdjacent(otherRange)){
        rangeStartingAtSame.unionThis(current);
        ranges.remove(i);
      }
      else{
        break;
      }
    }
  }

  public DiscreteIntegerRangeList union(DiscreteIntegerRangeList rangeListToAdd) {
    DiscreteIntegerRangeList result=new DiscreteIntegerRangeList(this);
    result.unionThis(rangeListToAdd);
    return result;
  }

  static class DiscreteIntegerRangeListIterator implements Iterator<Integer> {
    DiscreteIntegerRange currentRangeList;
    int currentDiscreteValue=-1;
    Iterator<DiscreteIntegerRange> backingRangeIt;
    DiscreteIntegerRangeList backingRangeList;

    public DiscreteIntegerRangeListIterator(DiscreteIntegerRangeList backingRangeList) {
      this.backingRangeList=backingRangeList;
      backingRangeIt=backingRangeList.ranges.iterator();
    }

    @Override
    public boolean hasNext() {
      if(currentDiscreteValue==-1){
        return backingRangeIt.hasNext();
      }
      if(currentDiscreteValue<currentRangeList.end){
        return true;
      }
      return backingRangeIt.hasNext();
    }

    @Override
    public Integer next() {
      if(hasNext()==false){
        throw new NoSuchElementException();
      }
      if(currentDiscreteValue==-1){
        currentRangeList=backingRangeIt.next();
        currentDiscreteValue=currentRangeList.start;
      }
      else{
        if(currentDiscreteValue<currentRangeList.end){
          currentDiscreteValue++;
        }
        else{
          currentRangeList=backingRangeIt.next();
          currentDiscreteValue=currentRangeList.start;
        }
      }
      return currentDiscreteValue;
    }
  }
  
  @Override
  public Iterator<Integer> iterator() {
    return new DiscreteIntegerRangeListIterator(this);
  }

  public static DiscreteIntegerRangeList newFromRangeString(String startStr, String endStr) {
    DiscreteIntegerRangeList result=new DiscreteIntegerRangeList();
    return result.unionThis(DiscreteIntegerRange.newFromString(startStr, endStr));
  }

  public static DiscreteIntegerRangeList newFromString(String rangeListStr) {
    DiscreteIntegerRangeList result=new DiscreteIntegerRangeList();
    for(String range : rangeListStr.split(",")){
      result.unionThis(DiscreteIntegerRange.newFromString(range));
    }
    return result;
  }
  
  public static DiscreteIntegerRangeList newZeroToInfinity() {
    return new DiscreteIntegerRangeList().unionThis(new DiscreteIntegerRange(0, Integer.MAX_VALUE));
  }


  public DiscreteIntegerRangeList minus(DiscreteIntegerRangeList other) {
    DiscreteIntegerRangeList copy=new DiscreteIntegerRangeList(this);
    copy.minusThis(other);
    return copy;
  }

  public DiscreteIntegerRangeList minusThis(DiscreteIntegerRangeList other) {
    for(DiscreteIntegerRange otherRange:other.ranges){
      minusThis(otherRange);
    }
    return this;
  }

  public DiscreteIntegerRangeList minusThis(DiscreteIntegerRange rangeToRemove) {
    if(rangeToRemove.isEmpty()){
      return this;
    }
    /**
     * Possible cases: 
     *  - We have a preceding range that overlapsOrAdjacent on the newRange
     *  - Both preceding and succeding overlapOrAdjacent on the newRange (this is the same case as the first one)
     *  - Only the succeding range overlapsOrAdjacent on the newRange
     *  - Neither preceding nor succeding overlapOrAdjacent on the newRange
     */
//0:7 - 1:2
    int indexOfFirstRangeThatOverlaps=Collections.binarySearch(ranges, rangeToRemove);
    if(indexOfFirstRangeThatOverlaps<0){
      indexOfFirstRangeThatOverlaps= -(indexOfFirstRangeThatOverlaps+1);
      if(indexOfFirstRangeThatOverlaps>0){
        DiscreteIntegerRange priorRange=ranges.get(indexOfFirstRangeThatOverlaps-1);
        if(priorRange.overlaps(rangeToRemove)){
          indexOfFirstRangeThatOverlaps--;
        }
      }
    }

    for(int i=indexOfFirstRangeThatOverlaps; i<ranges.size(); ){
      DiscreteIntegerRange currentRange=ranges.get(i);
      if(currentRange.overlaps(rangeToRemove)==false){
        break;
      }
      else if(currentRange.isFullyIncludedIn(rangeToRemove)){
        ranges.remove(i);
      }
      else{
        /**
         * 3 cases
         * - other is strictly to the left (can simply trim the start)
         * - other is strictly to the right (can trim the end)
         * - other is in the middle (trim the end, and insert new node)
         */
        boolean otherOnLeftSide=currentRange.overlapsOnLeftOf(rangeToRemove);
        boolean otherOnRightSide=currentRange.overlapsOnRightOf(rangeToRemove);
        if(otherOnLeftSide){
          currentRange.start=rangeToRemove.end+1;
        }
        else if(otherOnRightSide){
          currentRange.end=rangeToRemove.start-1;
        }
        else{
          DiscreteIntegerRange rightPortion=new DiscreteIntegerRange(rangeToRemove.end+1, currentRange.end);
          ranges.add(i+1, rightPortion);
          currentRange.end=rangeToRemove.start-1;
          break;
        }
        i++;
      }
    }

    return this;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((ranges == null) ? 0 : ranges.hashCode());
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
    DiscreteIntegerRangeList other = (DiscreteIntegerRangeList) obj;
    if (ranges == null) {
      if (other.ranges != null)
        return false;
    } else if (!ranges.equals(other.ranges))
      return false;
    return true;
  }
  
  public DiscreteIntegerRangeList intersection(DiscreteIntegerRangeList other) {
    DiscreteIntegerRangeList copy=new DiscreteIntegerRangeList(this);
    copy.intersectionThis(other);
    return copy;
  }
  
  public DiscreteIntegerRangeList intersectionThis(DiscreteIntegerRangeList other) {
    int thisIndex=0;
    int otherIndex=0;
    while(true){
      if(otherIndex>=other.ranges.size() || thisIndex>=ranges.size()){
        break;
      }
      DiscreteIntegerRange thisRange=this.ranges.get(thisIndex);
      DiscreteIntegerRange otherRange=other.ranges.get(otherIndex);

      //TODO Premature optimization: if both are equals, we can increment both counters

      /**
       * Possible cases:
       * - Full overlap (do nothing)
       * - No overlap (remove)
       * - Right overlap (truncate start)
       * - Left overlap (truncate end)
       */
      if(thisRange.overlaps(otherRange)==false){
        ranges.remove(thisIndex);
        continue;
      }

      if(thisRange.isFullyIncludedIn(otherRange)){
        thisIndex++;
        continue;
      }
      
      if(thisRange.start<otherRange.start){
        thisRange.start=otherRange.start;
      }
      if(thisRange.end>otherRange.end){
        //When truncating, the discarded portion may still fit in the next otherRange. Do not discard, but create a remainder range that will be processed in the next loop
        DiscreteIntegerRange remainder=new DiscreteIntegerRange(otherRange.end+2, thisRange.end);
        thisRange.end=otherRange.end;
        ranges.add(thisIndex+1, remainder);
        thisIndex++;
        otherIndex++;
        continue;
      }
      
      //We need to increment either thisIndex or otherIndex depending on which start/end pair occurs next
      if(thisRange.start<otherRange.start || thisRange.end<otherRange.end){
        thisIndex++;
      }
      else{
        otherIndex++;
      }
      
    }
    
    //Remove anything remaining
    while(thisIndex<ranges.size()){
      ranges.remove(ranges.size()-1);
    }
    
    return this;
  }

  public DiscreteIntegerRangeList intersectionThis(DiscreteIntegerRange otherRange) {
    if(otherRange.isEmpty()){
      ranges.clear();
      return this;
    }

    for(int i=0; i<ranges.size();){ //i does not increment every time
      DiscreteIntegerRange thisRange=ranges.get(i);
      if(thisRange.overlaps(otherRange)==false){
        ranges.remove(i);
        continue;
      }
      i++;
      
      if(thisRange.start<otherRange.start){
        thisRange.start=otherRange.start;
      }
      if(thisRange.end>otherRange.end){
        thisRange.end=otherRange.end;
      }
    }
    return this;
  }

  public DiscreteIntegerRangeList intersection(DiscreteIntegerRange otherRange) {
    DiscreteIntegerRangeList copy=new DiscreteIntegerRangeList(this);
    copy.intersectionThis(otherRange);
    return copy;
  }

  
}
