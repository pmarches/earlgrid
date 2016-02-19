package com.earlgrid.core.session;

import java.util.Hashtable;

public class SessionEnvironmentVariables extends Hashtable<String, Object> {
  private static final long serialVersionUID = 7218208664606757099L;

  public SessionEnvironmentVariables() {
    //loadEnvironmentVariablesFromConfiguration();
    put("default", "my value");
  }
}
