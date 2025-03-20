package com.mitchellbosecke.pebble.node.expression;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class OrExpression extends BinaryExpression<Boolean> {
  @SuppressWarnings(value = { "unchecked" }) @Override public Boolean evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    Boolean left = ((Expression<Boolean>) getLeftExpression()).evaluate(self, context);
    Boolean right = ((Expression<Boolean>) getRightExpression()).evaluate(self, context);
    if (context.isStrictVariables()) {
      if (left == null || right == null) {
        throw new PebbleException(null, "null value used in or operator and strict variables is set to true", getLineNumber(), self.getName());
      }
    } else {
      if (left == null) {
        left = false;
      }
      if (right == null) {
        right = false;
      }
    }
    return left || right;
  }
}