package org.chris.ontology;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;


/**
 * @author cmccarthy on 6/20/15.
 */
@SuppressWarnings("unchecked")
public class OntologyBuilder {

  private static final Map<String, Class<?>> classFromString = ImmutableMap.<String, Class<?>>builder()
    .put("boolean", Boolean.class)
    .put("int", Integer.class)
    .put("float", BigDecimal.class) //Jackson only serializes to double, so we'll store in BigDecimal and check later
    .put("string", String.class).build();

  /**
   * For any object:
   *  For all children nodes that are not map nodes:
   *    1. verify both property type and multiplicity exist
   *    2. add property type and multiplicity to object as property
   *  For all children nodes that are map nodes:
   *    1. add as children
   *    2. recurse
   *
   */
  public OntologicalObject parseFromJson(String name, Map<String, Object> objectMap) {
    OntologicalObject obj = new OntologicalObject(name);
    Map<String, Multiplicity> multiplicities = new HashMap<>();
    Map<String, Class<?>> propertyTypes = new HashMap<>();

    /**
     * For each member of the object to be parsed, add
     */
    objectMap.forEach((k, v) -> {

      if (k.startsWith("*")) {
        multiplicities.put(k.substring(1), toMultiplicity(v));
      } else if (v instanceof Map) {
        //Recurse down this node and add it as a child
        obj.addChild(k, parseFromJson(k, (Map<String, Object>)v));
      } else {
        if (k.equals("type") || !k.matches("^[a-zA-Z0-9]+$")) {
          throw new OntologyParseException("Invalid property name: " + k);
        }

        if (!classFromString.keySet().contains(v)) {
          throw new OntologyParseException("Invalid property: " + v);
        }
        propertyTypes.put(k, classFromString.get(v));
      }
    });

    /**
     * We cant have more multiplicities than we have property types!
     */
    if (multiplicities.size() > propertyTypes.size()) {
      throw new OntologyParseException("Too many multiplicities for property types");
    }

    /**
     * Property types can exist without a given multiplicity, so if we dont find a matching one, substitute "*"
     */
    propertyTypes.forEach((k, v) -> {
      Multiplicity p = multiplicities.getOrDefault(k, n -> n >= 0);
      obj.addProperty(k, new PropertyDefinition(p, v));
    });


    return obj;
  }

  Multiplicity toMultiplicity(Object representation) throws OntologyParseException {
    if (!(representation instanceof String)) {
      throw new OntologyParseException("Invalid multiplicity: " + representation);
    }

    switch ((String)representation) {
      case "?":
        return num -> num==0 || num==1;
      case "+":
        return num -> num>=1;
      case "*":
        return num -> num>=0;
      default:
        try {
          int expected = Integer.parseInt((String) representation);
          return num -> num==expected;
        } catch (NumberFormatException e) {
          throw new OntologyParseException("Invalid multiplicity: " + representation);
        }
    }
  }

}
