package com.earlgrid.remoting;

import java.io.File;
import java.io.IOException;

public class TestClient extends LoopbackRemotingClient {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(TestClient.class);
  private static InteractiveFormHandler formHandler=new MockInteractiveFormHandler();
  
  public TestClient() throws IOException {
    super(formHandler);
  }
  
  public static RemotingClient createNewSSHClient() throws Exception{
    UserCredentials pkCredentials=new UserCredentials("root", new File("srcunittest/testSSHKey_rsa"));
    RemoteHostConfiguration conf=new RemoteHostConfiguration("localhost", 8022, pkCredentials);
    SSHRemotingClient client = new SSHRemotingClient(conf, null);
    return client;
  }

  /**
   * Helper methond when running the tests under windows
   * @param outputFile
   * @return
   */
  public static String convertToUnixPath(File outputFile) {
    String unixStylePath=outputFile.toString().replaceAll("\\\\", "/");
    return unixStylePath;
  }

}
