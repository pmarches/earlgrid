package com.earlgrid.core.shellcommands;

import com.earlgrid.core.session.ExecutionHistoryRecord;
import com.earlgrid.core.sessionmodel.TabularOutput;


public class HistoryCmdSpecification extends BaseCmdSpecification<HistoryCmdArguments> {
  @Override
  protected void onThisCommandExecute() throws Exception {
    ExecutionHistoryRecord recalledRecord;
    if(args.historyIndex==null){
      recalledRecord=session.getSessionModel().getHistory().getLastExecutionRecord();
      if(recalledRecord==null){
        throw new NullPointerException("History is empty");
      }
    }
    else{
      if(args.historyIndex>=0){
        recalledRecord=session.getSessionModel().getHistory().get(args.historyIndex);
        if(recalledRecord==null){
          throw new NullPointerException(args.historyIndex+" is an invalid history index");
        }
      }
      else{
        recalledRecord=session.getSessionModel().getHistory().getHistoryRecordFromLast(-args.historyIndex);
        if(recalledRecord==null){
          throw new NullPointerException(args.historyIndex+" is an invalid relative history offset");
        }
      }
    }
    
    //TODO Copy the other outputs
    TabularOutput recalledOut = recalledRecord.getOut();
    emitOutput(recalledOut);
  }
  
}
