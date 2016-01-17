package com.earlgrid.ui.parts;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Text;

public class TerminalEditorCommandBoxKeyHandler extends KeyAdapter {
  private Integer lastRecalledIndex;
  private ScrolledComposite scrollComposite;
  TerminalEditor terminalEditor;
  
  public TerminalEditorCommandBoxKeyHandler(TerminalEditor terminalEditor) {
    this.terminalEditor=terminalEditor;
    this.scrollComposite=terminalEditor.scrollComposite;
  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {
    try {
      Text commandLineTxt=(Text) keyEvent.getSource();
      if (isStartShellCommandKeyStroke(keyEvent)) {
        String commandText = commandLineTxt.getText();
        if (commandText.isEmpty() == false) {
          terminalEditor.executeCommand(commandText);
          commandLineTxt.setText("");
        }
      }
      else if(isClearContentKeyStroke(keyEvent)){
        commandLineTxt.setText("");
      }
      else if(isDiscardLastOutputKeyStroke(keyEvent)){
        ExecutionHistoryRecord lastExecution = terminalEditor.app.getSessionModel().getHistory().getLastExecutionRecord();
        if(lastExecution!=null){
          terminalEditor.removeExecutionFromHistoryAndTerminal(lastExecution);
        }
      }
      else if(isMoveOutputFocusKeyStroke(keyEvent)){
        int newSelection=0;
        switch(keyEvent.keyCode){
          case SWT.ARROW_UP: newSelection-=scrollComposite.getVerticalBar().getIncrement(); break;
          case SWT.ARROW_DOWN: newSelection+=scrollComposite.getVerticalBar().getIncrement(); break;
          case SWT.PAGE_UP: newSelection-=scrollComposite.getVerticalBar().getPageIncrement(); break;
          case SWT.PAGE_DOWN: newSelection+=scrollComposite.getVerticalBar().getPageIncrement(); break;
        }
        if(newSelection!=0){
          newSelection+=scrollComposite.getVerticalBar().getSelection();
          scrollComposite.setOrigin(0, newSelection);
        }
      }
      else if (isRecallCommandFromHistoryKeyStroke(keyEvent)) {
        if(keyEvent.keyCode==SWT.ARROW_UP){
          if(lastRecalledIndex==null){
            lastRecalledIndex=Integer.MAX_VALUE;
          }
          ExecutionHistoryRecord recalledRecord=terminalEditor.app.getSessionModel().getHistory().getHistoryRecordBefore(lastRecalledIndex);
          if(recalledRecord!=null){
            commandLineTxt.setText(recalledRecord.userEditedCommand);
            lastRecalledIndex=recalledRecord.taskId;
          }
        }
        else if(keyEvent.keyCode==SWT.ARROW_DOWN){
          if(lastRecalledIndex==null){
            return;
          }
          ExecutionHistoryRecord recalledRecord=terminalEditor.app.getSessionModel().getHistory().getHistoryRecordAfter(lastRecalledIndex);
          if(recalledRecord==null){
            commandLineTxt.setText("");
            lastRecalledIndex=null;
          }
          else{
            commandLineTxt.setText(recalledRecord.userEditedCommand);
            lastRecalledIndex=recalledRecord.taskId;
          }
        }
      }
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }

  private boolean isDiscardLastOutputKeyStroke(KeyEvent keyEvent) {
    return (keyEvent.stateMask&SWT.MOD1)!=0 && keyEvent.keyCode == 'z';
  }

  private boolean isClearContentKeyStroke(KeyEvent keyEvent) {
    return keyEvent.keyCode == SWT.ESC;
  }

  private boolean isRecallCommandFromHistoryKeyStroke(KeyEvent keyEvent) {
    return keyEvent.keyCode == SWT.ARROW_UP || keyEvent.keyCode == SWT.ARROW_DOWN;
  }

  private boolean isMoveOutputFocusKeyStroke(KeyEvent keyEvent) {
    return (keyEvent.stateMask & SWT.SHIFT) != 0;
  }

  private boolean isStartShellCommandKeyStroke(KeyEvent keyEvent) {
    return keyEvent.keyCode==SWT.CR || keyEvent.keyCode==SWT.KEYPAD_CR;
  }

}
