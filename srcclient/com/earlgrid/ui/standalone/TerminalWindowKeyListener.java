package com.earlgrid.ui.standalone;

import org.eclipse.swt.events.KeyEvent;

public class TerminalWindowKeyListener extends HiearchicalKeyListener {
  public TerminalWindowKeyListener(ApplicationMainKeyListener parentKeyListener) {
    super(parentKeyListener);
  }
  
  @Override
  public void keyReleased(KeyEvent keyEvent) {
    super.keyReleased(keyEvent);
  }
}
