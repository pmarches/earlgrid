package com.earlgrid.ui.standalone.customterminal;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.earlgrid.core.shellcommands.MockCmdSpecification;
import com.earlgrid.ui.standalone.customterminal.LineBasedArea;
import com.earlgrid.ui.standalone.customterminal.LineBasedPanel;
import com.earlgrid.ui.standalone.customterminal.LineBasedViewport;
import com.earlgrid.ui.standalone.customterminal.PositionedLineBasedArea;

public class LineBasedViewportTest {
  @Test
  public void testLargeCommand(){
    final int NB_LINES_OF_VIEWPORT=3;
    final LineBasedArea AREA0_TO_1=new LineBasedArea(MockCmdSpecification.createMockExecutionRecord(0, 1, 1));
    final LineBasedArea AREA2_TO_5=new LineBasedArea(MockCmdSpecification.createMockExecutionRecord(0, 1, 5));
    LineBasedPanel panel=new LineBasedPanel();
    LineBasedViewport viewPort=new LineBasedViewport(panel, NB_LINES_OF_VIEWPORT);
    panel.occupyArea(AREA0_TO_1);
    panel.occupyArea(AREA2_TO_5);
    PositionedLineBasedArea[] positionedAreas=viewPort.getPositionedVisibleAreas();
    assertEquals(2, positionedAreas.length);
    assertEquals(0, positionedAreas[0].topOffsetOfArea);
    assertEquals(2, positionedAreas[1].topOffsetOfArea);

    viewPort.scrollTopOfViewPortToPanelLine(1);
    positionedAreas=viewPort.getPositionedVisibleAreas();
    assertEquals(2, positionedAreas.length);
    assertEquals(-1, positionedAreas[0].topOffsetOfArea);
    assertEquals(1,  positionedAreas[1].topOffsetOfArea);

    viewPort.scrollTopOfViewPortToPanelLine(2);
    positionedAreas=viewPort.getPositionedVisibleAreas();
    assertEquals(1, positionedAreas.length);
    assertEquals(0, positionedAreas[0].topOffsetOfArea);

    viewPort.scrollTopOfViewPortToPanelLine(3);
    positionedAreas=viewPort.getPositionedVisibleAreas();
    assertEquals(1, positionedAreas.length);
    assertEquals(-1, positionedAreas[0].topOffsetOfArea);

    viewPort.scrollTopOfViewPortToPanelLine(4);
    positionedAreas=viewPort.getPositionedVisibleAreas();
    assertEquals(1, positionedAreas.length);
    assertEquals(-2, positionedAreas[0].topOffsetOfArea);
    //The issue is that the widgets are drawn anchored to the bottom
  }
  
