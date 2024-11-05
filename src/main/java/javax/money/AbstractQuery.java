package javax.money;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Represents a general context of data targeting an item of type {@code Q}. Contexts are used to add arbitrary
 * data that cannot be be mapped in a standard way to the money API, e.g. use case or customer specific
 * extensions os specialities.<p>
 * Superclasses of this class must be final, immutable, serializable and thread-safe.
 */
public abstract class AbstractQuery extends AbstractContext {
  /**
     * Key for storing the target providers to be queried
     */
  public static final String KEY_QUERY_PROVIDERS = "Query.providers";

  /**
     * Key name for the timestamp attribute.
     */
  public static final String KEY_QUERY_TIMESTAMP = "Query.timestamp";

  /**
     * Key name for the target type attribute.
     */
  public static final String KEY_QUERY_TARGET_TYPE = "Query.targetType";

  /**
     * Constructor, using a builder.
     *
     * @param builder the builder, not null.
     */
  protected AbstractQuery(AbstractQueryBuilder builder) {
    super(builder);
  }

  /**
     * Returns the providers and their ordering to be considered. This information typically must be interpreted by the
     * singleton SPI implementations, which are backing the singleton accessors.
     * If the list returned is empty, the default provider list,
     * determined by methods like {@code getDefaultProviderNames()} should be used.
     *
     * @return the ordered providers, never null.
     */
  public List<String> getProviderNames() {
    List<String> result = get(KEY_QUERY_PROVIDERS, List.class);
    if (result == null) {
      return Collections.emptyList();
    }
    return result;
  }

  /**
     * Gets the target implementation type required. This can be used to explicitly acquire a specific implementation
     * type and use a query to configure the instance or factory to be returned.
     *
     * @return this Builder for chaining.
     */
  public Class<?> getTargetType() {
    return get(KEY_QUERY_TARGET_TYPE, Class.class);
  }

  /**
     * Get the current timestamp of the context in UTC milliseconds.  If not set it tries to of an
     * UTC timestamp from #getTimestamp().
     *
     * @return the timestamp in millis, or null.
     */
  @Override public Long getTimestampMillis() {
    LocalDateTime value = getTimestamp();
    if (Objects.nonNull(value)) {
      return Date.from(value.atZone(ZoneId.systemDefault()).toInstant()).getTime();
    }
    return null;
  }

  /**
     * Get the current timestamp. If not set it tries to of an Instant from #getTimestampMillis().
     *
     * @return the current timestamp, or null.
     */
  @Override public LocalDateTime getTimestamp() {
    return get(KEY_QUERY_TIMESTAMP, LocalDateTime.class);
  }
}