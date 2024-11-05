package javax.money;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public abstract class AbstractQuery extends AbstractContext {
  public static final String KEY_QUERY_PROVIDERS = "Query.providers";

  public static final String KEY_QUERY_TIMESTAMP = "Query.timestamp";

  public static final String KEY_QUERY_TARGET_TYPE = "Query.targetType";

  protected AbstractQuery(AbstractQueryBuilder builder) {
    super(builder);
  }

  public List<String> getProviderNames() {
    List<String> result = get(KEY_QUERY_PROVIDERS, List.class);
    if (result == null) {
      return Collections.emptyList();
    }
    return result;
  }

  public Class<?> getTargetType() {
    return get(KEY_QUERY_TARGET_TYPE, Class.class);
  }

  public @Override Long getTimestampMillis() {
    LocalDateTime value = getTimestamp();
    if (Objects.nonNull(value)) {
      return Date.from(value.atZone(ZoneId.systemDefault()).toInstant()).getTime();
    } else {
      if (value instanceof TemporalAccessor) {
        TemporalAccessor acc = (TemporalAccessor) value;
        return (acc.getLong(ChronoField.INSTANT_SECONDS) * 1000L) + acc.getLong(ChronoField.MILLI_OF_SECOND);
      }
    }
    return null;
  }

  public @Override LocalDateTime getTimestamp() {
    return get(KEY_QUERY_TIMESTAMP, LocalDateTime.class);
  }
}