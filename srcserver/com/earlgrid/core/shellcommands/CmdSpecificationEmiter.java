package com.earlgrid.core.shellcommands;

abstract public class CmdSpecificationEmiter {
  abstract public void startEmitting();
  
  abstract public void beginOutputStream(/*OutputStreamMeta*/);
  abstract public void endOutputStream(/*OutputStreamMeta*/);

  abstract public void emitOutputSchema(/**/); //Optional in the case of hiearchical data
  abstract public void emitOutputRow(/**/);
  abstract public void emitOutputAttribute(/**/);
}
