package com.earlgrid.ui.standalone;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationCmdLineOptions {
  public static ApplicationCmdLineOptions createFromStrings(String[] args){
    ApplicationCmdLineOptions options = new ApplicationCmdLineOptions();
    for(int i=0; i<args.length; i++){
      String arg=args[i];
      if(i==0){ //First arg is always the userHostDir
        Matcher userHostDirectoryMatch = USER_HOST_DIRECTORY_PATTERN.matcher(arg);
        if(userHostDirectoryMatch.matches()){
          if(userHostDirectoryMatch.group(1)!=null){
            options.username=userHostDirectoryMatch.group(2);
          }
          if(userHostDirectoryMatch.group(3)!=null){
            options.sessionName=userHostDirectoryMatch.group(3);
          }
          if(userHostDirectoryMatch.group(4)!=null){
            options.initialRemoteDirectory=userHostDirectoryMatch.group(5);
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

  @Deprecated
  public String username=System.getProperty("user.name");
  public String sessionName;
  @Deprecated
  public String initialRemoteDirectory;
  
  protected static final Pattern USER_HOST_DIRECTORY_PATTERN=Pattern.compile("(([^@]+)@)?([^:]+)(:(.+))?");
}
