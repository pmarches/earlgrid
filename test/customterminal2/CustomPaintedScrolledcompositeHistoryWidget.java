package com.earlgrid.ui.standalone.customterminal2;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.ui.standalone.ApplicationMain;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public class CustomPaintedScrolledcompositeHistoryWidget extends ScrolledComposite {
  protected static final int NB_VERTICAL_PIXEL_PER_ROW = 20;
  protected static final int RIGHT_MARGIN = 20;

  private ExecutionHistoryView executionHistoryView;
  Image closeImage;
  Image collapseImage;
  
  public CustomPaintedScrolledcompositeHistoryWidget(Composite parent, ApplicationMain app) {
    super(parent, SWT.V_SCROLL);
    setAlwaysShowScrollBars(true);
    setBackground(getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
    addPaintListener(paintListener);
    addControlListener(onResize);
    getVerticalBar().addSelectionListener(onScrollChange);
    executionHistoryView=new ExecutionHistoryView(app.getSessionModel().getHistory());

    closeImage=new Image(parent.getDisplay(), "resources/png/circle-x-2x.png");
    collapseImage=new Image(parent.getDisplay(), "resources/png/chevron-top-2x.png");
    
//    addMouseListener(mouseListener);
    addDragDetectListener(new DragToScrollHandler(this, getVerticalBar()));
    addDisposeListener(onDispose);
  }

  private DisposeListener onDispose=new DisposeListener() {
    @Override
    public void widgetDisposed(DisposeEvent e) {
      closeImage.dispose();
      collapseImage.dispose();
    }
  };

  private SelectionAdapter onScrollChange=new SelectionAdapter(){
    @Override
    public void widgetSelected(SelectionEvent e) {
      redraw();
    }
  };

  private int numberOfRowsPerScreen;
  private ControlAdapter onResize=new ControlAdapter() {
    public void controlResized(ControlEvent e) {
      numberOfRowsPerScreen=getSize().y/NB_VERTICAL_PIXEL_PER_ROW;
      maximumWidth=getClientArea().width;
    };
  };
  
  private PaintListener paintListener=new PaintListener() {
    @Override
    public void paintControl(PaintEvent e) {
//      System.out.println("Need to paint "+new Rectangle(e.x, e.y, e.width, e.height));

      GC gc = e.gc;
      gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
      gc.setFont(getParent().getFont());
      
      int baseRowOffsetAccordingToScrollbar=getTopLeftRowIndexAccordingToScrollbar();
      ExecutionHistoryRecord[] recordsThatFitInOneScreen=executionHistoryView.getExecutionRecords(baseRowOffsetAccordingToScrollbar, numberOfRowsPerScreen);
      
      y=0;
      for(int i=0; i<recordsThatFitInOneScreen.length; i++){
        drawOneExecutionRecord(gc, recordsThatFitInOneScreen[i]);
      }
    }

    private int getTopLeftRowIndexAccordingToScrollbar() {
      int scrolbarSelection=getVerticalBar().getSelection();
      int scrollBarMax=getVerticalBar().getMaximum()-getVerticalBar().getThumb();
      float ratio=((float)scrolbarSelection/scrollBarMax);
      int indexOfLastRow = (int) (ratio*executionHistoryView.getRowCount());
      indexOfLastRow-=numberOfRowsPerScreen;
      indexOfLastRow=Math.max(0, indexOfLastRow);
      return indexOfLastRow;
    }

  };

  private int y;
  private int maximumWidth;
  
  protected void drawOneExecutionRecord(GC gc, ExecutionHistoryRecord executionHistoryRecord) {
    //Draw Action icons
    int x=maximumWidth;

    x-=closeImage.getBounds().width;
    gc.drawImage(closeImage, x, y);

    x-=collapseImage.getBounds().width;
    gc.drawImage(collapseImage, x, y);

    String commandDescription=String.format("%s    #%d", executionHistoryRecord.userEditedCommand, executionHistoryRecord.taskId);
    Point commandPixelSize = gc.stringExtent(commandDescription);
    x-=commandPixelSize.x;
    gc.drawString(commandDescription, x-RIGHT_MARGIN, y, true);
    y+=NB_VERTICAL_PIXEL_PER_ROW;
    
    for(int rowIndex=0; rowIndex<executionHistoryRecord.getOut().getRowCount(); rowIndex++){
      gc.drawString(executionHistoryRecord.getOut().getRow(rowIndex).getCellAtColumn(0), 10, y, true);
      y+=NB_VERTICAL_PIXEL_PER_ROW;
    }

    gc.drawLine(0, y, maximumWidth-RIGHT_MARGIN, y);
  }

  public void onNewTask(int taskId) {
    // TODO Auto-generated method stub
    
  };

}
