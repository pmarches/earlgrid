package org;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultSelectionStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle.LineStyleEnum;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class SingleNatTable extends Composite {

  public SingleNatTable(Composite parent, int style, SessionOutput sessionOutput)  {
    super(parent, style|SWT.CLOSE);
    setLayout(new GridLayout(1, false));

    DataLayer bodyDataLayer = new SessionOutputBodyDataLayer(createBodyDataProvider(sessionOutput));
    SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer);
    ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);
    viewportLayer.setRegionName(GridRegion.BODY);

    NatTable natTable = new NatTable(this, viewportLayer, false);
    natTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    natTable.getConfigRegistry().registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new TextPainter(true,true));
    natTable.getConfigRegistry().registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES, false);

    Font monospacefont = new Font(getDisplay(), new FontData("Courier New", 10, SWT.NONE));
    
    // Setup selection styling
    DefaultSelectionStyleConfiguration selectionStyle = new DefaultSelectionStyleConfiguration();
    selectionStyle.selectionFont = monospacefont;
    selectionStyle.selectionBgColor = getBackground();
    selectionStyle.selectionFgColor = GUIHelper.COLOR_RED;
    selectionStyle.anchorBorderStyle = new BorderStyle(1, GUIHelper.COLOR_DARK_GRAY, LineStyleEnum.SOLID);
    selectionStyle.anchorBgColor = GUIHelper.getColor(65, 113, 43);
    selectionStyle.selectedHeaderBgColor = GUIHelper.getColor(156, 209, 103);
    natTable.addConfiguration(selectionStyle);
    
    
    Style cellStyle = new Style();
    cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.getBackground());
    cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, this.getForeground());
    cellStyle.setAttributeValue(CellStyleAttributes.FONT, monospacefont);
    //FIXME dispose of Font (should keep all of the Fonts at the Application level?
    cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    cellStyle.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, VerticalAlignmentEnum.TOP);
//    cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE,this.borderStyle);
    natTable.getConfigRegistry().registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle);
    
    natTable.configure();

  }

  private static final int NB_COLUMNS = 80;
  private IDataProvider createBodyDataProvider(final SessionOutput sessionOutput) {
    return new IDataProvider() {
      @Override
      public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
      }
      
      @Override
      public int getRowCount() {
        return sessionOutput.totalNumberOfRows;
      }
      
      @Override
      public Object getDataValue(int columnIndex, int rowIndex) {
        return toString(columnIndex);
      }
      
      private String toString(int number) {
        return Integer.toString(number%36, 36);
      }

      @Override
      public int getColumnCount() {
        return NB_COLUMNS;
      }
    };
  }

  class SessionOutputBodyDataLayer extends DataLayer {
    public SessionOutputBodyDataLayer(IDataProvider bodyDataProvider) {
      super(bodyDataProvider);
      setColumnPercentageSizing(true);
    }
  }
}
