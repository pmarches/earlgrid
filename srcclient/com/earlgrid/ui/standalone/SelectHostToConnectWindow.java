package com.earlgrid.ui.standalone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class SelectHostToConnectWindow {
  private ApplicationMain app;
  private Shell connectToHostShell;
  private Table hostTable;
  boolean exitApplicationWhenWindowCloses=true;
  private Button connectToHostBtn;

  public SelectHostToConnectWindow(ApplicationMain app) {
    this.app=app;
    initUI();
  }

  private void initUI() {
    createShell();
    createHostTable();

    connectToHostBtn=new Button(connectToHostShell, SWT.NONE);
    connectToHostBtn.setText("Connect");
    connectToHostBtn.addSelectionListener(onConnecToToHost);
    //    connectToHostShell.pack();
    connectToHostShell.open();
  }

  private void createShell() {
    connectToHostShell = new Shell(app.display);
    connectToHostShell.setLocation(0, 0);
    connectToHostShell.setSize(400, 200);

//    positionShellOnCenterOfDisplay();
    connectToHostShell.setLayout(new GridLayout(1, false));
    connectToHostShell.setText("Connect to host");
    connectToHostShell.addKeyListener(ApplicationMain.getInstance().mainWindow.mainAppkeyListener);
    connectToHostShell.addListener(SWT.Close, new Listener() {
      public void handleEvent(Event event) {
        connectToHostShell.dispose();
        if(exitApplicationWhenWindowCloses){
          app.exitApplication();
        }
      }
    });
  }

  private void positionShellOnCenterOfDisplay() {
    int x=app.display.getClientArea().width/2-connectToHostShell.getSize().x/2;
    int y=app.display.getClientArea().height/2-connectToHostShell.getSize().y/2;
    connectToHostShell.setLocation(x, y);
  }

  private void createHostTable() {
    hostTable = new Table(connectToHostShell, SWT.BORDER | SWT.VIRTUAL|SWT.MULTI|SWT.HIDE_SELECTION);
    hostTable.addMouseListener(hostTableMouseListener);
    hostTable.addListener(SWT.SetData, hostTableDataListener);
    hostTable.setItemCount(app.hostManager.size());
    hostTable.setHeaderVisible(true);
    hostTable.setLinesVisible(true);
    hostTable.deselectAll();

//    hostTable.addFocusListener(new FocusAdapter() {
//      @Override
//      public void focusGained(FocusEvent e) {
//        connectToHostBtn.setFocus();
//      }
//    });
    TableColumn tblclmnHostname = new TableColumn(hostTable, SWT.NONE);
    tblclmnHostname.setWidth(150);
    tblclmnHostname.setText("Hostname");

    TableColumn tblclmnUser = new TableColumn(hostTable, SWT.NONE);
    tblclmnUser.setWidth(100);
    tblclmnUser.setText("User");

    TableColumn tblclmnNewColumn = new TableColumn(hostTable, SWT.NONE);
    tblclmnNewColumn.setWidth(150);
    tblclmnNewColumn.setText("Key");
    
  }

  private SelectionAdapter onConnecToToHost=new SelectionAdapter() {
    @Override
    public void widgetSelected(SelectionEvent e) {
      exitApplicationWhenWindowCloses=false;
      
      int selectedHostIndexInTable=hostTable.getSelectionIndex();
      try {
        app.openRemoteTerminalWindow(app.hostManager.get(selectedHostIndexInTable));
      } catch (Exception ex) {
        MessageBox dialog = new MessageBox(connectToHostShell, SWT.ICON_ERROR | SWT.OK);
        dialog.setText("My info");
        dialog.setMessage("Failed to connect because "+ex.toString());
      }

      connectToHostShell.close();
      connectToHostShell.dispose();
    }
  };

  private Listener hostTableDataListener=new Listener() {
    public void handleEvent(Event event) {
      TableItem item = (TableItem)event.item;
      int index = event.index;
      item.setText(0, app.hostManager.get(index).hostname+":"+app.hostManager.get(index).port);
      item.setText(1, app.hostManager.get(index).credentials.getUsername());
      item.setText(2, app.hostManager.get(index).credentials.getPrivateKeyPath());
      event.detail &= ~SWT.FOCUSED;
    }
  };

  private MouseListener hostTableMouseListener=new MouseAdapter() {
    @Override
    public void mouseDoubleClick(MouseEvent arg0) {
      onConnecToToHost.widgetSelected(null);
    }
  };

}
