package com.earlgrid.ui.standalone;

import java.util.Arrays;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultBodyDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DummyColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class InteractiveInputWindow extends Composite {
  ApplicationMain app;
  private NatTable natTable;
  
  public InteractiveInputWindow(ApplicationMain app) {
    super(new Shell(app.display, SWT.CLOSE|SWT.APPLICATION_MODAL), SWT.NONE);
    this.app=app;
    getShell().setLayout(new FillLayout(SWT.VERTICAL));
    getShell().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
    getShell().setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
    getShell().setFont(ResourceCache.getInstance().monospaceFont);
    getShell().setImage(ResourceCache.getInstance().appIcon);

    getShell().setText(String.format("Interactive input"));

    ApplicationMainWindow.configureLookOfControlFromParent(this);
//    setLayout(new GridLayout(1, false));
    setLayout(new FillLayout(SWT.VERTICAL));


    DefaultBodyDataProvider<String> bodyDataProvider=new DefaultBodyDataProvider<String>(
        Arrays.asList("Row1", "Row2"),
        new String[] {"Col1", "Col2"}
        ); 
    DataLayer bodyDataLayer=new DataLayer(bodyDataProvider);
    SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer);
    ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);
    viewportLayer.setRegionName(GridRegion.BODY);
    viewportLayer.setHorizontalScrollbarEnabled(true);
//    viewportLayer.setClientAreaProvider(() -> new Rectangle(0, 0, 500, 1000));

    IDataProvider columnHeaderDataProvider=new DummyColumnHeaderDataProvider(bodyDataProvider);
    DefaultColumnHeaderDataLayer columnHeaderLayer=new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
    columnHeaderLayer.setRegionName(GridRegion.COLUMN_HEADER);
    IDataProvider rowHeaderDataProvider=new DefaultRowHeaderDataProvider(bodyDataProvider);
    DefaultRowHeaderDataLayer rowHeaderLayer=new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
    rowHeaderLayer.setRegionName(GridRegion.ROW_HEADER);
    
    CornerLayer cornerLayer = new CornerLayer(new DataLayer(new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider)), rowHeaderLayer, columnHeaderLayer);
    GridLayer gridLayer = new GridLayer(selectionLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

    natTable = new NatTable(this, gridLayer, false);

    Rectangle parentBounds = app.mainWindow.getShell().getClientArea();
    Rectangle ourBounds=getShell().getBounds();
    getShell().setLocation(parentBounds.x, parentBounds.y+50);
    getShell().setSize(parentBounds.width, parentBounds.height-100);
  }

  public void open() {
    getShell().layout();
    getShell().open();
  }


  private KeyListener keyHandler=new KeyAdapter() {
    @Override
    public void keyReleased(KeyEvent e) {
      if(e.keyCode==SWT.ESC){
        getShell().dispose();
      }
    }
  };
}
