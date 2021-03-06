package com.earlgrid.ui.model;

import java.io.File;
import java.util.ArrayList;

import com.earlgrid.remoting.RemoteHostConfiguration;
import com.earlgrid.remoting.UserCredentials;

public class HostManager extends ArrayList<RemoteHostConfiguration>{
  public HostManager() {
    UserCredentials pkCredentials=new UserCredentials("root", new File("srcunittest/testSSHKey_rsa"));
    RemoteHostConfiguration defaultHost = new RemoteHostConfiguration("localhost", 8022, pkCredentials);
    add(defaultHost);

    add(new RemoteHostConfiguration("remote.host.com", 22, pkCredentials));
    add(new RemoteHostConfiguration("remote2.host.com", 22, pkCredentials));
    add(new RemoteHostConfiguration("remote4.host.com", 22, pkCredentials));
}

  public RemoteHostConfiguration getBySessionName(String sessionName) {
    for(RemoteHostConfiguration host : this){
      String currentSessionName=host.credentials.getUsername()+"@"+host.hostname+":"+host.port;
      if(currentSessionName.equals(sessionName)){
        return host;
      }
    }
    return null;
  }
}
