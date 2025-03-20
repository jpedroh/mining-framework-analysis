package org.wicketopia.builder;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

/**
 * @author James Carman
 */
public interface ComponentBuilder {
  void addBehavior(Behavior behavior);

  void visible(boolean viewable);

  Component build();
}