package org.chris.ontology;

import static com.google.common.collect.Sets.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Sets;

/**
 * A class that represents the member of an ontology.
 *
 * It contains the following:
 *  A name, which may be used to address it
 *  (Optionally) A map of names to child objects
 *  (Optionally) A map of names to properties
 *
 * The object can be used to assert validity against given sets of properties.
 *
 * @author cmccarthy on 6/20/15.
 */
public final class OntologicalObject {
  public final String name;

  private final Map<String, OntologicalObject> children = new HashMap<>();
  private final Map<String, PropertyDefinition> properties = new HashMap<>();

  public OntologicalObject(String name) {
    this.name = name;
  }

  public void addProperty(String name, PropertyDefinition property) {
    properties.put(name, property);
  }

  public void addChild(String name, OntologicalObject child) {
    if (!intersection(properties.keySet(), child.properties.keySet()).isEmpty()) {
      throw new OntologyParseException("Children cannot overwrite parent properties");
    }
    children.put(name, child);
  }

  public Map<String, OntologicalObject> getChildren() {
    return new HashMap<>(children);
  }

  public Map<String, PropertyDefinition> getStrictProperties() {
    return new HashMap<>(properties);
  }

  /**
   * @param type A period delimited string representing a child type of this object,
   *             with parent (supposedly this) type included
   * @return A map of property name to it's definition
   */
  public Map<String, PropertyDefinition> getAllProperties(String type) {

    Map<String, PropertyDefinition> definitions = getStrictProperties();

    List<String> subType = Arrays.asList(type.split("\\."));
    definitions.putAll(getAllProperties(subType.subList(1,subType.size())));
    return definitions;
  }

  /**
   * A helper method to avoid ugly string operations everywhere. List of only subtype objects
   */
  Map<String, PropertyDefinition> getAllProperties(List<String> subType) {
    //Base case: no more types to examine
    if (subType.size() == 0) return Collections.emptyMap();

    OntologicalObject child = children.get(subType.get(0));

    if (child == null) throw new IllegalTypeException("Unexpected null child, type string invalid");

    List<String> remainingTypes = subType.subList(1, subType.size());

    //Get the properties of this node, and add the proper child node's as well
    Map<String, PropertyDefinition> properties = child.getStrictProperties();
    properties.putAll(child.getAllProperties(remainingTypes));
    return properties;
  }

  public PropertyDefinition getProperty(String name) {
    return properties.get(name);
  }

  public class IllegalTypeException extends RuntimeException {
    public IllegalTypeException(String s) {
      super(s);
    }
  }
}
