package com.mitchellbosecke.pebble.extension.core;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import java.lang.reflect.Array;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import java.util.Collections;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.util.List;
import java.util.Map;

/**
 * Concatenates all entries of a collection, optionally glued together with a
 * particular character such as a comma.
 *
 * @author mbosecke
 *
 */
public class JoinFilter implements Filter {
  private static final List<String> argumentNames = Collections.singletonList("separator");

  public JoinFilter() {
  }

  @Override public List<String> getArgumentNames() {
    return argumentNames;
  }

  @Override public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) throws PebbleException {
    if (input == null) {
      return null;
    }
    String separator = args.containsKey("separator") ? (String) args.get("separator") : null;

<<<<<<< /usr/src/app/output/mbosecke/pebble/8dbd27621c8d8c5db8d229da6ebf18a851c146c6/src/main/java/com/mitchellbosecke/pebble/extension/core/JoinFilter.java/left.java
    if (input.getClass().isArray()) {
      List<Object> items = new ArrayList<>();
      int length = Array.getLength(input);
      for (int i = 0; i < length; i++) {
        items.add(Array.get(input, i));
      }
      return join(items, glue);
    } else {
      if (input instanceof Collection) {
        return join((Collection<?>) input, glue);
      } else {
        throw new PebbleException(null, "The \'join\' filter expects that the input is either a collection or an array.", lineNumber, self.getName());
      }
    }
=======
    if (input instanceof Iterable<?>) {
      boolean isFirst = true;
      for (Object data : ((Iterable<?>) input)) {
        append(builder, data, isFirst ? null : separator);
        isFirst = false;
      }
    } else {
      if (input instanceof Object[]) {
        Object[] array = (Object[]) input;
        for (int i = 0; i < array.length; i++) {
          append(builder, array[i], i >= 1 ? separator : null);
        }
      } else {
        if (input.getClass().isArray()) {
          for (int i = 0; i < Array.getLength(input); i++) {
            append(builder, Array.get(input, i), i >= 1 ? separator : null);
          }
        } else {
          throw new IllegalArgumentException("input is not an array or collection");
        }
      }
    }
>>>>>>> /usr/src/app/output/mbosecke/pebble/8dbd27621c8d8c5db8d229da6ebf18a851c146c6/src/main/java/com/mitchellbosecke/pebble/extension/core/JoinFilter.java/right.java
  }

  private String join(Collection<?> inputCollection, String glue) {
    StringBuilder builder = new StringBuilder();
    boolean isFirst = true;
    for (Object entry : inputCollection) {
      if (!isFirst && glue != null) {
        builder.append(glue);
      }
      builder.append(entry);
      isFirst = false;
    }
    return builder.toString();
  }

  private void append(StringBuilder builder, Object data, String separator) {
    if (separator != null) {
      builder.append(separator);
    }
    builder.append(data);
  }
}