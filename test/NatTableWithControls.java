package org;

import java.util.Arrays;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.AbstractTextPainter;
import org.eclipse.nebula.widgets.nattable.resize.command.ColumnResizeCommand;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

public class NatTableWithControls {
  SessionOutput outputs=new SessionOutput();
  IDataProvider dataProvider=new IDataProvider() {
  int NB_COLUMN_CELLS=50;

    @Override
    public void setDataValue(int arg0, int arg1, Object arg2) {
      // TODO Auto-generated method stub
    }
    
    @Override
    public int getRowCount() {
      return outputs.totalNumberOfRows;
    }
    
    @Override
    public Object getDataValue(int column, int row) {
      return null;
    }
    
    @Override
    public int getColumnCount() {
      return NB_COLUMN_CELLS;
    }
  };

  public NatTableWithControls(Shell shell) {
    configureTableOfWidgets(shell);
  }

  void configureTableOfWidgets(Shell shell) {
    DataLayer dataLayer = new DataLayer(dataProvider);
    dataLayer.setColumnPercentageSizing(true);
    dataLayer.setColumnWidthPercentageByPosition(0, 100);
//    dataLayer.setConfigLabelAccumulator(new ValidatorMessageLabelAccumulator());
    ViewportLayer layer = new ViewportLayer(dataLayer);
    layer.setRegionName(GridRegion.BODY);
    NatTable natTable = new NatTable(shell, NatTable.DEFAULT_STYLE_OPTIONS | SWT.BORDER, layer, false);
    natTable.addConfiguration(new WidgetTableStyleConfiguration());
    natTable.configure();
  }

  private class WidgetTableStyleConfiguration extends DefaultNatTableStyleConfiguration{
    {
      hAlign = HorizontalAlignmentEnum.LEFT;
      cellPainter= new AbstractTextPainter() {
        @Override
        protected void setNewMinLength(ILayerCell cell, int contentWidth) {
            int cellLength = cell.getBounds().width;
            if (cellLength < contentWidth) {
                // execute ColumnResizeCommand
                ILayer layer = cell.getLayer();
                layer.doCommand(
                        new ColumnResizeCommand(layer, cell.getColumnPosition(), contentWidth));
            }
        }
        
        @Override
        protected int calculatePadding(ILayerCell cell, int availableLength) {
          return cell.getBounds().width - availableLength;
        }
        
//        @Override
//        protected boolean performRowResize(int contentHeight, Rectangle rectangle) {
//            return ((contentHeight != rectangle.height) && this.calculateByTextHeight);
//        }
        
        @Override
        public void paintCell(ILayerCell cell, GC gc, Rectangle drawWhere, IConfigRegistry configRegistry) {
            if (this.paintBg) {
                super.paintCell(cell, gc, drawWhere, configRegistry);
            }

//            Canvas canvas=new Canvas(gc);
//            s.setLayout(new FillLayout());
//            Label label= new Label(s, SWT.NONE);
//            label.setForeground(display.getSystemColor(SWT.COLOR_RED));
//            label.setText((String) cell.getDataValue());
//            s.pack();
//            
//            GC widgetGC=new GC(s);
//            s.redraw();
//            
////            widgetGC.setBackground(display.getSystemColor(SWT.COLOR_DARK_YELLOW));
////            widgetGC.fillRectangle(0, 0, 200, 200);
////            widgetGC.drawText("XXX", 0, 0);
//            Point widgetSize = s.getSize();
//            final Image image=new Image(display, widgetSize.x, widgetSize.y);
//            widgetGC.copyArea(image, 0, 0);
//            widgetGC.dispose();
//
//            gc.drawImage(image, drawWhere.x, drawWhere.y);
////            gc.drawText((String) cell.getDataValue(), drawWhere.x, drawWhere.y);
//
//            image.dispose();
            
//            if(rowHeigthNeedsToChange){
//              ILayer layer = cell.getLayer();
//              layer.doCommand(new RowResizeCommand(layer, cell.getRowPosition(), contentHeight + contentToCellDiff));
//            }
        }
      };
    }
  };
}
