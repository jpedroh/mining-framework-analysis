package net.javacrumbs.jsonunit.fluent;
import org.codehaus.jackson.JsonNode;

/**
 * Contains JSON related fluent assertions inspired by FEST or AssertJ. Typical usage is:
 * <p/>
 * <code>
 * assertThatJson("{\"test\":1}").isEqualTo("{\"test\":2}");
 * assertThatJson("{\"test\":1}").hasSameStructureAs("{\"test\":21}");
 * assertThatJson("{\"root\":{\"test\":1}}").node("root.test").isEqualTo("2");
 * </code>
 * <p/>
 * Please note that the method name is assertThatJson and not assertThat. The reason is that we need to accept String parameter
 * and do not want to override standard FEST or AssertJ assertThat(String) method.
 *
 * @deprecated use JsonFluentAssert instead
 */
@Deprecated public class JsonAssert extends JsonFluentAssert {
  protected JsonAssert(JsonNode actual, String path, String description, String ignorePlaceholder) {
    super(actual, path, description, ignorePlaceholder, null);
  }

  public JsonAssert(JsonNode actual) {
    super(actual);
  }
}