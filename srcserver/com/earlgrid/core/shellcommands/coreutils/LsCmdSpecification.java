package com.earlgrid.core.shellcommands.coreutils;
import java.io.File;

import org.apache.commons.cli.Options;

import com.earlgrid.core.sessionmodel.TabularOutputColumn;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TabularOutput.ColumnType;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;



public class LsCmdSpecification extends BaseCmdSpecification<LsCmdArguments> {
  @Override
  public void onThisCommandExecute() throws Exception {
    int numberOfColumns=1;
    
    if(args.longListing){
      numberOfColumns=2;
    }
    else{
//      numberOfColumns=session.getScreenSizeInChar().x/20; //TODO
    }
    TabularOutputColumn[] columns=new TabularOutputColumn[numberOfColumns];
    columns[0]=new TabularOutputColumn("Name", ColumnType.STRING);

    if(args.longListing){
      columns[1]=new TabularOutputColumn("Size", ColumnType.NUMBER);
    }
    emit(new TabularOutputColumnHeader(taskId, columns));
    
    if(args.directoryToList==null){
      args.directoryToList=session.getSessionModel().getCurrentWorkingDirectory();
    }
    File dirFile = args.directoryToList.toFile();
    if(dirFile.exists()==false){
      throw new Exception("The directory '"+args.directoryToList+"' does not exist");
    }
    
    for(File f : dirFile.listFiles()){
      String fileName=f.getName();
      boolean isHiddenFile=fileName.charAt(0)=='.';
      if(isHiddenFile && args.showAllFiles==false){
        continue;
      }
      
      //TODO Think of a better way to do coloring than having a cell class 
      String[] rowCells=new String[numberOfColumns];
      rowCells[0]=fileName;
      if(args.longListing){
        rowCells[1]=Long.toString(f.length());
      }
      emit(new TabularOutputRow(taskId, rowCells));
    }
  }

  public static Options getParser(){
    Options options = new Options();
    options.addOption("t", false, "display current time");
    return options;
  }

}
