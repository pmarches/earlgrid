package com.earlgrid.ui.standalone;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.ui.standalone.customterminal.TaskWidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TaskOutputWindow extends Composite {
  ApplicationMain app;
  
  public TaskOutputWindow(ApplicationMain app) {
    super(new Shell(app.display, SWT.CLOSE|SWT.APPLICATION_MODAL), SWT.NONE);
    this.app=app;
    getShell().setLayout(new FillLayout(SWT.VERTICAL));
    getShell().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
    getShell().setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
    getShell().setFont(ResourceCache.getInstance().monospaceFont);
    getShell().setImage(ResourceCache.getInstance().appIcon);

    ExecutionHistoryRecord taskModel=app.getSessionModel().getHistory().get(1);
    getShell().setText(String.format("Task output %d", taskModel.taskId));

    ApplicationMainWindow.configureLookOfControlFromParent(this);
//    setLayout(new GridLayout(1, false));
    setLayout(new FillLayout(SWT.VERTICAL));

    TaskWidget taskWidget=new TaskWidget(this, SWT.NONE, taskModel);
    taskWidget.addKeyListener(keyHandler);
    taskWidget.setFocus();
    
    Rectangle parentBounds = app.mainWindow.getShell().getClientArea();
    Rectangle ourBounds=getShell().getBounds();
    getShell().setLocation(parentBounds.x, parentBounds.y+50);
    getShell().setSize(parentBounds.width, parentBounds.height-100);
  }

  public void open() {
    getShell().layout();
    getShell().open();
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
