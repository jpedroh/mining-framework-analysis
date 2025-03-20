/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.attributes.AttributeResolver;
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

import java.util.List;
import java.util.Optional;

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
<<<<<<< /usr/src/app/output/mbosecke/pebble/7782fd3abe099bf8c22a34159b6b05e66bc13d05/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/left.java
    public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) throws PebbleException {
        final Object object = this.node.evaluate(self, context);
        final Object attributeNameValue = this.attributeNameExpression.evaluate(self, context);
        final String attributeName = String.valueOf(attributeNameValue);
        final Object[] argumentValues = this.getArgumentValues(self, context);
||||||| /usr/src/app/output/mbosecke/pebble/7782fd3abe099bf8c22a34159b6b05e66bc13d05/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/base.java
    public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) throws PebbleException {
        Object object = this.node.evaluate(self, context);
        Object attributeNameValue = this.attributeNameExpression.evaluate(self, context);
        String attributeName = String.valueOf(attributeNameValue);
=======
    public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
        Object object = this.node.evaluate(self, context);
        Object attributeNameValue = this.attributeNameExpression.evaluate(self, context);
        String attributeName = String.valueOf(attributeNameValue);
>>>>>>> /usr/src/app/output/mbosecke/pebble/7782fd3abe099bf8c22a34159b6b05e66bc13d05/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/right.java

        if (object == null && context.isStrictVariables()) {
            if (this.node instanceof ContextVariableExpression) {
                final String rootPropertyName = ((ContextVariableExpression) this.node).getName();
                throw new RootAttributeNotFoundException(null, String.format(
                        "Root attribute [%s] does not exist or can not be accessed and strict variables is set to true.",
                        rootPropertyName), rootPropertyName, this.lineNumber, this.filename);
            } else {
                throw new RootAttributeNotFoundException(null,
                        "Attempt to get attribute of null object and strict variables is set to true.", attributeName, this.lineNumber, this.filename);
            }
        }

<<<<<<< /usr/src/app/output/mbosecke/pebble/7782fd3abe099bf8c22a34159b6b05e66bc13d05/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/left.java
        for (AttributeResolver attributeResolver: context.getExtensionRegistry().getAttributeResolver()) {
            Optional<ResolvedAttribute> resolvedAttribute = attributeResolver.resolve(object, attributeNameValue, argumentValues, context.isStrictVariables(), this.filename, this.lineNumber);
            if (resolvedAttribute.isPresent()) {
                return resolvedAttribute.get().evaluate();
||||||| /usr/src/app/output/mbosecke/pebble/7782fd3abe099bf8c22a34159b6b05e66bc13d05/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/base.java
    }

    private Object getObjectFromMap(Map<?, ?> object, Object attributeNameValue) throws PebbleException {
        if (object.isEmpty()) {
            return null;
        }
        if (attributeNameValue != null && Number.class.isAssignableFrom(attributeNameValue.getClass())) {
            Number keyAsNumber = (Number) attributeNameValue;

            Class<?> keyClass = object.keySet().iterator().next().getClass();
            Object key = this.cast(keyAsNumber, keyClass);
            return object.get(key);
        }
        return object.get(attributeNameValue);
    }

    private Object cast(Number number, Class<?> desiredType) throws PebbleException {
        if (desiredType == Long.class) {
            return number.longValue();
        } else if (desiredType == Integer.class) {
            return number.intValue();
        } else if (desiredType == Double.class) {
            return number.doubleValue();
        } else if (desiredType == Float.class) {
            return number.floatValue();
        } else if (desiredType == Short.class) {
            return number.shortValue();
        } else if (desiredType == Byte.class) {
            return number.byteValue();
        }
        throw new PebbleException(null, String.format("type %s not supported for key %s", desiredType, number), this.getLineNumber(), this.filename);
    }

    /**
     * Invoke the "Member" that was found via reflection.
     *
     * @param object
     * @param member
     * @param argumentValues
     * @return
     */
    private Object invokeMember(Object object, Member member, Object[] argumentValues) {
        Object result = null;
        try {
            if (member instanceof Method) {
                result = ((Method) member).invoke(object, argumentValues);
            } else if (member instanceof Field) {
                result = ((Field) member).get(object);
=======
    }

    private Object getObjectFromMap(Map<?, ?> object, Object attributeNameValue) {
        if (object.isEmpty()) {
            return null;
        }
        if (attributeNameValue != null && Number.class.isAssignableFrom(attributeNameValue.getClass())) {
            Number keyAsNumber = (Number) attributeNameValue;

            Class<?> keyClass = object.keySet().iterator().next().getClass();
            Object key = this.cast(keyAsNumber, keyClass);
            return object.get(key);
        }
        return object.get(attributeNameValue);
    }

    private Object cast(Number number, Class<?> desiredType) {
        if (desiredType == Long.class) {
            return number.longValue();
        } else if (desiredType == Integer.class) {
            return number.intValue();
        } else if (desiredType == Double.class) {
            return number.doubleValue();
        } else if (desiredType == Float.class) {
            return number.floatValue();
        } else if (desiredType == Short.class) {
            return number.shortValue();
        } else if (desiredType == Byte.class) {
            return number.byteValue();
        }
        throw new PebbleException(null, String.format("type %s not supported for key %s", desiredType, number), this.getLineNumber(), this.filename);
    }

    /**
     * Invoke the "Member" that was found via reflection.
     *
     * @param object
     * @param member
     * @param argumentValues
     * @return
     */
    private Object invokeMember(Object object, Member member, Object[] argumentValues) {
        Object result = null;
        try {
            if (member instanceof Method) {
                result = ((Method) member).invoke(object, argumentValues);
            } else if (member instanceof Field) {
                result = ((Field) member).get(object);
>>>>>>> /usr/src/app/output/mbosecke/pebble/7782fd3abe099bf8c22a34159b6b05e66bc13d05/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/right.java
            }
        }
        
        if (context.isStrictVariables()) {
            throw new AttributeNotFoundException(null, String.format(
                    "Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.",
                    attributeName, object.getClass().getName()), attributeName, this.lineNumber, this.filename);
        }

        return null;
    }

    /**
     * Fully evaluates the individual arguments.
     *
     * @param self
     * @param context
     * @return
     */
    private Object[] getArgumentValues(PebbleTemplateImpl self, EvaluationContextImpl context) {

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
