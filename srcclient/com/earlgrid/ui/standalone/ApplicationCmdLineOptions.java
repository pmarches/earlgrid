package com.earlgrid.ui.standalone;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationCmdLineOptions {
  public static ApplicationCmdLineOptions createFromStrings(String[] args){
    ApplicationCmdLineOptions options = new ApplicationCmdLineOptions();
    for(int i=0; i<args.length; i++){
      String arg=args[i];
      if(i==0){ //First arg is always the sessionName
        Matcher sessionNameMatch = SESSION_NAME_PATTERN.matcher(arg);
        if(sessionNameMatch.matches()){
          if(sessionNameMatch.group(1)!=null){
            options.sessionName=sessionNameMatch.group(1);
          }
          if(sessionNameMatch.group(2)!=null){
            options.initialRemoteDirectory=sessionNameMatch.group(3);
          }
          continue;
        }
        else{
          throw new RuntimeException("Failed to parse '"+arg+"' as user@host:CWD format");
        }
      }
      
      
    }
    return options;
  }

  public String sessionName;
  public String initialRemoteDirectory;
  
  protected static final Pattern SESSION_NAME_PATTERN=Pattern.compile("([^/]+)(/(.+))?");
}
