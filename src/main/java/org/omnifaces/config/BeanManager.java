/*
 * Copyright 2013 OmniFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.omnifaces.ApplicationListener;
import org.omnifaces.util.Beans;
import org.omnifaces.util.JNDI;

/**
 * <p>
 * This configuration enum allows you to get the CDI <code>BeanManager</code> anyway in cases where
 * <code>&#64;Inject</code> and/or <code>CDI#current()</code> may not work, or when you'd like to test availability of
 * CDI without having any direct CDI dependency (as done in {@link ApplicationListener}). It will during initialization
 * grab the CDI bean manager instance as generic object from JNDI.
 * <p>
 * <strong>Do not use it directly.</strong> Use {@link Beans} utility class instead. It will under the covers use this
 * configuration enum. This configuration enum is basically a leftover from OmniFaces 1.x where the CDI dependency was
 * optional. The {@link #getReference(Class)} method is deprecated since OmniFaces 2.3 and will be removed in OmniFaces
 * 3.0.
 *
 * @author Bauke Scholtz
 * @since 1.6.1
 */
public enum BeanManager {

	// Enum singleton -------------------------------------------------------------------------------------------------

	/**
	 * Returns the lazily loaded enum singleton instance.
	 * Throws {@link IllegalStateException} when initialization fails.
	 */
	INSTANCE;

	// Private constants ----------------------------------------------------------------------------------------------

	private static final Logger logger = Logger.getLogger(BeanManager.class.getName());
	private static final String LOG_INITIALIZATION_ERROR = "BeanManager enum singleton failed to initialize.";
	private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

	// Properties -----------------------------------------------------------------------------------------------------

	private AtomicBoolean initialized = new AtomicBoolean();
	private Object beanManager;
	private Method getBeans;
	private Method resolve;
	private Method createCreationalContext;
	private Method getReference;

	// Init -----------------------------------------------------------------------------------------------------------

	/**
	 * Perform automatic initialization whereby the bean manager is looked up from the JNDI. If the bean manager is
	 * found, then invoke {@link #init(Object)} with the found bean manager.
	 */
	private void init() {
		if (!initialized.getAndSet(true)) {
			try {
				Class.forName("javax.enterprise.inject.spi.BeanManager"); // Is CDI present?
				JNDI.lookup("java:comp"); // Is JNDI present? (not on Google App Engine)
			}
			catch (Throwable e) {
				return; // CDI or JNDI not supported on this environment.
			}

			try {
				Object beanManager = JNDI.lookup("java:comp/BeanManager"); // CDI spec.

				if (beanManager == null) {
					beanManager = JNDI.lookup("java:comp/env/BeanManager"); // Tomcat.
				}

				if (beanManager == null) {
					return; // CDI not registered on this environment.
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
			}
			catch (RuntimeException e) {
				return; // CDI most likely just not supported on this environment.
			}
			catch (Exception e) {
				initialized.set(false);
				logger.log(Level.SEVERE, LOG_INITIALIZATION_ERROR, e);
				throw new RuntimeException(e);
			}
		}
	}

	// Actions --------------------------------------------------------------------------------------------------------

	/**
<<<<<<< /usr/src/app/output/omnifaces/omnifaces/33b5cb552f5a2362066606d943400a7ea8ea48ca/src/main/java/org/omnifaces/config/BeanManager.java/left.java
	 * Returns the CDI managed bean instance of the given class, or <code>null</code> if there is none.
	 * @param <T> The generic bean type.
	 * @param beanClass The type of the CDI managed bean instance.
	 * @return The CDI managed bean instance of the given class, or <code>null</code> if there is none.
||||||| /usr/src/app/output/omnifaces/omnifaces/33b5cb552f5a2362066606d943400a7ea8ea48ca/src/main/java/org/omnifaces/config/BeanManager.java/base.java
	 * Returns the CDI bean manager.
	 * @param <T> The <code>javax.enterprise.inject.spi.BeanManager</code>.
	 * @return The CDI bean manager.
	 * @throws ClassCastException When you assign it to a variable which is not declared as CDI BeanManager.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get() {
		return (T) beanManager;
	}

	/**
	 * Returns the CDI managed bean reference (proxy) of the given class.
	 * Note that this actually returns a client proxy and the underlying actual instance is thus always auto-created.
	 * @param <T> The expected return type.
	 * @param beanClass The CDI managed bean class.
	 * @return The CDI managed bean reference (proxy) of the given class, or <code>null</code> if there is none.
	 * @throws UnsupportedOperationException When obtaining the CDI managed bean reference failed with an exception.
=======
	 * Returns the CDI bean manager.
	 * <strong>It's preferred that you use {@link Beans#getManager()} for this.</strong>
	 * @param <T> The <code>javax.enterprise.inject.spi.BeanManager</code>.
	 * @return The CDI bean manager.
	 * @throws ClassCastException When you assign it to a variable which is not declared as CDI BeanManager.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get() {
		return (T) beanManager;
	}

	/**
	 * Returns the CDI managed bean reference (proxy) of the given class.
	 * Note that this actually returns a client proxy and the underlying actual instance is thus always auto-created.
	 * @param <T> The expected return type.
	 * @param beanClass The CDI managed bean class.
	 * @return The CDI managed bean reference (proxy) of the given class, or <code>null</code> if there is none.
	 * @throws UnsupportedOperationException When obtaining the CDI managed bean reference failed with an exception.
	 * @deprecated Use {@link Beans#getReference(Class)} instead.
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/33b5cb552f5a2362066606d943400a7ea8ea48ca/src/main/java/org/omnifaces/config/BeanManager.java/right.java
	 */
	@Deprecated // TODO: Remove in OmniFaces 3.0.
	public <T> T getReference(Class<T> beanClass) {
		// This init() call is performed here instead of in constructor, because WebLogic loads this enum as a CDI
		// managed bean (in spite of having a VetoAnnotatedTypeExtension) which in turn implicitly invokes the enum
		// constructor and thus causes an init while CDI context isn't fully initialized and thus the bean manager
		// isn't available in JNDI yet. Perhaps it's fixed in newer WebLogic versions.
		init();

		if (beanManager == null) {
			return null; // CDI not supported on this environment.
		}

		try {
			Object bean = resolve.invoke(beanManager, getBeans.invoke(beanManager, beanClass, NO_ANNOTATIONS));
			Object creationalContext = createCreationalContext.invoke(beanManager, bean);
			Object reference = getReference.invoke(beanManager, bean, beanClass, creationalContext);
			return beanClass.cast(reference);
		}
		catch (Exception e) {
			return null;
		}
	}

}
