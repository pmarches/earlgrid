package com.earlgrid.ui.parts;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.ui.standalone.ApplicationMain;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class CommandHistoryView extends Composite {
  private Table commandHistoryTable;

  private ApplicationMain app;

  public CommandHistoryView(Composite parent, ApplicationMain app) {
    super(parent, SWT.NONE);
    this.app=app;
    createPartControl(parent);
  }

  public void createPartControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);
    final Font monospaceFont = new Font(parent.getDisplay(), new FontData("Monospace", 12, SWT.NONE));
    final Color blackColor = parent.getDisplay().getSystemColor(SWT.COLOR_BLACK);
    final Color greyColor = parent.getDisplay().getSystemColor(SWT.COLOR_GRAY);

    commandHistoryTable = new Table(container, SWT.FULL_SELECTION | SWT.VIRTUAL);
    FormData historyTableLayoutData=new FormData();
    historyTableLayoutData.top=new FormAttachment(0);
    historyTableLayoutData.left=new FormAttachment(0);
    historyTableLayoutData.right=new FormAttachment(100);
    commandHistoryTable.setLayoutData(historyTableLayoutData);
    TableColumn historyCommandColumn = new TableColumn(commandHistoryTable, SWT.NONE);
    TableColumn historyResultColumn = new TableColumn(commandHistoryTable, SWT.NONE);
    historyResultColumn.setWidth(60);
    commandHistoryTable.addControlListener(createCommandHistoryControl(historyCommandColumn, historyResultColumn.getWidth()));
    commandHistoryTable.getVerticalBar().setVisible(true);
    commandHistoryTable.setFont(monospaceFont);
    commandHistoryTable.addSelectionListener(historyTableSelectionHandler);
    commandHistoryTable.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        if(commandHistoryTable.getSelectionIndex()==-1){
          commandHistoryTable.setSelection(commandHistoryTable.getItemCount()-1);
        }
      }
      @Override
      public void focusLost(FocusEvent e) {
//        commandLineTxt.setFocus();
      }
    });
    commandHistoryTable.addListener(SWT.SetData, historyTableDataHandler);
    commandHistoryTable.setItemCount(app.getSessionModel().getHistory().size());
    commandHistoryTable.addKeyListener(historyTableKeyListener);
    commandHistoryTable.setBackground(blackColor);
    commandHistoryTable.setForeground(greyColor);

    createActions();

    monospaceFont.dispose();
  }

  /**
   * Create the actions.
   */
  private void createActions() {
    // Create the actions
  }

  private ControlListener createCommandHistoryControl(final TableColumn commandColumn, final int widthOfOtherColumns) { 
    return new ControlAdapter() {
      @Override
      public void controlResized(ControlEvent e) {
        int clientAreaWidth = commandHistoryTable.getParent().getClientArea().width;
        int commandHistoryTableWidth = commandHistoryTable.computeTrim(0,0,0,0).width+commandHistoryTable.getVerticalBar().getSize().x;
        int remainingWidth = clientAreaWidth - commandHistoryTableWidth - widthOfOtherColumns;
        commandColumn.setWidth(remainingWidth);
      }
    };
  }

  private KeyAdapter historyTableKeyListener = new KeyAdapter() {
    public void keyPressed(KeyEvent e) {
      if (e.keyCode == '\r') {
//        int selectedLine = commandHistoryTable.getSelectionIndex();
//        if (selectedLine >= 0) {
//          String commandStringSelected = commandHistoryTable.getItem(selectedLine).getText(0);
//          if ((e.stateMask & SWT.SHIFT) != 0) {
//            String existingCommand = commandLineTxt.getText();
//            if (existingCommand.isEmpty() == false) {
//              commandStringSelected = existingCommand + "|" + commandStringSelected;
//            }
//          }
//          commandLineTxt.setText(commandStringSelected);
//        }
//        commandLineTxt.setFocus();
      }
    };
  };

  private SelectionAdapter historyTableSelectionHandler = new SelectionAdapter() {
    @Override
    public void widgetSelected(SelectionEvent e) {
//      Table sourceTable = (Table) e.getSource();
//      int rowSelectedInTable = sourceTable.getSelectionIndex();
//      ExecutionHistoryRecord executionRecord = interactiveSession.history.get(rowSelectedInTable);
//      changeCurrentlyDisplayedExecutionRecord(executionRecord);
    }
  };
  private Listener historyTableDataHandler = new Listener() {
    @Override
    public void handleEvent(Event event) {
      Table sourceTable = (Table) event.widget;
      TableItem item = (TableItem) event.item;
      int rowInTableWidget = sourceTable.indexOf(item);

      ExecutionHistoryRecord record = app.getSessionModel().getHistory().get(rowInTableWidget);
      item.setText(0, record.userEditedCommand);
      item.setText(1, ""+rowInTableWidget);
//      if(record.exitStatus.getExitMessage() != null) {
//        Image errorIcon = new Image(getDisplay(),"resources/png/terminal-8x.png");
//        item.setImage(1, errorIcon);
//      }
    }
  };

}
