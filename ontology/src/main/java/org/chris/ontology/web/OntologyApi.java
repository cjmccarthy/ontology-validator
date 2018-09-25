package org.chris.ontology.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.chris.ontology.OntologicalObject;
import org.chris.ontology.OntologicalValidator;
import org.chris.ontology.OntologyBuilder;
import org.chris.ontology.OntologyParseException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;


/**
 *
 * An api which performs validation against a given ontology. It also allows the ontology to be retrieved.
 *
 * @author cmccarthy on 6/20/15.
 */
@Path("/api/ontology/")
@SuppressWarnings("unchecked")
public class OntologyApi {

  /**
   * We use a loading cache here to avoid re-validating requests.
   *
   * It is important to invalidate this if the ontology is rewritten!!
   */
  static final LoadingCache<Object, OntologicalValidator.ValidationStatus> cache = CacheBuilder.newBuilder()
    .maximumSize(1000)
    .build(new CacheLoader<Object, OntologicalValidator.ValidationStatus>() {
      @Override
      public OntologicalValidator.ValidationStatus load(Object key) throws Exception {
          return OntologicalValidator.validate(ontology.getChildren(), (Map) key);
      }
    });

  //Initialize to an empty ontology
  static OntologicalObject ontology = new OntologicalObject("");
  static Object ontologyRepresentation = ImmutableMap.of("","");

  private static final Object ontologyWriteLock = new Object();


  final HttpServletRequest request;
  final HttpServletResponse response;
  private final OntologyBuilder builder;

  @Inject
  public OntologyApi(@Context HttpServletRequest request, @Context HttpServletResponse response,
    OntologyBuilder builder) {
    this.request = request;
    this.response = response;
    this.builder = builder;
  }


  /**
   * Update the ontology against which we validate requests
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/update")
  public String updateOntology(Object requestBody) throws IOException {

    /**
     * Using a write-lock on the ontology builder. Reads to the ontology need not be synchronized, but we can't allow
     * separate threads to overwrite each other here
     */
    synchronized (ontologyWriteLock) {
      try {
        //Jackson should have taken care of this mapping for us.
        var mappedRequestBody = (Map) requestBody;

        //The empty string is to represent a parent node, which has no type itself
        ontology = builder.parseFromJson("", mappedRequestBody);

        //Saving the request body for easy fetching
        ontologyRepresentation = requestBody;

        //New ontology, invalidate any cached results
        cache.invalidateAll();
        response.setStatus(200);
        return "";
      } catch (Throwable e) {
        if (e instanceof OntologyParseException) {
          //The ontology was invalid, pass on the reason why to the client
          response.sendError(400, e.getMessage());
          return "";
        } else {
          System.err.println(e.toString());
          response.sendError(500, "Server encountered an internal error");
          return "";
        }
      }
    }
  }

  /**
   * Fetch the ontology from the server
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/fetch")
  public Object fetchOntology() {
    response.setStatus(200);
    return ontologyRepresentation;
  }

  /**
   * Validate the given object against the current ontology
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/validate")
  public Object validateObject(Object toValidate) throws IOException {
    try {
      return cache.get(toValidate);
    } catch (Throwable e) {
      System.err.println(e.toString());
      response.sendError(500, "Server encountered an error in validation");
      return null;
    }
  }


}
