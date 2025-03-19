package org.apache.olingo.server.core.uri;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourceLambdaAll;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;

public class UriResourceLambdaAllImpl extends UriResourceTypedImpl implements UriResourceLambdaAll {
  private final String lambdaVariable;

  private final Expression expression;

  public UriResourceLambdaAllImpl(final String lambdaVariable, final Expression expression) {
    super(UriResourceKind.lambdaAll);
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
    return "all";
  }
}