package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.sessionmodel.TabularOutputColumn;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TabularOutput.ColumnType;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class PwdCmdSpecification extends BaseCmdSpecification<PwdCmdArguments> {
  @Override
  protected void onThisCommandExecute() throws Exception {
    emit(new TabularOutputColumnHeader(taskId, new TabularOutputColumn("cwd", ColumnType.STRING)));

    TabularOutputRow wcRow=new TabularOutputRow(taskId, session.getSessionModel().getCurrentWorkingDirectory().toAbsolutePath().toString());
    emit(wcRow);
  }
}
