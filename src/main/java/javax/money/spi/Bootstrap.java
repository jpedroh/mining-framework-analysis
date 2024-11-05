package javax.money.spi;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import javax.money.MonetaryException;

/**
 * This singleton provides access to the services available in the current runtime environment and context. The
 * behaviour can be adapted, by calling {@link Bootstrap#init(ServiceProvider)} before accessing any moneteray
 * services.
 *
 * @author Anatole Tresch
 */
public final class Bootstrap {
  /**
     * The ServiceProvider used.
     */
  private static volatile ServiceProvider serviceProviderDelegate;

  /**
     * The shared lock instance user.
     */
  private static final Object LOCK = new Object();

  /**
     * Private singletons constructor.
     */
  private Bootstrap() {
  }

  /**
     * Load the {@link ServiceProvider} to be used.
     *
     * @return {@link ServiceProvider} to be used for loading the services.
     */
  private static ServiceProvider loadDefaultServiceProvider() {
    try {
      for (ServiceProvider sp : ServiceLoader.load(ServiceProvider.class)) {
        return sp;
      }
    } catch (Exception e) {
      Logger.getLogger(Bootstrap.class.getName()).info("No ServiceProvider loaded, using default.");
    }
    return new DefaultServiceProvider();
  }

  /**
     * Replace the current {@link ServiceProvider} in use.
     *
     * @param serviceProvider the new {@link ServiceProvider}
     * @return the removed , or null.
     */
  public static ServiceProvider init(ServiceProvider serviceProvider) {
    Objects.requireNonNull(serviceProvider);
    synchronized (LOCK) {
      if (Objects.isNull(Bootstrap.serviceProviderDelegate)) {
        Bootstrap.serviceProviderDelegate = serviceProvider;
        Logger.getLogger(Bootstrap.class.getName()).info("Money Bootstrap: new ServiceProvider set: " + serviceProvider.getClass().getName());
        return null;
      } else {
        ServiceProvider prevProvider = Bootstrap.serviceProviderDelegate;
        Bootstrap.serviceProviderDelegate = serviceProvider;
        Logger.getLogger(Bootstrap.class.getName()).warning("Money Bootstrap: ServiceProvider replaced: " + serviceProvider.getClass().getName());
        return prevProvider;
      }
    }
  }

  /**
     * Ge {@link ServiceProvider}. If necessary the {@link ServiceProvider} will be laziliy loaded.
     *
     * @return the {@link ServiceProvider} used.
     */
  static ServiceProvider getServiceProvider() {
    if (Objects.isNull(serviceProviderDelegate)) {
      synchronized (LOCK) {
        if (Objects.isNull(serviceProviderDelegate)) {
          serviceProviderDelegate = loadDefaultServiceProvider();
        }
      }
    }
    return serviceProviderDelegate;
  }

  /**
     * Delegate method for {@link ServiceProvider#getServices(Class)}.
     *
     * @param serviceType the service type.
     * @return the services found.
     * @see ServiceProvider#getServices(Class)
     */
  public static <T extends java.lang.Object> Collection<T> getServices(Class<T> serviceType) {
    return getServiceProvider().getServices(serviceType);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
     * Delegate method for {@link ServiceProvider#getServices(Class)}.
     *
     * @param serviceType the service type.
     * @return the service found, never {@code null}.
     * @see ServiceProvider#getServices(Class)
     */
  public static <T extends java.lang.Object> T getService(Class<T> serviceType) {
    List<T> services = getServiceProvider().getServices(serviceType);
    return services.stream().findFirst().orElseThrow(() -> new MonetaryException("No such service found: " + serviceType));
  }
>>>>>>> /usr/src/app/output/javamoney/jsr354-api/b5c1fbf3e07e805e8351775ba0a67133e5c3fe65/src/main/java/javax/money/spi/Bootstrap.java/right.java


  /**
     * Delegate method for {@link ServiceProvider#getServices(Class)}.
     *
     * @param serviceType the service type.
     * @return the service found, never {@code null}.
     * @see ServiceProvider#getServices(Class)
     */
  public static <T extends java.lang.Object> T getService(Class<T> serviceType) {
    List<T> services = getServiceProvider().getServices(serviceType);
    if (services.isEmpty()) {
    }
    return services.get(0);
  }
}