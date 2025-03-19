package com.google.inject.struts2;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.internal.Annotations;
import com.google.inject.servlet.ServletModule;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @deprecated Use {@link com.google.inject.servlet.Struts2Factory} instead.
 */
@Deprecated public class GuiceObjectFactory extends ObjectFactory {
  static final Logger logger = Logger.getLogger(GuiceObjectFactory.class.getName());

  Module module;

  volatile Injector injector;

  boolean developmentMode = false;

  List<ProvidedInterceptor> interceptors = new ArrayList<ProvidedInterceptor>();

  @Override public boolean isNoArgConstructorRequired() {
    return false;
  }

  @Inject(value = "guice.module", required = false) void setModule(String moduleClassName) {
    try {
      @SuppressWarnings(value = { "unchecked" }) Class<? extends Module> moduleClass = (Class<? extends Module>) Class.forName(moduleClassName);
      this.module = moduleClass.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Inject(value = "struts.devMode", required = false) void setDevelopmentMode(String developmentMode) {
    this.developmentMode = developmentMode.trim().equals("true");
  }

  Set<Class<?>> boundClasses = new HashSet<Class<?>>();

  public Class getClassInstance(String name) throws ClassNotFoundException {
    Class<?> clazz = super.getClassInstance(name);
    synchronized (this) {
      if (injector == null) {
        if (!boundClasses.contains(clazz)) {
          try {
            clazz.getDeclaredFields();
            clazz.getDeclaredMethods();
            boundClasses.add(clazz);
          } catch (Throwable t) {
            return clazz;
          }
        }
      }
    }
    return clazz;
  }

  @SuppressWarnings(value = { "unchecked" }) public Object buildBean(Class clazz, Map extraContext) {
    if (injector == null) {
      synchronized (this) {
        if (injector == null) {
          createInjector();
        }
      }
    }
    return injector.getInstance(clazz);
  }

  private void createInjector() {
    try {
      logger.info("Creating injector...");
      this.injector = Guice.createInjector(new AbstractModule() {
        protected void configure() {
          install(new ServletModule());
          if (module != null) {
            logger.info("Installing " + module + "...");
            install(module);
          } else {
            logger.info("No module found. Set \'guice.module\' to a Module " + "class name if you\'d like to use one.");
          }
          for (Class<?> boundClass : boundClasses) {
            bind(boundClass);
          }
          for (ProvidedInterceptor interceptor : interceptors) {
            interceptor.validate(binder());
          }
        }
      });
      for (ProvidedInterceptor interceptor : interceptors) {
        interceptor.inject();
      }
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
    logger.info("Injector created successfully.");
  }

  @SuppressWarnings(value = { "unchecked" }) public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map interceptorRefParams) throws ConfigurationException {
    Class<? extends Interceptor> interceptorClass;
    try {
      interceptorClass = getClassInstance(interceptorConfig.getClassName());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    ProvidedInterceptor providedInterceptor = new ProvidedInterceptor(interceptorConfig, interceptorRefParams, interceptorClass);
    interceptors.add(providedInterceptor);
    return providedInterceptor;
  }

  Interceptor superBuildInterceptor(InterceptorConfig interceptorConfig, Map interceptorRefParams) throws ConfigurationException {
    return super.buildInterceptor(interceptorConfig, interceptorRefParams);
  }

  class ProvidedInterceptor implements Interceptor {
    final InterceptorConfig config;

    final Map params;

    final Class<? extends Interceptor> interceptorClass;

    Interceptor delegate;

    ProvidedInterceptor(InterceptorConfig config, Map params, Class<? extends Interceptor> interceptorClass) {
      this.config = config;
      this.params = params;
      this.interceptorClass = interceptorClass;
    }

    void validate(Binder binder) {
      if (hasScope(interceptorClass)) {
        binder.addError("Scoping interceptors is not currently supported." + " Please remove the scope annotation from " + interceptorClass.getName() + ".");
      }
      if (!Interceptor.class.isAssignableFrom(interceptorClass)) {
        binder.addError(interceptorClass.getName() + " must implement " + Interceptor.class.getName() + ".");
      }
    }

    void inject() {
      delegate = superBuildInterceptor(config, params);
    }

    public void destroy() {
      if (null != delegate) {
        delegate.destroy();
      }
    }

    public void init() {
      throw new AssertionError();
    }

    public String intercept(ActionInvocation invocation) throws Exception {
      return delegate.intercept(invocation);
    }
  }

  /**
   * Returns true if the given class has a scope annotation.
   */
  private static boolean hasScope(Class<? extends Interceptor> interceptorClass) {
    for (Annotation annotation : interceptorClass.getAnnotations()) {
      if (Annotations.isScopeAnnotation(annotation.annotationType())) {
        return true;
      }
    }
    return false;
  }
}