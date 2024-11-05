package javax.money;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public abstract class AbstractQueryBuilder<B extends javax.money.AbstractQueryBuilder, C extends AbstractQuery> extends AbstractContextBuilder<B, C> {
  public AbstractQueryBuilder() {
  }

  public B setProviderNames(String... providers) {
    return setProviderNames(Arrays.asList(providers));
  }

  public B setProviderNames(List<String> providers) {
    Objects.requireNonNull(providers);
    return set(AbstractQuery.KEY_QUERY_PROVIDERS, providers);
  }

  public B set(List<String> providers) {
    return set(AbstractQuery.KEY_QUERY_PROVIDERS, providers);
  }

  @Override public B setProviderName(String provider) {
    return setProviderNames(provider);
  }

  public @Override B setTimestampMillis(long timestamp) {
    Date date = new Date(timestamp);
    LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    return set(AbstractQuery.KEY_QUERY_TIMESTAMP, localDateTime);
  }

  public B setTargetType(Class<?> type) {
    Objects.requireNonNull(type);
    set(AbstractQuery.KEY_QUERY_TARGET_TYPE, type);
    return (B) this;
  }

  public abstract @Override C build();

  @Override public B setTimestamp(LocalDateTime timestamp) {
    set(AbstractQuery.KEY_QUERY_TIMESTAMP, Objects.requireNonNull(timestamp));
    return (B) this;
  }

  public B setTimestamp(LocalDate timestamp) {
    return setTimestamp(timestamp.atTime(LocalTime.now()));
  }

  public B setTimestamp(LocalTime timestamp) {
    return setTimestamp(LocalDate.now().atTime(timestamp));
  }
}