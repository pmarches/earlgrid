package com.earlgrid.core.shellcommands.coreutils;

import java.util.Collections;
import java.util.Comparator;

import com.earlgrid.core.sessionmodel.CmdExitStatus;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;

public class SortCmdSpecification extends CollectingCmdSpecification<SortCmdArguments> {
  protected int[] columnsIndexesToUseAsSortCriteria;

  @Override
  public void onUpstreamCommandColumnHeader(TabularOutputColumnHeader parentColumnHeader) throws Exception {
    super.onUpstreamCommandColumnHeader(parentColumnHeader);

    columnsIndexesToUseAsSortCriteria = parentColumnHeader.resolveColumnNamesToColumnIndexes(args.columnNameSortCriteria, parentColumnHeader);
    if(columnsIndexesToUseAsSortCriteria.length==0){
      columnsIndexesToUseAsSortCriteria=new int[parentColumnHeader.size()];
      for(int i=0; i<columnsIndexesToUseAsSortCriteria.length; i++){
        columnsIndexesToUseAsSortCriteria[i]=i;
      }
    }
  }
  
  @Override
  public void onUpstreamCommandFinished(CmdExitStatus input) throws Exception {
    Comparator<TabularOutputRow> comparator = createComparatorFromArguments();
    Collections.sort(outputCollector.rows, comparator);
    emitOutput(outputCollector);
  }
  
  private Comparator<TabularOutputRow> createComparatorFromArguments() {
    Comparator<TabularOutputRow> comparator=new Comparator<TabularOutputRow>() {
      @Override
      public int compare(TabularOutputRow row1, TabularOutputRow row2) {
        for(int i=0; i<columnsIndexesToUseAsSortCriteria.length; i++){
          int ret;
          if(args.caseInsensitive){
            ret=row1.getCellAtColumn(i).compareToIgnoreCase(row2.getCellAtColumn(i));
          }
          else{
            ret=row1.getCellAtColumn(i).compareTo(row2.getCellAtColumn(i));
          }
          if(args.reverseSort){
            ret=-ret;
          }
          if(ret!=0){
            return ret;
          }
        }
        return 0;
      }
    };
    return comparator;
  }

}
