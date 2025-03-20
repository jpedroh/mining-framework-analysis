package com.mitchellbosecke.pebble.node;
import com.github.benmanes.caffeine.cache.Cache;
import com.mitchellbosecke.pebble.cache.CacheKey;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RuntimePebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.CompletionException;
import static java.util.Objects.isNull;

/**
 * Node for the cache tag
 *
 * @author Eric Bussieres
 */
public class CacheNode extends AbstractRenderableNode {
  private final BodyNode body;

  private final Expression<?> name;

  public CacheNode(int lineNumber, Expression<?> name, BodyNode body) {
    super(lineNumber);
    this.body = body;
    this.name = name;
  }

  @Override public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override public void render(final PebbleTemplateImpl self, Writer writer, final EvaluationContextImpl context) throws PebbleException, IOException {
    try {
      final String 
<<<<<<< /usr/src/app/output/mbosecke/pebble/edcb7368e0c64c3268f102f7be46f122f067a45c/src/main/java/com/mitchellbosecke/pebble/node/CacheNode.java/left.java
      body
=======
      key = new CacheKey(this, (String) this.name.evaluate(self, context), context.getLocale())
>>>>>>> /usr/src/app/output/mbosecke/pebble/edcb7368e0c64c3268f102f7be46f122f067a45c/src/main/java/com/mitchellbosecke/pebble/node/CacheNode.java/right.java
      ;
      Cache tagCache = context.getTagCache();
      if (isNull(tagCache)) {
        body = render(self, context);
      } else {
        CacheKey key = new CacheKey((String) this.name.evaluate(self, context), context.getLocale());
        body = (String) context.getTagCache().get(key, (k) -> {
          try {
            return render(self, context);
          } catch (PebbleException e) {
            throw new RuntimePebbleException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
      }
      writer.write(body);
    } catch (CompletionException e) {
      throw new PebbleException(e, "Could not render cache block [" + this.name + "]");
    }
  }

  private String render(final PebbleTemplateImpl self, final EvaluationContextImpl context) throws PebbleException, IOException {
    StringWriter tempWriter = new StringWriter();
    CacheNode.this.body.render(self, tempWriter, context);
    return tempWriter.toString();
  }
}