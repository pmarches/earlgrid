package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;
import com.earlgrid.core.text.LineOfTextToSingleColumnOutputConverter;

public class ExternalProcessCmdSpecification extends BaseCmdSpecification<ExternalProcessCmdArguments> {
  Thread stdinThread=null;

  @Override
  public void validateCmdArgumentsBeforeExecution() throws Exception {
    if(args.programAndArgs==null || args.programAndArgs.length==0){
      throw new Exception("exec requires programs and arguments");
    }
  }
  
//  @Override
//  protected void onUpstreamCommandBegun(TaskBeginStatus parentCommandBegun) throws Exception {
//    TabularOutputToLineOfTextConverter tabularToLineConverter=new TabularOutputToLineOfTextConverter();
//    stdinThread=tabularToLineConverter.newThread(inputComputation.out, ps.getOutputStream());
//    stdinThread.start();
//  }

  protected void onThisCommandExecute() throws Exception {
    String[] argv=args.programAndArgs[0].split(" +");
    ProcessBuilder psBuilder = new ProcessBuilder(argv);
    psBuilder.directory(session.getSessionModel().getCurrentWorkingDirectory().toFile());
    Process ps=psBuilder.start();
    
    super.onThisCommandExecute();

    LineOfTextToSingleColumnOutputConverter stdoutLinesToTabularConverter=new LineOfTextToSingleColumnOutputConverter();
    Thread stdoutCopyThread=stdoutLinesToTabularConverter.newThread(ps.getInputStream(), this);
    stdoutCopyThread.start();
    
    LineOfTextToSingleColumnOutputConverter stderrLinesToTabularConverter=new LineOfTextToSingleColumnOutputConverter();
    Thread stderrThread=stderrLinesToTabularConverter.newThread(ps.getErrorStream(), this);
    stderrThread.start();

    if(stdinThread!=null){
      stdinThread.join();
    }
    stdoutCopyThread.join();
    stderrThread.join();

    int subProcessReturnCode=ps.waitFor();
    if(subProcessReturnCode!=0){
      //FIXME Maybe the return code should be outpued to the err stream?
      emit(new TabularOutputRow(taskId, Integer.toString(subProcessReturnCode), "'"+argv[0]+"' returned "+subProcessReturnCode));
    }
  }
}
