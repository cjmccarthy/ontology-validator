package org.chris.ontology;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

/**
 * @author cmccarthy on 6/20/15.
 */
@Ignore
public class OntologyBuilderTest {
  OntologyBuilder builder;

  @Before
  public void setup() {
    builder = new OntologyBuilder();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testHappyPathParse() throws Exception {

    //This should parse and not throw
    builder.parseFromJson("", new ObjectMapper().readValue("{ \"animal\": { \"weight\": \"int\", \"*weight\": \"?\", " +
      "\"livestock\": { \"value\": \"float\", \"*value\": \"?\", \"cattle\": { \"grassFed\": \"boolean\", " +
      "\"*grassFed\": \"?\", \"angus\": \"boolean\", \"*angus\": \"?\" }, \"chicken\": { \"eggLaying\": " +
      "\"boolean\", \"*eggLaying\": \"1\", \"eggsProduced\": \"int\" } }, \"domestic\": { \"name\": \"string\", " +
      "\"*name\": \"+\", \"cat\": { \"declawed\": \"boolean\", \"*declawed\": \"1\" }, \"dog\": { \"trained\": " +
      "\"boolean\", \"*trained\": \"1\", \"breed\": \"string\" } } } }", Map.class));

  }

  @Test
  public void testInvalidPropertyName() {
    try {
      builder.parseFromJson("", ImmutableMap.<String, Object>builder()
        .put("object1", ImmutableMap.builder().put("type", "string").build()).build());
      fail();
    } catch (OntologyParseException ignored) {
    }

    try {
      builder.parseFromJson("", ImmutableMap.<String, Object>builder()
        .put("object1", ImmutableMap.builder().put("na.me", "string").build()).build());
      fail();
    } catch (OntologyParseException ignored) {
    }
  }

  @Test(expected = OntologyParseException.class)
  public void testInvalidPropertyType() {
    builder.parseFromJson("", ImmutableMap.<String, Object>builder()
      .put("object1", ImmutableMap.builder().put("name", "notallowedtype").build()).build());
  }

  @Test(expected = OntologyParseException.class)
  public void testExtraMultiplicities() {
    builder.parseFromJson("", ImmutableMap.<String, Object>builder()
      .put("object1", ImmutableMap.builder()
        .put("name", "int")
        .put("*name", "1")
        .put("*another", "*")
        .build())
      .build());
  }

  @Test
  public void testToPlurality() throws Exception {
    OntologyBuilder builder = new OntologyBuilder();
    Multiplicity fourCount = builder.toMultiplicity("4");
    assertTrue(fourCount.verify(4));
    assertFalse(fourCount.verify(0));
    assertFalse(fourCount.verify(1));
    assertFalse(fourCount.verify(5));

    Multiplicity zeroOrOne = builder.toMultiplicity("?");
    assertTrue(zeroOrOne.verify(0));
    assertTrue(zeroOrOne.verify(1));
    assertFalse(zeroOrOne.verify(2));

    Multiplicity oneOrMore = builder.toMultiplicity("+");
    assertFalse(oneOrMore.verify(0));
    assertTrue(oneOrMore.verify(1));
    assertTrue(oneOrMore.verify(2));

    Multiplicity zeroOrMore = builder.toMultiplicity("*");
    assertTrue(zeroOrMore.verify(0));
    assertTrue(zeroOrMore.verify(1));
    assertTrue(zeroOrMore.verify(2));

    try {
      builder.toMultiplicity(4);
      fail();
    } catch (OntologyParseException ignored) {
    }

  }
}