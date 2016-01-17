package com.earlgrid.ui.standalone.customterminal2;

import java.util.ArrayList;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Rectangle;

//Less than ideal O(n) map of rectangles to mouse listener
public class PixelMap implements MouseListener {
  MouseListener defaultListener;
  ArrayList<Rectangle> rectangles=new ArrayList<>();
  ArrayList<MouseListener> mouseListeners=new ArrayList<>();
  
  public PixelMap(MouseListener defaultListener) {
    this.defaultListener=defaultListener;
  }
  
  void addListener(Rectangle rect, MouseListener listener) throws Exception{
    for(Rectangle r:rectangles){
      if(r.intersects(rect)){
        throw new Exception("Specified rectangle "+rect+" overlaps with existing listener at "+r);
      }
    }
    rectangles.add(rect);
    mouseListeners.add(listener);
  }
  
  protected MouseListener lookupMouseListener(int x, int y){
    for(int i=0; i<rectangles.size(); i++){
      Rectangle r=rectangles.get(i);
      if(r.contains(x, y)){
        return mouseListeners.get(i);
      }
    }
    return defaultListener;
  }

  @Override
  public void mouseDoubleClick(MouseEvent e) {
    MouseListener listener = lookupMouseListener(e.x, e.y);
    if(listener!=null){
      listener.mouseDoubleClick(e);
    }
  }

  @Override
  public void mouseDown(MouseEvent e) {
    MouseListener listener = lookupMouseListener(e.x, e.y);
    if(listener!=null){
      listener.mouseUp(e);
    }
  }

  @Override
  public void mouseUp(MouseEvent e) {
    MouseListener listener = lookupMouseListener(e.x, e.y);
    if(listener!=null){
      listener.mouseUp(e);
    }
  }
  
}
