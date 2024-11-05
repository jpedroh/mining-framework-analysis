package javax.money;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings(value = { "unchecked" }) public abstract class AbstractContext implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String KEY_PROVIDER = "provider";

  public static final String KEY_TIMESTAMP = "timestamp";

  final Map<String, Object> data = new HashMap<>();

  @SuppressWarnings(value = { "rawtypes" }) protected AbstractContext(AbstractContextBuilder<?, ?> builder) {
    data.putAll(builder.data);
  }

  public Set<String> getKeys(Class<?> type) {
    return data.entrySet().stream().filter((val) -> type.isAssignableFrom(val.getValue().getClass())).map(Map.Entry::getKey).collect(Collectors.toSet());
  }

  public Class<?> getType(String key) {
    Object val = this.data.get(key);
    return val == null ? null : val.getClass();
  }

  public <T extends java.lang.Object> T get(String key, Class<T> type) {
    Object value = this.data.get(key);
    if (value != null && type.isAssignableFrom(value.getClass())) {
      return (T) value;
    }
    return null;
  }

  public <T extends java.lang.Object> T get(Class<T> type) {
    return get(type.getName(), type);
  }

  public Long getLong(String key) {
    return get(key, Long.class);
  }

  public Float getFloat(String key) {
    return get(key, Float.class);
  }

  public Integer getInt(String key) {
    return get(key, Integer.class);
  }

  public Boolean getBoolean(String key) {
    return get(key, Boolean.class);
  }

  public Double getDouble(String key) {
    return get(key, Double.class);
  }

  public String getText(String key) {
    return get(key, String.class);
  }

  public String getProviderName() {
    return getText(KEY_PROVIDER);
  }

  public Long getTimestampMillis() {
    Long value = get(KEY_TIMESTAMP, Long.class);
    if (Objects.isNull(value)) {
      TemporalAccessor acc = getTimestamp();
      if (Objects.nonNull(acc)) {
        return (acc.getLong(ChronoField.INSTANT_SECONDS) * 1000L) + acc.getLong(ChronoField.MILLI_OF_SECOND);
      }
    }
    return value;
  }

  public TemporalAccessor getTimestamp() {
    TemporalAccessor acc = get(KEY_TIMESTAMP, TemporalAccessor.class);
    if (Objects.isNull(acc)) {
      Long value = get(KEY_TIMESTAMP, Long.class);
      if (Objects.nonNull(value)) {
        acc = Instant.ofEpochMilli(value);
      }
    }
    return acc;
  }

  public boolean isEmpty() {
    return this.data.isEmpty();
  }

  @Override public int hashCode() {
    return Objects.hash(data);
  }

  public <T extends java.lang.Object> Map<String, T> getValues(Class<T> type) {
    Map<String, T> result = new HashMap<>();
    data.entrySet().stream().filter((en) -> type.isAssignableFrom(en.getValue().getClass())).forEach((en) -> result.put(en.getKey(), type.cast(en.getValue())));
    return result;
  }

  @Override public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof AbstractContext) {
      AbstractContext other = (AbstractContext) obj;
      return Objects.equals(data, other.data);
    }
    return false;
  }

  @Override public String toString() {
    return getClass().getSimpleName() + " (\n" + data + ')';
  }
}