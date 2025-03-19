package ezvcard.io.json;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonToken;
import ezvcard.Messages;

/**
 * Thrown during the parsing of a jCard, when a jCard is not formatted in the
 * correct way (the JSON syntax is valid, but it's not in the correct jCard
 * format).
 * @author Michael Angstadt
 */
public class JCardParseException extends IOException {
  private static final long serialVersionUID = 5139480815617303404L;

  private final JsonToken expected, actual;

  /**
	 * Creates a jCard parse exception.
	 * @param expected the JSON token that the parser was expecting
	 * @param actual the actual JSON token
	 */
  public JCardParseException(JsonToken expected, JsonToken actual) {
    super(Messages.INSTANCE.getExceptionMessage(35, expected, actual));
    this.expected = expected;
    this.actual = actual;
  }

  /**
	 * Creates a jCard parse exception.
	 * @param message the detail message
	 * @param expected the JSON token that the parser was expecting
	 * @param actual the actual JSON token
	 */
  public JCardParseException(String message, JsonToken expected, JsonToken actual) {
    super(message);
    this.expected = expected;
    this.actual = actual;
  }

  /**
	 * Gets the JSON token that the parser was expected.
	 * @return the expected token
	 */
  public JsonToken getExpectedToken() {
    return expected;
  }

  /**
	 * Gets the JSON token that was read.
	 * @return the actual token
	 */
  public JsonToken getActualToken() {
    return actual;
  }
}