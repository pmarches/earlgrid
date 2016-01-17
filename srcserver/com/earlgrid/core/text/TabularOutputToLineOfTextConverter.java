package com.earlgrid.core.text;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.codehaus.jparsec.internal.util.Strings;

import com.earlgrid.core.sessionmodel.TabularOutput;
import com.earlgrid.core.sessionmodel.TabularOutputColumn;

public class TabularOutputToLineOfTextConverter {
  boolean convertHeader=false;
  String columnDelimiter="\t";
  
  public void convert(TabularOutput tabularOutput, OutputStream outputStream) throws IOException {
    convert(tabularOutput, new BufferedWriter(new OutputStreamWriter(outputStream)));
  }

  public void convert(TabularOutput tabularOutput, BufferedWriter writer) throws IOException {
    if(convertHeader){
      for(int i=0; i<tabularOutput.columnHeader.size(); i++){
        TabularOutputColumn col=tabularOutput.getColumnHeader().get(i);
        writer.write(col.name);
        if(i+1<tabularOutput.getColumnHeader().size()){
          writer.write(columnDelimiter);
        }
      }
      writer.newLine();
    }

    for(int i=0; i<tabularOutput.getRowCount(); i++) {
      String[] rowsStr=tabularOutput.getRow(i).getAllCells();
      String line=Strings.join(columnDelimiter, rowsStr);
      writer.write(line);
      writer.newLine();
    }
    writer.close();
  }

  public Thread newThread(final TabularOutput tabularOutput, final OutputStream outputStream){
    return new Thread(){
      @Override
      public void run() {
        try {
          TabularOutputToLineOfTextConverter.this.convert(tabularOutput, outputStream);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
  }

}
