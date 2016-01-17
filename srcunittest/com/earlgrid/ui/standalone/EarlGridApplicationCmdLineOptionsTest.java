package com.earlgrid.ui.standalone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.earlgrid.ui.standalone.ApplicationCmdLineOptions;

public class EarlGridApplicationCmdLineOptionsTest {
  @Test
  public void testCreateFromStrings() {
    ApplicationCmdLineOptions option = ApplicationCmdLineOptions.createFromStrings(new String[]{"luser1@host.com:/tmp"});
    assertEquals("luser1", option.username);
    assertEquals("host.com", option.sessionName);
    assertEquals("/tmp", option.initialRemoteDirectory);

    ApplicationCmdLineOptions implicitOptions = ApplicationCmdLineOptions.createFromStrings(new String[]{"host.com"});
    assertNotNull( "luser1", implicitOptions.username);
    assertEquals("host.com", implicitOptions.sessionName);
    assertNull(implicitOptions.initialRemoteDirectory);
  }
}
