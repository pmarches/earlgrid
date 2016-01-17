package com.earlgrid.remoting;

public class RemoteHostConfiguration {
  public String hostname;
  public UserCredentials credentials;
  public String jvmPath="/usr/bin/java";
  public int port=22;
  public String classPathSeparator=":";
  protected String jvmOptions;
  
  public RemoteHostConfiguration(String hostName, int port, UserCredentials credentials) {
    this.hostname=hostName;
    this.credentials=credentials;
    this.port=port;
  }

  public String getJVMOptions() {
    if(jvmOptions==null){
      return "";
    }
    return jvmOptions;
  }

  public void setJVMOptions(String newOptions){
    jvmOptions=newOptions;
  }
}
