package net.logstash.logback;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.io.IOException;
import ch.qos.logback.classic.spi.IThrowableProxy;
import java.util.Iterator;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import java.util.Map;
import ch.qos.logback.core.Context;
import org.apache.commons.lang.time.FastDateFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import org.slf4j.Marker;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 */
public class LogstashFormatter {
  private static final ObjectMapper MAPPER = new ObjectMapper().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);

  private static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLIS = FastDateFormat.getInstance("yyyy-MM-dd\'T\'HH:mm:ss.SSSZZ");

  private static final StackTraceElement DEFAULT_CALLER_DATA = new StackTraceElement("", "", "", 0);

  private boolean includeCallerInfo;

  private JsonNode customFields;

  public LogstashFormatter(boolean includeCallerInfo) {
    this.includeCallerInfo = includeCallerInfo;
  }

  public LogstashFormatter() {
    this(false);
  }

  public LogstashFormatter(boolean includeCallerInfo, JsonNode customFields) {
    this.includeCallerInfo = includeCallerInfo;
    this.customFields = customFields;
  }

  public byte[] writeValueAsBytes(ILoggingEvent event, Context context) throws IOException {
    return MAPPER.writeValueAsBytes(eventToNode(event, context));
  }

  public String writeValueAsString(ILoggingEvent event, Context context) throws IOException {
    return MAPPER.writeValueAsString(eventToNode(event, context));
  }

  private ObjectNode eventToNode(ILoggingEvent event, Context context) {
    ObjectNode eventNode = MAPPER.createObjectNode();
    eventNode.put("@timestamp", ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLIS.format(event.getTimeStamp()));
    eventNode.put("@version", 1);
    eventNode.put("message", event.getFormattedMessage());
    createFields(event, context, eventNode);
    eventNode.put("tags", createTags(event));
    return eventNode;
  }

  private void createFields(ILoggingEvent event, Context context, ObjectNode eventNode) {
    final Marker marker = event.getMarker();
    eventNode.put("logger_name", event.getLoggerName());
    eventNode.put("thread_name", event.getThreadName());
    eventNode.put("level", event.getLevel().toString());
    eventNode.put("level_value", event.getLevel().toInt());
    if (includeCallerInfo) {
      StackTraceElement callerData = extractCallerData(event);
      eventNode.put("caller_class_name", callerData.getClassName());
      eventNode.put("caller_method_name", callerData.getMethodName());
      eventNode.put("caller_file_name", callerData.getFileName());
      eventNode.put("caller_line_number", callerData.getLineNumber());
    }
    IThrowableProxy throwableProxy = event.getThrowableProxy();
    if (throwableProxy != null) {
      eventNode.put("stack_trace", ThrowableProxyUtil.asString(throwableProxy));
    }
    if (context != null) {
      addPropertiesAsFields(eventNode, context.getCopyOfPropertyMap());
    }
    if (marker != null && marker.contains("JSON")) {
      eventNode.put("json_message", getJsonNode(event));
    }
    addPropertiesAsFields(eventNode, event.getMDCPropertyMap());
    addCustomFields(eventNode);
  }

  private ArrayNode createTags(ILoggingEvent event) {
    ArrayNode node = null;
    final Marker marker = event.getMarker();
    if (marker != null) {
      node = MAPPER.createArrayNode();
      if (marker.getName() != "JSON") {
        node.add(marker.getName());
      }
      if (marker.hasReferences()) {
        final Iterator<?> i = event.getMarker().iterator();
        while (i.hasNext()) {
          Marker next = (Marker) i.next();
          if (marker.getName() != "JSON") {
            node.add(next.getName());
          }
        }
      }
    }
    return node;
  }

  private void addPropertiesAsFields(final ObjectNode fieldsNode, final Map<String, String> properties) {
    if (properties != null) {
      for (Map.Entry<String, String> entry : properties.entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        fieldsNode.put(key, value);
      }
    }
  }

  private JsonNode getJsonNode(ILoggingEvent event) {
    final Object[] args = event.getArgumentArray();
    return MAPPER.convertValue(args, JsonNode.class);
  }

  private StackTraceElement extractCallerData(final ILoggingEvent event) {
    final StackTraceElement[] ste = event.getCallerData();
    if (ste == null || ste.length == 0) {
      return DEFAULT_CALLER_DATA;
    }
    return ste[0];
  }

  private void addCustomFields(ObjectNode eventNode) {
    if (customFields != null) {
      Iterator<String> i = customFields.fieldNames();
      while (i.hasNext()) {
        String k = i.next();
        JsonNode v = customFields.get(k);
        eventNode.put(k, v);
      }
    }
  }

  public boolean isIncludeCallerInfo() {
    return includeCallerInfo;
  }

  public void setIncludeCallerInfo(boolean includeCallerInfo) {
    this.includeCallerInfo = includeCallerInfo;
  }

  public void setCustomFields(JsonNode customFields) {
    this.customFields = customFields;
  }

  public JsonNode getCustomFields() {
    return this.customFields;
  }
}