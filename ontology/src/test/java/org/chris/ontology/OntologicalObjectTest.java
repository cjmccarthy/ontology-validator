package org.chris.ontology;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;

/**
 * @author cmccarthy on 6/22/15.
 */
public class OntologicalObjectTest {

  @Test(expected = OntologyParseException.class)
  public void testUniqueProperties() {
    OntologicalObject animal = new OntologicalObject("animal");
    OntologicalObject dog = new OntologicalObject("dog");
    animal.addProperty("name", new PropertyDefinition(n -> n >= 0, String.class));
    dog.addProperty("name", new PropertyDefinition(n -> n >= 0, String.class));

    animal.addChild("dog", dog);
  }

  @Test
  public void testGetProperties() {
    OntologicalObject animal = new OntologicalObject("animal");
    OntologicalObject dog = new OntologicalObject("dog");
    OntologicalObject cat = new OntologicalObject("cat");
    animal.addProperty("name", new PropertyDefinition(n -> n >= 0, String.class));
    dog.addProperty("weight", new PropertyDefinition(n -> n == 0, BigDecimal.class));
    cat.addProperty("declawed", new PropertyDefinition(n -> n == 0, Boolean.class));
    animal.addChild("dog", dog);
    animal.addChild("cat", cat);

    Map<String, PropertyDefinition> dogProperties = animal.getAllProperties("animal.dog");

    assertTrue(dogProperties.containsKey("name"));
    assertTrue(dogProperties.containsKey("weight"));
    assertFalse(dogProperties.containsKey("declawed"));


  }

}