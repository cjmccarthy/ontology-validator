package org.chris.ontology.web;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Singleton;

/**
 * @author cmccarthy on 6/20/15.
 */
@Provider
@Singleton
class JSONParseExceptionMapper implements ExceptionMapper<JsonProcessingException> {
  @Override
  public Response toResponse(final JsonProcessingException jpe) {
    return Response.status(Response.Status.BAD_REQUEST)
      .type(MediaType.APPLICATION_JSON)
      .entity(jpe.getMessage())
      .build();

  }
}
