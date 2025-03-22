package net.logstash.logback.marker;
import java.io.IOException;
import org.slf4j.Marker;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * A {@link Marker} that is known and understood by the logstash logback encoder.
 * <p>
 * In particular these markers are used to write data into the logstash json event via {@link #writeTo(JsonGenerator)}.
 */
@SuppressWarnings(value = { "serial" }) public abstract class LogstashMarker extends LogstashBasicMarker {
  public static final String MARKER_NAME_PREFIX = "LS_";

  public LogstashMarker(String name) {
    super(name);
  }

  /**
     * Adds the given marker as a reference, and returns this marker.
     * <p>
     * This can be used to chain markers together fluently on a log line. For example:
     * 
     * <pre>
     * {@code
     * import static net.logstash.logback.marker.Markers.*
     *     
     * logger.info(append("name1", "value1).and(append("name2", "value2")), "log message");
     * }
     * </pre>
     */
  @SuppressWarnings(value = { "unchecked" }) public <T extends LogstashMarker> T and(Marker reference) {
    add(reference);
    return (T) this;
  }

  /**
     * @deprecated Use {@link #and(Marker)} instead
     * @see #and(Marker)
     */
  @Deprecated public <T extends LogstashMarker> T with(Marker reference) {
    return and(reference);
  }

  /**
     * Writes the data associated with this marker to the given {@link JsonGenerator}.
     */
  public abstract void writeTo(JsonGenerator generator) throws IOException;
}