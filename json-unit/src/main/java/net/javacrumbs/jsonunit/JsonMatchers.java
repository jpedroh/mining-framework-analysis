package net.javacrumbs.jsonunit;
import net.javacrumbs.jsonunit.core.internal.Diff;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static net.javacrumbs.jsonunit.core.internal.Diff.create;

/**
 * Contains Hamcrest matchers to be used with Hamcrest assertThat and other tools.
 * <p/>
 * All the methods accept Objects as parameters. The supported types are:
 * <ol>
 * <li>Jackson JsonNode</li>
 * <li>Numbers, booleans and any other type parseable by Jackson's ObjectMapper.convertValue</li>
 * <li>String is parsed as JSON. For expected values the string is quoted if it contains obviously invalid JSON.</li>
 * <li>{@link java.io.Reader} similarly to String</li>
 * <li>null as null Node</li>
 * </ol>
 */
public class JsonMatchers {
  /**
     * Are the JSONs equivalent?
     *
     * @param expected
     * @return
     */
  public static <T extends java.lang.Object> Matcher<T> jsonEquals(Object expected) {
    return new JsonPartMatcher<T>("", expected);
  }

  /**
     * Is the part of the JSON equivalent?
     *
     * @param expected
     * @return
     */
  public static <T extends java.lang.Object> Matcher<T> jsonPartEquals(String path, Object expected) {
    return new JsonPartMatcher<T>(path, expected);
  }

  /**
     * Are the JSONs equivalent?
     *
     * This method exist only for those cases, when you need to use it as Matcher&lt;String&gt; and Java refuses to
     * do the type inference correctly.
     *
     * @param expected
     * @return
     */
  public static Matcher<String> jsonStringEquals(Object expected) {
    return jsonEquals(expected);
  }

  /**
     * Is the part of the JSON equivalent?
     *
     * This method exist only for those cases, when you need to use it as Matcher&lt;String&gt; and Java refuses to
     * do the type inference correctly.
     *
     * @param expected
     * @return
     */
  public static Matcher<String> jsonStringPartEquals(String path, Object expected) {
    return jsonPartEquals(path, expected);
  }

  private static final class JsonPartMatcher<T extends java.lang.Object> extends BaseMatcher<T> {
    private final Object expected;

    private final String path;

    private String differences;

    JsonPartMatcher(String path, Object expected) {
      this.expected = expected;
      this.path = path;
    }

    public boolean matches(Object item) {
      Diff diff = create(expected, item, "fullJson", path, JsonAssert.getIgnorePlaceholder(), JsonAssert.getTolerance());
      if (!diff.similar()) {
        differences = diff.differences();
      }
      return diff.similar();
    }

    public void describeTo(Description description) {
      if ("".equals(path)) {
        description.appendText(expected.toString());
      } else {
        description.appendText(expected.toString()).appendText(" in \"").appendText(path).appendText("\"");
      }
    }

    @Override public void describeMismatch(Object item, Description description) {
      description.appendText(differences);
    }
  }
}