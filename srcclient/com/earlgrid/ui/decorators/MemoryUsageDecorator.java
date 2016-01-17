package com.earlgrid.ui.decorators;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import com.earlgrid.ui.standalone.ApplicationMainWindow;
import com.earlgrid.ui.standalone.CommandLineInputArea;

public class MemoryUsageDecorator extends CommandLineInputAreaDecorator{
  private final CommandLineInputArea commandLineInputArea;

  public MemoryUsageDecorator(CommandLineInputArea commandLineInputArea, Composite contributionArea) {
    this.commandLineInputArea = commandLineInputArea;
    memoryUsageLabel=new Label(contributionArea, SWT.NONE);
//    promptLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true));
    ApplicationMainWindow.configureLookOfControlFromParent(memoryUsageLabel);
    handleEvent(null);
  }
  Label memoryUsageLabel;

  @Override
  public void handleEvent(Event event) {
    if(commandLineInputArea.isDisposed() || commandLineInputArea.getDisplay().isDisposed()){
      return;
    }
    
    String memUsageText=String.format("%d/%d MB", 
        (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1_000_000, 
        Runtime.getRuntime().totalMemory()/1024/1024);
    memoryUsageLabel.setText(memUsageText);
    this.commandLineInputArea.layout(true);
    
    commandLineInputArea.getDisplay().timerExec(1_000, new Runnable() {
      public void run() {
        handleEvent(null);
      }
    });
  }
}