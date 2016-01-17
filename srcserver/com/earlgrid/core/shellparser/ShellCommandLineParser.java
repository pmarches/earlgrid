package com.earlgrid.core.shellparser;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.pattern.CharPredicates;
import org.codehaus.jparsec.pattern.Patterns;

import com.earlgrid.core.shellparser.UnResolvedArgument.ArgumentKind;

public class ShellCommandLineParser {
  public List<UnResolvedCommandChain> parseUserEditedCommand(CommandLineInvocation userInvocation){
    return MULTI_COMMAND_CHAIN.parse(userInvocation.commandLine);
  }

  protected static Parser<UnResolvedArgument> WHITESPACE_UNRESOLVED_ARG=Scanners.WHITESPACES.source()
      .map(mapStringToUnResolvedArgument(ArgumentKind.WHITESPACE));
  
  protected static Map<List<List<UnResolvedArgument>>, List<UnResolvedArgument>> MERGE_LISTS=new Map<List<List<UnResolvedArgument>>, List<UnResolvedArgument>>(){
    @Override
    public List<UnResolvedArgument> map(List<List<UnResolvedArgument>> from) {
      List<UnResolvedArgument> simplifiedList=new ArrayList<>();
      for(List<UnResolvedArgument> item : from){
        simplifiedList.addAll(item);
      }
      return simplifiedList;
    }
  };

  protected static final Parser<String> ENVIRONMENT_VARIABLE_VALID_CHARS=Patterns.isChar(CharPredicates.IS_ALPHA_NUMERIC_).many().toScanner("Environment variable").source();

  protected static Parser<UnResolvedArgument> SINGLE_QUOTE_UNRESOLVED_ARG=
      Patterns.isChar('\'').toScanner("SINGLE_QUOTE").next(
          Scanners.string("\\'").or(Scanners.notChar('\'')).many().source()
        .map(mapStringToUnResolvedArgument(ArgumentKind.ORDINARY)))
        .followedBy(Scanners.isChar('\''));

  protected static Parser<UnResolvedArgument> DOUBLE_QUOTED_UNRESOLVED_ARG=
      Patterns.isChar('"').toScanner("DOUBLE_QUOTE").next(
          Scanners.string("\\\"").or(Scanners.notChar('"')).many().source()
        .map(mapStringToUnResolvedArgument(ArgumentKind.ORDINARY)))
        .followedBy(Scanners.isChar('"'));

  protected static Parser<UnResolvedArgument> ESCAPED_UNRESOLVED_ARG=Patterns.isChar('\\').toScanner("ESCAPE_SEQUENCE").next(Scanners.ANY_CHAR.source().map(mapStringToUnResolvedArgument(ArgumentKind.ORDINARY)));

  protected static Parser<UnResolvedArgument> ORDINARY_ARG=Patterns.many(CharPredicates.notAmong("\\\"'| \t;<>$")).toScanner("ORDINARY").source()
      .map(mapStringToUnResolvedArgument(ArgumentKind.ORDINARY));
  
  protected static Parser<UnResolvedArgument> REDIRECTION_UNRESOLVED_ARG=Patterns.among("<>").toScanner("REDIRECTION").source()
      .map(mapStringToUnResolvedArgument(ArgumentKind.REDIRECTION));

  protected static Parser<UnResolvedArgument> BACKGROUND_UNRESOLVED_ARG=Patterns.isChar('&').toScanner("BACKGROUND").source().map(mapStringToUnResolvedArgument(ArgumentKind.BACKGROUND));
  

  protected static Parser<UnResolvedArgument> DOLLAR_VARIABLE_UNRESOLVED_ARG=Patterns.isChar('$').toScanner("ENVIRONMENT_VARIABLE").next(ENVIRONMENT_VARIABLE_VALID_CHARS.source())
      .map(mapStringToUnResolvedArgument(ArgumentKind.ENVIRONMENT_VARIABLE));

  protected static Parser<UnResolvedArgument> DOUBLE_QUOTED_TEXT=Scanners.string("\\\"").or(Scanners.notAmong("$\"")).many().source()
      .map(mapStringToUnResolvedArgument(ArgumentKind.ORDINARY));
  
  protected static Parser<List<UnResolvedArgument>> DOUBLE_QUOTED_UNRESOLVED_ARG_LIST=
      Patterns.isChar('"').toScanner("DOUBLE_QUOTED_STUFF").next(
          DOLLAR_VARIABLE_UNRESOLVED_ARG.or(DOUBLE_QUOTED_TEXT).many()
        .followedBy(Scanners.isChar('"')));

  protected static Parser<List<UnResolvedArgument>> UNQUOTED_ARGUMENT=
      WHITESPACE_UNRESOLVED_ARG
      .or(ESCAPED_UNRESOLVED_ARG)
      .or(SINGLE_QUOTE_UNRESOLVED_ARG)
      .or(REDIRECTION_UNRESOLVED_ARG)
      .or(DOLLAR_VARIABLE_UNRESOLVED_ARG)
      .or(ORDINARY_ARG)
      .many();
  
  protected static Map<List<UnResolvedArgument>, UnResolvedSingleCommand> UNRESOLVED_ARG_TO_SINGLE_COMMAND=new Map<List<UnResolvedArgument>,UnResolvedSingleCommand> (){
    @Override
    public UnResolvedSingleCommand map(List<UnResolvedArgument> arg0) {
      return new UnResolvedSingleCommand(arg0);
    }
  };

  protected static Parser<UnResolvedSingleCommand> SINGLE_COMMAND=DOUBLE_QUOTED_UNRESOLVED_ARG_LIST.or(UNQUOTED_ARGUMENT).many()
      .map(MERGE_LISTS)
      .map(UNRESOLVED_ARG_TO_SINGLE_COMMAND);

  protected static Parser<String> PIPE_DELIMITER=Patterns.isChar('|').toScanner("PIPE").source();

  protected static Parser<UnResolvedCommandChain> ONE_COMMAND_CHAIN=SINGLE_COMMAND.sepBy(PIPE_DELIMITER).map(new Map<List<UnResolvedSingleCommand>, UnResolvedCommandChain>(){
    @Override
    public UnResolvedCommandChain map(List<UnResolvedSingleCommand> pipedCommands) {
      UnResolvedCommandChain chain=new UnResolvedCommandChain();
      chain.pipedCommands=pipedCommands;
      return chain;
    }
  });

  protected static Parser<String> SEMI_COLON_DELIMITER=Patterns.isChar(';').toScanner("END_OF_COMMAND").source();

  protected static Parser<List<UnResolvedCommandChain>> MULTI_COMMAND_CHAIN=ONE_COMMAND_CHAIN.sepBy(SEMI_COLON_DELIMITER);
  
  protected static Map<String, UnResolvedArgument> mapStringToUnResolvedArgument(final UnResolvedArgument.ArgumentKind kind){
    return new Map<String, UnResolvedArgument>(){
      @Override
      public UnResolvedArgument map(String ordinaryArg) {
        return new UnResolvedArgument(ordinaryArg, kind);
      }
    };
  }

}
