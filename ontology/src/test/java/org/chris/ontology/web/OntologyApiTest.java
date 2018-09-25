package org.chris.ontology.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chris.ontology.OntologyBuilder;
import org.chris.ontology.OntologyParseException;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author cmccarthy on 6/22/15.
 */
public class OntologyApiTest {
  /*
   * Using mockito in this class because mocking http objects by hand is the worst
   */
  HttpServletRequest request;
  HttpServletResponse response;
  OntologyBuilder builder;
  OntologyApi api;

  @Before
  public void setup() {
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    builder = mock(OntologyBuilder.class);
    api = new OntologyApi(request, response, builder);
  }

  @Test
  public void testSuccessfulUpdate() throws IOException {
    //Just need it not to throw a parsing exception
    when(builder.parseFromJson(anyString(), anyObject())).thenReturn(null);

    api.updateOntology(ImmutableMap.of());

    verify(response).setStatus(eq(200));
  }

  @Test
  public void testBadOntologyUpdate() throws IOException {
    //Now we "do" want a parsing exception
    when(builder.parseFromJson(anyString(), anyObject())).thenThrow(OntologyParseException.class);

    api.updateOntology(ImmutableMap.of());

    verify(response).sendError(eq(400), anyString());
  }

  @Test
  public void gracefulJacksonErrorExit() throws IOException {

    //Method expects this to be made into a map by jackson
    api.updateOntology("");

    verify(response).sendError(eq(500), anyString());
  }

  @Test
  public void testFetch() throws IOException {
    //Ensure the ontology is initialized to a proper default
    assertEquals(ImmutableMap.of("", ""), (Map)api.fetchOntology());

    when(builder.parseFromJson(anyString(), anyObject())).thenReturn(null);

    //Builder is mocked, dont care about real values here
    ImmutableMap<String, String> requestBody = ImmutableMap.of("hello", "world");
    api.updateOntology(requestBody);

    //Ensure the ontology is initialized to a proper default
    assertEquals(requestBody, (Map) api.fetchOntology());

    verify(response, times(3)).setStatus(eq(200));
  }



}