/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Concatenates all entries of a collection or array, optionally glued together with a
 * particular character such as a comma.
<<<<<<< /usr/src/app/output/mbosecke/pebble/8dbd27621c8d8c5db8d229da6ebf18a851c146c6/src/main/java/com/mitchellbosecke/pebble/extension/core/JoinFilter.java/left.java
 *
 * @author mbosecke
||||||| /usr/src/app/output/mbosecke/pebble/8dbd27621c8d8c5db8d229da6ebf18a851c146c6/src/main/java/com/mitchellbosecke/pebble/extension/core/JoinFilter.java/base.java
 * 
 * @author mbosecke
=======
>>>>>>> /usr/src/app/output/mbosecke/pebble/8dbd27621c8d8c5db8d229da6ebf18a851c146c6/src/main/java/com/mitchellbosecke/pebble/extension/core/JoinFilter.java/right.java
 *
 * @author mbosecke
 */
public class JoinFilter implements Filter {

    private static final List<String> argumentNames = Collections.singletonList("separator");

    public JoinFilter() {
    }

    @Override
    public List<String> getArgumentNames() {
        return argumentNames;
    }

    @Override
    public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber)
            throws PebbleException {
        if (input == null) {
            return null;
        }

        String glue = null;
        if (args.containsKey("separator")) {
            glue = (String) args.get("separator");
        }

        if (input.getClass().isArray()) {
            List<Object> items = new ArrayList<>();
            int length = Array.getLength(input);
            for (int i = 0; i < length; i++) {
                items.add(Array.get(input, i));
            }
            return join(items, glue);
        }

        else if (input instanceof Collection) {
            return join((Collection<?>) input, glue);
        } else {
            throw new PebbleException(null,
                    "The 'join' filter expects that the input is either a collection or an array.", lineNumber,
                    self.getName());
        }
    }

    private String join(Collection<?> inputCollection, String glue) {
<<<<<<< /usr/src/app/output/mbosecke/pebble/8dbd27621c8d8c5db8d229da6ebf18a851c146c6/src/main/java/com/mitchellbosecke/pebble/extension/core/JoinFilter.java/left.java
||||||| /usr/src/app/output/mbosecke/pebble/8dbd27621c8d8c5db8d229da6ebf18a851c146c6/src/main/java/com/mitchellbosecke/pebble/extension/core/JoinFilter.java/base.java
        if (input == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Collection<Object> inputCollection = (Collection<Object>) input;

=======
        if (input == null) {
            return null;
        }

        String separator = args.containsKey("separator") ? (String) args.get("separator") : null;
>>>>>>> /usr/src/app/output/mbosecke/pebble/8dbd27621c8d8c5db8d229da6ebf18a851c146c6/src/main/java/com/mitchellbosecke/pebble/extension/core/JoinFilter.java/right.java
        StringBuilder builder = new StringBuilder();
<<<<<<< /usr/src/app/output/mbosecke/pebble/8dbd27621c8d8c5db8d229da6ebf18a851c146c6/src/main/java/com/mitchellbosecke/pebble/extension/core/JoinFilter.java/left.java
    
        boolean isFirst = true;
        for (Object entry : inputCollection) {

            if (!isFirst && glue != null) {
                builder.append(glue);
||||||| /usr/src/app/output/mbosecke/pebble/8dbd27621c8d8c5db8d229da6ebf18a851c146c6/src/main/java/com/mitchellbosecke/pebble/extension/core/JoinFilter.java/base.java
    
        String glue = null;
        if (args.containsKey("separator")) {
            glue = (String) args.get("separator");
        }

        boolean isFirst = true;
        for (Object entry : inputCollection) {

            if (!isFirst && glue != null) {
                builder.append(glue);
=======
    
        if (input instanceof Iterable<?>) {
            boolean isFirst = true;
            for (Object data : ((Iterable<?>) input)) {
                append(builder, data, isFirst ? null : separator);
                isFirst = false;
>>>>>>> /usr/src/app/output/mbosecke/pebble/8dbd27621c8d8c5db8d229da6ebf18a851c146c6/src/main/java/com/mitchellbosecke/pebble/extension/core/JoinFilter.java/right.java
            }
        } else if (input instanceof Object[]) {
            //optimized handling of Object[] arrays (we assume that this is very common)
            Object[] array = (Object[]) input;
            for (int i = 0; i < array.length; i++) {
                append(builder, array[i], i >= 1 ? separator : null);
            }
        } else if (input.getClass().isArray()) {
            //fallback to reflection to iterate all types of arrays of primitive types
            for (int i = 0; i < Array.getLength(input); i++) {
                append(builder, Array.get(input, i), i >= 1 ? separator : null);
            }
        } else {
            throw new IllegalArgumentException("input is not an array or collection");
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
