package com.earlgrid.core.shellcommands.coreutils;

import java.util.Map;

import com.earlgrid.core.sessionmodel.TabularOutput.ColumnType;
import com.earlgrid.core.sessionmodel.TabularOutputColumn;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;
import com.earlgrid.core.shellcommands.coreutils.SessionCmdArguments.SESSION_SUB_COMMAND;

public class SessionCmdSpecification extends BaseCmdSpecification<SessionCmdArguments> {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(SessionCmdSpecification.class);

  @Override
  public void onUpstreamCommandOutputRow(TabularOutputRow outputRow) throws Exception {
    //Called only for SET
    if(args.subCommand!=SESSION_SUB_COMMAND.SET){
      throw new Exception("Expected SET command");
    }
    Object value=outputRow.getCellAtColumn(0);
    session.getSessionModel().sessionVariables.put(args.keyName, value);
  }
  
  @Override
  public void onThisCommandExecute() throws Exception {
    if(args.subCommand==SESSION_SUB_COMMAND.SET){
      super.onThisCommandExecute();
    }
    else if(args.subCommand==SESSION_SUB_COMMAND.LS){
      executeSessionList();
    }
    else if(args.subCommand==SESSION_SUB_COMMAND.GET){
      emit(new TabularOutputColumnHeader(taskId, new TabularOutputColumn("value", ColumnType.STRING)));
      Object value = session.getSessionModel().sessionVariables.get(args.keyName);
      if(value!=null){
        emit(new TabularOutputRow(taskId, value.toString()));
      }
    }
    else if(args.subCommand==SESSION_SUB_COMMAND.RM){
      session.getSessionModel().sessionVariables.remove(args.keyName);
    }
    
  }

  private void executeSessionList() throws InterruptedException {
    emit(new TabularOutputColumnHeader(taskId, new TabularOutputColumn("key", ColumnType.STRING), new TabularOutputColumn("value", ColumnType.STRING)));
    for(Map.Entry<String, Object> entry : session.getSessionModel().sessionVariables.entrySet()){
      emit(new TabularOutputRow(taskId, entry.getKey(), entry.getValue().toString()));
    }
  }
  
}
