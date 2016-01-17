package com.earlgrid.core.shellcommands.coreutils;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import com.earlgrid.core.sessionmodel.ChangeCurrentWorkingDirectorySessionModelEvent;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class CdCmdSpecification extends BaseCmdSpecification<CdCmdArguments> {
  //TODO Keep this as a list of previous paths
  static Path previousWorkingDirectory; //TODO Find a way for commands to customize the sessionModel? 
  
  @Override
  public void onThisCommandExecute() throws Exception {
    Path newPath;
    if(args.paths.length==0){
      newPath=FileSystems.getDefault().getPath(System.getProperty("user.home"));
    }
    else if("-".equals(args.paths[0])){
      if(previousWorkingDirectory==null){
        emit(new TabularOutputRow(taskId, "No previous directory")); //TODO Add return code?
        return;
      }
      newPath=previousWorkingDirectory;
      previousWorkingDirectory=null;
    }
    else{
      newPath=FileSystems.getDefault().getPath(args.paths[0]);
    }
    Path newWorkingDirectory;
    if(newPath.isAbsolute()){
      newWorkingDirectory=newPath.normalize();
    }
    else{
      newWorkingDirectory=session.getSessionModel().getCurrentWorkingDirectory().resolve(newPath).normalize();
    }
    
    File newWorkingDirectoryFile=newWorkingDirectory.toFile();
    if(newWorkingDirectoryFile.isDirectory()){
      previousWorkingDirectory=session.getSessionModel().getCurrentWorkingDirectory();
      session.getSessionModel().onChangeCurrentWorkingDirectory(new ChangeCurrentWorkingDirectorySessionModelEvent(taskId, newWorkingDirectory));
    }
    else if(newWorkingDirectoryFile.exists()==false){
      emit(new TabularOutputRow(taskId, "The directory '"+newWorkingDirectory.toString()+"' does not exist")); //TODO Add return code?
    }
    else{
      emit(new TabularOutputRow(taskId, "'"+newWorkingDirectory.toString()+"' is not a directory")); //TODO Add return code?
    }
  }
}
