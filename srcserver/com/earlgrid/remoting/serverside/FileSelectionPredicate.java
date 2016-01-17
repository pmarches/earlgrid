package com.earlgrid.remoting.serverside;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public interface FileSelectionPredicate extends Serializable {
  public boolean includeInResult(Path file, BasicFileAttributes attr);
  public boolean visitChildren(Path file, BasicFileAttributes attr);
}
