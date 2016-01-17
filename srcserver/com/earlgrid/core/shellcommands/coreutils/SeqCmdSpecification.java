package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class SeqCmdSpecification extends BaseCmdSpecification<SeqCmdArguments> {
  @Override
  protected void onThisCommandExecute() throws Exception {
    for(int i=args.start; i<=args.end; i+=args.step){
      if(Thread.interrupted()){
        break;
      }
      emit(new TabularOutputRow(taskId, Integer.toString(i)));
    }
  }

}
