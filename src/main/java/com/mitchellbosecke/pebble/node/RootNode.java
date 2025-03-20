package com.mitchellbosecke.pebble.node;
import java.io.IOException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import java.io.Writer;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;

public class RootNode extends AbstractRenderableNode {
  private final BodyNode body;

  public RootNode(BodyNode body) {
    super(0);
    this.body = body;
  }

  @Override public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context) throws IOException {
    body.setOnlyRenderInheritanceSafeNodes(true);
    body.render(self, writer, context);
  }

  @Override public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public BodyNode getBody() {
    return body;
  }
}