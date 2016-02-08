package com.earlgrid.ui.standalone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class ApplicationMainKeyListener extends HiearchicalKeyListener {

  public ApplicationMainKeyListener(ApplicationMain app) {
    super(null);
    this.app=app;
  }
  
  @Override
  public void keyPressed(KeyEvent keyEvent) {
    if(isExitApplication(keyEvent)){
      app.exitApplication();
    }
    else super.keyPressed(keyEvent);
  }
  
  private boolean isExitApplication(KeyEvent keyEvent) {
    return (keyEvent.stateMask&SWT.CONTROL)!=0 && keyEvent.keyCode=='q';
  }

}
