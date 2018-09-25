package org.chris.ontology.web;

import java.util.EnumSet;
import java.util.HashMap;

import org.chris.ontology.OntologicalValidator;
import org.chris.ontology.OntologyBuilder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * @author cmccarthy on 6/20/15.
 */
public class OntologyServer {

  private static class OntologyModule extends ServletModule {
    private static final OntologyBuilder builder = new OntologyBuilder();
    private static final OntologicalValidator validator = new OntologicalValidator();

    @Provides
    @Singleton
    private JacksonJsonProvider jacksonJsonProvider() {
      ObjectMapper mapper = new ObjectMapper();
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
      mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
      return new JacksonJsonProvider(mapper);
    }

    @Provides
    @Singleton
    private OntologyBuilder ontologyBuilder() {
      return builder;
    }

    @Provides
    @Singleton
    private OntologicalValidator ontologicalValidator() {
      return validator;
    }

    @Override
    protected void configureServlets() {
      bind(DefaultServlet.class).in(Singleton.class);
      bind(OntologyApi.class);

      /**
       * Jersey also comes with one of these as a default, but I like both making it explicit and
       * controlling the messaging
       */
      bind(JSONParseExceptionMapper.class);

      HashMap<String, String> options = new HashMap<>();
      serve("/*").with(GuiceContainer.class, options);
    }
  }

  public static Server getServer() {
    Injector injector = Guice.createInjector(Stage.PRODUCTION, new OntologyModule());

    Server server = new Server(8888);
    ((QueuedThreadPool)server.getThreadPool()).setMaxThreads(100);

    ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
    context.addFilter(GuiceFilter.class, "/*", EnumSet.of(javax.servlet.DispatcherType.REQUEST,
      javax.servlet.DispatcherType.ASYNC));
    context.setResourceBase("");

    context.addServlet(DefaultServlet.class, "/*");

    return server;
  }


  public static void main(String[] args) throws Exception {
    Server server = getServer();
    server.start();

    server.join();
  }
}
