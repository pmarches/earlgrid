package com.earlgrid.remoting.serverside;

import com.earlgrid.core.serverside.EarlGridPb.PbTopLevel;

public interface IOConnectionMessageHandler {
  public void onNotificationOrRequestReceived(PbTopLevel topLevelMsg);
  public void onIOExceptionOccured(Exception e);
}
