package org.omnifaces.config;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;
import org.omnifaces.util.JNDI;

/**
 * <p>
 * This configuration enum allows you to get a reference to CDI managed beans without having any direct CDI dependency.
 * It will during initialization grab the CDI bean manager instance from JNDI and if it's not <code>null</code>, then
 * it's using reflection to get and store the necessary methods in the enum instance which are then invoked on instance
 * methods such as <code>getReference()</code>.
 *
 * <h3>Usage</h3>
 * <pre>
 * // Get the CDI managed bean instance of the given bean class.
 * SomeBean someBean = BeanManager.INSTANCE.getReference(SomeBean.class);
 * </pre>
 *
 * @author Bauke Scholtz
 * @since 1.6.1
 */public enum BeanManager {
  INSTANCE
  ;

  private static final String ERROR_CDI_IMPL_UNAVAILABLE = "CDI BeanManager instance is not available in JNDI.";

  private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

  private static final String ERROR_CDI_API_UNAVAILABLE = "CDI API is not available in this environment.";

  private static final String ERROR_JNDI_UNAVAILABLE = "JNDI is not available in this environment.";

  private static final String ERROR_INITIALIZATION_FAIL = "CDI BeanManager instance is available, but preparing getReference() method failed.";

  private Object beanManager;

  private Method getBeans;

  private Method resolve;

  private Method createCreationalContext;

  private Method getReference;


<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/config/BeanManager.java/left.java
  /**
	 * Perform automatic initialization whereby the bean manager is looked up from the JNDI. If the bean manager is
	 * found, then invoke {@link #init(Object)} with the found bean manager.
	 */
  private void init() {
    if (!initialized.getAndSet(true)) {
      try {
        Class.forName("javax.enterprise.inject.spi.BeanManager");
        JNDI.lookup("java:comp");
      } catch (Throwable e) {
        return;
      }
      try {
        Object beanManager = JNDI.lookup("java:comp/BeanManager");
        if (beanManager == null) {
          beanManager = JNDI.lookup("java:comp/env/BeanManager");
        }
        if (beanManager == null) {
          return;
        }
        this.beanManager = beanManager;
        Class<?> beanManagerClass = beanManager.getClass();
        Class<?> contextualClass = Class.forName("javax.enterprise.context.spi.Contextual");
        Class<?> beanClass = Class.forName("javax.enterprise.inject.spi.Bean");
        Class<?> creationalContextClass = Class.forName("javax.enterprise.context.spi.CreationalContext");
        getBeans = beanManagerClass.getMethod("getBeans", Type.class, Annotation[].class);
        resolve = beanManagerClass.getMethod("resolve", Set.class);
        createCreationalContext = beanManagerClass.getMethod("createCreationalContext", contextualClass);
        getReference = beanManagerClass.getMethod("getReference", beanClass, Type.class, creationalContextClass);
      } catch (RuntimeException e) {
        return;
      } catch (Exception e) {
        initialized.set(false);
        logger.log(Level.SEVERE, LOG_INITIALIZATION_ERROR, e);
        throw new RuntimeException(e);
      }
    }
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  /**
	 * Perform automatic initialization whereby the bean manager is looked up from the JNDI.
	 * @throws IllegalStateException When initialization fails.
	 */
  private BeanManager() {
    Class<?> beanManagerClass, contextualClass, beanClass, creationalContextClass;
    try {
      beanManagerClass = Class.forName("javax.enterprise.inject.spi.BeanManager");
      contextualClass = Class.forName("javax.enterprise.context.spi.Contextual");
      beanClass = Class.forName("javax.enterprise.inject.spi.Bean");
      creationalContextClass = Class.forName("javax.enterprise.context.spi.CreationalContext");
    } catch (Exception | LinkageError e) {
      throw new IllegalStateException(ERROR_CDI_API_UNAVAILABLE, e);
    }
    try {
      beanManager = JNDI.lookup("java:comp/BeanManager");
      if (beanManager == null) {
        beanManager = JNDI.lookup("java:comp/env/BeanManager");
      }
    } catch (IllegalStateException e) {
      throw new IllegalStateException(ERROR_CDI_IMPL_UNAVAILABLE, e);
    } catch (Exception | LinkageError e) {
      throw new IllegalStateException(ERROR_JNDI_UNAVAILABLE, e);
    }
    if (beanManager == null) {
      throw new IllegalStateException(ERROR_CDI_IMPL_UNAVAILABLE);
    }
    try {
      getBeans = beanManagerClass.getMethod("getBeans", Type.class, Annotation[].class);
      resolve = beanManagerClass.getMethod("resolve", Set.class);
      createCreationalContext = beanManagerClass.getMethod("createCreationalContext", contextualClass);
      getReference = beanManagerClass.getMethod("getReference", beanClass, Type.class, creationalContextClass);
    } catch (Exception e) {
      throw new IllegalStateException(ERROR_INITIALIZATION_FAIL, e);
    }
  }

  /**
	 * Returns the CDI bean manager.
	 * @param <T> The <code>javax.enterprise.inject.spi.BeanManager</code>.
	 * @return The CDI bean manager.
	 * @throws ClassCastException When you assign it to a variable which is not declared as CDI BeanManager.
	 */
  @SuppressWarnings(value = { "unchecked" }) public <T extends java.lang.Object> T get() {
    return (T) beanManager;
  }

  /**
	 * Returns the CDI managed bean instance of the given class, or <code>null</code> if there is none.
	 * @param <T> The generic bean type.
	 * @param beanClass The type of the CDI managed bean instance.
	 * @return The CDI managed bean instance of the given class, or <code>null</code> if there is none.
	 */
  public <T extends java.lang.Object> T getReference(Class<T> beanClass) {
    try {
      Object bean = resolve.invoke(beanManager, getBeans.invoke(beanManager, beanClass, NO_ANNOTATIONS));
      if (bean == null) {
        return null;
      }
      Object creationalContext = createCreationalContext.invoke(beanManager, bean);
      Object reference = getReference.invoke(beanManager, bean, beanClass, creationalContext);
      return beanClass.cast(reference);
    } catch (Exception e) {
      throw new UnsupportedOperationException(e);
    }
  }
}