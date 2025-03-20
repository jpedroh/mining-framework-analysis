package com.mitchellbosecke.pebble.node.expression;
import java.util.ArrayList;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import java.util.Collections;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import java.util.List;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;

public class ArrayExpression implements Expression<List<?>> {
  private final List<Expression<?>> values;

  private final int lineNumber;

  public ArrayExpression(int lineNumber) {
    this.values = Collections.emptyList();
    this.lineNumber = lineNumber;
  }

  public ArrayExpression(List<Expression<?>> values, int lineNumber) {
    if (values == null) {
      this.values = Collections.emptyList();
    } else {
      this.values = values;
    }
    this.lineNumber = lineNumber;
  }

  @Override public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override public List<?> evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    List<Object> returnValues = new ArrayList<>(values.size());
    for (int i = 0; i < values.size(); i++) {
      Expression<?> expr = values.get(i);
      Object value = expr == null ? null : expr.evaluate(self, context);
      returnValues.add(value);
    }
    return returnValues;
  }

  @Override public int getLineNumber() {
    return this.lineNumber;
  }
}