package com.earlgrid.ui.standalone;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.ui.standalone.CommandLineInputArea.InputAreaMode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class CommandLineInputAreaKeyHandler extends HiearchicalKeyListener {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(CommandLineInputAreaKeyHandler.class);
  CommandLineInputArea commandLineInputArea;
  ExecutionHistoryRecord lastRecalledHistory;
  
  public CommandLineInputAreaKeyHandler(HiearchicalKeyListener parentKeyListener, CommandLineInputArea commandLineInputArea) {
    super(parentKeyListener);
    this.commandLineInputArea=commandLineInputArea;
  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {
    if(isOpenMultiLineEditor(keyEvent)){
      log.debug("Multi line editor goes here");
    }
    else if(isRecallCommandFromHistory(keyEvent)){
      handleRecallCommandFromHistory(keyEvent);
    }
    else if(isExecuteCommand(keyEvent)){
      keyEvent.doit=false;
      handleExecuteCommand();
    }
    else if(isSwitchToMetaMode(keyEvent)){
      commandLineInputArea.setEditionMode(CommandLineInputArea.InputAreaMode.META_MODE);
    }
    else if(isClearCommandArea(keyEvent)){
      commandLineInputArea.emptyCommandArea();
    }
    else if(isShowTaskOutputWindow(keyEvent)){
      app.mainWindow.showTaskOutputWindow();
    }
    else if(isShowInteractiveInputWindow(keyEvent)){
      app.mainWindow.showInteractiveInputWindow();
    }
    else if(isShowCommandhistoryWindow(keyEvent)){
      app.mainWindow.showCommandLineHistoryWindow();
    }
    else if(isSelectTaskOutput(keyEvent)){
      app.mainWindow.setFocusToTaskOutput(2);
    }
    else {
      parentKeyListener.keyPressed(keyEvent);
      return;
    }
    
    keyEvent.doit=false; //This is to prevent beeping for some hot keys (for example Ctrl-o) Probably because SWT tries to do something with it?
  }

  private void handleRecallCommandFromHistory(KeyEvent keyEvent) {
    if(keyEvent.keyCode==SWT.ARROW_UP){
      if(lastRecalledHistory==null){
        lastRecalledHistory=app.getSessionModel().getHistory().getLastExecutionRecord();
      }
      else{
        lastRecalledHistory=app.getSessionModel().getHistory().getHistoryRecordBefore(lastRecalledHistory.taskId);
      }
    }
    else if(keyEvent.keyCode==SWT.ARROW_DOWN){
      if(lastRecalledHistory==null){
        lastRecalledHistory=app.getSessionModel().getHistory().getHistoryRecordAfter(0);
      }
      else{
        lastRecalledHistory=app.getSessionModel().getHistory().getHistoryRecordAfter(lastRecalledHistory.taskId);
      }
    }

    if(lastRecalledHistory!=null){
      commandLineInputArea.setText(lastRecalledHistory.userEditedCommand);
    }
    else{
      commandLineInputArea.setText("");
    }
  }

  private void handleExecuteCommand() {
    lastRecalledHistory=null;
    String commandStringInTextBox=commandLineInputArea.getAndClearText();
    if(commandStringInTextBox.isEmpty()){
      return;
    }
    
    if(commandLineInputArea.currentEditionMode==InputAreaMode.SHELL_MODE){
      new Thread(){
        public void run() {
          setName("Accepted command line input Thread");
          app.acceptUserCommand(commandStringInTextBox);
        }
      }.start();
    }
    else{
      commandLineInputArea.setEditionMode(InputAreaMode.SHELL_MODE);
    }
  }

  private boolean isRecallCommandFromHistory(KeyEvent keyEvent) {
    return keyEvent.keyCode==SWT.ARROW_UP || keyEvent.keyCode==SWT.ARROW_DOWN;
  }

  private boolean isExecuteCommand(KeyEvent keyEvent) {
    return keyEvent.keyCode=='\r' || keyEvent.keyCode=='\n';
  }

  private boolean isOpenMultiLineEditor(KeyEvent keyEvent) {
    return (keyEvent.stateMask&SWT.CONTROL)!=0 && keyEvent.keyCode=='e';
  }

  private boolean isSwitchToMetaMode(KeyEvent keyEvent) {
    return keyEvent.keyCode==SWT.ESC;
  }

  private boolean isClearCommandArea(KeyEvent keyEvent) {
    return (keyEvent.stateMask&SWT.CONTROL)!=0 && keyEvent.keyCode=='c';
  }

  private boolean isShowCommandhistoryWindow(KeyEvent keyEvent) {
    return (keyEvent.stateMask&SWT.CONTROL)!=0 && keyEvent.keyCode=='h';
  }

  private boolean isShowTaskOutputWindow(KeyEvent keyEvent) {
    return (keyEvent.stateMask&SWT.CONTROL)!=0 && keyEvent.keyCode=='o';
  }

  private boolean isShowInteractiveInputWindow(KeyEvent keyEvent) {
    return (keyEvent.stateMask&SWT.CONTROL)!=0 && keyEvent.keyCode=='i';
  }

  private boolean isSelectTaskOutput(KeyEvent keyEvent) {
    return (keyEvent.stateMask&SWT.CONTROL)!=0 && keyEvent.keyCode=='g';
  }
}
