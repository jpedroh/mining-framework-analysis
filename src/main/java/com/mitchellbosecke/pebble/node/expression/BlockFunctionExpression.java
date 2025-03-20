package com.mitchellbosecke.pebble.node.expression;
import java.io.IOException;
import com.mitchellbosecke.pebble.error.PebbleException;
import java.io.StringWriter;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import java.io.Writer;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class BlockFunctionExpression implements Expression<String> {
  private final Expression<?> blockNameExpression;

  private final int lineNumber;

  public BlockFunctionExpression(ArgumentsNode args, int lineNumber) {
    this.blockNameExpression = args.getPositionalArgs().get(0).getValueExpression();
    this.lineNumber = lineNumber;
  }

  @Override public String evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    Writer writer = new StringWriter();
    String blockName = (String) blockNameExpression.evaluate(self, context);
    try {
      self.block(writer, context, blockName, false);
    } catch (IOException e) {
      throw new PebbleException(e, "Could not render block [" + blockName + "]", this.getLineNumber(), self.getName());
    }
    return writer.toString();
  }

  @Override public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override public int getLineNumber() {
    return this.lineNumber;
  }
}