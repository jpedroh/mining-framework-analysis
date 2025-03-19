package org.omnifaces.config;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ServletContext;
import java.util.logging.Level;
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

  private static final 
<<<<<<< /usr/src/app/output/omnifaces/omnifaces/513e06404724a0c2fe2db0e0ce70e68a8430c18c/src/main/java/org/omnifaces/config/BeanManager.java/left.java
  Logger
=======
  String
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/513e06404724a0c2fe2db0e0ce70e68a8430c18c/src/main/java/org/omnifaces/config/BeanManager.java/right.java
   
<<<<<<< /usr/src/app/output/omnifaces/omnifaces/513e06404724a0c2fe2db0e0ce70e68a8430c18c/src/main/java/org/omnifaces/config/BeanManager.java/left.java
  logger = Logger.getLogger(BeanManager.class.getName())
=======
  WELD_BEAN_MANAGER = "org.jboss.weld.environment.servlet.javax.enterprise.inject.spi.BeanManager"
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/513e06404724a0c2fe2db0e0ce70e68a8430c18c/src/main/java/org/omnifaces/config/BeanManager.java/right.java
  ;

  private static final String LOG_INITIALIZATION_ERROR = "BeanManager enum singleton failed to initialize.";

  private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

  private AtomicBoolean initialized = new AtomicBoolean();

  private volatile Object beanManager;

  private Method getBeans;

  private Method resolve;

  private Method createCreationalContext;

  private Method getReference;


<<<<<<< Unknown file: This is a bug in JDime.
=======
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
    try {
      getBeans = beanManagerClass.getMethod("getBeans", Type.class, Annotation[].class);
      resolve = beanManagerClass.getMethod("resolve", Set.class);
      createCreationalContext = beanManagerClass.getMethod("createCreationalContext", contextualClass);
      getReference = beanManagerClass.getMethod("getReference", beanClass, Type.class, creationalContextClass);
    } catch (Exception e) {
      throw new IllegalStateException(ERROR_INITIALIZATION_FAIL, e);
    }
  }
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/513e06404724a0c2fe2db0e0ce70e68a8430c18c/src/main/java/org/omnifaces/config/BeanManager.java/right.java


  /**
	 * Perform automatic initialization whereby the bean manager is looked up from the JNDI. If the bean manager is
	 * found, then invoke {@link #init(Object)} with the found bean manager.
	 */
  private public void init(ServletContext servletContext) {
    if (beanManager == null) {
      beanManager = servletContext.getAttribute(WELD_BEAN_MANAGER);
    }

<<<<<<< /usr/src/app/output/omnifaces/omnifaces/513e06404724a0c2fe2db0e0ce70e68a8430c18c/src/main/java/org/omnifaces/config/BeanManager.java/left.java
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
=======
    if (beanManager == null) {
      throw new IllegalStateException(ERROR_CDI_IMPL_UNAVAILABLE);
    }
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/513e06404724a0c2fe2db0e0ce70e68a8430c18c/src/main/java/org/omnifaces/config/BeanManager.java/right.java
  }

  /**
	 * Returns the CDI managed bean instance of the given class, or <code>null</code> if there is none.
	 * @param <T> The generic bean type.
	 * @param beanClass The type of the CDI managed bean instance.
	 * @return The CDI managed bean instance of the given class, or <code>null</code> if there is none.
	 */
  public <T extends java.lang.Object> T getReference(Class<T> beanClass) {
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