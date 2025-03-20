package com.mitchellbosecke.pebble.node;
import java.io.IOException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import java.io.Writer;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class ExtendsNode extends AbstractRenderableNode {
  Expression<?> parentExpression;

  public ExtendsNode(int lineNumber, Expression<?> parentExpression) {
    super(lineNumber);
    this.parentExpression = parentExpression;
  }

  @Override public void render(final PebbleTemplateImpl self, Writer writer, final EvaluationContextImpl context) throws IOException {
    self.setParent(context, (String) parentExpression.evaluate(self, context));
  }

  @Override public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public Expression<?> getParentExpression() {
    return parentExpression;
  }
}