package com.earlgrid.remoting;

import java.io.File;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

public class UserCredentials {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(UserCredentials.class);
  public UserCredentials(String username, String password) {
    this.username=username;
    this.password=password;
  }
  
  public UserCredentials(String username, File privateKeyPath) {
    this.username=username;
    this.privateKeyPath=privateKeyPath.getAbsolutePath();
  }

  public void generateKeys(JSch jsch) throws JSchException{
    KeyPair kpair=KeyPair.genKeyPair(jsch, KeyPair.RSA);
//    kpair.setPassphrase(passphrase);
//    kpair.writePrivateKey(filename);
//    kpair.writePublicKey(filename+".pub", "");
    log.debug("Finger print: "+kpair.getFingerPrint());
    kpair.dispose(); 
  }
  
  String username;
  String password;
  String privateKeyPath;
  
  public String getUsername(){
    return username;
  }
  
  public String getPrivateKeyPath(){
    return privateKeyPath;
  }
}
