package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.serverside.EarlGridPb.PbClientClipboard;
import com.earlgrid.core.serverside.EarlGridPb.PbClientClipboard.ClipboardOperation;
import com.earlgrid.core.sessionmodel.CmdExitStatus;
import com.earlgrid.core.sessionmodel.TabularOutputColumn;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TabularOutput.ColumnType;


public class ClipboardCmdSpecification extends CollectingCmdSpecification<ClipboardCmdArguments> {
  @Override
  protected void onThisCommandExecute() throws Exception {
    if(inputQueue!=null){
      super.onThisCommandExecute();
    }
    else{
      PbClientClipboard.Builder clipboardRequest=PbClientClipboard.newBuilder();
      clipboardRequest.setOperation(ClipboardOperation.READ_CLIPBOARD);
      PbClientClipboard clipboardResponse=session.requestClientClipboard(clipboardRequest);
      if(clipboardResponse.hasClipboardContent()){
        emit(new TabularOutputColumnHeader(taskId, new TabularOutputColumn("Clipboard taskContent", ColumnType.STRING)));
        String userEnteredValue=clipboardResponse.getClipboardContent();
        for(String line:userEnteredValue.split("\n|\r")){
          emit(new TabularOutputRow(taskId, line));
        }
      }
    }
  }
  
  @Override
  public void onUpstreamCommandFinished(CmdExitStatus upstreamCommandExitStatus) throws Exception {
    //TODO Need to determine if this command was invoked with data being piped in, or should it read from the standard input window. 
    //     Actually, all commands should do this. So this behaviour needs to be generalized to all commands.
    
    PbClientClipboard.Builder clipboardRequest=PbClientClipboard.newBuilder();
    clipboardRequest.setOperation(ClipboardOperation.WRITE_CLIPBOARD);
    StringBuffer newClipboardContent=new StringBuffer();
    for(int i=0;i< outputCollector.getRowCount(); i++){
      newClipboardContent.append(super.outputCollector.getRow(i).getCellAtColumn(0));
      newClipboardContent.append(System.lineSeparator());
    }
    clipboardRequest.setClipboardContent(newClipboardContent.toString());
    PbClientClipboard clipboardResponse=session.requestClientClipboard(clipboardRequest);
  }
}
