package com.earlgrid.core.shellcommands.coreutils;
//package com.earlgrid.core.shellcommands;
//import java.util.List;
//
//import com.earlgrid.core.execution.TaskExitStatus;
//
//import com.beust.jcommander.Parameter;
//import com.beust.jcommander.Parameters;
//
//
//@Parameters(commandNames="xargs", commandDescription="Constructs parameter lists and runs commands.")
//public class XargsCmdSpecification extends BaseCmdSpecification {
//  @Parameter(names="-I", arity=1, description=" Inserts each line of standard input as an argument for the Command parameter, inserting it in Argument for each occurrence of ReplaceString")
//  String extraArgument; 
//  
//  @Parameter(names="-E", arity=1, description="Specifies a logical EOF string to replace the default underscore(_ ). The xargs command reads standard input until either EOF or the specified string is reached.")
//  String eofString;
//  
//  @Parameter(variableArity=true)
//  List<String> cmdAndParamsToExecute;
//
//  @Override
//  public TaskExitStatus performComputation() throws Exception {
//    System.out.println("Do the xargs!");
//  }
//}
