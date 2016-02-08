package com.earlgrid.ui.standalone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.earlgrid.core.session.ExecutionHistoryRecord;

public class CommandHistoryWindow extends Composite {
  Text searchTextBox;
  Table historyTable;
  
  public CommandHistoryWindow(Composite parent) {
    super(parent, SWT.NONE);
    TerminalActionWindow.configureLookOfControlFromParent(this);
    setLayout(new GridLayout(2, false));

    Label searchLabel=new Label(this, SWT.NONE);
    TerminalActionWindow.configureLookOfControlFromParent(searchLabel);
    searchLabel.setText("Search history");
    searchLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
    
    searchTextBox=new Text(this, SWT.BORDER);
    TerminalActionWindow.configureLookOfControlFromParent(searchTextBox);
    searchTextBox.addKeyListener(searchTextBoxKeyListener);
    searchTextBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    searchTextBox.setFocus();
    
    historyTable = new Table(this, SWT.BORDER|SWT.FULL_SELECTION|SWT.VIRTUAL);
    TerminalActionWindow.configureLookOfControlFromParent(historyTable);
    historyTable.addListener(SWT.SetData, historyTableDataListener);
    historyTable.setItemCount(ApplicationMain.getInstance().getSessionModel().getHistory().size());
    historyTable.addKeyListener(historyTableKeyListener);
    historyTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
    historyTable.setHeaderVisible(true);
//    historyTable.addListener(SWT.EraseItem, onEraseHistoryTable);
    
    final TableColumn taskIdCol = new TableColumn(historyTable, SWT.NONE);
    taskIdCol.setText("Id");

    final TableColumn commandCol = new TableColumn(historyTable, SWT.NONE);
    commandCol.setText("Command");

    final TableColumn outputSizeCol = new TableColumn(historyTable, SWT.NONE);
    outputSizeCol.setText("Out");
    
    historyTable.addListener(SWT.Resize, event -> {
      taskIdCol.setWidth(35);
      outputSizeCol.setWidth(1); //The last column will never have a fixed with, but setting any value here will ensure the column is visible
      final int OUTPUT_SIZE_COL=45;
      int widthRemaining=historyTable.getClientArea().width-taskIdCol.getWidth()-OUTPUT_SIZE_COL;
      commandCol.setWidth(widthRemaining);
    });
  }

  Listener historyTableDataListener=new Listener() {
    @Override
    public void handleEvent(Event event) {
      TableItem item = (TableItem)event.item;
      int index = event.index;
      //FIXME the index is not the taskId! Either we lookup the taskId from the index, 
      ExecutionHistoryRecord record = ApplicationMain.getInstance().getSessionModel().getHistory().get(index);
      item.setText(0, String.format("%d", record.taskId)); 
      item.setText(1, record.userEditedCommand);
      item.setText(2, String.format("%d", record.out.getRowCount()));
    }
  };

  private Listener onEraseHistoryTable=event-> {
    event.detail &= ~SWT.HOT;
    if ((event.detail & SWT.SELECTED) == 0){
      return; /* item not selected */
    }
    int clientWidth = historyTable.getClientArea().width;
    GC gc = event.gc;
    Color oldForeground = gc.getForeground();
    Color oldBackground = gc.getBackground();

    gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
    gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));
    gc.fillGradientRectangle(0, event.y, clientWidth, event.height, false);

    gc.setForeground(oldForeground);
    gc.setBackground(oldBackground);
    event.detail &= ~SWT.SELECTED;
  };

  private KeyListener historyTableKeyListener=new KeyAdapter() {
    public void keyReleased(KeyEvent e) {
      if(e.keyCode==SWT.CR){
        TableItem selectedRow = historyTable.getSelection()[0];
        String recalledCommand=selectedRow.getText(1);
        ApplicationMain.getInstance().mainWindow.terminalWindow.inputArea.setText(recalledCommand);
        getShell().dispose();
      }
      else if(e.keyCode==SWT.ESC){
        getShell().dispose();
      }
    };
  };

  private KeyListener searchTextBoxKeyListener=new KeyAdapter() {
    @Override
    public void keyReleased(KeyEvent e) {
      if(e.keyCode==SWT.ARROW_DOWN || e.keyCode==SWT.PAGE_DOWN){
        historyTable.setFocus();
        historyTable.setSelection(0);
      }
      else if(e.keyCode==SWT.ARROW_UP || e.keyCode==SWT.PAGE_UP){
        historyTable.setFocus();
        historyTable.setSelection(historyTable.getItemCount()-1);
      }
      else if(e.keyCode==SWT.ESC){
        getShell().dispose();
      }
    }
  };
}
