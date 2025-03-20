/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;
import java.util.List;
import java.util.Optional;
import com.mitchellbosecke.pebble.attributes.DefaultAttributeResolver;
import com.mitchellbosecke.pebble.attributes.ResolvedAttribute;
import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RootAttributeNotFoundException;
import com.mitchellbosecke.pebble.extension.DynamicAttributeProvider;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.PositionalArgumentNode;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.error.ClassAccessException;

/**
 * Used to get an attribute from an object. It will look up attributes in the
 * following order: map entry, array item, list item,
 * {@link DynamicAttributeProvider}, get method, is method, has method, public method,
 * public field.
 *
 * @author Mitchell
 */
public class GetAttributeExpression implements Expression<Object> {

    private final Expression<?> node;

    private final Expression<?> attributeNameExpression;

    private final ArgumentsNode args;

    private final String filename;

    private final int lineNumber;

    public GetAttributeExpression(Expression<?> node, Expression<?> attributeNameExpression, String filename,
                                  int lineNumber) {
        this(node, attributeNameExpression, null, filename, lineNumber);
    }

    public GetAttributeExpression(Expression<?> node, Expression<?> attributeNameExpression, ArgumentsNode args,
                                  String filename, int lineNumber) {

        this.node = node;
        this.attributeNameExpression = attributeNameExpression;
        this.args = args;
        this.filename = filename;
        this.lineNumber = lineNumber;
    }

