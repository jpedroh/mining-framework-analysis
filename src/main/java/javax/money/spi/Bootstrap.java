package javax.money.spi;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import javax.money.MonetaryException;

public final class Bootstrap {
  private static volatile ServiceProvider serviceProviderDelegate;

  private static final Object LOCK = new Object();

  private Bootstrap() {
  }

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

  public static <T extends java.lang.Object> Collection<T> getServices(Class<T> serviceType) {
    return getServiceProvider().getServices(serviceType);
  }

  public static <T extends java.lang.Object> T getService(Class<T> serviceType) {
    List<T> services = getServiceProvider().getServices(serviceType);
    return services.stream().findFirst().orElseThrow(() -> new MonetaryException("No such service found: " + serviceType));
  }
}