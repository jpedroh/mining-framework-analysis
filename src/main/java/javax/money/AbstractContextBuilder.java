package javax.money;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.time.LocalDateTime;

public abstract class AbstractContextBuilder<B extends AbstractContextBuilder, C extends AbstractContext> {
  final Map<String, Object> data = new HashMap<>();

  public B importContext(AbstractContext context, boolean overwriteDuplicates) {
    for (Map.Entry<String, Object> en : context.data.entrySet()) {
      if (overwriteDuplicates) {
        this.data.put(en.getKey(), en.getValue());
      } else {
        this.data.putIfAbsent(en.getKey(), en.getValue());
      }
    }
    return (B) this;
  }

  public B importContext(AbstractContext context) {
    Objects.requireNonNull(context);
    return importContext(context, false);
  }

  public B set(String key, int value) {
    this.data.put(key, Objects.requireNonNull(value));
    return (B) this;
  }

  public B set(String key, boolean value) {
    this.data.put(key, Objects.requireNonNull(value));
    return (B) this;
  }

  public B set(String key, long value) {
    this.data.put(key, Objects.requireNonNull(value));
    return (B) this;
  }

  public B set(String key, float value) {
    this.data.put(key, Objects.requireNonNull(value));
    return (B) this;
  }

  public B set(String key, double value) {
    this.data.put(key, Objects.requireNonNull(value));
    return (B) this;
  }

  public B set(String key, char value) {
    this.data.put(key, Objects.requireNonNull(value));
    return (B) this;
  }

  public B set(Object value) {
    data.put(value.getClass().getName(), value);
    return (B) this;
  }

  public B set(String key, Object value) {
    data.put(key, value);
    return (B) this;
  }

  public <T extends java.lang.Object> B set(Class<T> key, T value) {
    Object old = set(key.getName(), value);
    if (old != null && old.getClass().isAssignableFrom(value.getClass())) {
      return (B) old;
    }
    return (B) this;
  }

  public B setProviderName(String provider) {
    Objects.requireNonNull(provider);
    set(AbstractContext.KEY_PROVIDER, provider);
    return (B) this;
  }

  public B setTimestampMillis(long timestamp) {
    set(AbstractContext.KEY_TIMESTAMP, timestamp);
    return (B) this;
  }

  public B removeAttributes(String... keys) {
    for (String key : keys) {
      this.data.remove(key);
    }
    return (B) this;
  }

  public B removeAll() {
    this.data.clear();
    return (B) this;
  }

  public abstract C build();

  @Override public String toString() {
    return getClass().getSimpleName() + " [attributes:\n" + new TreeMap<>(data).toString() + ']';
  }

  public B setTimestamp(LocalDateTime timestamp) {
    set(AbstractContext.KEY_TIMESTAMP, Objects.requireNonNull(timestamp));
    return (B) this;
  }
}