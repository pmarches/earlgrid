package com.earlgrid.ui.standalone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.earlgrid.ui.standalone.customterminal.MutableCompositeHistoryWidget;

public class TerminalActionWindow extends Composite {
  public CommandLineInputArea inputArea;
  MutableCompositeHistoryWidget historyArea;
  TerminalWindowKeyListener terminalKeyListener;

  public TerminalActionWindow(ApplicationMainShell parent) {
    super(parent.getShell(), SWT.NONE);
    terminalKeyListener= new TerminalWindowKeyListener(parent.mainAppkeyListener);
    
    configureLookOfControlFromParent(this);
    createContent();
  }

  private void createContent() {
    configureTightGridLayout(this, 1, true);

    historyArea=new MutableCompositeHistoryWidget(this, SWT.NONE);
    historyArea.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

    inputArea=new CommandLineInputArea(this);
    inputArea.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
  }

  public static void configureTightGridLayout(Composite control, int nbColumns, boolean columnsSameSize) {
    GridLayout tightLayout=new GridLayout(nbColumns, columnsSameSize);
    tightLayout.marginHeight=0;
    tightLayout.marginWidth=0;
    tightLayout.horizontalSpacing=0;
    tightLayout.verticalSpacing=0;
    control.setLayout(tightLayout);
  }

  @Override
  public boolean setFocus() {
    return inputArea.setFocus();
  }
  
  public static void configureLookOfControlFromParent(Control control){
    control.setBackground(control.getParent().getBackground());
    control.setForeground(control.getParent().getForeground());
    control.setFont(control.getParent().getFont());
  }

}
