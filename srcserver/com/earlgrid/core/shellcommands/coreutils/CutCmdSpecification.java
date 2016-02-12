package com.earlgrid.core.shellcommands.coreutils;

import java.util.ArrayList;

import com.earlgrid.core.sessionmodel.TabularOutputColumn;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TabularOutputSelection;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;


public class CutCmdSpecification extends BaseCmdSpecification<CutCmdArguments> {
  int[] columnIndexesToAccept;
  
  @Override
  public void validateCmdArgumentsBeforeExecution() throws Exception {
    if(args.selectionExpression!=null){
    }
    else if(args.columnNameFilter.isEmpty()){
      throw new Exception("cut requires at least one pattern. You specified none");
    }
  };

  @Override
  public void onUpstreamCommandColumnHeader(TabularOutputColumnHeader parentColumnHeader) throws Exception {
    ArrayList<Integer> keptColumnIndexes=new ArrayList<>();

    if(args.columnNameFilter.isEmpty()==false){
      for(int columnIndex=0; columnIndex<parentColumnHeader.size(); columnIndex++){
        boolean columnNameMatchesFilter=(args.columnNameFilter.contains(parentColumnHeader.get(columnIndex).name));
        if(columnNameMatchesFilter ^ args.discardMatches){
          keptColumnIndexes.add(columnIndex);
        }
      }
    }
    else if(args.selectionExpression!=null){
      int numberOfParentColumns=parentColumnHeader.size();
      TabularOutputSelection selection=TabularOutputSelection.newFromString(args.selectionExpression);
      for(Integer colIndex : selection.getColumnSelection()){
        if(colIndex.intValue()<numberOfParentColumns){
          keptColumnIndexes.add(colIndex);
        }
      }
    }

    if(keptColumnIndexes.isEmpty()){
      super.stopThisCommand();
      return;
    }

    TabularOutputColumn[] outputColumnHeaders=new TabularOutputColumn[keptColumnIndexes.size()];
    columnIndexesToAccept=new int[keptColumnIndexes.size()];
    for(int i=0; i<keptColumnIndexes.size(); i++){
      columnIndexesToAccept[i]=keptColumnIndexes.get(i);
      outputColumnHeaders[i]=parentColumnHeader.get(keptColumnIndexes.get(i));
    }

    TabularOutputColumnHeader newColumns=new TabularOutputColumnHeader(taskId);
    newColumns.setColumnHeaders(outputColumnHeaders);
    emit(newColumns);
  }
  
  @Override
  public void onUpstreamCommandOutputRow(TabularOutputRow newRow) throws Exception {
    String[] acceptedCells=new String[columnIndexesToAccept.length];
    for (int i = 0; i < columnIndexesToAccept.length; i++) {
      acceptedCells[i]=newRow.getCellAtColumn(columnIndexesToAccept[i]);
    }
    TabularOutputRow outputRow=new TabularOutputRow(taskId, acceptedCells);
    emit(outputRow);
  }
  
}
