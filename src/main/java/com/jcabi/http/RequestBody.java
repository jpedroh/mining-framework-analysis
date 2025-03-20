package com.jcabi.http;
import com.jcabi.aspects.Immutable;
import java.nio.charset.Charset;
import java.util.Map;
import javax.json.JsonStructure;
import javax.validation.constraints.NotNull;

/**
 * Request body.
 *
 * <p>Instance of this interface is returned by {@link Request#body()},
 * and can be modified using one of the methods below. When modification
 * is done, method {@code back()} returns a modified instance of
 * {@link Request}, for example:
 *
 * <pre> new JdkRequest("http://my.example.com")
 *   .header("Content-Type", "application/x-www-form-urlencoded")
 *   .body()
 *   .formParam("name", "Jeff Lebowski")
 *   .formParam("age", "37")
 *   .formParam("employment", "none")
 *   .back() // returns a modified instance of Request
 *   .fetch()</pre>
 *
 * <p>Instances of this interface are immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable public interface RequestBody {
  /**
     * Get back to the request it's related to.
     * @return The request we're in
     */
  @NotNull(message = "request is never NULL") Request back();

  /**
     * Get text content.
     * @return Content in UTF-8
     */
  @NotNull(message = "body can\'t be NULL") String get();

  /**
     * Set text content.
     * @param body Body content
     * @return New alternated body
     */
  @NotNull(message = "body is never NULL") RequestBody set(@NotNull(message = "body can\'t be NULL") String body);

  /**
     * Set JSON content.
     * @param json JSON object
     * @return New alternated body
     * @since 0.11
     */
  @NotNull(message = "body is never NULL") RequestBody set(@NotNull(message = "JSON structure can\'t be NULL") JsonStructure json);

  /**
     * Set byte array content.
     * @param body Body content
     * @return New alternated body
     */
  @NotNull(message = "modified body is never NULL") RequestBody set(@NotNull(message = "body can\'t be NULL") byte[] body);

  /**
     * Add form param.
     * @param name Query param name
     * @param value Value of the query param to set
     * @return New alternated body
     */
  @NotNull(message = "alternated body is never NULL") RequestBody formParam(@NotNull(message = "form param name can\'t be NULL") String name, @NotNull(message = "form param value can\'t be NULL") Object value);

  /**
     * Add form params.
     * @param params Map of params
     * @return New alternated body
     * @since 0.10
     */
  @NotNull(message = "alternated body is never NULL") RequestBody formParams(@NotNull(message = "map of params can\'t be NULL") Map<String, String> params);

  @Immutable final class Printable {
    /**
         * The Charset to use.
         */
    private static final Charset CHARSET = Charset.forName("UTF-8");

    /**
         * Utility class.
         */
    private Printable() {
    }

    /**
         * Safely print byte array.
         * @param bytes Bytes to print
         * @return Text, with ASCII symbols only
         */
    public static String toString(final byte[] bytes) {
      final StringBuilder text = new StringBuilder(0);
      final char[] chrs = new String(bytes, Printable.CHARSET).toCharArray();
      if (chrs.length > 0) {
        for (final char chr : chrs) {
          if (chr < 128) {
            text.append(chr);
          } else {
            text.append("\\u").append(Integer.toHexString(chr));
          }
        }
      } else {
        text.append("<<empty>>");
      }
      return text.toString();
    }
  }
}