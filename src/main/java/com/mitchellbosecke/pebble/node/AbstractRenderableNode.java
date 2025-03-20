package com.mitchellbosecke.pebble.node;
import java.io.IOException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import java.io.Writer;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;

public abstract class AbstractRenderableNode implements RenderableNode {
  private int lineNumber;

  @Override public abstract void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context) throws IOException;

  @Override public abstract void accept(NodeVisitor visitor);

  public AbstractRenderableNode() {
  }

  public AbstractRenderableNode(int lineNumber) {
    this.setLineNumber(lineNumber);
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }
}