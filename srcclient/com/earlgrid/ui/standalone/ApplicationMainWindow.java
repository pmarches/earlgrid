package com.earlgrid.ui.standalone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.earlgrid.ui.standalone.customterminal.MutableCompositeHistoryWidget;

public class ApplicationMainWindow extends Composite {
  public ApplicationMain app;
  TerminalWindowKeyListener appKeyListener;

  public CommandLineInputArea inputArea;
  MutableCompositeHistoryWidget historyArea;

  public ApplicationMainWindow(ApplicationMain app) {
    super(new Shell(app.display, SWT.APPLICATION_MODAL|SWT.SHELL_TRIM), SWT.NONE);
    this.app=app;
    this.appKeyListener=new TerminalWindowKeyListener(app.keyListener);
    
    configureShellWindow();
    configureLookOfControlFromParent(this);
    createContent();
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

  private void createContent() {
    configureTightGridLayout(this, 1, true);

    historyArea=new MutableCompositeHistoryWidget(this, SWT.NONE, app);
    historyArea.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
    historyArea.addKeyListener(appKeyListener);

    inputArea=new CommandLineInputArea(this, app);
    inputArea.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
  }

  public void open() {
    getShell().setText("earlgrid "+app.client.getName());
    getShell().open();
    inputArea.setFocus();
  }

  public static void configureTightGridLayout(Composite control, int nbColumns, boolean columnsSameSize) {
    GridLayout tightLayout=new GridLayout(nbColumns, columnsSameSize);
    tightLayout.marginHeight=0;
    tightLayout.marginWidth=0;
    tightLayout.horizontalSpacing=0;
    tightLayout.verticalSpacing=0;
    control.setLayout(tightLayout);
  }

  public static void configureLookOfControlFromParent(Control control){
    control.setBackground(control.getParent().getBackground());
    control.setForeground(control.getParent().getForeground());
    control.setFont(control.getParent().getFont());
  }

}
