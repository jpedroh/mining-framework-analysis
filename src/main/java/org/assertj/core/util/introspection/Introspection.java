package org.assertj.core.util.introspection;
import static java.lang.String.format;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.util.Preconditions.*;
import static org.assertj.core.util.Strings.quote;
import java.beans.*;
import java.lang.reflect.Method;

/**
 * Utility methods related to <a
 * href="http://java.sun.com/docs/books/tutorial/javabeans/introspection/index.html">JavaBeans Introspection</a>.
 *
 * @author Alex Ruiz
 */
public final class Introspection {
  /**
   * Returns a {@link PropertyDescriptor} for a property matching the given name in the given object.
   *
   * @param propertyName the given property name.
   * @param target the given object.
   * @return a {@code PropertyDescriptor} for a property matching the given name in the given object.
   * @throws NullPointerException if the given property name is {@code null}.
   * @throws IllegalArgumentException if the given property name is empty.
   * @throws NullPointerException if the given object is {@code null}.
   * @throws IntrospectionError if a matching property cannot be found or accessed.
   */
  public static PropertyDescriptor getProperty(String propertyName, Object target) {
    checkNotNullOrEmpty(propertyName);
    checkNotNull(target);
    PropertyDescriptor prop = getBeanProperty(target.getClass(), propertyName);
    if (prop == null) {
      prop = digForDefaultImplementations(target.getClass(), propertyName);
    }
    if (prop != null) {
      return prop;
    } else {
      throw new IntrospectionError(propertyNotFoundErrorMessage(propertyName, target));
    }
  }

  private static PropertyDescriptor digForDefaultImplementations(Class<?> type, String propertyName) {
    for (Class<?> interfaz : type.getInterfaces()) {
      PropertyDescriptor prop = getBeanProperty(interfaz, propertyName);
      if (prop == null) {
        prop = digForDefaultImplementations(interfaz, propertyName);
      }
      if (prop != null) {
        return prop;
      }
    }
    return null;
  }

  private static PropertyDescriptor getBeanProperty(Class<?> type, String propertyName) {
    BeanInfo beanInfo;
    try {
      beanInfo = Introspector.getBeanInfo(type);
    } catch (Throwable t) {
      throw new IntrospectionError(format("Unable to get BeanInfo for type %s", type.getName()), t);
    }
    for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
      if (propertyName.equals(descriptor.getName())) {
        return descriptor;
      }
    }
    return null;
  }

  private static String propertyNotFoundErrorMessage(String propertyName, Object target) {
    String targetTypeName = target.getClass().getName();
    String property = quote(propertyName);
    Method getter = findGetter(propertyName, target);
    if (getter == null) {
      return format("No getter for property %s in %s", property, targetTypeName);
    }
    if (!isPublic(getter.getModifiers())) {
      return format("No public getter for property %s in %s", property, targetTypeName);
    }
    return format("Unable to find property %s in %s", property, targetTypeName);
  }

  private static Method findGetter(String propertyName, Object target) {
    String capitalized = propertyName.substring(0, 1).toUpperCase(ENGLISH) + propertyName.substring(1);
    Method getter = findMethod("get" + capitalized, target);
    if (getter != null) {
      return getter;
    } else {
      return findMethod("is" + capitalized, target);
    }
  }

  private static Method findMethod(String name, Object target) {
    try {
      return target.getClass().getDeclaredMethod(name);
    } catch (Throwable t) {
      return null;
    }
  }

  private Introspection() {
  }
}