package com.mitchellbosecke.pebble.node;
import java.io.IOException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import java.io.Writer;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;

public class AutoEscapeNode extends AbstractRenderableNode {
  private final BodyNode body;

  private final String strategy;

  private final boolean active;

  public AutoEscapeNode(int lineNumber, BodyNode body, boolean active, String strategy) {
    super(lineNumber);
    this.body = body;
    this.strategy = strategy;
    this.active = active;
  }

  @Override public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context) throws IOException {
    body.render(self, writer, context);
  }

  @Override public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public BodyNode getBody() {
    return body;
  }

  public String getStrategy() {
    return strategy;
  }

  public boolean isActive() {
    return active;
  }
}