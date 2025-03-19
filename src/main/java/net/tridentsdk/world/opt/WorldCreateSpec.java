package net.tridentsdk.world.opt;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * The options for creating a new world.
 *
 * <p>These options will be transferred over to their
 * appropriate opt classes when the world has finished
 * generating.</p>
 *
 * <p>Although this class is not thread safe, it should be
 * safely published by passing it to the world loader.</p>
 *
 * @author TridentSDK
 * @since 0.4-alpha
 */
@NotThreadSafe public class WorldCreateSpec {
  /**
     * The default instance of the world creator
     * specification, which sets all of the settings to
     * their default in a vanilla world.
     */
  private static final WorldCreateSpec DEFAULT = new DefaultSpec();

  private static class DefaultSpec extends WorldCreateSpec {
    private DefaultSpec() {
      super(true);
    }
  }

  /**
     * Whether this spec uses the default world options
     */
  private final boolean def;

  private WorldCreateSpec(boolean def) {
    this.def = def;
  }

  /**
     * Uses the default world options to build the world.
     *
     * @return the default world specification
     */
  public static WorldCreateSpec getDefaultOptions() {
    return DEFAULT;
  }

  /**
     * Create a new custom world options specification that
     * can be passed to the server world loader to create
     * a custom world.
     *
     * @return a new custom world specification
     */
  public static WorldCreateSpec custom() {
    return new WorldCreateSpec(false);
  }

  /**
     * Determines whether this option specification is uses
     * all default values or not.
     *
     * @return {@code true} for default values
     */
  public boolean isDefault() {
    return this.def;
  }
}