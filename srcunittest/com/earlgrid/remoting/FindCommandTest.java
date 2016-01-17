package com.earlgrid.remoting;

import com.earlgrid.core.serverside.EarlGridPb.PbTopLevel;

public class FindCommandTest {
  PbTopLevel.Builder topLevelResponse;

  public void runCommand(PbTopLevel topLevelRequest, PbTopLevel.Builder topLevelResponse){
//    fileSelectionPredicate = convertExecutableBytesToInstance(topLevelRequest.getFileSystemManagement().getFindPredicate());
//    startDirectory=convertPathSpecificationToPath(topLevelRequest.getFileSystemManagement().getPath());
//
//    final FileSystemManagement.Builder fsMgtResponse=FileSystemManagement.newBuilder();
//    SimpleFileVisitor<Path> visitorBackedBySelectionPredicate=new SimpleFileVisitor<Path>(){
//      @Override
//      public FileVisitResult visitFile(Path pathOfFile, BasicFileAttributes attributesOfFile) throws IOException {
//        if(fileSelectionPredicate.includeInResult(pathOfFile, attributesOfFile)){
//          FileSystemObjectSpecification.Builder fileSpec=FileSystemObjectSpecification.newBuilder();
//          fileSpec.setPath(convertFileToPathSpecification(pathOfFile));
//          fileSpec.setSizeInBytes(attributesOfFile.size());
//          fsMgtResponse.addFileListing(fileSpec);
//        }
//        
//        if(attributesOfFile.isDirectory()){
//          if(fileSelectionPredicate.visitChildren(pathOfFile, attributesOfFile)){
//            return FileVisitResult.CONTINUE;
//          }
//          return FileVisitResult.SKIP_SUBTREE;
//        }
//        return FileVisitResult.CONTINUE;
//      }
//    };
//    Files.walkFileTree(startDirectory, visitorBackedBySelectionPredicate);
//    topMsgResponse.setFileSystemManagement(fsMgtResponse);
  }
}
