package com.earlgrid.core.shellcommands;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.List;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.earlgrid.core.sessionmodel.CmdBeginStatus;
import com.earlgrid.core.sessionmodel.CmdExitStatus;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;


public class CSVCmdSpecification extends BaseCmdSpecification<CSVCmdArguments> {
  private CsvListWriter csvWriter;

  @Override
  public void validateCmdArgumentsBeforeExecution() throws Exception {
    if(args.inputCSVFilePath==null && args.outputCSVFilePath==null){
      throw new Exception("CSV requires either the -i or -o option. You specified neither.");
    }
    if(args.inputCSVFilePath!=null && args.outputCSVFilePath!=null){
      throw new Exception("CSV requires either the -i or -o option. You specified both.");
    }
    if(args.inputCSVFilePath==null && args.hasHeader){
      throw new Exception("The -h option requires the -i option");
    }
  };
  
  @Override
  public void onUpstreamCommandBegun(CmdBeginStatus upstreamCommandBegun) throws Exception {
    if(args.outputCSVFilePath!=null){
      Path cwd = session.getSessionModel().getCurrentWorkingDirectory();
      File csvFile=cwd.resolve(args.outputCSVFilePath).toFile();
      
      csvWriter = new CsvListWriter(new FileWriter(csvFile), CsvPreference.STANDARD_PREFERENCE);
    }
    else {
    }
  }
  
  @Override
  public void onUpstreamCommandColumnHeader(TabularOutputColumnHeader upstreamColumnHeader) throws Exception {
    csvWriter.writeHeader(upstreamColumnHeader.getColumnNames());
  }
  
  @Override
  public void onUpstreamCommandOutputRow(TabularOutputRow upstreamRow) throws Exception {
    csvWriter.write(upstreamRow.getAllCells());
  }
  
  @Override
  public void onUpstreamCommandFinished(CmdExitStatus upstreamCommandExitStatus) throws Exception {
    csvWriter.close();
  }

  @Override
  protected void onThisCommandExecute() throws Exception {
    if(args.outputCSVFilePath!=null){
      super.onThisCommandExecute();
    }
    else{
      readFromCSVFile();
    }
  }
  
  private void readFromCSVFile() throws Exception {
    Path cwd = session.getSessionModel().getCurrentWorkingDirectory();
    File csvFile=cwd.resolve(args.inputCSVFilePath).toFile();

    CsvListReader csvReader = new CsvListReader(new FileReader(csvFile), CsvPreference.STANDARD_PREFERENCE);
    if(args.hasHeader){
      String[] csvHeaders=csvReader.getHeader(true);
      emit(new TabularOutputColumnHeader(taskId).setColumnHeaders(csvHeaders));
    }
    
    while(true){
      List<String> csvLine = csvReader.read();
      if(csvLine==null){
        break;
      }
      String[] csvArray=new String[csvLine.size()];
      csvLine.toArray(csvArray);
      emit(new TabularOutputRow(taskId, csvArray));
    }
    csvReader.close();
  }

}
