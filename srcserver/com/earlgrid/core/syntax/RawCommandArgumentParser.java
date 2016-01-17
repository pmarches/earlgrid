package com.earlgrid.core.syntax;

import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Token;
import org.codehaus.jparsec.functors.Map;

public class RawCommandArgumentParser {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(RawCommandArgumentParser.class);
  String source;
  int autoCompleteInvocationOffset;
  
  public RawCommandArgumentParser(String commandLine, int autoCompleteInvocationOffset) {
    this.source=commandLine;
    this.autoCompleteInvocationOffset=autoCompleteInvocationOffset;
  }
  
  public AnnotatedCmdLine parse(){
    AnnotatedCmdLine rawArgs=topLevel.parse(this.source);
    return rawArgs;
  }
  
//  Parser<String> spaceDelimited=Scanners.WHITESPACES.skipMany().next(
//      Scanners.notAmong(" |").many().source()
//      .followedBy(Scanners.WHITESPACES.skipMany()));

  Parser<String> spaceDelimited=Scanners.WHITESPACES.skipMany().sepEndBy(Scanners.isChar('|')).many().source()
      .followedBy(Scanners.WHITESPACES.skipMany());
  
  Map<List<AnnotatedString>, AnnotatedCommandAndArgument> mapCommandsToCommandLine=new Map<List<AnnotatedString>, AnnotatedCommandAndArgument>() {
    @Override
    public AnnotatedCommandAndArgument map(List<AnnotatedString> from) {
      AnnotatedCommandAndArgument oneCommand=new AnnotatedCommandAndArgument();
      oneCommand.rawCommandAndArguments=new AnnotatedString[from.size()];
      from.toArray(oneCommand.rawCommandAndArguments);
      return oneCommand;
    }
  };
  
  private Map<Token, AnnotatedString> annotateString=new Map<Token, AnnotatedString>(){
    @Override
    public AnnotatedString map(Token from) {
      int end=from.index()+from.length();
      String currentString=(String) from.value();
      log.debug("index=" + from.index() + " len=" + from.length()+" '"+currentString+"'");

      AnnotatedString ret=new AnnotatedString(currentString);
      ret.offsetInTopCommandLine=from.index();
      if(from.index()<=autoCompleteInvocationOffset && autoCompleteInvocationOffset<=end){
        log.debug("bingo "+currentString);
        ret.autoCompleteRequested=true;
      }
      return ret;
    }
  };

  Parser<List<AnnotatedString>> manySpaceDelimited=spaceDelimited.token().map(annotateString).many();

  Map<List<AnnotatedCommandAndArgument>, AnnotatedCmdLine> mapAnnotatedArgumentsToCmdLine=new Map<List<AnnotatedCommandAndArgument>, AnnotatedCmdLine>() {
    @Override
    public AnnotatedCmdLine map(List<AnnotatedCommandAndArgument> from) {
      AnnotatedCmdLine aggregate=new AnnotatedCmdLine();
      aggregate.commandsAndArguments=new AnnotatedCommandAndArgument[from.size()];
      from.toArray(aggregate.commandsAndArguments);
      return aggregate;
    }
  };
  
  Parser<AnnotatedCmdLine> topLevel=manySpaceDelimited.map(mapCommandsToCommandLine)
      .many().map(mapAnnotatedArgumentsToCmdLine);


//  <T> Parser<T> annotate(Parser<T> parserToAnnotate) {
//    return (Parser<T>) parserToAnnotate.token().map(new Map<Token, T>() {
//      @Override
//      public T map(Token from) {
//        int end=from.index()+from.length();
//        T currentMatch=(T) from.value();
//        System.out.println("index=" + from.index() + " len=" + from.length()+" '"+currentMatch+"'");
//
//        if(from.index()<=autoCompleteInvocationOffset && autoCompleteInvocationOffset<=end){
//          System.out.println("bingo "+currentMatch);
//        }
//        return currentMatch;
//      }
//    });
//  }

//  static Parser<CommandChain> ListCommandLink_TO_CommandChain=STRINGS_TO_GCL.sepEndBy(Scanners.isChar('|')).map(LINK_TO_CHAIN);

}
