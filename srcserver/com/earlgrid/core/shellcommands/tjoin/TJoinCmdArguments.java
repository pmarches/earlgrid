package com.earlgrid.core.shellcommands.tjoin;

import java.util.ArrayList;

import com.earlgrid.core.shellcommands.BaseCmdArguments;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

/**
 * Req: 
 *  - Option of keeping all of the old columns
 *  - Adding new columns from labmda(s)
 *  - Flexible formating for new columns
 *  - Should it cut old columnns? Is that cut's job?
 *  - Ordering columns is another command
 *  - Mix input from other streams?
 * Examples:
 * tjoin 
 */
public class TJoinCmdArguments extends BaseCmdArguments<TJoinCmdArguments> {
  ArrayList<TJoinExpresssion> expressions; 

  @Override
  public BaseCmdSpecification newCmdSpecification() {
    return new TJoinCmdSpecification();
  }

  @Override
  public void parseArguments(String commandString, String[] argumentsArray) throws Exception {
    for (int i = 0; i < argumentsArray.length; i++) {
      String arg = argumentsArray[i];
      if(arg.equals("-p")){
        
      }
    }
  }

}
