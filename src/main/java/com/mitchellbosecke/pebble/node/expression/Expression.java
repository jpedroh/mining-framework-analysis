package com.mitchellbosecke.pebble.node.expression;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public interface Expression<T extends java.lang.Object> extends Node {
  T evaluate(PebbleTemplateImpl self, EvaluationContextImpl context);

  /**
     * Returns the line number on which the expression is defined on.
     *
     * @return the line number on which the expression is defined on.
     */
  int getLineNumber();
}