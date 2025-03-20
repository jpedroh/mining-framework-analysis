package org.apache.cayenne.value;
import java.io.Serializable;
import java.util.Objects;
import org.apache.cayenne.value.json.JsonUtils;
import org.apache.cayenne.value.json.MalformedJsonException;

/**
 * A Cayenne-supported values object that holds Json string.
 *
 * @since 4.2
 */
public class Json implements Serializable {
  private static final long serialVersionUID = 7594825997288498022L;

  private final String json;

  /**
     *
     * @param json json string representation
     * @throws MalformedJsonException if json is empty or blank
     */
  public Json(String json) {
    if (json.isBlank()) {
      throw new MalformedJsonException("Unexpected EOF");
    }
    this.json = json;
  }

  public String getRawJson() {
    return json;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Json other = (Json) o;
    return JsonUtils.compare(json, other.json);
  }

  @Override public int hashCode() {
    return Objects.hash(json);
  }

  @Override public String toString() {
    return "JSON value: " + json;
  }
}