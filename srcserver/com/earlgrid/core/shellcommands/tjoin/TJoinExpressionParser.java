package com.earlgrid.core.shellcommands.tjoin;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Scanners;

public class TJoinExpressionParser {
  Parser<String> spaceDelimited=Scanners.WHITESPACES.skipMany().sepEndBy(Scanners.isChar('|')).many().source()
      .followedBy(Scanners.WHITESPACES.skipMany());

  public TJoinExpresssion parse(String stringToParse){
    return null;
  }
}
