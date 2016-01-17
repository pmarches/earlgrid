package com.earlgrid.ui.decorators;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import com.earlgrid.ui.standalone.CommandLineInputArea;
import com.earlgrid.ui.standalone.ResourceCache;
import com.earlgrid.ui.standalone.CommandLineInputArea.InputAreaMode;

public class InputAreaStateIndicatorDecorator extends CommandLineInputAreaDecorator{
  private final CommandLineInputArea commandLineInputArea;
  Label inputModeLabel;

  public InputAreaStateIndicatorDecorator(CommandLineInputArea commandLineInputArea, Composite contributionArea) {
    this.commandLineInputArea = commandLineInputArea;
    inputModeLabel=new Label(contributionArea, SWT.None);
//    inputModeLabel.setFont(ResourceCache.getInstance().fontAwesomeFont);
    handleEvent(null);
  }

  @Override
  public void handleEvent(Event event) {
    if(commandLineInputArea.isDisposed() || commandLineInputArea.getDisplay().isDisposed()){
      return;
    }
    
    if(commandLineInputArea.currentEditionMode==InputAreaMode.SHELL_MODE){
      inputModeLabel.setImage(ResourceCache.getInstance().EDIT_ICON);
    }
    else if(commandLineInputArea.currentEditionMode==InputAreaMode.META_MODE){
//      inputModeLabel.setText(ResourceCache.getInstance().FA_CLOSE);
      inputModeLabel.setImage(ResourceCache.getInstance().COMMAND_ICON);
    }
    else {
      inputModeLabel.setText("??");
    }
  }
}
