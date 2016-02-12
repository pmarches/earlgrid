package com.earlgrid.core.sessionmodel;

public class DiscreteIntegerRange implements Comparable<DiscreteIntegerRange> {
  int start=-1; //inclusive
  int end=-1; //inclusive

  public DiscreteIntegerRange(){ //Empty range
  }
  
  public DiscreteIntegerRange(int start, int end){
    if(start>end){
      throw new RuntimeException("The start "+start+" is less than the end "+end);
    }
    this.start=start;
    this.end=end;
  }
  
  public int getCardinality(){
    return end-start+1;
  }
  
  @Override
  public String toString() {
    if(start==end){
      return Integer.toString(start);
    }
    return String.format("%d-%d", start, end);
  }

  @Override
  public int compareTo(DiscreteIntegerRange other) {
    return Integer.compare(start, other.start);
  }

  public static DiscreteIntegerRange newFromString(String startStr, String endStr) {
    if(endStr==null){
      endStr=startStr;
    }
    int start=Integer.parseInt(startStr);
    int end=Integer.parseInt(endStr);
    return new DiscreteIntegerRange(start, end);
  }

  public static <T extends DiscreteIntegerRange> DiscreteIntegerRange newFromString(String range) {
    if(range.trim().isEmpty()){
      return new DiscreteIntegerRange();
    }
    String[] startEndStr=range.split("-");
    if(startEndStr.length==1){
      return DiscreteIntegerRange.newFromString(startEndStr[0], startEndStr[0]);
    }
    return DiscreteIntegerRange.newFromString(startEndStr[0], startEndStr[1]);
  }

  public void unionThis(DiscreteIntegerRange other) {
    if(start>other.start){
      start=other.start;
    }
    if(end<other.end){
      end=other.end;
    }
  }

  public boolean overlaps(DiscreteIntegerRange other) {
    if(other.start<=start){ //other is to our left (or directly over)
      return other.end>=start;
    }
    //Other is to the right of the start
    return other.start<=end;
  }

  public boolean overlapsOrAdjacent(DiscreteIntegerRange other) {
    if(other.start<=start){ //other is to our left (or directly over)
      return other.end+1>=start;
    }
    //Other is to the right of the start
    return other.start<=end+1;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + end;
    result = prime * result + start;
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
    @SuppressWarnings("rawtypes")
    DiscreteIntegerRange other = (DiscreteIntegerRange) obj;
    if (end != other.end)
      return false;
    if (start != other.start)
      return false;
    return true;
  }

  public boolean isFullyIncludedIn(DiscreteIntegerRange other) {
    if(start>=other.start){
      if(end<=other.end){
        return true;
      }
    }
    return false;
  }

}
