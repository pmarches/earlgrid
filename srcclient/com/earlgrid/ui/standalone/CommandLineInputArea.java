package com.earlgrid.ui.standalone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

import com.earlgrid.ui.decorators.CWDDecorator;
import com.earlgrid.ui.decorators.InputAreaStateIndicatorDecorator;
import com.earlgrid.ui.decorators.MemoryUsageDecorator;

public class CommandLineInputArea extends Composite {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(CommandLineInputArea.class);
  ApplicationMainWindow terminalWindow;
  public ApplicationMain app;

  StyledText commandLineTxt;
  Composite northContributionArea;
  Composite southContributionArea;
  Composite eastContributionArea;
  Composite westContributionArea;
  
  public enum InputAreaMode { SHELL_MODE, META_MODE };
  public InputAreaMode currentEditionMode=InputAreaMode.SHELL_MODE;
  private InputAreaStateIndicatorDecorator editModeIndicator;

  public CommandLineInputArea(ApplicationMainWindow terminalWindow, ApplicationMain app) {
    super(terminalWindow, SWT.NONE);
    this.terminalWindow=terminalWindow;
    this.app=app;
    ApplicationMainWindow.configureTightGridLayout(this, 3, false);
    ApplicationMainWindow.configureLookOfControlFromParent(this);
    
    createNorthContributionArea();
    createWestContributionArea();
    
    commandLineTxt = new StyledText(this, SWT.SINGLE);
    commandLineTxt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    commandLineTxt.addKeyListener(new CommandLineInputAreaKeyHandler(terminalWindow.appKeyListener, this));
    commandLineTxt.addTraverseListener(preventTraversal);
    ApplicationMainWindow.configureLookOfControlFromParent(commandLineTxt);

    createEastContributionArea();
    createSouthContributionArea();
    
    createDecorators();
  }

  private void createNorthContributionArea() {
    northContributionArea=createContributionArea();
    northContributionArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
  }

  private void createWestContributionArea() {
    westContributionArea=createContributionArea();
    westContributionArea.setBackground(getDisplay().getSystemColor(SWT.COLOR_GREEN));
    westContributionArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
  }

  private void createEastContributionArea() {
    eastContributionArea=createContributionArea();
    eastContributionArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
  }

  private void createSouthContributionArea() {
    southContributionArea=createContributionArea();
    southContributionArea.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 3, 1));
  }

  private Composite createContributionArea() {
    Composite contributionArea = new Composite(this, SWT.NONE);
    ApplicationMainWindow.configureLookOfControlFromParent(contributionArea);
    RowLayout layout=new RowLayout(SWT.HORIZONTAL);
    layout.fill=true;
    layout.marginTop=0;
    layout.marginBottom=0;
    layout.marginLeft=0;
    layout.marginRight=0;
    contributionArea.setLayout(layout);
    return contributionArea;
  }

  private void createDecorators() {
    CWDDecorator currentWorkingDirectoryDecorator = new CWDDecorator(this, northContributionArea);
    app.getSessionModel().addDelayedObserver(currentWorkingDirectoryDecorator);
    
    editModeIndicator=new InputAreaStateIndicatorDecorator(this, westContributionArea);
    new MemoryUsageDecorator(this, southContributionArea);
  }

  private TraverseListener preventTraversal=new TraverseListener() {
    @Override
    public void keyTraversed(TraverseEvent e) {
      if (e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
        e.doit = false;
      }
    }
  };

  public void setText(String command) {
    commandLineTxt.setText(command);
    commandLineTxt.setSelection(command.length(), command.length());
  }

  public String getAndClearText() {
    String commandStringFromInputArea=commandLineTxt.getText();
    commandLineTxt.setText("");
    return commandStringFromInputArea;
  }

  @Override
  public boolean setFocus() {
    return commandLineTxt.setFocus();
  }

  public void setEditionMode(InputAreaMode newEditMode) {
    currentEditionMode=newEditMode;
    editModeIndicator.handleEvent(null);
  }

  public void emptyCommandArea() {
    setText("");
  }
}
