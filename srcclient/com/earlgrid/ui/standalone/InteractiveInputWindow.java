package com.earlgrid.ui.standalone;

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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import java.util.Arrays;

public class InteractiveInputWindow extends Composite {
  private NatTable natTable;
  
  public InteractiveInputWindow(Composite parent) {
    super(parent, SWT.NONE);
    TerminalActionWindow.configureLookOfControlFromParent(this);
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
  }

  private KeyListener keyHandler=new KeyAdapter() {
    @Override
    public void keyReleased(KeyEvent e) {
      if(e.keyCode==SWT.ESC){
        getShell().dispose();
      }
    }
  };
  
  @Override
  public void setVisible(boolean visible) {
    layout();
    super.setVisible(visible);
  }
}
