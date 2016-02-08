package com.earlgrid.ui.standalone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class ApplicationMainShell extends Composite {
  public TerminalActionWindow terminalWindow;
  public CommandHistoryWindow historyWindow;
  protected StackLayout stackLayout;
  private TerminalWindowKeyListener appKeyListener;

  public ApplicationMainShell(ApplicationMain app, Display display) {
    super(new Shell(display, SWT.APPLICATION_MODAL|SWT.SHELL_TRIM), SWT.NONE);
    this.appKeyListener=new TerminalWindowKeyListener(app.keyListener);
    configureShellWindow();

    stackLayout=new StackLayout();
    getShell().setLayout(stackLayout);
    terminalWindow=new TerminalActionWindow(getShell());
    historyWindow=new CommandHistoryWindow(getShell());

    stackLayout.topControl=terminalWindow;
  }

  private void configureShellWindow() {
    getShell().setMaximized(true);
    getShell().addKeyListener(appKeyListener);
    getShell().setLayout(new FillLayout(SWT.VERTICAL));

    getShell().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
    getShell().setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
    getShell().setFont(ResourceCache.getInstance().monospaceFont);
    getShell().setImage(ResourceCache.getInstance().appIcon);

    getShell().addListener(SWT.Close, new Listener() { 
      @Override 
      public void handleEvent(Event event) {
        if(isDisposed()==false){
          getDisplay().dispose();
        }
      } 
    });
  }

  public void switchToHistoryWindow(){
    stackLayout.topControl=historyWindow;
    layout();
  }
  
  public void open() {
    Rectangle parentBounds = getShell().getClientArea();
    Rectangle ourBounds=getShell().getBounds();
    getShell().setLocation(parentBounds.x+(parentBounds.width-ourBounds.width)/2, parentBounds.y+50);

    getShell().setText("earlgrid "+ApplicationMain.getInstance().client.getName());
    getShell().open();
  }

  public void showCommandLineHistoryWindow() {
    CommandHistoryWindow historyWindow = new CommandHistoryWindow(this);
  }

  public void showTaskOutputWindow() {
    TaskOutputWindow taskOutputWindow=new TaskOutputWindow(this);
  }

  public void showInteractiveInputWindow() {
    InteractiveInputWindow interactiveInputWindow=new InteractiveInputWindow(this);
  }

  public void setFocusToTaskOutput(int taskIdToFocus) {
    terminalWindow.historyArea.setFocusOnTask(taskIdToFocus);
  }


}
