package uk.co.techblue.alfresco.jackson;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import uk.co.techblue.alfresco.dto.util.AlfrescoDtoUtil;

/**
 * The Class ISO8601DateSerializer.
 */
public class ISO8601DateSerializer extends JsonSerializer<Date> {
  @Override public void serialize(final Date value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
    try {
      final String dateString = AlfrescoDtoUtil.formatISO8601Date(value);
      jgen.writeString(dateString);
    } catch (final ParseException pe) {
      throw new IOException("Error occurred while formatting date to ISO8601 format", pe);
    }
  }
}