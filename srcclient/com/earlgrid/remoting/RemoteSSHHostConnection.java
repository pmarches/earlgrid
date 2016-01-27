package com.earlgrid.remoting;

import java.io.File;
import java.io.IOException;

import com.earlgrid.remoting.serverside.IOEndPoint;
import com.earlgrid.remoting.serverside.RemotingServerMain;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

public class RemoteSSHHostConnection implements AutoCloseable {
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(RemoteSSHHostConnection.class);
  private static final String REMOTE_BASE_DIR = ".EarlGrid";
  private static final String REMOTE_CLASSES_DIR = REMOTE_BASE_DIR+"/classes";
  private static final String REMOTE_JVM_DIR = REMOTE_BASE_DIR+"/jvm";
  
  Session sshSession;
  ChannelExec execChannel;
  private IOEndPoint endPoint;
  
  public void connect(RemoteHostConfiguration remoteHostConf) throws Exception{
    JSch.setLogger(new MyLogger());
    JSch.setConfig("StrictHostKeyChecking", "no");
    JSch.setConfig("PreferredAuthentications", "publickey,password");
    JSch jsch=new JSch();
    sshSession=jsch.getSession(remoteHostConf.credentials.username, remoteHostConf.hostname, remoteHostConf.port);
    if(remoteHostConf.credentials.privateKeyPath!=null){
      jsch.addIdentity(remoteHostConf.credentials.privateKeyPath);
    }
    if(remoteHostConf.credentials.password!=null){
      sshSession.setPassword(remoteHostConf.credentials.password);
    }
    sshSession.connect();

    sshSession.setConfig("compression.s2c", "zlib@openssh.com,zlib,none");
    sshSession.setConfig("compression.c2s", "zlib@openssh.com,zlib,none");
    sshSession.setConfig("compression_level", "9");
    
    String[] remoteClassPath=uploadFilesFromCurrentClassPath();
    startRemoteJavaProcess(remoteHostConf, remoteClassPath, RemotingServerMain.class);
  }
  
  private void startRemoteJavaProcess(RemoteHostConfiguration remoteHostConf, String[] remoteClassPath, Class remoteServerMainClass) throws Exception {
    String remoteClassPathStr=StringJoin(remoteHostConf.classPathSeparator, remoteClassPath);
    String remoteJavaCommand=remoteHostConf.jvmPath+" "+remoteHostConf.getJVMOptions()+" -cp "+remoteClassPathStr+" "+remoteServerMainClass.getName();
    log.debug("remoteJavaCommand="+remoteJavaCommand);
    
    execChannel=(ChannelExec) sshSession.openChannel("exec");
    execChannel.setCommand(remoteJavaCommand);
    execChannel.setErrStream(System.err);
    execChannel.connect();
    
    endPoint=new IOEndPoint(execChannel.getOutputStream(), execChannel.getInputStream(), execChannel.getErrStream());
  }
  
  private String StringJoin(String separatorString, String[] elementsToJoin) {
    StringBuffer sb=new StringBuffer();
    for(int i=0; i<elementsToJoin.length; i++){
      sb.append(elementsToJoin[i]);
      if(i+1!=elementsToJoin.length){
        sb.append(separatorString);
      }
    }
    return sb.toString();
  }

  /**
   * FIXME This is pretty slow, need to do like rsync 
   * @param sshSession 
   */
  private String[] uploadFilesFromCurrentClassPath() throws Exception {
    ChannelSftp sftpChannel=(ChannelSftp) sshSession.openChannel("sftp");
    sftpChannel.connect();
    try {
      sftpChannel.mkdir(REMOTE_BASE_DIR);
      sftpChannel.mkdir(REMOTE_CLASSES_DIR);
    } catch (Exception e) {
    }

    SftpATTRS classDirAttrs = sftpChannel.stat(REMOTE_CLASSES_DIR);
    if(classDirAttrs.isDir()==false){

    }

    String[] localClassPathEntries=System.getProperty("java.class.path").split(System.getProperty("path.separator"));
    String[] remoteClassPath=new String[localClassPathEntries.length];
    
    for(int i=0; i<localClassPathEntries.length; i++){
      String localClassPathEntry = localClassPathEntries[i];
      File classPathFile=new File(localClassPathEntry);
      if(classPathFile.isFile()){
        SftpProgressMonitor monitor=null;
        String remotePath=REMOTE_CLASSES_DIR+"/"+localClassPathEntry.replaceAll("[:/\\\\]", "_");
        sftpChannel.put(localClassPathEntry, remotePath, monitor, ChannelSftp.RESUME);
        remoteClassPath[i]=remotePath;
      }
      else{
        //Recursive file upload
        String remoteClassDirectory=REMOTE_CLASSES_DIR+"/"+classPathFile.getName();//FIXME Create unique name on the remote end (Hash maybe?)
        recursiveUpload(sftpChannel, classPathFile, remoteClassDirectory);
        remoteClassPath[i]=remoteClassDirectory;
      }
    }
    return remoteClassPath;
  }

  private void recursiveUpload(ChannelSftp sftpChannel, File directoryToUpload, String remoteDirectoryPath) throws SftpException {
    try {
      sftpChannel.mkdir(remoteDirectoryPath);
    } 
    catch (Exception e) {}

    for(File f : directoryToUpload.listFiles()){
      String remoteChildPath=remoteDirectoryPath+"/"+f.getName();
      if(f.isDirectory()){
        recursiveUpload(sftpChannel, f, remoteChildPath);
      }
      else{
        sftpChannel.put(f.getAbsolutePath(), remoteChildPath);
      }
    }
  }

  public static class MyLogger implements com.jcraft.jsch.Logger {
    static java.util.Hashtable name=new java.util.Hashtable();
    static{
      name.put(new Integer(DEBUG), "DEBUG: ");
      name.put(new Integer(INFO), "INFO: ");
      name.put(new Integer(WARN), "WARN: ");
      name.put(new Integer(ERROR), "ERROR: ");
      name.put(new Integer(FATAL), "FATAL: ");
    }
    public boolean isEnabled(int level){
      return true;
    }
    public void log(int level, String message){
      System.err.print(name.get(new Integer(level)));
      System.err.println(message);
    }
  }

  @Override
  public void close() throws IOException {
    execChannel.disconnect();
    sshSession.disconnect();
  }

  public IOEndPoint getEndPoint() {
    return endPoint;
  } 
}
