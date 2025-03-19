package org.apache.olingo.server.core.uri.queryoption.expression;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.server.api.uri.queryoption.expression.TypeLiteral;

public class TypeLiteralImpl implements TypeLiteral {
  private final EdmType type;

  public TypeLiteralImpl(final EdmType type) {
    this.type = type;
  }

  @Override public EdmType getType() {
    return type;
  }

  @Override public <T extends java.lang.Object> T accept(final ExpressionVisitor<T> visitor) throws ExpressionVisitException, ODataApplicationException {
    return visitor.visitTypeLiteral(type);
  }

  @Override public String toString() {
    return type == null ? null : type.getFullQualifiedName().getFullQualifiedNameAsString();
  }
}