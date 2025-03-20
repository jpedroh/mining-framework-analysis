package com.mitchellbosecke.pebble.node.expression;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.utils.OperatorUtils;

public class NotEqualsExpression extends BinaryExpression<Boolean> {
  @Override public Boolean evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    try {
      return !OperatorUtils.equals(getLeftExpression().evaluate(self, context), getRightExpression().evaluate(self, context));
    } catch (Exception ex) {
      throw new PebbleException(ex, "Could not perform not equals comparison", getLineNumber(), self.getName());
    }
  }
}