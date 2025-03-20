package org.omnifaces.config;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.omnifaces.ApplicationListener;
import java.util.logging.Level;
import org.omnifaces.util.Beans;
import java.util.logging.Logger;
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

  private static final Logger logger = Logger.getLogger(BeanManager.class.getName());

  private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

  private static final String LOG_INITIALIZATION_ERROR = "BeanManager enum singleton failed to initialize.";

  private AtomicBoolean initialized = new AtomicBoolean();

  private Object beanManager;

  private Method getBeans;

  private Method resolve;

  private Method createCreationalContext;

  private Method getReference;

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

  /**
	 * Returns the CDI managed bean instance of the given class, or <code>null</code> if there is none.
	 * @param <T> The generic bean type.
	 * @param beanClass The type of the CDI managed bean instance.
	 * @return The CDI managed bean instance of the given class, or <code>null</code> if there is none.
	 */
  @Deprecated public <T extends java.lang.Object> T getReference(Class<T> beanClass) {
    init();
    if (beanManager == null) {
      return null;
    }
    try {
      Object bean = resolve.invoke(beanManager, getBeans.invoke(beanManager, beanClass, NO_ANNOTATIONS));
      Object creationalContext = createCreationalContext.invoke(beanManager, bean);
      Object reference = getReference.invoke(beanManager, bean, beanClass, creationalContext);
      return beanClass.cast(reference);
    } catch (Exception e) {
      return null;
    }
  }
}