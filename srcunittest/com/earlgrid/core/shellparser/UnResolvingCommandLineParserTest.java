package com.earlgrid.core.shellparser;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.earlgrid.core.session.SessionEnvironmentVariables;
import com.earlgrid.core.shellparser.CommandLineInvocation;
import com.earlgrid.core.shellparser.CommandLineResolver;
import com.earlgrid.core.shellparser.ResolvedCommandChain;
import com.earlgrid.core.shellparser.ShellCommandLineParser;
import com.earlgrid.core.shellparser.UnResolvedCommandChain;
import com.earlgrid.core.shellparser.UnResolvedSingleCommand;

public class UnResolvingCommandLineParserTest {
  static ShellCommandLineParser parser=new ShellCommandLineParser();
  static CommandLineResolver resolver;

  @BeforeClass
  public static void setup(){
    SessionEnvironmentVariables sessionVariables=new SessionEnvironmentVariables();
    resolver=new CommandLineResolver(sessionVariables);
    
    sessionVariables.put("HOME", "/home/myuser");
    sessionVariables.put("FULL_NAME", "Mr Keyboard User");
  }
  
  @Test
  public void multipleCommandsTest(){
    CommandLineInvocation twoCommandChains=new CommandLineInvocation("echo toto |wc -c; banner done>/dev/null");
    List<UnResolvedCommandChain> twoUnresolvedChains = parser.parseUserEditedCommand(twoCommandChains);
    assertEquals(2, twoUnresolvedChains.size());
  }
  
  @Test
  public void testRedirection(){
    UnResolvedSingleCommand lsRedirected=parser.parseUserEditedCommand(new CommandLineInvocation("ls>/dev/null")).get(0).pipedCommands.get(0);
    assertEquals(3, lsRedirected.arguments.size());

    UnResolvedCommandChain chainOfRedirection=parser.parseUserEditedCommand(new CommandLineInvocation("echo | ./scriptErrAndOut A | ./scriptErrAndOut B > /dev/null  | ./scriptErrAndOut C")).get(0);
    assertEquals(4, chainOfRedirection.pipedCommands.size());
    UnResolvedSingleCommand redirectedCommand = chainOfRedirection.pipedCommands.get(2);
    assertEquals(9, redirectedCommand.arguments.size());
  }
  
  @Test
  public void testQuotes() {
    CommandLineInvocation cdCommand=new CommandLineInvocation("cd $HOME/tmp");
    UnResolvedCommandChain unresolvedCdCommand = parser.parseUserEditedCommand(cdCommand).get(0);
    assertEquals(1, unresolvedCdCommand.numberOfCommands());
//    assertArrayEquals(new String[]{"cd", ".."}, unresolvedCdCommand.get(0));
//    assertEquals(new CommandLineInvocation(), unresolvedCdCommand.getAction());
    ResolvedCommandChain resolvedCdCommand = resolver.resolve(unresolvedCdCommand);
    assertEquals("cd /home/myuser/tmp", resolvedCdCommand.toString());
    
    UnResolvedCommandChain cdNewDir = parser.parseUserEditedCommand(new CommandLineInvocation("cd 'New Directory' ")).get(0);
    assertEquals(1, cdNewDir.pipedCommands.size());
    assertEquals(Arrays.asList("cd", " ", "'New Directory'", " ").size(), cdNewDir.pipedCommands.get(0).arguments.size());
    
    UnResolvedCommandChain cdQuoted = parser.parseUserEditedCommand(new CommandLineInvocation("cd \"New Directory\"    ")).get(0);
    assertEquals(4, cdQuoted.pipedCommands.get(0).arguments.size());
    
    UnResolvedCommandChain unresolvedDiffCommand = parser.parseUserEditedCommand(
        new CommandLineInvocation("diff \"$HOME/New Directory/$USER/SomeFile.txt\" $HOME/'Project one'/'SomeFile.txt'   ")).get(0);
    assertEquals(13, unresolvedDiffCommand.pipedCommands.get(0).arguments.size());

    UnResolvedCommandChain lsgrepUnResolved = parser.parseUserEditedCommand(new CommandLineInvocation("ls $HOME/'New dir'/     | grep -v [Mm].* ")).get(0);
    assertEquals(2, lsgrepUnResolved.pipedCommands.size());
    assertEquals(7, lsgrepUnResolved.pipedCommands.get(0).arguments.size());
    assertEquals(7, lsgrepUnResolved.pipedCommands.get(1).arguments.size());
    
    assertEquals("concatenatedSingleQuotes", "AB/home/myuser'C'$HOME", resolveSingleCommandToString("A'B'$HOME\\'C\\''$HOME'"));
    assertEquals("nestedSingleQuotes", "/home/myuser", resolveSingleCommandToString("''$HOME''"));
    assertEquals("nestedSingleQuotes2", "$HOME", resolveSingleCommandToString("'''$HOME'''"));
    assertEquals("unbalancedSingleQuotes", "A\"B\"C\"", resolveSingleCommandToString("'A\"B\"C\"'"));

    assertEquals("concatenatedDoubleQuotes", "AB/home/myuser\"C\"/home/myuser", resolveSingleCommandToString("A\"B\"$HOME\\\"C\\\"$HOME"));
    assertEquals("concatenatedDoubleQuotesChar", "\"A\"", resolveSingleCommandToString("\\\"A\\\""));//A"B"$HOME\"C\""$HOME"
    assertEquals("nestedDoubleQuotes", "AB'CDE'F", resolveSingleCommandToString("A\"B'C\"D\"E'\"F"));
//TODO     assertEquals("unbalancedDoubleQuotes", "A\"B\"C\"", resolveSingleCommandToString("A\"B\"C\"")); //Should throw exception?
    
    //TODO Concatenated fragments
//TODO    assertEquals("concatenatedMixed", resolveSingleCommandToString(""), "");
    assertEquals("nestedSingleDoubleMixed", "\"$HOME\"", resolveSingleCommandToString("'\"$HOME\"'"));
    assertEquals("nestedDoubleSingleMixed", "'/home/myuser'_$HOME", resolveSingleCommandToString("\"'$HOME'\"_'$HOME'"));

  
    assertEquals("not\"$HOME\"expanded/New dir", resolveSingleCommandToString("'not\"$HOME\"expanded'/'New dir'"));
    assertEquals("will '/home/myuser' expanded", resolveSingleCommandToString("\"will '$HOME' expanded\""));
    
    assertEquals("Mr Keyboard User", resolveSingleCommandToString("$FULL_NAME lives in $HOME"));
    
    assertEquals("[diff, /home/myuser/New Directory//SomeFile.txt, /home/myuser/Project one/SomeFile.txt]", 
        resolveSingleCommandToArguments("diff \"$HOME/New Directory/$UNDEFINED/SomeFile.txt\" $HOME/'Project one'/'SomeFile.txt'   "));
    assertEquals("[ls, /home/myuser/New dir/, -v, [Mm].*]", resolveSingleCommandToArguments("ls $HOME/'New dir'/     -v [Mm].* "));
  }

  private static String resolveSingleCommandToArguments(String command){
    return resolver.resolve(parser.parseUserEditedCommand(new CommandLineInvocation(command))).get(0).pipedCommands.get(0).cmdAndArguments.toString();
  }

  private static String resolveSingleCommandToString(String command){
    return resolver.resolve(parser.parseUserEditedCommand(new CommandLineInvocation(command))).get(0).pipedCommands.get(0).cmdAndArguments.get(0).toString();
  }
}
