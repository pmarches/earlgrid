package com.earlgrid.core.sessionmodel;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;

import com.earlgrid.core.serverside.EarlGridPb.PbPathSpecification;

public class ChangeCurrentWorkingDirectorySessionModelEvent extends SessionModelChangeEvent {
  Path newWorkingDirectory;
  
  public ChangeCurrentWorkingDirectorySessionModelEvent(int taskId, Path newWorkingDirectory) {
    super(taskId);
    this.newWorkingDirectory=newWorkingDirectory;
  }
  
  public Path getNewWorkingDirectory(){
    return newWorkingDirectory;
  }

  public PbPathSpecification.Builder toProtoBuf() {
    PbPathSpecification.Builder pathPb=PbPathSpecification.newBuilder();
    for (Iterator<Path> iterator = newWorkingDirectory.iterator(); iterator.hasNext();) {
      Path elem = iterator.next();
      pathPb.addPathElement(elem.toString());
    }
    return pathPb;
  }

  public static ChangeCurrentWorkingDirectorySessionModelEvent fromProtoBuf(int taskId, PbPathSpecification protoBuf) {
    ChangeCurrentWorkingDirectorySessionModelEvent event=new ChangeCurrentWorkingDirectorySessionModelEvent(taskId, convertPathSpecificationToPath(protoBuf));
    return event;
  }
  
  protected static Path convertPathSpecificationToPath(PbPathSpecification pathToList) {
    FileSystem rootFS = FileSystems.getFileSystem(URI.create("file:///"));
    return rootFS.getPath(File.separator+String.join(File.separator, pathToList.getPathElementList()));
  }

}
