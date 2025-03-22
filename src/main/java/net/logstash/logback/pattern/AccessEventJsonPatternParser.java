package net.logstash.logback.pattern;
import ch.qos.logback.access.PatternLayout;
import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import com.fasterxml.jackson.core.JsonFactory;

/**
 * @author <a href="mailto:dimas@dataart.com">Dmitry Andrianov</a>
 */
public class AccessEventJsonPatternParser extends AbstractJsonPatternParser<IAccessEvent> {
  public AccessEventJsonPatternParser(final Context context, final JsonFactory jsonFactory) {
    super(context, jsonFactory);
    addOperation("nullNA", new NullNaValueOperation());
  }

  protected class NullNaValueOperation extends 
<<<<<<< Unknown file: This is a bug in JDime.
=======
  AbstractJsonPatternParser<IAccessEvent>.Operation<String>
>>>>>>> /usr/src/app/output/logstash/logstash-logback-encoder/35759d0a000d8ea4bd238e736572bf94bc7891d9/src/main/java/net/logstash/logback/pattern/AccessEventJsonPatternParser.java/right.java
   implements Operation<IAccessEvent, String> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    public NullNaValueOperation() {
      super(true);
    }
>>>>>>> /usr/src/app/output/logstash/logstash-logback-encoder/35759d0a000d8ea4bd238e736572bf94bc7891d9/src/main/java/net/logstash/logback/pattern/AccessEventJsonPatternParser.java/right.java


    @Override public ValueGetter<String, IAccessEvent> createValueGetter(String data) {
      return makeLayoutValueGetter(data).andThen(this::convert);
    }

    private String convert(final String value) {
      return "-".equals(value) ? null : value;
    }
  }

  @Override protected PatternLayoutBase<IAccessEvent> createLayout() {
    return new PatternLayout();
  }
}