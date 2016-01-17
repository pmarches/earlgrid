package com.earlgrid.core.syntax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.earlgrid.core.syntax.AnnotatedCmdLine;
import com.earlgrid.core.syntax.AnnotatedCommandAndArgument;
import com.earlgrid.core.syntax.RawCommandArgumentParser;

public class RawCommandArgumentParserTest {

  @Test
  public void testSingleCommand(){
    AnnotatedCmdLine annotatedCmdLine=new RawCommandArgumentParser("ls -a", 4).parse();
    assertEquals(1, annotatedCmdLine.commandsAndArguments.length);
    AnnotatedCommandAndArgument lsCommand=annotatedCmdLine.commandsAndArguments[0];
    assertEquals(2, lsCommand.rawCommandAndArguments.length);
    assertEquals("ls", lsCommand.rawCommandAndArguments[0].theString);
    assertEquals("-a", lsCommand.rawCommandAndArguments[1].theString);
    assertTrue(lsCommand.rawCommandAndArguments[1].autoCompleteRequested);
  }

  @Test
  public void testPipedTwoCommands(){
    AnnotatedCmdLine annotatedCmdLine=new RawCommandArgumentParser("ls -wqa|wc -l", 14).parse();
    assertEquals(2, annotatedCmdLine.commandsAndArguments.length);
    AnnotatedCommandAndArgument lsCommand=annotatedCmdLine.commandsAndArguments[0];
    assertEquals(2, lsCommand.rawCommandAndArguments.length);
    assertEquals("ls", lsCommand.rawCommandAndArguments[0].theString);
    assertEquals("-wqa", lsCommand.rawCommandAndArguments[1].theString);
    assertFalse(lsCommand.rawCommandAndArguments[1].autoCompleteRequested);
  }

}
