package com.earlgrid.core.syntax;

import java.util.Arrays;

public class AnnotatedCommandAndArgument {
  public AnnotatedString[] rawCommandAndArguments;

  @Override
  public String toString() {
    return getClass().getSimpleName()+" [commandAndArguments=" + Arrays.toString(rawCommandAndArguments)+"]";
  }
  
}
