package com.earlgrid.ui.parts;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.LineBorderDecorator;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle.LineStyleEnum;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.theme.DefaultNatTableThemeConfiguration;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;


public class EarlGridTheme extends DefaultNatTableThemeConfiguration {
  private static final String BLUE_LABEL = "foo";
  private static final String BAR_LABEL = "bar";

  public EarlGridTheme() {
    FontData monospaceFont = new FontData("Monospace", 12, SWT.NONE);
    cHeaderBgColor = GUIHelper.COLOR_BLACK;
    cHeaderFgColor = GUIHelper.COLOR_WHITE;
    cHeaderHAlign = HorizontalAlignmentEnum.LEFT;
    cHeaderFont = GUIHelper.getFont(monospaceFont);
    cHeaderCellPainter = new LineBorderDecorator(new TextPainter(), new BorderStyle(1, GUIHelper.COLOR_WHITE, LineStyleEnum.SOLID));
    cHeaderFullSelectionFont = GUIHelper.getFont(monospaceFont);
    cHeaderSelectionFont = GUIHelper.getFont(monospaceFont);

    cornerSelectionFont = GUIHelper.getFont(monospaceFont);
    
    defaultHAlign = HorizontalAlignmentEnum.LEFT;
    defaultFont = GUIHelper.getFont(monospaceFont);
    defaultFgColor = GUIHelper.COLOR_GRAY;
    defaultBgColor = GUIHelper.COLOR_BLACK;
    defaultCellPainter = cellPainter;
    
    selectionAnchorSelectionFgColor = GUIHelper.COLOR_BLACK;
    selectionAnchorSelectionBgColor = GUIHelper.COLOR_GRAY;

    defaultSelectionFgColor = GUIHelper.COLOR_BLACK;
    defaultSelectionBgColor = GUIHelper.COLOR_GRAY;
    defaultSelectionFont = GUIHelper.getFont(monospaceFont);

    renderBodyGridLines = Boolean.FALSE;
  }

  private ICellPainter cellPainter=new TextPainter(){
    @Override
    public void paintCell(ILayerCell cell, org.eclipse.swt.graphics.GC gc, org.eclipse.swt.graphics.Rectangle rectangle, IConfigRegistry configRegistry) {
      super.paintCell(cell, gc, rectangle, configRegistry);
    };
  };

//  @Override
//  public void configureRegistry(IConfigRegistry configRegistry) {
//    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, getDefaultCellStyle(), DisplayMode.NORMAL);
//
//    Style cellStyle = new Style();
//    cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLUE);
//    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, BLUE_LABEL);
//
////    cellStyle = new Style();
////    cellStyle.setAttributeValue(CellStyleAttributes.TEXT_DECORATION, TextDecorationEnum.UNDERLINE_STRIKETHROUGH);
////    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, BAR_LABEL);
//  }
}
