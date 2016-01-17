package com.earlgrid.ui.standalone.customterminal2;

import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ScrollBar;

public class DragToScrollHandler extends MouseAdapter implements DragDetectListener, MouseMoveListener {
  Point scrollingSpeed;
  Point mouseDragStartingPoint;
  Control widgetToMonitor;
  ScrollBar scrollbarToControl;

  public DragToScrollHandler(Control widgetToMonitor, ScrollBar scrollbarToControl) {
    this.widgetToMonitor=widgetToMonitor;
    this.scrollbarToControl=scrollbarToControl;
  }

  public void mouseUp(org.eclipse.swt.events.MouseEvent e) {
    widgetToMonitor.removeMouseMoveListener(this);
    scrollingSpeed=null;
  }

  @Override
  public void mouseMove(MouseEvent e) {
    scrollingSpeed=new Point(e.x-mouseDragStartingPoint.x, e.y-mouseDragStartingPoint.y);
    System.out.println("Speed "+scrollingSpeed);
    int newSelection=scrollbarToControl.getSelection()-scrollingSpeed.y;
    scrollbarToControl.setSelection(newSelection);
    
  }

  @Override
  public void dragDetected(DragDetectEvent e) {
    System.out.println("Drag detected "+e);
    mouseDragStartingPoint=new Point(e.x, e.y);
    widgetToMonitor.addMouseListener(this);
    widgetToMonitor.addMouseMoveListener(this);
  }
}