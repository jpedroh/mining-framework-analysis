package org.apache.olingo.server.core.uri;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourceLambdaAny;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;

public class UriResourceLambdaAnyImpl extends UriResourceTypedImpl implements UriResourceLambdaAny {
  private final String lambdaVariable;

  private final Expression expression;

  public UriResourceLambdaAnyImpl(final String lambdaVariable, final Expression expression) {
    super(UriResourceKind.lambdaAny);
    this.lambdaVariable = lambdaVariable;
    this.expression = expression;
  }

  @Override public EdmType getType() {
    return EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean);
  }

  @Override public boolean isCollection() {
    return false;
  }

  @Override public String getLambdaVariable() {
    return lambdaVariable;
  }

  @Override public Expression getExpression() {
    return expression;
  }

  @Override public String getSegmentValue() {
    return "any";
  }
}