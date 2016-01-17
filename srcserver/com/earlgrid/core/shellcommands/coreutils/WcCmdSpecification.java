package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.sessionmodel.CmdExitStatus;
import com.earlgrid.core.sessionmodel.TabularOutputColumn;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TabularOutput.ColumnType;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;


public class WcCmdSpecification extends BaseCmdSpecification<WcCmdArguments>  {
  int numberOfLines=0;

  @Override
  public void onUpstreamCommandFinished(CmdExitStatus exitStatus) throws Exception {
    emit(new TabularOutputColumnHeader(taskId, new TabularOutputColumn("count", ColumnType.NUMBER)));

    TabularOutputRow wcRow=new TabularOutputRow(taskId, Integer.toString(numberOfLines));
    emit(wcRow);
  }

  @Override
  public void onUpstreamCommandOutputRow(TabularOutputRow outputElement) {
    numberOfLines++;
  }
}
