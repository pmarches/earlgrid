package com.earlgrid.remoting;

import java.util.concurrent.CompletableFuture;

import com.earlgrid.core.serverside.EarlGridPb.PbTopLevel;

public class TopLevelResponseFuture extends CompletableFuture<PbTopLevel> {
  private int requestId;
  
  public TopLevelResponseFuture(int requestId) {
    this.requestId=requestId;
  }

}
