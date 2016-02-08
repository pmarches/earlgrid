package com.earlgrid.ui.standalone.customterminal;

import java.util.ArrayList;


public class LineBasedViewport {
  LineBasedPanel backingPanel;
  int nbOfVisibleLinesInViewport;
  int currentTopOffsetOfViewport=0;
  
  public LineBasedViewport(LineBasedPanel backingPanel, int numberOfVisibleLines) {
    this.backingPanel=backingPanel;
    this.nbOfVisibleLinesInViewport=numberOfVisibleLines;
  }

  public void setSize(int nbOfVisibleLines) {
    this.nbOfVisibleLinesInViewport=nbOfVisibleLines;
  }

  public int getNumberOfConsumedLinesVisible() {
    return backingPanel.getNumberOfOccupiedLine(currentTopOffsetOfViewport, nbOfVisibleLinesInViewport);
  }

  public LineBasedArea getAreaConsumingLine(int viewPortLineNumber) {
    if(viewPortLineNumber>nbOfVisibleLinesInViewport){
      return null;
    }
    return backingPanel.getAreaOccupyingLine(currentTopOffsetOfViewport+viewPortLineNumber);
  }

  public LineBasedArea[] getVisibleAreas(){
    return backingPanel.getAreasOccupyingLines(currentTopOffsetOfViewport, nbOfVisibleLinesInViewport);
  }

  public void scrollTopOfViewPortToPanelLine(int panelLine) {
    currentTopOffsetOfViewport=panelLine;
  }

  public void scrollToBottom() {
    currentTopOffsetOfViewport=backingPanel.getNumberOfOccupiedLines()-nbOfVisibleLinesInViewport;
    if(currentTopOffsetOfViewport<0){
      currentTopOffsetOfViewport=0;
    }
  }

  public void scrollToTop() {
    currentTopOffsetOfViewport=0;
  }

  @Override
  public String toString() {
    return "LineBasedViewport [nbOfVisibleLines=" + nbOfVisibleLinesInViewport + ", currentPanelLineOffsetAtTop="+ currentTopOffsetOfViewport + "]";
  }

  public int getNumberOfVisibleLinesForArea(LineBasedArea area) {
    final int topOffsetOfArea=backingPanel.getTopLineOffsetForArea(area);
    final int bottomOffsetOfViewport = currentTopOffsetOfViewport+nbOfVisibleLinesInViewport;
    if(topOffsetOfArea>=bottomOffsetOfViewport){
      return 0; //Area is below the viewport
    }
    final int bottomOffsetOfArea=topOffsetOfArea+area.getNumberOfLines();
    if(bottomOffsetOfArea<currentTopOffsetOfViewport){
      return 0; //Area is above the viewport
    }
    //From here we know the area has something visible
    
    int nbVisibleLinesForArea=area.getNumberOfLines();
    if(topOffsetOfArea<currentTopOffsetOfViewport){
      nbVisibleLinesForArea-=(currentTopOffsetOfViewport-topOffsetOfArea);  //Clip above
    }
    if(bottomOffsetOfArea>bottomOffsetOfViewport){
      nbVisibleLinesForArea-=(bottomOffsetOfArea-bottomOffsetOfViewport);  //Clip below
    }

    return nbVisibleLinesForArea;
  }

  public PositionedLineBasedArea[] getPositionedVisibleAreas() {
    ArrayList<PositionedLineBasedArea> visibleAreas=new ArrayList<>();
    
    final int bottomOffsetOfViewport = currentTopOffsetOfViewport+nbOfVisibleLinesInViewport;
    int topOffsetAreaAccumulator=0;
    for(int i=0; i<backingPanel.occupiedAreas.size(); i++){
      LineBasedArea area=backingPanel.occupiedAreas.get(i);
      final int bottomOffsetOfArea=topOffsetAreaAccumulator+area.getNumberOfLines();
      if(bottomOffsetOfArea>currentTopOffsetOfViewport){ //The bottom area is below the top of the viewport, thus visible
        PositionedLineBasedArea positionedArea=new PositionedLineBasedArea(area);
        positionedArea.topOffsetOfArea=topOffsetAreaAccumulator-currentTopOffsetOfViewport+1;
        visibleAreas.add(positionedArea);
      }
      topOffsetAreaAccumulator+=area.getNumberOfLines();
      if(bottomOffsetOfArea>bottomOffsetOfViewport){
        break;
      }
    }
    
    //Shift down the widgets if there is space remining at the bottom of the viewport
    if(topOffsetAreaAccumulator<bottomOffsetOfViewport){
      int nbRowsToShiftDown=bottomOffsetOfViewport-topOffsetAreaAccumulator;
      for(int i=0; i<visibleAreas.size(); i++){
        visibleAreas.get(i).topOffsetOfArea+=nbRowsToShiftDown;
      }
    }

    PositionedLineBasedArea[] positionedAreas=new PositionedLineBasedArea[visibleAreas.size()];
    visibleAreas.toArray(positionedAreas);
    return positionedAreas;
  }

  
}
