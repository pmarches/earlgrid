package org;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;

public class TerminalCustomWidget extends Composite implements DisposeListener {
  private ApplicationMain app;
  private Slider slider;
  private Canvas outputCanvas;
  private Canvas verticalStatusCanvas;
  private Font outputRowFont;
  private Font commandFont;
  private Color background;
  private Color outputRowColor;
  private Color commandRowColor;
  private Color buttonColor;
  
  class OutputsSelection {
    ArrayList<Integer> outputIndexes=new ArrayList<>();
    int bottomOutputLastRowSelected=-1;
  }
  
  int NB_PIXELS_PER_ROW=13;
  private PaintListener onPaintCanvas=new PaintListener() {
    int currentXPosition=0;
    int currentYPosition=0;
    
    @Override
    public void paintControl(PaintEvent e) {
      e.gc.setBackground(background);
      Rectangle clientArea = outputCanvas.getClientArea();
      e.gc.fillRectangle(clientArea); //Because SWT.NO_BACKGROUND
      
      currentYPosition=clientArea.height-NB_PIXELS_PER_ROW;
      
      int rowIndexAtTheBottom=slider.getSelection();
      OutputsSelection selectionToPaint=getSelectionToPaint(rowIndexAtTheBottom, clientArea);
      paintOutputSelection(e, selectionToPaint);
      e.gc.drawString(app.sessionOutput.totalNumberOfRows+":"+slider.getSelection()+"/"+slider.getMaximum()+"/"+slider.getThumb(), 0, 0);
    }

    private void paintOutputSelection(PaintEvent e, OutputsSelection selection) {
      if(selection==null){
        return;
      }
      //Each item is painted from last to first, to achieve the bottom-up feel of a terminal

      if(selection.outputIndexes.size()==0){
        return;
      }
      RunOutput bottomPartialOutput=app.sessionOutput.outputs[selection.outputIndexes.get(selection.outputIndexes.size()-1)];
      paintSingleOutput(e, bottomPartialOutput, selection.bottomOutputLastRowSelected);

      for(int selectionIndex=selection.outputIndexes.size()-2; selectionIndex>=0; selectionIndex--){
        RunOutput currentOutput=app.sessionOutput.outputs[selection.outputIndexes.get(selectionIndex)];
        paintSingleOutput(e, currentOutput, -1);

        if(currentYPosition<0){
          break;
        }
      }
    }

    private void paintSingleOutput(PaintEvent e, RunOutput currentOutput, int rowIndexToStartPainting) {
      int rowIndex;
      if(rowIndexToStartPainting==-1){
        rowIndex=currentOutput.rowCol.length-1;
      }
      else{
        rowIndex=rowIndexToStartPainting-1;
      }
      
      for(; rowIndex>=0; rowIndex--){
        paintSingleOutputRow(e, currentOutput, rowIndex);
        if(currentYPosition<0){
          break;
        }
      }
      
      drawCommandRow(e, currentOutput);
    }

    private void paintSingleOutputRow(PaintEvent e, RunOutput currentOutput, int rowIndex) {
      currentXPosition=0;
      String[] rowContent=currentOutput.rowCol[rowIndex];
      int columnWidth=outputCanvas.getClientArea().width/currentOutput.numberOfColumns;
      
      e.gc.setFont(outputRowFont);
      e.gc.setForeground(outputRowColor);
      for(int columnIndex=0; columnIndex<rowContent.length; columnIndex++){
        String cellContent=rowContent[columnIndex];
        e.gc.drawString(cellContent, currentXPosition, currentYPosition);
        currentXPosition+=columnWidth;
      }
      currentYPosition-=NB_PIXELS_PER_ROW;
    }

    private void drawCommandRow(PaintEvent e, RunOutput currentOutput) {
      currentXPosition=e.width;
      int BTN_SIZE=NB_PIXELS_PER_ROW-1;
      e.gc.setForeground(buttonColor);
      currentXPosition-=BTN_SIZE;
      e.gc.drawRectangle(currentXPosition, currentYPosition, BTN_SIZE, BTN_SIZE);
      
      currentXPosition-=5;//Right margin
      e.gc.setFont(commandFont);
      e.gc.setForeground(commandRowColor);

      String cmdLine=String.format("%s $%s", currentOutput.command, currentOutput.index); 
      Point commandExtent=e.gc.textExtent(cmdLine);
      currentXPosition-=commandExtent.x;
      e.gc.drawString(cmdLine, currentXPosition, currentYPosition);
      
      
      currentYPosition-=NB_PIXELS_PER_ROW;
    }
  };

