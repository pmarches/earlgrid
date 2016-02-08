package com.earlgrid.ui.standalone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class CommandHistoryWindow extends Composite {
  Text searchTextBox;
  Table historyTable;
  ApplicationMain app;
  
  public CommandHistoryWindow(ApplicationMain app) {
    super(new Shell(app.display, SWT.CLOSE|SWT.APPLICATION_MODAL), SWT.NONE);
    this.app=app;
    getShell().setLayout(new FillLayout(SWT.VERTICAL));
    ApplicationMainWindow.configureLookOfShellWindow(getShell());

    ApplicationMainWindow.configureLookOfControlFromParent(this);
    setLayout(new GridLayout(2, false));

    Rectangle parentBounds = app.mainWindow.getShell().getClientArea();
    Rectangle ourBounds=getShell().getBounds();
    getShell().setLocation(parentBounds.x+(parentBounds.width-ourBounds.width)/2, parentBounds.y+50);

    Label searchLabel=new Label(this, SWT.NONE);
    ApplicationMainWindow.configureLookOfControlFromParent(searchLabel);
    searchLabel.setText("Search history");
    searchLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
    
    searchTextBox=new Text(this, SWT.BORDER);
    ApplicationMainWindow.configureLookOfControlFromParent(searchTextBox);
    searchTextBox.addKeyListener(searchTextBoxKeyListener);
    searchTextBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    searchTextBox.setFocus();
    
    historyTable = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
    ApplicationMainWindow.configureLookOfControlFromParent(historyTable);
    historyTable.addKeyListener(historyTableKeyListener);
    historyTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
    historyTable.setHeaderVisible(true);
//    historyTable.addListener(SWT.EraseItem, onEraseHistoryTable);
    
    TableColumn taskIdCol = new TableColumn(historyTable, SWT.NONE);
    taskIdCol.setText("Id");
    taskIdCol.setWidth(35);

    TableColumn commandCol = new TableColumn(historyTable, SWT.NONE);
    commandCol.setText("Command");

    TableColumn outputSizeCol = new TableColumn(historyTable, SWT.NONE);
    outputSizeCol.setText("Out");
    outputSizeCol.setWidth(45);

    historyTable.addListener(SWT.Resize, event -> {
        historyTable.getColumn(1).setWidth(historyTable.getClientArea().width-taskIdCol.getWidth()-outputSizeCol.getWidth());
    });

    app.getSessionModel().getHistory().forEach(record -> {
      TableItem item = new TableItem(historyTable, SWT.NONE);
      item.setText(0, String.format("%d", record.taskId)); 
      item.setText(1, record.userEditedCommand);
      item.setText(2, String.format("%d", record.out.getRowCount()));
    });
  }

  public void open() {
    getShell().setText("History");
    getShell().open();
  }

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
        app.mainWindow.inputArea.setText(recalledCommand);
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
