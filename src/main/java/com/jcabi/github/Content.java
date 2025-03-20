package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.net.URL;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github content.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/repos/contents/">Contents API</a>
 */
@Immutable @SuppressWarnings(value = { "PMD.TooManyMethods" }) public interface Content extends Comparable<Content>, JsonReadable, JsonPatchable {
  /**
     * Get content path.
     * @return Content path
     */
  @NotNull(message = "contentPath is never NULL") String contentPath();

  /**
     * Get the name of the commit/branch/tag.
     * @return Content ref
     */
  String ref();

  @Immutable @ToString @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = "content") final class Smart implements Content {
    /**
         * Encapsulated content.
         */
    private final transient Content content;

    /**
         * Public ctor.
         * @param cont Content
         */
    public Smart(@NotNull(message = "content is never NULL") final Content cont) {
      this.content = cont;
    }

    @Override @NotNull(message = "contentPath is never NULL") public String contentPath() {
      return this.contentPath();
    }

    @Override public String ref() {
      return this.ref();
    }

    /**
         * Get its name.
         * @return Name of content
         * @throws IOException If there is any I/O problem
         */
    public String name() throws IOException {
      return new SmartJson(this).text("name");
    }

    /**
         * Get its type.
         * @return Type of content
         * @throws IOException If there is any I/O problem
         */
    public String type() throws IOException {
      return new SmartJson(this).text("type");
    }

    /**
         * Get its size.
         * @return Size content
         * @throws IOException If it fails
         */
    public int size() throws IOException {
      return new SmartJson(this).number("size");
    }

    /**
         * Get its sha hash.
         * @return Sha hash of content
         * @throws IOException If there is any I/O problem
         */
    public String sha() throws IOException {
      return new SmartJson(this).text("sha");
    }

    /**
         * Get its path.
         * @return Path of content
         * @throws IOException If there is any I/O problem
         */
    public String path() throws IOException {
      return new SmartJson(this).text("path");
    }

    /**
         * Get its URL.
         * @return URL of content
         * @throws IOException If there is any I/O problem
         */
    public URL url() throws IOException {
      return new URL(new SmartJson(this).text("url"));
    }

    /**
         * Get its HTML URL.
         * @return URL of content
         * @throws IOException If there is any I/O problem
         */
    public URL htmlUrl() throws IOException {
      return new URL(new SmartJson(this).text("html_url"));
    }

    /**
         * Get its GIT URL.
         * @return URL of content
         * @throws IOException If there is any I/O problem
         */
    public URL gitUrl() throws IOException {
      return new URL(new SmartJson(this).text("git_url"));
    }

    @Override public int compareTo(final Content cont) {
      return this.content.compareTo(cont);
    }

    @Override public void patch(@NotNull(message = "JSON is never NULL") final JsonObject json) throws IOException {
      this.content.patch(json);
    }

    @Override public JsonObject json() throws IOException {
      return this.content.json();
    }
  }
}