package com.mitchellbosecke.pebble.node.expression;
import java.io.IOException;
import com.mitchellbosecke.pebble.error.PebbleException;
import java.io.StringWriter;
import com.mitchellbosecke.pebble.node.RenderableNode;
import java.io.Writer;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;

/**
 * This class wraps a {@link RenderableNode} into an expression. This is used by
 * the filter TAG to apply a filter to large chunk of template which is
 * contained within a renderable node.
 *
 * @author mbosecke
 *
 */
public class RenderableNodeExpression extends UnaryExpression {
  private final RenderableNode node;

  private final int lineNumber;

  public RenderableNodeExpression(RenderableNode node, int lineNumber) {
    this.node = node;
    this.lineNumber = lineNumber;
  }

  @Override public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    Writer writer = new StringWriter();
    try {
      node.render(self, writer, context);
    } catch (IOException e) {
      throw new PebbleException(e, "Error occurred while rendering node", this.getLineNumber(), self.getName());
    }
    return writer.toString();
  }

  @Override public int getLineNumber() {
    return this.lineNumber;
  }
}