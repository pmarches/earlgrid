package com.earlgrid.core.text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.earlgrid.core.sessionmodel.TabularOutputColumn;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TabularOutput.ColumnType;
import com.earlgrid.core.shellcommands.coreutils.ExternalProcessCmdSpecification;

public class LineOfTextToSingleColumnOutputConverter {
  public Thread newThread(final InputStream inputStream, final ExternalProcessCmdSpecification externalProcessCmdSpecification){
    return new Thread(){
      @Override
      public void run() {
        try {
          LineOfTextToSingleColumnOutputConverter.this.convert(inputStream, externalProcessCmdSpecification);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
  }

  public void convert(InputStream inputStream, ExternalProcessCmdSpecification externalProcessCmdSpecification) throws Exception {
    externalProcessCmdSpecification.emit(new TabularOutputColumnHeader(externalProcessCmdSpecification.getTaskId(), new TabularOutputColumn("A", ColumnType.STRING)));

    BufferedReader lineReader=new BufferedReader(new InputStreamReader(inputStream));
    while (true) {
      String line = lineReader.readLine();
      if(line==null){
        break;
      }
      
      externalProcessCmdSpecification.emit(new TabularOutputRow(externalProcessCmdSpecification.getTaskId(), line));
    }
  }
}
