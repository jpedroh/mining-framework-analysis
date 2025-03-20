package com.jcabi.github.mock;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Content;
import java.io.IOException;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.ToString;

/**
 * Mock Github content.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @todo #166 Content mock should be implemented.
 *  Need to implement the methods of MkContent: 1) compareTo,
 *  2) json, 3) patch, 4) contentPath
 *  Don't forget to update the unit test class {@link MkContent}.
 *  See http://developer.github.com/v3/repos/contents
 * @since 0.8
 */
@Immutable @Loggable(value = Loggable.DEBUG) @ToString final class MkContent implements Content {
  @Override public int compareTo(final Content cont) {
    throw new UnsupportedOperationException("MkContent#compareTo()");
  }

  @Override public void patch(@NotNull(message = "JSON is never NULL") final JsonObject json) throws IOException {
    throw new UnsupportedOperationException("MkContent#patch()");
  }

  @Override public JsonObject json() throws IOException {
    throw new UnsupportedOperationException("MkContent#json()");
  }

  @Override public String contentPath() {
    throw new UnsupportedOperationException("MkContent#contentPath()");
  }

  @Override public String ref() {
    throw new UnsupportedOperationException("MkContent#ref()");
  }
}