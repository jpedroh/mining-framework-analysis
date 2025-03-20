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

import org.omnifaces.util.JNDI;

/**
 * <p>
<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/config/BeanManager.java/left.java
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
||||||| /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/config/BeanManager.java/base.java
 * If you already have a CDI bean manager instance at hands (and thus having a CDI dependency is no problem), then use
 * {@link org.omnifaces.util.Beans} instead.
=======
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
 * <p>
 * If you however already have a CDI bean manager instance at hands via <code>@Inject</code>, use
 * {@link org.omnifaces.util.BeansLocal#getReference(javax.enterprise.inject.spi.BeanManager, Class)} instead.
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/config/BeanManager.java/right.java
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

	private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

	private static final String ERROR_CDI_API_UNAVAILABLE =
		"CDI API is not available in this environment.";
	private static final String ERROR_JNDI_UNAVAILABLE =
		"JNDI is not available in this environment.";
	private static final String ERROR_CDI_IMPL_UNAVAILABLE =
		"CDI BeanManager instance is not available in JNDI.";
	private static final String ERROR_INITIALIZATION_FAIL =
		"CDI BeanManager instance is available, but preparing getReference() method failed.";

	// Properties -----------------------------------------------------------------------------------------------------

	private Object beanManager;
	private Method getBeans;
	private Method resolve;
	private Method createCreationalContext;
	private Method getReference;

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * Perform automatic initialization whereby the bean manager is looked up from the JNDI.
	 * @throws IllegalStateException When initialization fails.
	 */
<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/config/BeanManager.java/left.java
	private void init() {
		if (!initialized.getAndSet(true)) {
			try {
				Class.forName("javax.enterprise.inject.spi.BeanManager"); // Is CDI present?
				JNDI.lookup("java:comp"); // Is JNDI present? (not on Google App Engine)
			}
			catch (Throwable e) {
				return; // CDI or JNDI not supported on this environment.
			}
||||||| /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/config/BeanManager.java/base.java
	private void init() {
		if (!initialized.getAndSet(true)) {
			try {
				Class.forName("javax.enterprise.inject.spi.BeanManager"); // Is CDI present?
				JNDI.lookup("java:comp"); // Is JNDI present? (not on Google App Engine)
			}
			catch (Exception e) {
				return; // CDI or JNDI not supported on this environment.
			}
=======
	private BeanManager() {
		Class<?> beanManagerClass, contextualClass, beanClass, creationalContextClass;
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/config/BeanManager.java/right.java

		try {
			beanManagerClass = Class.forName("javax.enterprise.inject.spi.BeanManager");
			contextualClass = Class.forName("javax.enterprise.context.spi.Contextual");
			beanClass = Class.forName("javax.enterprise.inject.spi.Bean");
			creationalContextClass = Class.forName("javax.enterprise.context.spi.CreationalContext");
		}
		catch (Exception | LinkageError e) {
			throw new IllegalStateException(ERROR_CDI_API_UNAVAILABLE, e);
		}

		try {
			beanManager = JNDI.lookup("java:comp/BeanManager"); // CDI spec.

			if (beanManager == null) {
				beanManager = JNDI.lookup("java:comp/env/BeanManager"); // Tomcat.
			}
		}
		catch (IllegalStateException e) {
			throw new IllegalStateException(ERROR_CDI_IMPL_UNAVAILABLE, e);
		}
		catch (Exception | LinkageError e) {
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
		}
		catch (Exception e) {
			throw new IllegalStateException(ERROR_INITIALIZATION_FAIL, e);
		}
	}

	// Actions --------------------------------------------------------------------------------------------------------

	/**
<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/config/BeanManager.java/left.java
	 * Returns the CDI managed bean instance of the given class, or <code>null</code> if there is none.
	 * @param <T> The generic bean type.
	 * @param beanClass The type of the CDI managed bean instance.
	 * @return The CDI managed bean instance of the given class, or <code>null</code> if there is none.
||||||| /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/config/BeanManager.java/base.java
	 * Returns the CDI managed bean instance of the given class, or <code>null</code> if there is none.
	 * @param beanClass The type of the CDI managed bean instance.
	 * @return The CDI managed bean instance of the given class, or <code>null</code> if there is none.
=======
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
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/config/BeanManager.java/right.java
	 */
	public <T> T getReference(Class<T> beanClass) {
		try {
			Object bean = resolve.invoke(beanManager, getBeans.invoke(beanManager, beanClass, NO_ANNOTATIONS));

			if (bean == null) {
				return null;
			}

			Object creationalContext = createCreationalContext.invoke(beanManager, bean);
			Object reference = getReference.invoke(beanManager, bean, beanClass, creationalContext);
			return beanClass.cast(reference);
		}
		catch (Exception e) {
			throw new UnsupportedOperationException(e);
		}
	}

}
