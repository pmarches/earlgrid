package com.earlgrid.ui.standalone.customterminal;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * TODO The for loops can be optimized from O(n) to O(log n) by keeping the cumulative sums of numberOfLines.
 */
public class LineBasedPanel {
  ArrayList<LineBasedArea> occupiedAreas=new ArrayList<>();
  
  public void occupyArea(LineBasedArea lineBasedArea) {
    occupiedAreas.add(lineBasedArea);
  }

  public int getNumberOfOccupiedLines() {
    return occupiedAreas.stream().collect(Collectors.summingInt(LineBasedArea::getNumberOfLines));
  }

  public int getNumberOfOccupiedLine(int startingLineOffset, int maxNbOfLinesToCount) {
    if(occupiedAreas.isEmpty()){
      return 0;
    }
    int topAreaOccupyingStartingLineNumber=getIndexOfAreaOccupyingLineNumber(startingLineOffset);
    int nbOccupiedLinesAfterTopArea=0;
    for (int i=topAreaOccupyingStartingLineNumber; i < occupiedAreas.size(); i++) {
      LineBasedArea area=occupiedAreas.get(i);
      nbOccupiedLinesAfterTopArea+=area.getNumberOfLines();
      if(nbOccupiedLinesAfterTopArea>maxNbOfLinesToCount){
        return maxNbOfLinesToCount;
      }
    }
    return nbOccupiedLinesAfterTopArea;
  }

  public int getIndexOfAreaOccupyingLineNumber(int occupiedLineOffsetToSearch) {
    int cumulativeNbOfLines=0;
    for (int i = 0; i < occupiedAreas.size(); i++) {
      LineBasedArea area=occupiedAreas.get(i);
      cumulativeNbOfLines+=area.getNumberOfLines();
      if(cumulativeNbOfLines>occupiedLineOffsetToSearch){
        return i;
      }
    }
    return occupiedAreas.size()-1;
  }

  public LineBasedArea getAreaOccupyingLine(int panelLineNumber) {
    int areaIndex=getIndexOfAreaOccupyingLineNumber(panelLineNumber);
    return occupiedAreas.get(areaIndex);
  }

  public LineBasedArea[] getAreasOccupyingLines(int startingLineOffset, int maxNbOfLinesToCount) {
    if(occupiedAreas.isEmpty()){
      return new LineBasedArea[]{};
    }
    
    ArrayList<LineBasedArea> areasSelected=new ArrayList<>();
    int nbLinesBeforeStartingArea=0;
    int areaOffset=0;
    for(; areaOffset< occupiedAreas.size(); areaOffset++) {
      LineBasedArea area=occupiedAreas.get(areaOffset);
      nbLinesBeforeStartingArea+=area.getNumberOfLines();
      if(nbLinesBeforeStartingArea>startingLineOffset){
        areasSelected.add(area);
        areaOffset++;
        break;
      }
    }

    int nbOccupiedLinesAfterTopArea=nbLinesBeforeStartingArea-startingLineOffset;
    for(; areaOffset < occupiedAreas.size(); areaOffset++) {
      LineBasedArea area=occupiedAreas.get(areaOffset);
      areasSelected.add(area);
      nbOccupiedLinesAfterTopArea+=area.getNumberOfLines();
      if(nbOccupiedLinesAfterTopArea>=maxNbOfLinesToCount){
        break;
      }
    }

    LineBasedArea[] ret=new LineBasedArea[areasSelected.size()];
    areasSelected.toArray(ret);
    return ret;
  }

  public int getTopLineOffsetForArea(LineBasedArea areaOfInterest) {
    int cumulativeNbOfLines=0;
    for (int i = 0; i < occupiedAreas.size(); i++) {
      LineBasedArea area=occupiedAreas.get(i);
      if(area==areaOfInterest){
        return cumulativeNbOfLines;
      }
      cumulativeNbOfLines+=area.getNumberOfLines();
    }
    throw new RuntimeException("The area "+areaOfInterest+" was not found in the "+getClass().getSimpleName()+" instance");
  }

  public void freeArea(int taskId) {
    occupiedAreas.removeIf(area -> area.getRecord().taskId==taskId);
  }

  public void freeAllAreas() {
    occupiedAreas.clear();
  }
}
