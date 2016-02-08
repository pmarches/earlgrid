package com.earlgrid.ui.standalone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class ApplicationMainShell {
  public TerminalActionWindow terminalWindow;
  protected StackLayout stackLayout;
  private Shell shell;
  public ApplicationMainKeyListener mainAppkeyListener;

  public ApplicationMainShell(ApplicationMain app, Display display) {
    mainAppkeyListener=new ApplicationMainKeyListener(app);
    shell=new Shell(display, SWT.APPLICATION_MODAL|SWT.SHELL_TRIM);
    configureShellWindow();
  }

  public Shell getShell() {
    return shell;
  }

  private void configureShellWindow() {
    getShell().setMaximized(true);
    getShell().addKeyListener(mainAppkeyListener);
    getShell().setLayout(new FillLayout(SWT.VERTICAL));

    getShell().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
    getShell().setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
    getShell().setFont(ResourceCache.getInstance().monospaceFont);
    getShell().setImage(ResourceCache.getInstance().appIcon);

    getShell().addListener(SWT.Close, new Listener() { 
      @Override 
      public void handleEvent(Event event) {
        if(getShell().isDisposed()==false){
          getShell().getDisplay().dispose();
        }
      } 
    });
  }

  public void open() {
    stackLayout=new StackLayout();
    getShell().setLayout(stackLayout);

    terminalWindow=new TerminalActionWindow(this);
    switchToWindow(terminalWindow);

    /* Center shell?
    Rectangle parentBounds = getShell().getClientArea();
    Rectangle ourBounds=getShell().getBounds();
    getShell().setLocation(parentBounds.x+(parentBounds.width-ourBounds.width)/2, parentBounds.y+50);
     */
    getShell().setText("earlgrid "+ApplicationMain.getInstance().client.getName());
    getShell().open();
  }

  public void switchToWindow(Composite newWindow){
    stackLayout.topControl=newWindow;
    shell.layout();
    newWindow.setFocus();
  }
  
  public void setFocusToTaskOutput(int taskIdToFocus) {
    terminalWindow.historyArea.setFocusOnTask(taskIdToFocus);
  }

  public void showTaskOutputWindow() {
    TaskOutputWindow taskOutputWindow = new TaskOutputWindow(shell);
    switchToWindow(taskOutputWindow);
  }

  public void showInteractiveInputWindow() {
    InteractiveInputWindow interactiveInputWindow = new InteractiveInputWindow(shell);
    switchToWindow(interactiveInputWindow);
  }

  public void showCommandLineHistoryWindow() {
    CommandHistoryWindow historyWindow = new CommandHistoryWindow(getShell());
    switchToWindow(historyWindow);
  }

}
