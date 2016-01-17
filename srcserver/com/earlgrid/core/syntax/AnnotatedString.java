package com.earlgrid.core.syntax;

public class AnnotatedString {
  public AnnotatedString(String string) {
    theString=string;
  }
  
  String theString;
  boolean autoCompleteRequested;
  int offsetInTopCommandLine;
}