  @Test
  public void testViewport(){
    final int NB_LINES_OF_VIEWPORT=25;
    final LineBasedArea AREA0_TO_4=new LineBasedArea(MockCmdSpecification.createMockExecutionRecord(0, 1, 4)); //4 rows plus the command makes 5 lines
    final LineBasedArea AREA5_TO_14=new LineBasedArea(MockCmdSpecification.createMockExecutionRecord(0, 1, 9)); //9 rows+1 command=10 lines
    final LineBasedArea AREA15_TO_24=new LineBasedArea(MockCmdSpecification.createMockExecutionRecord(0, 1, 9));
    final LineBasedArea AREA25_TO_34=new LineBasedArea(MockCmdSpecification.createMockExecutionRecord(0, 1, 9));
    final LineBasedArea AREA35_TO_39=new LineBasedArea(MockCmdSpecification.createMockExecutionRecord(0, 1, 4));

    LineBasedPanel panel=new LineBasedPanel();
    LineBasedViewport viewPort=new LineBasedViewport(panel, NB_LINES_OF_VIEWPORT);
    assertEquals(0, viewPort.getNumberOfConsumedLinesVisible());

    panel.occupyArea(AREA0_TO_4);
    assertEquals(5, viewPort.getNumberOfConsumedLinesVisible());
    assertEquals(5, viewPort.getNumberOfVisibleLinesForArea(AREA0_TO_4));
    panel.occupyArea(AREA5_TO_14);
    assertEquals(15, viewPort.getNumberOfConsumedLinesVisible());
    panel.occupyArea(AREA15_TO_24);
    assertEquals(NB_LINES_OF_VIEWPORT, viewPort.getNumberOfConsumedLinesVisible());
    panel.occupyArea(AREA25_TO_34);
    assertEquals(NB_LINES_OF_VIEWPORT, viewPort.getNumberOfConsumedLinesVisible());
    panel.occupyArea(AREA35_TO_39);
    assertEquals(0, viewPort.getNumberOfVisibleLinesForArea(AREA35_TO_39));
    
    assertSame(AREA0_TO_4, viewPort.getAreaConsumingLine(0));
    assertSame(AREA0_TO_4, viewPort.getAreaConsumingLine(4));
    assertSame(AREA5_TO_14, viewPort.getAreaConsumingLine(5));
    assertSame(AREA5_TO_14, viewPort.getAreaConsumingLine(6));
    assertNull(viewPort.getAreaConsumingLine(NB_LINES_OF_VIEWPORT+1));
    assertArrayEquals(new LineBasedArea[]{AREA0_TO_4,AREA5_TO_14,AREA15_TO_24}, viewPort.getVisibleAreas());

    viewPort.scrollTopOfViewPortToPanelLine(1);
    assertArrayEquals(new LineBasedArea[]{AREA0_TO_4,AREA5_TO_14,AREA15_TO_24,AREA25_TO_34}, viewPort.getVisibleAreas());
    assertEquals(4, viewPort.getNumberOfVisibleLinesForArea(AREA0_TO_4));
    assertEquals(1, viewPort.getNumberOfVisibleLinesForArea(AREA25_TO_34));

    viewPort.scrollTopOfViewPortToPanelLine(10);
    assertArrayEquals(new LineBasedArea[]{AREA5_TO_14,  AREA15_TO_24, AREA25_TO_34}, viewPort.getVisibleAreas());

    assertSame(AREA5_TO_14, viewPort.getAreaConsumingLine(0));
    assertSame(AREA5_TO_14, viewPort.getAreaConsumingLine(4));
    assertSame(AREA15_TO_24, viewPort.getAreaConsumingLine(5));
    assertSame(AREA15_TO_24, viewPort.getAreaConsumingLine(6));
    
    viewPort.scrollToBottom();
    assertArrayEquals(new LineBasedArea[]{AREA15_TO_24, AREA25_TO_34, AREA35_TO_39}, viewPort.getVisibleAreas());
    assertEquals(AREA25_TO_34.getNumberOfLines(), viewPort.getNumberOfVisibleLinesForArea(AREA25_TO_34));
    assertEquals(AREA25_TO_34.getNumberOfLines(), viewPort.getNumberOfVisibleLinesForArea(AREA25_TO_34));
    assertEquals(AREA35_TO_39.getNumberOfLines(), viewPort.getNumberOfVisibleLinesForArea(AREA35_TO_39));

    viewPort.scrollToTop();
    assertArrayEquals(new LineBasedArea[]{AREA0_TO_4, AREA5_TO_14, AREA15_TO_24}, viewPort.getVisibleAreas());
    
    //How does this behave when we change the size if the areas?
    MockCmdSpecification.addRowsToRecord(AREA0_TO_4.getRecord(), 10);
    assertArrayEquals(new LineBasedArea[]{AREA0_TO_4, AREA5_TO_14}, viewPort.getVisibleAreas());

    viewPort.scrollToBottom();
    assertArrayEquals(new LineBasedArea[]{AREA15_TO_24, AREA25_TO_34, AREA35_TO_39}, viewPort.getVisibleAreas());
    MockCmdSpecification.addRowsToRecord(AREA35_TO_39.getRecord(), 10);
    assertArrayEquals(new LineBasedArea[]{AREA15_TO_24, AREA25_TO_34, AREA35_TO_39}, viewPort.getVisibleAreas());

    viewPort.scrollToBottom();
    assertArrayEquals(new LineBasedArea[]{AREA25_TO_34, AREA35_TO_39}, viewPort.getVisibleAreas());
  }

}
