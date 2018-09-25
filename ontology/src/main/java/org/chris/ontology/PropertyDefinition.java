package org.chris.ontology;

/**
 * @author cmccarthy on 6/20/15.
 */
public class PropertyDefinition {
  final Multiplicity multiplicity;
  final Class<?> propertyType;

  public PropertyDefinition(Multiplicity multiplicity, Class<?> propertyType) {
    this.multiplicity = multiplicity;
    this.propertyType = propertyType;
  }
}
