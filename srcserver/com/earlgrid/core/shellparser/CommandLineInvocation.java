package com.earlgrid.core.shellparser;

import com.earlgrid.core.serverside.EarlGridPb.PbCommandLineUserAction;
import com.earlgrid.core.serverside.EarlGridPb.PbCommandLineUserAction.UserRequestedActionKind;
import com.earlgrid.core.session.CaretPosition;

public class CommandLineInvocation {
  String commandLine;
  CaretPosition caretPosition;
  public UserRequestedActionKind whatWasinvoked;

  public CommandLineInvocation(String commandLine, CaretPosition caretPosition, UserRequestedActionKind userRequestedActionKind){
    this.commandLine=commandLine;
    this.caretPosition=caretPosition;
    this.whatWasinvoked=userRequestedActionKind;
  }

  public CommandLineInvocation(String commandStr) {
    this(commandStr, new CaretPosition(commandStr.length()), UserRequestedActionKind.EXECUTE);
  }

  /**
   * @return The command index where the CommandLineAction was invoked
   */
  public int getTargetCommandIndex(){
    return -1;
  }

  /**
   * @return The argument index where the action was invoked
   */
  public int getTargetArgumentIndex(){
    return -1;
  }
  
//    INVOKE_EXECUTE, //Enter
//    INVOKE_AUTO_COMPLETE, //Tab, or Ctrl-Space?
//    INVOKE_FILE_DIALOG,  //Ctrl-O
//    INVOKE_BOOKMARKS,  //Should this just be in the file dialog?
//    INVOKE_ABORT, //Ctrl-C
//    INVOKE_LARGE_EDITOR, //For hairy commands
//    INVOKE_HELP, //Shows the help dialog for the current context

  public static CommandLineInvocation newFromProtoBuf(PbCommandLineUserAction userActionMessage) {
    String cmdLineToExecute=userActionMessage.getCommandLine();
    CaretPosition caretPosition=new CaretPosition(userActionMessage.getCaretPosition());
    CommandLineInvocation invocation=new CommandLineInvocation(cmdLineToExecute, caretPosition, userActionMessage.getActionKind());
    return invocation;
  }
}
