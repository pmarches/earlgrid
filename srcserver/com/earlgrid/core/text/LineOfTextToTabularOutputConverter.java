package com.earlgrid.core.text;
//package com.earlgrid.core.text;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.LinkedList;
//import java.util.regex.Pattern;
//
//import com.earlgrid.core.sessionmodel.TabularOutput;
//
//public class LineOfTextToTabularOutputConverter {
//  boolean ignoreBlankLines=false;
//  Pattern columnDelimiterPattern=Pattern.compile("\\s+");
//  public boolean firstRowIsHeader=false;
//  
//  public void convert(InputStream inputStream, TabularOutput tabularOutput) throws IOException {
//    convertLinesToTabularOutputAutoHeader(new BufferedReader(new InputStreamReader(inputStream)), tabularOutput);
//  }
//
//  protected void convertLinesToTabularOutputAutoHeader(BufferedReader lineReader, TabularOutput tabularOutput) throws IOException {
//    int maxNumberOfColumns=0;
//    LinkedList<String[]> allLines=new LinkedList<>();
//    while (true) {
//      String line = lineReader.readLine();
//      if(line==null){
//        break;
//      }
//      String[] splitLines=columnDelimiterPattern.split(line);
//      maxNumberOfColumns=Math.max(maxNumberOfColumns, splitLines.length);
//      allLines.add(splitLines);
//    }
//    
//    String[] headers=new String[maxNumberOfColumns];
//    for(int i=0; i<headers.length; i++){
//      headers[i]=String.format("%c", 'A'+i);
//    }
//    if(firstRowIsHeader){
//      String[] firstRow = allLines.removeFirst();
//      for(int i=0; i<firstRow.length; i++){
//        headers[i]=firstRow[i];
//      }
//    }
//    tabularOutput.getColumnHeader().setColumnHeaders(headers);
//    for(String[] splitLine : allLines){
//      tabularOutput.newRow(splitLine);
//    }
//  }
//
//  protected void convertLinesToTabularOutputHeaderDefinedByFirstLine(BufferedReader lineReader, TabularOutput tabularOutput) throws IOException {
//    if(columnDelimiterPattern==null){
//      tabularOutput.getColumnHeader().setColumnHeaders("A");
//    }
//    boolean hasHeadersBeenSet=false;
//    
//    while (true) {
//      String line = lineReader.readLine();
//      if(line==null){
//        break;
//      }
//      if(columnDelimiterPattern!=null){
//        String[] splitLines=columnDelimiterPattern.split(line);
//        if(hasHeadersBeenSet==false){
//          hasHeadersBeenSet=true;
//          if(firstRowIsHeader){
//            firstRowIsHeader=false;
//            tabularOutput.getColumnHeader().setColumnHeaders(splitLines);
//            continue;
//          }
//          else{
//            String[] headers=new String[splitLines.length];
//            for(int i=0; i<headers.length; i++){
//              headers[i]=String.format("%c", 'A'+i);
//            }
//            tabularOutput.getColumnHeader().setColumnHeaders(headers);
//          }
//        }
//        tabularOutput.newRow(splitLines);
//      }
//      else{
//        tabularOutput.newRow(line);
//      }
//    }
//  }
//
//  public Thread newThread(final InputStream inputStream, final TabularOutput tabularOutput){
//    return new Thread(){
//      @Override
//      public void run() {
//        try {
//          LineOfTextToTabularOutputConverter.this.convert(inputStream, tabularOutput);
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//      }
//    };
//  }
//}
