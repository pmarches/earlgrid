//package org;
//
//import java.util.Arrays;
//
//import org.eclipse.nebula.widgets.grid.Grid;
//import org.eclipse.nebula.widgets.grid.GridColumn;
//import org.eclipse.nebula.widgets.grid.GridItem;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.graphics.Font;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Event;
//import org.eclipse.swt.widgets.Listener;
//
//public class NebulaGrid {
//  final Font monospaceBold;
//  final Font monospace;
//  private Grid grid;
//  int NB_COLUMN_CELLS=50;
//  
//  SessionOutput outputs=new SessionOutput();
//  
//  public NebulaGrid(Composite parent) {
//    monospaceBold=new Font(parent.getDisplay(), "Courrier", 10, SWT.BOLD);
//    monospace=new Font(parent.getDisplay(), "Courrier", 8, SWT.BOLD);
//    
//    grid = new Grid(parent,SWT.BORDER | SWT.V_SCROLL | SWT.MULTI |  SWT.VIRTUAL);
//    grid.setCellSelectionEnabled(true);
//    grid.setLinesVisible(false);
//    grid.setHeaderVisible(true);
//    grid.addListener(SWT.SetData, dataListener);
//    grid.setFont(monospaceBold);
//
//    for(int i=0; i<NB_COLUMN_CELLS; i++){
//      GridColumn column = new GridColumn(grid,SWT.NONE);
//      column.setText(""+i);
//      column.setWidth(20);
//    }
//    
//    grid.setItemCount(outputs.totalNumberOfRows);
//  }
//
//  Listener dataListener=new Listener() {
//    @Override
//    public void handleEvent(Event event) {
//      GridItem item=(GridItem) event.item;
//      item.setHeight(20);
//      
//      int rowIndexFromOutput=-1;
//      int outputIndex=Arrays.binarySearch(outputs.cumulativeNumberOfRowsPerOutput, item.getRowIndex());
//      if(outputIndex<0){
//        outputIndex=(outputIndex+1)*-1-1;
//        rowIndexFromOutput=item.getRowIndex()-outputs.cumulativeNumberOfRowsPerOutput[outputIndex]-1;
//      }
//      RunOutput runOutput=outputs.outputs[outputIndex];
//      if(rowIndexFromOutput==-1){
//        item.setColumnSpan(0, NB_COLUMN_CELLS);
//        item.setText(0, runOutput.command);
//        
////        GridEditor editor=new GridEditor(grid);
////        editor.minimumWidth = 50;
////        editor.grabVertical=true;
////        editor.horizontalAlignment=SWT.RIGHT;
////
////        Composite btnPanel=new Composite(grid, SWT.NONE);
////        btnPanel.setLayout(new FillLayout(SWT.HORIZONTAL));
////        
////        Button menuBtn=new Button(btnPanel, SWT.PUSH);
////        menuBtn.setText("V "+outputIndex);
////        menuBtn.pack();
////        Button closeBtn=new Button(btnPanel, SWT.PUSH);
////        closeBtn.setText("X ");
////
////        btnPanel.pack();
////        editor.setEditor(btnPanel, item, 0);
//      }
//      else{
//        String[] rowContent=runOutput.rowCol[rowIndexFromOutput];
//        int nbCellsPerColumn=NB_COLUMN_CELLS/runOutput.numberOfColumns;
//        for(int i=0; i<rowContent.length; i++){
//          int columnThatwillHaveContent=i*nbCellsPerColumn;
//          item.setColumnSpan(columnThatwillHaveContent, nbCellsPerColumn-1);
//          item.setText(columnThatwillHaveContent, rowContent[i]);
//        }
//      }
//    }
//  };
//
//  public void createSpanningGrid() {
//  }
////  void createNatGrid(Composite parent) {
////    grid.setRowsResizeable(true);
////    grid.setItemCount(10_000);
////  grid.addListener(SWT.SetData, 
////  Listener dataListener=new Listener() {
////    @Override
////    public void handleEvent(Event event) {
////      GridItem item=(GridItem) event.item;
////      item.setHeight(item.getRowIndex()%200+10);
////
////      GridEditor editor=new GridEditor(grid);
////      editor.minimumWidth = 50;
////      editor.grabHorizontal=true;
////      editor.grabVertical=true;
////
////      Button myBtn=new Button(grid, SWT.PUSH);
////      myBtn.setText("Row "+item.getRowIndex());
////      myBtn.pack();
////      editor.setEditor(myBtn, item, 0);
////    }
////  };
////    
////    GridColumn columnA = new GridColumn(grid,SWT.NONE);
////    columnA.setText("A");
////    columnA.setWidth(100);
////  }
//
//
//}
