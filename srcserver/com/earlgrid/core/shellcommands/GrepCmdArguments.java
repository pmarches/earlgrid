package com.earlgrid.core.shellcommands;


public class GrepCmdArguments extends BaseCmdArguments<GrepCmdArguments> {
  boolean discardMatches=false;
  public String whatToGrep;


  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    for (int i = 0; i < argumentsArray.length; i++) {
      String arg = argumentsArray[i];
      if("-v".equals(arg)){
        discardMatches=true;
      }
      else{
        if(whatToGrep!=null){
          throw new Exception("grep accepts at most one pattern. You specified the pattern ''"+arg+"' but had already specified '"+whatToGrep+"'");
        }
        whatToGrep=arg;
      }
    }
  }

  @Override
  public BaseCmdSpecification<GrepCmdArguments> newCmdSpecification() {
    return new GrepCmdSpecification();
  }

}
