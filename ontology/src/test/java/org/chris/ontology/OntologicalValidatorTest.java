package org.chris.ontology;

import static org.chris.ontology.OntologicalValidator.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @author cmccarthy on 6/22/15.
 */
public class OntologicalValidatorTest {

  private Map<String, OntologicalObject> createDogAnimalOntology(Multiplicity colorMultiplicity, Class<?> colorClass) {
    OntologicalObject dog = new OntologicalObject("dog");
    dog.addProperty("color", new PropertyDefinition(colorMultiplicity, colorClass));
    OntologicalObject animal = new OntologicalObject("animal");
    animal.addChild("dog", dog);
    return ImmutableMap.of("animal", animal);
  }

  @Test
  public void validateWorkingObject() {
    Map<String, OntologicalObject> ontology = createDogAnimalOntology(n -> n >= 0, String.class);
    HashMap<String, Object> goodDog = new HashMap<>();
    goodDog.put("type", "animal.dog");
    goodDog.put("color", ImmutableList.of("brown", "black", "white"));
    assertTrue(validate(ontology, goodDog).isValid());
  }

  @Test
  public void failOnMultiplicity() {
    Map<String, OntologicalObject> ontology = createDogAnimalOntology(n -> n == 1, String.class);
    HashMap<String, Object> goodDog = new HashMap<>();
    goodDog.put("type", "animal.dog");
    goodDog.put("color", ImmutableList.of("brown", "black", "white"));
    assertFalse(validate(ontology, goodDog).isValid());
  }

  @Test
  public void failOnType() {
    Map<String, OntologicalObject> ontology = createDogAnimalOntology(n -> n >= 0, Integer.class);
    HashMap<String, Object> goodDog = new HashMap<>();
    goodDog.put("type", "animal.dog");
    goodDog.put("color", ImmutableList.of("brown", "black", "white"));
    assertFalse(validate(ontology, goodDog).isValid());
  }

  @Test
  public void testTestObjectType() {
    assertTrue(testObjectType(0, Integer.class));
    assertTrue(testObjectType(BigDecimal.valueOf(0.0f), BigDecimal.class));

    assertFalse(testObjectType(0, String.class));
    assertFalse(testObjectType(BigDecimal.valueOf(Double.MAX_VALUE), BigDecimal.class));
  }


  @Test
  public void testValidateObject() {
    assertTrue(validateObject(0, new PropertyDefinition(n -> n==1, Integer.class)));
    assertTrue(validateObject(ImmutableList.of(1,2,3), new PropertyDefinition(n -> n>=1, Integer.class)));
    assertTrue(validateObject(null, new PropertyDefinition(n -> n>=0, Integer.class)));

    assertFalse(validateObject(null, new PropertyDefinition(n -> n == 1, Integer.class)));
    assertFalse(validateObject(ImmutableList.of(1, "2", 3), new PropertyDefinition(n -> n >= 1, Integer.class)));

  }

}