    @Override
    public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) throws PebbleException {
        final Object object = this.node.evaluate(self, context);
        final Object attributeNameValue = this.attributeNameExpression.evaluate(self, context);
        final String attributeName = String.valueOf(attributeNameValue);
        final Object[] argumentValues = this.getArgumentValues(self, context);

<<<<<<< /usr/src/app/output/mbosecke/pebble/c8c30f79bfa1849d04e41ed14402ddc2f2746fe9/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/left.java
        if (object == null && context.isStrictVariables()) {
            if (this.node instanceof ContextVariableExpression) {
                final String rootPropertyName = ((ContextVariableExpression) this.node).getName();
                throw new RootAttributeNotFoundException(null, String.format(
                        "Root attribute [%s] does not exist or can not be accessed and strict variables is set to true.",
                        rootPropertyName), rootPropertyName, this.lineNumber, this.filename);
||||||| /usr/src/app/output/mbosecke/pebble/c8c30f79bfa1849d04e41ed14402ddc2f2746fe9/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/base.java
        Object result = null;

        Object[] argumentValues = this.getArgumentValues(self, context);

        // check if the object is able to provide the attribute dynamically
        if(object != null && object instanceof DynamicAttributeProvider) {
            DynamicAttributeProvider dynamicAttributeProvider = (DynamicAttributeProvider) object;
            if(dynamicAttributeProvider.canProvideDynamicAttribute(attributeName)) {
                return dynamicAttributeProvider.getDynamicAttribute(attributeNameValue, argumentValues);
            }
        }

        Member member = object == null ? null : this.memberCache.get(new MemberCacheKey(object.getClass(), attributeName));
        if (object != null && member == null) {

            /*
             * If, and only if, no arguments were provided does it make sense to
             * check maps/arrays/lists
             */
            if (this.args == null) {

                // first we check maps
                if (object instanceof Map) {
                    return this.getObjectFromMap((Map<?, ?>) object, attributeNameValue);
                }

                try {

                    // then we check arrays
                    if (object.getClass().isArray()) {
                        int index = Integer.parseInt(attributeName);
                        int length = Array.getLength(object);
                        if (index < 0 || index >= length) {
                            if (context.isStrictVariables()) {
                                throw new AttributeNotFoundException(null,
                                        "Index out of bounds while accessing array with strict variables on.",
                                        attributeName, this.lineNumber, this.filename);
                            } else {
                                return null;
                            }
                        }
                        return Array.get(object, index);
                    }

                    // then lists
                    if (object instanceof List) {

                        @SuppressWarnings("unchecked")
                        List<Object> list = (List<Object>) object;

                        int index = Integer.parseInt(attributeName);
                        int length = list.size();

                        if (index < 0 || index >= length) {
                            if (context.isStrictVariables()) {
                                throw new AttributeNotFoundException(null,
                                        "Index out of bounds while accessing array with strict variables on.",
                                        attributeName, this.lineNumber, this.filename);
                            } else {
                                return null;
                            }
                        }

                        return list.get(index);
                    }
                } catch (NumberFormatException ex) {
                    // do nothing
                }

            }

            /*
             * turn args into an array of types and an array of values in order
             * to use them for our reflection calls
             */
            Class<?>[] argumentTypes = new Class<?>[argumentValues.length];

            for (int i = 0; i < argumentValues.length; i++) {
                Object o = argumentValues[i];
                if (o == null) {
                    argumentTypes[i] = null;
                } else {
                    argumentTypes[i] = o.getClass();
                }
            }

            member = this.reflect(object, attributeName, argumentTypes);
            if (member != null) {
                this.memberCache.put(new MemberCacheKey(object.getClass(), attributeName), member);
            }

        }

        if (object != null && member != null) {
            result = this.invokeMember(object, member, argumentValues);
        } else if (context.isStrictVariables()) {
            if (object == null) {

                if (this.node instanceof ContextVariableExpression) {
                    final String rootPropertyName = ((ContextVariableExpression) this.node).getName();
                    throw new RootAttributeNotFoundException(null, String.format(
                            "Root attribute [%s] does not exist or can not be accessed and strict variables is set to true.",
                            rootPropertyName), rootPropertyName, this.lineNumber, this.filename);
                } else {
                    throw new RootAttributeNotFoundException(null,
                            "Attempt to get attribute of null object and strict variables is set to true.", attributeName, this.lineNumber, this.filename);
                }

=======
        Object result = null;

        Object[] argumentValues = this.getArgumentValues(self, context);

        Member member = object == null ? null : this.memberCache.get(new MemberCacheKey(object.getClass(), attributeName));
        if (object != null && member == null) {

            /*
             * If, and only if, no arguments were provided does it make sense to
             * check maps/arrays/lists
             */
            if (this.args == null) {

                // first we check maps
                if (object instanceof Map) {
                    return this.getObjectFromMap((Map<?, ?>) object, attributeNameValue);
                }

                try {

                    // then we check arrays
                    if (object.getClass().isArray()) {
                        int index = Integer.parseInt(attributeName);
                        int length = Array.getLength(object);
                        if (index < 0 || index >= length) {
                            if (context.isStrictVariables()) {
                                throw new AttributeNotFoundException(null,
                                        "Index out of bounds while accessing array with strict variables on.",
                                        attributeName, this.lineNumber, this.filename);
                            } else {
                                return null;
                            }
                        }
                        return Array.get(object, index);
                    }

                    // then lists
                    if (object instanceof List) {

                        @SuppressWarnings("unchecked")
                        List<Object> list = (List<Object>) object;

                        int index = Integer.parseInt(attributeName);
                        int length = list.size();

                        if (index < 0 || index >= length) {
                            if (context.isStrictVariables()) {
                                throw new AttributeNotFoundException(null,
                                        "Index out of bounds while accessing array with strict variables on.",
                                        attributeName, this.lineNumber, this.filename);
                            } else {
                                return null;
                            }
                        }

                        return list.get(index);
                    }
                } catch (NumberFormatException ex) {
                    // do nothing
                }

            }

            // check if the object is able to provide the attribute dynamically
            if (object instanceof DynamicAttributeProvider) {
                DynamicAttributeProvider dynamicAttributeProvider = (DynamicAttributeProvider) object;
                if (dynamicAttributeProvider.canProvideDynamicAttribute(attributeName)) {
                    return dynamicAttributeProvider.getDynamicAttribute(attributeNameValue, argumentValues);
                }
            }

            /*
             * turn args into an array of types and an array of values in order
             * to use them for our reflection calls
             */
            Class<?>[] argumentTypes = new Class<?>[argumentValues.length];

            for (int i = 0; i < argumentValues.length; i++) {
                Object o = argumentValues[i];
                if (o == null) {
                    argumentTypes[i] = null;
                } else {
                    argumentTypes[i] = o.getClass();
                }
            }

            member = this.reflect(object, attributeName, argumentTypes);
            if (member != null) {
                this.memberCache.put(new MemberCacheKey(object.getClass(), attributeName), member);
            }

        }

        if (object != null && member != null) {
            result = this.invokeMember(object, member, argumentValues);
        } else if (context.isStrictVariables()) {
            if (object == null) {

                if (this.node instanceof ContextVariableExpression) {
                    final String rootPropertyName = ((ContextVariableExpression) this.node).getName();
                    throw new RootAttributeNotFoundException(null, String.format(
                            "Root attribute [%s] does not exist or can not be accessed and strict variables is set to true.",
                            rootPropertyName), rootPropertyName, this.lineNumber, this.filename);
                } else {
                    throw new RootAttributeNotFoundException(null,
                            "Attempt to get attribute of null object and strict variables is set to true.", attributeName, this.lineNumber, this.filename);
                }

>>>>>>> /usr/src/app/output/mbosecke/pebble/c8c30f79bfa1849d04e41ed14402ddc2f2746fe9/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/right.java
            } else {
<<<<<<< /usr/src/app/output/mbosecke/pebble/c8c30f79bfa1849d04e41ed14402ddc2f2746fe9/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/left.java
                throw new RootAttributeNotFoundException(null,
                        "Attempt to get attribute of null object and strict variables is set to true.", attributeName, this.lineNumber, this.filename);
||||||| /usr/src/app/output/mbosecke/pebble/c8c30f79bfa1849d04e41ed14402ddc2f2746fe9/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/base.java
                throw new AttributeNotFoundException(null, String.format(
                        "Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.",
                        attributeName, object.getClass().getName()), attributeName, this.lineNumber, this.filename);
=======
                if (attributeName.equals("class") || attributeName.equals("getClass")) {
                    throw new ClassAccessException(this.lineNumber, this.filename);
                } else {
                    throw new AttributeNotFoundException(null, String.format(
                            "Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.",
                            attributeName, object.getClass().getName()), attributeName, this.lineNumber, this.filename);
                }
>>>>>>> /usr/src/app/output/mbosecke/pebble/c8c30f79bfa1849d04e41ed14402ddc2f2746fe9/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/right.java
            }
        }
        
        Optional<ResolvedAttribute> resolvedAttribute = DefaultAttributeResolver.resolve(context.getExtensionRegistry().getAttributeResolver(), object, attributeNameValue, argumentValues, context.isStrictVariables(), filename, this.lineNumber);
        
        if (resolvedAttribute.isPresent()) {
            return resolvedAttribute.get().evaluate();
        } 
        
        if (context.isStrictVariables()) {
            throw new AttributeNotFoundException(null, String.format(
                    "Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.",
                    attributeName, object.getClass().getName()), attributeName, this.lineNumber, this.filename);
        }
<<<<<<< /usr/src/app/output/mbosecke/pebble/c8c30f79bfa1849d04e41ed14402ddc2f2746fe9/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/left.java
||||||| /usr/src/app/output/mbosecke/pebble/c8c30f79bfa1849d04e41ed14402ddc2f2746fe9/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/base.java
        if (Number.class.isAssignableFrom(attributeNameValue.getClass())) {
            Number keyAsNumber = (Number) attributeNameValue;
=======
        if (attributeNameValue != null && Number.class.isAssignableFrom(attributeNameValue.getClass())) {
            Number keyAsNumber = (Number) attributeNameValue;
>>>>>>> /usr/src/app/output/mbosecke/pebble/c8c30f79bfa1849d04e41ed14402ddc2f2746fe9/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/right.java

        return null;

    }

    /**
     * Fully evaluates the individual arguments.
     *
     * @param self
     * @param context
     * @return
     * @throws PebbleException
     */
    private Object[] getArgumentValues(PebbleTemplateImpl self, EvaluationContextImpl context) throws PebbleException {

        Object[] argumentValues;

        if (this.args == null) {
            argumentValues = null; //new Object[0];
        } else {
            List<PositionalArgumentNode> args = this.args.getPositionalArgs();

            argumentValues = new Object[args.size()];

            int index = 0;
            for (PositionalArgumentNode arg : args) {
                Object argumentValue = arg.getValueExpression().evaluate(self, context);
                argumentValues[index] = argumentValue;
                index++;
            }
        }
        return argumentValues;
<<<<<<< /usr/src/app/output/mbosecke/pebble/c8c30f79bfa1849d04e41ed14402ddc2f2746fe9/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/left.java
||||||| /usr/src/app/output/mbosecke/pebble/c8c30f79bfa1849d04e41ed14402ddc2f2746fe9/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/base.java
    }

    /**
     * Performs the actual reflection to obtain a "Member" from a class.
     *
     * @param object
     * @param attributeName
     * @param parameterTypes
     * @return
     */
    private Member reflect(Object object, String attributeName, Class<?>[] parameterTypes) {

        Class<?> clazz = object.getClass();

        Member result = null;

        // capitalize first letter of attribute for the following attempts
        String attributeCapitalized = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);

        // check get method
        result = this.findMethod(clazz, "get" + attributeCapitalized, parameterTypes);

        // check is method
        if (result == null) {
            result = this.findMethod(clazz, "is" + attributeCapitalized, parameterTypes);
        }

        // check has method
        if (result == null) {
            result = this.findMethod(clazz, "has" + attributeCapitalized, parameterTypes);
        }

        // check if attribute is a public method
        if (result == null) {
            result = this.findMethod(clazz, attributeName, parameterTypes);
        }

        // public field
        if (result == null) {
            try {
                result = clazz.getField(attributeName);
            } catch (NoSuchFieldException | SecurityException e) {
            }
        }

        if (result != null) {
            ((AccessibleObject) result).setAccessible(true);
        }

        return result;
    }

    /**
     * Finds an appropriate method by comparing if parameter types are
     * compatible. This is more relaxed than class.getMethod.
     *
     * @param clazz
     * @param name
     * @param requiredTypes
     * @return
     */
    private Method findMethod(Class<?> clazz, String name, Class<?>[] requiredTypes) {
        Method result = null;

        Method[] candidates = clazz.getMethods();

        for (Method candidate : candidates) {
            if (!candidate.getName().equalsIgnoreCase(name)) {
                continue;
            }

            Class<?>[] types = candidate.getParameterTypes();

            if (types.length != requiredTypes.length) {
                continue;
            }

            boolean compatibleTypes = true;
            for (int i = 0; i < types.length; i++) {
                if (requiredTypes[i] != null && !this.widen(types[i]).isAssignableFrom(requiredTypes[i])) {
                    compatibleTypes = false;
                    break;
                }
            }

            if (compatibleTypes) {
                result = candidate;
                break;
            }
        }
        return result;
    }

    /**
     * Performs a widening conversion (primitive to boxed type)
     *
     * @param clazz
     * @return
     */
    private Class<?> widen(Class<?> clazz) {
        Class<?> result = clazz;
        if (clazz == int.class) {
            result = Integer.class;
        } else if (clazz == long.class) {
            result = Long.class;
        } else if (clazz == double.class) {
            result = Double.class;
        } else if (clazz == float.class) {
            result = Float.class;
        } else if (clazz == short.class) {
            result = Short.class;
        } else if (clazz == byte.class) {
            result = Byte.class;
        } else if (clazz == boolean.class) {
            result = Boolean.class;
        }
        return result;
    }

    private class MemberCacheKey {
        private final Class<?> clazz;
        private final String attributeName;

        private MemberCacheKey(Class<?> clazz, String attributeName) {
            this.clazz = clazz;
            this.attributeName = attributeName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;

            MemberCacheKey that = (MemberCacheKey) o;

            if (!this.clazz.equals(that.clazz)) return false;
            return this.attributeName.equals(that.attributeName);

        }

        @Override
        public int hashCode() {
            int result = this.clazz.hashCode();
            result = 31 * result + this.attributeName.hashCode();
            return result;
        }
=======
    }

    /**
     * Performs the actual reflection to obtain a "Member" from a class.
     *
     * @param object
     * @param attributeName
     * @param parameterTypes
     * @return
     */
    private Member reflect(Object object, String attributeName, Class<?>[] parameterTypes) {

        Class<?> clazz = object.getClass();

        Member result = null;

        // capitalize first letter of attribute for the following attempts
        String attributeCapitalized = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);

        // check get method
        result = this.findMethod(clazz, "get" + attributeCapitalized, parameterTypes);

        // check is method
        if (result == null) {
            result = this.findMethod(clazz, "is" + attributeCapitalized, parameterTypes);
        }

        // check has method
        if (result == null) {
            result = this.findMethod(clazz, "has" + attributeCapitalized, parameterTypes);
        }

        // check if attribute is a public method
        if (result == null) {
            result = this.findMethod(clazz, attributeName, parameterTypes);
        }

        // public field
        if (result == null) {
            try {
                result = clazz.getField(attributeName);
            } catch (NoSuchFieldException | SecurityException e) {
            }
        }

        if (result != null) {
            ((AccessibleObject) result).setAccessible(true);
        }

        return result;
    }

    /**
     * Finds an appropriate method by comparing if parameter types are
     * compatible. This is more relaxed than class.getMethod.
     *
     * @param clazz
     * @param name
     * @param requiredTypes
     * @return
     */
    private Method findMethod(Class<?> clazz, String name, Class<?>[] requiredTypes) {
        if (name.equals("getClass")) {
            return null;
        }

        Method result = null;

        Method[] candidates = clazz.getMethods();

        for (Method candidate : candidates) {
            if (!candidate.getName().equalsIgnoreCase(name)) {
                continue;
            }

            Class<?>[] types = candidate.getParameterTypes();

            if (types.length != requiredTypes.length) {
                continue;
            }

            boolean compatibleTypes = true;
            for (int i = 0; i < types.length; i++) {
                if (requiredTypes[i] != null && !this.widen(types[i]).isAssignableFrom(requiredTypes[i])) {
                    compatibleTypes = false;
                    break;
                }
            }

            if (compatibleTypes) {
                result = candidate;
                break;
            }
        }
        return result;
    }

    /**
     * Performs a widening conversion (primitive to boxed type)
     *
     * @param clazz
     * @return
     */
    private Class<?> widen(Class<?> clazz) {
        Class<?> result = clazz;
        if (clazz == int.class) {
            result = Integer.class;
        } else if (clazz == long.class) {
            result = Long.class;
        } else if (clazz == double.class) {
            result = Double.class;
        } else if (clazz == float.class) {
            result = Float.class;
        } else if (clazz == short.class) {
            result = Short.class;
        } else if (clazz == byte.class) {
            result = Byte.class;
        } else if (clazz == boolean.class) {
            result = Boolean.class;
        }
        return result;
    }

    private class MemberCacheKey {
        private final Class<?> clazz;
        private final String attributeName;

        private MemberCacheKey(Class<?> clazz, String attributeName) {
            this.clazz = clazz;
            this.attributeName = attributeName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;

            MemberCacheKey that = (MemberCacheKey) o;

            if (!this.clazz.equals(that.clazz)) return false;
            return this.attributeName.equals(that.attributeName);

        }

        @Override
        public int hashCode() {
            int result = this.clazz.hashCode();
            result = 31 * result + this.attributeName.hashCode();
            return result;
        }
>>>>>>> /usr/src/app/output/mbosecke/pebble/c8c30f79bfa1849d04e41ed14402ddc2f2746fe9/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/right.java
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public Expression<?> getNode() {
        return this.node;
    }

    public Expression<?> getAttributeNameExpression() {
        return this.attributeNameExpression;
    }

    public ArgumentsNode getArgumentsNode() {
        return this.args;
    }

    @Override
    public int getLineNumber() {
        return this.lineNumber;
    }

}
