package org.chris.ontology.web;

import org.eclipse.jetty.server.Server;
import org.junit.Test;

/**
 * @author cmccarthy on 6/22/15.
 */
public class OntologyServerTest {

  /**
   * Simple test to ensure everything is wired up correctly
   *
   * This could be @Ignored if we expect port conflicts
   */
  @Test
  public void testServerStartup() throws Exception {
    Server server = OntologyServer.getServer();
    server.start();
    server.stop();
  }

}