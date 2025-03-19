package org.junit.contrib.truth.util;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Reflection utility methods.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
public class ReflectionUtil {
  /** Returns the captured type. */
  public static Class<?> typeParameter(Class<?> clazz, int paramIndex) {
    Type superclass = clazz.getGenericSuperclass();
    if (!(superclass instanceof ParameterizedType)) {
      throw new IllegalArgumentException("" + superclass + " isn\'t parameterized");
    }
    Type[] typeParams = ((ParameterizedType) superclass).getActualTypeArguments();
    return (Class<?>) typeParams[paramIndex];
  }

  public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
    Class<?> currentClass = clazz;
    while (currentClass != null) {
      try {
        return clazz.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
        currentClass = currentClass.getSuperclass();
      }
    }
    throw new NoSuchFieldException("No such field " + fieldName + " declared on " + clazz.getSimpleName() + " or its parent classes.");
  }
}