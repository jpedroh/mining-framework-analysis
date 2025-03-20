package com.mitchellbosecke.pebble.node.expression;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class UnaryNotExpression extends UnaryExpression {
  @Override public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    Boolean result = (Boolean) getChildExpression().evaluate(self, context);
    if (context.isStrictVariables()) {
      if (result == null) {
        throw new PebbleException(null, "null value given to not() and strict variables is set to true", getLineNumber(), self.getName());
      }
      return !result;
    } else {
      return result == null || !result;
    }
  }
}