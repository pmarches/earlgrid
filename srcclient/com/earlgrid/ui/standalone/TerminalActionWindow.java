package com.earlgrid.ui.standalone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.earlgrid.ui.standalone.customterminal.MutableCompositeHistoryWidget;

public class TerminalActionWindow extends Composite {
  public CommandLineInputArea inputArea;
  MutableCompositeHistoryWidget historyArea;

  public TerminalActionWindow(Composite parent) {
    super(parent, SWT.NONE);
    
    configureLookOfControlFromParent(this);
    createContent();
  }

  private void createContent() {
    configureTightGridLayout(this, 1, true);

    historyArea=new MutableCompositeHistoryWidget(this, SWT.NONE);
    historyArea.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

    inputArea=new CommandLineInputArea(this);
    inputArea.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
    
    addFocusListener(onFocusListener);
  }

  public static void configureTightGridLayout(Composite control, int nbColumns, boolean columnsSameSize) {
    GridLayout tightLayout=new GridLayout(nbColumns, columnsSameSize);
    tightLayout.marginHeight=0;
    tightLayout.marginWidth=0;
    tightLayout.horizontalSpacing=0;
    tightLayout.verticalSpacing=0;
    control.setLayout(tightLayout);
  }

  private FocusListener onFocusListener=new FocusAdapter() {
    @Override
    public void focusGained(FocusEvent e) {
      inputArea.setFocus();
    }
  };

  public static void configureLookOfControlFromParent(Control control){
    control.setBackground(control.getParent().getBackground());
    control.setForeground(control.getParent().getForeground());
    control.setFont(control.getParent().getFont());
  }

}
