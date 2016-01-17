package com.earlgrid.remoting;

import java.util.concurrent.CompletableFuture;

import com.earlgrid.core.serverside.EarlGridPb.PbTopLevel;

public class TopLevelResponseFuture extends CompletableFuture<PbTopLevel> {
  private int messageId;
  
  public TopLevelResponseFuture(int messageId) {
    this.messageId=messageId;
  }

}
