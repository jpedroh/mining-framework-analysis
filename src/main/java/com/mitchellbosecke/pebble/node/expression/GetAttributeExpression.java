package com.mitchellbosecke.pebble.node.expression;
import com.mitchellbosecke.pebble.attributes.AttributeResolver;
import com.mitchellbosecke.pebble.attributes.ResolvedAttribute;
import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RootAttributeNotFoundException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.PositionalArgumentNode;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import java.util.List;

/**
 * Used to get an attribute from an object. It will look up attributes in the
 * following order: map entry, array item, list item,
 * get method, is method, has method, public method,
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

  public GetAttributeExpression(Expression<?> node, Expression<?> attributeNameExpression, String filename, int lineNumber) {
    this(node, attributeNameExpression, null, filename, lineNumber);
  }

  public GetAttributeExpression(Expression<?> node, Expression<?> attributeNameExpression, ArgumentsNode args, String filename, int lineNumber) {
    this.node = node;
    this.attributeNameExpression = attributeNameExpression;
    this.args = args;
    this.filename = filename;
    this.lineNumber = lineNumber;
  }

  @Override public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) throws PebbleException {
    final Object object = this.node.evaluate(self, context);
    final Object attributeNameValue = this.attributeNameExpression.evaluate(self, context);
    final String attributeName = String.valueOf(attributeNameValue);
    final Object[] argumentValues = this.getArgumentValues(self, context);
    if (object == null && context.isStrictVariables()) {
      if (this.node instanceof ContextVariableExpression) {
        final String rootPropertyName = ((ContextVariableExpression) this.node).getName();
        throw new RootAttributeNotFoundException(null, String.format("Root attribute [%s] does not exist or can not be accessed and strict variables is set to true.", rootPropertyName), rootPropertyName, this.lineNumber, this.filename);
      } else {
        throw new RootAttributeNotFoundException(null, "Attempt to get attribute of null object and strict variables is set to true.", attributeName, this.lineNumber, this.filename);
      }
    }
    for (AttributeResolver attributeResolver : context.getExtensionRegistry().getAttributeResolver()) {
      ResolvedAttribute resolvedAttribute = attributeResolver.resolve(object, attributeNameValue, argumentValues, context, this.filename, this.lineNumber);
      if (resolvedAttribute != null) {
        return resolvedAttribute.evaluate();
      }
    }
    if (context.isStrictVariables()) {
      throw new AttributeNotFoundException(null, String.format("Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.", attributeName, object != null ? object.getClass().getName() : null), attributeName, this.lineNumber, this.filename);
    } else {

<<<<<<< Unknown file: This is a bug in JDime.
=======
      if (context.isStrictVariables()) {
        if (object == null) {
          if (this.node instanceof ContextVariableExpression) {
            final String rootPropertyName = ((ContextVariableExpression) this.node).getName();
            throw new RootAttributeNotFoundException(null, String.format("Root attribute [%s] does not exist or can not be accessed and strict variables is set to true.", rootPropertyName), rootPropertyName, this.lineNumber, this.filename);
          } else {
            throw new RootAttributeNotFoundException(null, "Attempt to get attribute of null object and strict variables is set to true.", attributeName, this.lineNumber, this.filename);
          }
        } else {
          throw new AttributeNotFoundException(null, String.format("Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.", attributeName, object.getClass().getName()), attributeName, this.lineNumber, this.filename);
        }
      }
>>>>>>> /usr/src/app/output/mbosecke/pebble/db486e71ca089ecd959bc8d25d53748f52a5afe2/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/right.java

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
      argumentValues = null;
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


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
     * Finds an appropriate method by comparing if parameter types are
     * compatible. This is more relaxed than class.getMethod.
     *
     * @param clazz
     * @param name
     * @param requiredTypes
     * @return
     */
  private Method findMethod(Class<?> clazz, String name, Class<?>[] requiredTypes, boolean allowGetClass) {
    if (!allowGetClass && name.equals("getClass")) {
      throw new ClassAccessException(this.lineNumber, this.filename);
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
>>>>>>> /usr/src/app/output/mbosecke/pebble/db486e71ca089ecd959bc8d25d53748f52a5afe2/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java/right.java


  @Override public void accept(NodeVisitor visitor) {
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

  @Override public int getLineNumber() {
    return this.lineNumber;
  }
}