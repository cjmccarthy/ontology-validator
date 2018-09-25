package org.chris.ontology;

import com.newrelic.api.agent.Trace;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 *
 * An object which validates an object (represented by a map of String -> Object, which can contain nested maps) against
 * a given ontology. It will assert that the given object meets all criterion for its type.
 *
 * @author cmccarthy on 6/18/15.
 */
public final class OntologicalValidator {

  //Bean for jackson convenience
  public static class ValidationStatus {
    final boolean valid;

    public ValidationStatus(boolean valid) {
      this.valid = valid;
    }

    //Getter to make jackson happy
    public boolean isValid() {
      return valid;
    }
  }

  @Trace
  public static ValidationStatus validate(Map<String,OntologicalObject> ontology, Map<String, Object> objectMap) {

    try {
      String completeType = (String) objectMap.get("type");

      OntologicalObject parent = ontology.get(completeType.substring(0, completeType.indexOf(".")));
      if (parent == null) {
        //Type string invalid
        return new ValidationStatus(false);
      }
      Map<String, PropertyDefinition> definitions = parent.getAllProperties(completeType);

      //We dont want to validate this part as a property
      objectMap.remove("type");

      for (Map.Entry<String, PropertyDefinition> entry : definitions.entrySet()) {
        Object toValidate = objectMap.get(entry.getKey());
        boolean valid = validateObject(toValidate, entry.getValue());
        if (!valid) return new ValidationStatus(false);
      }

      return new ValidationStatus(true);
    } catch (OntologicalObject.IllegalTypeException e) {
      return new ValidationStatus(false);
    }
  }

  /**
   * Assert an object's type and multiplicity against a definition
   *
   */
  static boolean validateObject(Object toValidate, PropertyDefinition definition) {
    /**
     * If no object was provided, ensure the ontology allows for multiplicity of zero
     */
    if (toValidate == null) {
      return definition.multiplicity.verify(0);
    }

    /**
     * If multiple objects were provided, ensure each object is of the correct type, AND the multiplicity is allowed
     */
    if (toValidate instanceof List) {
      for (Object o : (List) toValidate) {
        if (!testObjectType(o, definition.propertyType)) {
          return false;
        }
      }

      return definition.multiplicity.verify(((List) toValidate).size());
    }

    /**
     * The only remaining case is one object. Ensure it is of the correct type and multiplicity
     */
    return testObjectType(toValidate, definition.propertyType) && definition.multiplicity.verify(1);


  }

  /**
   * Asserts an object's type against a given property type
   *
   */
  static boolean testObjectType(Object toValidate, Class<?> propertyType) {
    /**
     * A bit of a hack, since Jersey will only serialize to Double or Big Decimal
     * BigDecimal's floatValue method will return + - INFINITY upon overflow, in which case it's invalid
     *
     * If the JSON spec ever changes to include infinities, this code will fail on that edge case
     */
    if (toValidate instanceof BigDecimal) {
      float testFloat = ((BigDecimal) toValidate).floatValue();
      if (testFloat == Float.NEGATIVE_INFINITY || testFloat == Float.POSITIVE_INFINITY) return false;
    }

    return toValidate.getClass().equals(propertyType);
  }

}

