package com.earlgrid.ui.standalone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.ui.standalone.customterminal.TaskWidget;

public class TaskOutputWindow extends Composite {
  public TaskOutputWindow(Composite parent) {
    super(parent, SWT.NONE);
    ExecutionHistoryRecord taskModel=ApplicationMain.getInstance().getSessionModel().getHistory().get(1);

    TerminalActionWindow.configureLookOfControlFromParent(this);
//    setLayout(new GridLayout(1, false));
    setLayout(new FillLayout(SWT.VERTICAL));

    TaskWidget taskWidget=new TaskWidget(this, SWT.NONE, taskModel);
    taskWidget.addKeyListener(keyHandler);
    taskWidget.setFocus();
  }

  @Override
  public void setVisible(boolean visible) {
    layout();
    super.setVisible(visible);
  }
  
  private KeyListener keyHandler=new KeyAdapter() {
    @Override
    public void keyReleased(KeyEvent e) {
      if(e.keyCode==SWT.ESC){
        getShell().dispose();
      }
    }
  };
}
