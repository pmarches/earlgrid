package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.sessionmodel.CmdBeginStatus;
import com.earlgrid.core.sessionmodel.TabularOutput;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class CollectingCmdSpecification<T> extends BaseCmdSpecification<T> {
  protected TabularOutput outputCollector;

  @Override
  public void onUpstreamCommandBegun(CmdBeginStatus parentCommandBegun) throws Exception {
    outputCollector=new TabularOutput(taskId);
  }
  
  @Override
  public void onUpstreamCommandColumnHeader(TabularOutputColumnHeader parentColumnHeader) throws Exception {
    outputCollector.columnHeader=parentColumnHeader;
  }
  
  @Override
  public void onUpstreamCommandOutputRow(TabularOutputRow rowFromParent) throws Exception {
    outputCollector.rows.add(rowFromParent);
  }
}
