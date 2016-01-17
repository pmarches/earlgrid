package com.earlgrid.core.shellcommands;
import java.util.regex.Pattern;

import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;

public class GrepCmdSpecification extends BaseCmdSpecification<GrepCmdArguments> {
  private Pattern userPattern;

  @Override
  public void validateCmdArgumentsBeforeExecution() throws Exception {
    userPattern = Pattern.compile(args.whatToGrep);
  };

  @Override
  public void onUpstreamCommandColumnHeader(TabularOutputColumnHeader parentColumnHeader) throws Exception {
    emit(parentColumnHeader);
  }
  
  @Override
  public void onUpstreamCommandOutputRow(TabularOutputRow newRow) throws Exception {
    boolean rowHasMatch=false;
    String[] inputRow=newRow.getAllCells();
    for(int columnIndex=0; columnIndex<inputRow.length; columnIndex++){
      if(userPattern.matcher(inputRow[columnIndex]).matches()){
        rowHasMatch=true;
        break;
      }
    }
    if(rowHasMatch ^ args.discardMatches){
      emit(new TabularOutputRow(taskId, inputRow));
    }
  }
  
}