  //Get the outputs that fit the displayableArea
  private OutputsSelection getSelectionToPaint(int rowIndexToDisplayAtTheBottom, Rectangle displayableArea) {
    OutputsSelection sel=new OutputsSelection();
    
    int numberOfDisplayableRowsRemaining=displayableArea.height/TerminalCustomWidget.this.NB_PIXELS_PER_ROW;
    int bottomRowToPaint=rowIndexToDisplayAtTheBottom;
    if(bottomRowToPaint<numberOfDisplayableRowsRemaining){
      bottomRowToPaint=Math.min(numberOfDisplayableRowsRemaining, app.sessionOutput.totalNumberOfRows)-1;
    }
    
    int outputIndexToDisplayABottom=Arrays.binarySearch(app.sessionOutput.cumulativeNumberOfRowsPerOutput, bottomRowToPaint);
    if(outputIndexToDisplayABottom<0){
      outputIndexToDisplayABottom=(outputIndexToDisplayABottom+1)*-1-1;
      
      sel.bottomOutputLastRowSelected=app.sessionOutput.outputs[outputIndexToDisplayABottom].getNumberOfRows()-1;
      int totalNbRowsUpToLastCommand=app.sessionOutput.cumulativeNumberOfRowsPerOutput[outputIndexToDisplayABottom]+
          app.sessionOutput.outputs[outputIndexToDisplayABottom].getNumberOfRows();
      
      sel.bottomOutputLastRowSelected-=(totalNbRowsUpToLastCommand-rowIndexToDisplayAtTheBottom);
    }

    while(numberOfDisplayableRowsRemaining>0 && outputIndexToDisplayABottom>=0){
      sel.outputIndexes.add(0, outputIndexToDisplayABottom);
      RunOutput currentOutput=app.sessionOutput.outputs[outputIndexToDisplayABottom];
      numberOfDisplayableRowsRemaining-=currentOutput.getNumberOfRows();
      outputIndexToDisplayABottom--;
    }
    return sel;
  }

  private SelectionListener onSliderChange=new SelectionAdapter(){
    @Override
    public void widgetSelected(SelectionEvent e) {
      outputCanvas.redraw();
      outputCanvas.update();//it waits until the drawing is done before returning. This avoids doing multiple paint() while we are painting? 
    }
  };

  Point beginDrag;
  private MouseListener onMouseClickListener=new MouseAdapter() {
    @Override
    public void mouseDown(MouseEvent e) {
      beginDrag=new Point(e.x, e.y);
    }
    
    public void mouseUp(MouseEvent e) {
      if(beginDrag==null){
        return;
      }
      Point endDrag=new Point(e.x, e.y);
      if(endDrag.equals(beginDrag)==false){
        System.out.println("end drag");
      }
    };
  };
  
  public TerminalCustomWidget(Shell shell, ApplicationMain app) {
    super(shell, SWT.NONE);
    this.app=app;
    addDisposeListener(this);
    
    FontData fontSpecification=new FontData("Courier New", 10, SWT.NONE);
    outputRowFont=new Font(getDisplay(), fontSpecification);
//    fontSpecification.setHeight(12);
    fontSpecification.setStyle(SWT.BOLD);
    commandFont=new Font(getDisplay(), fontSpecification);
    outputRowColor=getDisplay().getSystemColor(SWT.COLOR_GRAY);
    commandRowColor=getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN);
    buttonColor=getDisplay().getSystemColor(SWT.COLOR_MAGENTA);
    background=getDisplay().getSystemColor(SWT.COLOR_BLACK);
    
    GridLayout thisLayout = new GridLayout(3, false);
    thisLayout.marginTop=0;
    thisLayout.marginBottom=0;
    thisLayout.marginLeft=0;
    thisLayout.marginRight=0;
    thisLayout.marginHeight=0;
    thisLayout.marginWidth=0;
    thisLayout.horizontalSpacing=0;
    thisLayout.verticalSpacing=0;
    setLayout(thisLayout);
    
    outputCanvas=new Canvas(this, SWT.NO_BACKGROUND);
    outputCanvas.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
    outputCanvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    outputCanvas.addPaintListener(onPaintCanvas);
    outputCanvas.addMouseListener(onMouseClickListener);
    
    slider=new Slider(this, SWT.VERTICAL);
    slider.setMaximum(app.sessionOutput.totalNumberOfRows+slider.getThumb());
    slider.setSelection(slider.getMaximum());
    slider.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
    slider.addSelectionListener(onSliderChange);
    
    verticalStatusCanvas=new Canvas(this, SWT.None);
    GridData verticalStatusLayout = new GridData(SWT.RIGHT, SWT.FILL, false, true);
    verticalStatusLayout.widthHint=10;
    verticalStatusCanvas.setLayoutData(verticalStatusLayout);
    verticalStatusCanvas.setBackground(getDisplay().getSystemColor(SWT.COLOR_CYAN));
    
  }

  @Override
  public void widgetDisposed(DisposeEvent e) {
    if(outputRowFont!=null){
      outputRowFont.dispose();
    }
    if(commandFont!=null){
      commandFont.dispose();
    }
  }

  
}
