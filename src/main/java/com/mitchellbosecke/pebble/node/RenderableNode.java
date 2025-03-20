package com.mitchellbosecke.pebble.node;
import java.io.IOException;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import java.io.Writer;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;

public interface RenderableNode extends Node {
  void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context) throws IOException;
}