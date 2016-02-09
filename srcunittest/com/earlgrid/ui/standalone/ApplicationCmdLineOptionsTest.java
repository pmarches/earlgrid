package com.earlgrid.ui.standalone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ApplicationCmdLineOptionsTest {
  @Test
  public void testCreateFromStrings() {
    ApplicationCmdLineOptions option = ApplicationCmdLineOptions.createFromStrings(new String[]{"luser1@host.com:8022//tmp"});
    assertEquals("luser1@host.com:8022", option.sessionName);
    assertEquals("/tmp", option.initialRemoteDirectory);

    ApplicationCmdLineOptions implicitOptions = ApplicationCmdLineOptions.createFromStrings(new String[]{"host.com"});
    assertEquals("host.com", implicitOptions.sessionName);
    assertNull(implicitOptions.initialRemoteDirectory);
  }
}
