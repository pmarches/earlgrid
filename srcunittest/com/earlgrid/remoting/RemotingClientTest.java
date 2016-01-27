package com.earlgrid.remoting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.CompletionException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.earlgrid.core.serverside.EarlGridPb.PbFileSystemManagement;
import com.earlgrid.core.serverside.EarlGridPb.PbTopLevel;
import com.earlgrid.remoting.RemoteHostConfiguration;
import com.earlgrid.remoting.RemotingClient;
import com.earlgrid.remoting.SSHRemotingClient;
import com.earlgrid.remoting.UserCredentials;
import com.earlgrid.remoting.serverside.FileSelectionPredicate;

public class RemotingClientTest {
  private static RemotingClient client;

  @BeforeClass
  public static void setup() throws Exception{
//    client=new LoopbackRemotingClient(pipedEndPoints[1]);

    UserCredentials pkCredentials=new UserCredentials("root", new File("/home/pmarches/projects/earlgrid/EarlGrid/unittest/testSSHKey_rsa"));
    RemoteHostConfiguration conf=new RemoteHostConfiguration("localhost", 8022, pkCredentials);
    TestClient testClient=new TestClient();
    client = new SSHRemotingClient(conf, null);
  }
  
  @AfterClass
  public static void teardown() throws IOException{
    client.shutdown();
  }

  @Test
  public void testPing() throws Exception{
    assertTrue(client.ping());
  }
  
  @Test
  public void testFileSystemListAPI() throws Exception{
    PbTopLevel listFileResponse = client.sendRequestMessage(client.createListDirectoryMessage("/tmp")).get();
    PbFileSystemManagement fsMgtResponse=listFileResponse.getFileSystemManagement();
    assertEquals(1, fsMgtResponse.getFileListingCount());
    
    try{
      client.sendRequestMessage(client.createListDirectoryMessage("/randomDirectoryThatCannotExists/1231ddqd3q3")).join();
      fail();
    }
    catch(CompletionException e){
      assertEquals(java.io.FileNotFoundException.class, e.getCause().getClass());
    }
  }

  static class AcceptAllPredicate implements FileSelectionPredicate {
    @Override
    public boolean includeInResult(Path file, BasicFileAttributes attr) {
      return true;
    }
    @Override
    public boolean visitChildren(Path file, BasicFileAttributes attr) {
      return true;
    }
  };
  
  @Test
  public void testFileSystemFindAPI() throws Exception {
    AcceptAllPredicate testVisitor=new AcceptAllPredicate();
    PbTopLevel findFileResponse = client.sendRequestMessage(client.createFileSystemFindMessage(testVisitor, "/tmp")).get();
    PbFileSystemManagement fsMgtResponse=findFileResponse.getFileSystemManagement();
//    assertEquals(23, fsMgtResponse.getFileListingCount());
  }
  
  @Test
  public void testCommandLine() throws Exception{
    client.executeCommand("who");
  }
}
