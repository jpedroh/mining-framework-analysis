package org.fusesource.restygwt.rebind.util;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * An utility class that gets a String representation of an annotation.
 *
 * @author <a href="mailto:bogdan.mustiata@gmail.com">Bogdan Mustiata</a>
 */
public class AnnotationCopyUtil {
  public static String getAnnotationAsString(Annotation annotation) {
    StringBuilder result = encodeAnnotationName(annotation);
    if (hasAnnotationAttributes(annotation)) {
      encodeAnnotationAttributes(annotation, result);
    }
    return result.toString();
  }

  private static StringBuilder encodeAnnotationName(Annotation annotation) {
    return new StringBuilder("@").append(annotation.annotationType().getCanonicalName());
  }

  private static boolean hasAnnotationAttributes(Annotation annotation) {
    return annotation.annotationType().getDeclaredMethods().length != 0;
  }

  private static void encodeAnnotationAttributes(Annotation annotation, StringBuilder result) {
    result.append("(");
    OnceFirstIterator<String> comma = new OnceFirstIterator<String>("", ", ");
    for (Method method : annotation.annotationType().getDeclaredMethods()) {
      Object value = readAnnotationAttribute(annotation, method);
      result.append(comma.next()).append(method.getName()).append(" = ").append(encodeAnnotationValue(value));
    }
    result.append(")");
  }

  private static Object readAnnotationAttribute(Annotation annotation, Method annotationAttribute) {
    try {
      return annotationAttribute.invoke(annotation);
    } catch (Exception e) {
      throw new IllegalArgumentException("Unable to read attribute " + annotationAttribute + " from " + annotation, e);
    }
  }

  private static String encodeAnnotationValue(Object value) {
    if (value instanceof String) {
      return readStringValue(value);
    } else {
      if (value instanceof Number) {
        return readNumberValue(value);
      } else {
        if (value == null) {
          return "null";
        } else {
          if (value.getClass().isArray()) {
            return readArrayValue(value);
          } else {
            if (value instanceof Annotation) {
              return getAnnotationAsString((Annotation) value);
            } else {
              if (value instanceof Boolean) {
                return readBooleanValue((Boolean) value);
              } else {
                if (value instanceof Class) {
                  return readClassValue((Class) value);
                }
              }
            }
          }
        }
      }
    }
    throw new RuntimeException("Unsupported value for encodeAnnotationValue: " + value);
  }

  private static String readBooleanValue(Boolean value) {
    return Boolean.toString(value);
  }

  private static String readArrayValue(Object value) {
    StringBuilder result = new StringBuilder();
    OnceFirstIterator<String> comma = new OnceFirstIterator<String>("", ", ");
    result.append("{");
    for (int i = 0; i < Array.getLength(value); i++) {
      Object arrayValue = Array.get(value, i);
      result.append(comma.next()).append(encodeAnnotationValue(arrayValue));
    }
    result.append("}");
    return result.toString();
  }

  private static String readNumberValue(Object value) {
    return value.toString();
  }

  private static String readStringValue(Object value) {
    return "\"" + value.toString().replace("\"", "\\\"").replace("\n", "\\n") + "\"";
  }

  private static String readClassValue(Class value) {
    return value.getCanonicalName() + ".class";
  }
}