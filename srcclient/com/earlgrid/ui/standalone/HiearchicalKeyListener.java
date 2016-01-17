package com.earlgrid.ui.standalone;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

public class HiearchicalKeyListener implements KeyListener {
  protected HiearchicalKeyListener parentKeyListener;
  protected ApplicationMain app;

  public HiearchicalKeyListener(HiearchicalKeyListener parentKeyListener) {
    if(parentKeyListener!=null){
      this.parentKeyListener=parentKeyListener;
      this.app=parentKeyListener.app;
    }
  }
  
  @Override
  public void keyPressed(KeyEvent keyEvent) {
    if(parentKeyListener!=null){
      parentKeyListener.keyPressed(keyEvent);
    }
  }

  @Override
  public void keyReleased(KeyEvent keyEvent) {
    if(parentKeyListener!=null){
      parentKeyListener.keyReleased(keyEvent);
    }
  }
}
