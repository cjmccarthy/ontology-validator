package org.chris.ontology;

/**
 * @author cmccarthy on 6/20/15.
 */
@FunctionalInterface
public interface Multiplicity {
  boolean verify(Integer num);
}
