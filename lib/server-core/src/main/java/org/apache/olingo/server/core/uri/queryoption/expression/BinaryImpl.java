package org.apache.olingo.server.core.uri.queryoption.expression;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.expression.Binary;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;

public class BinaryImpl implements Binary {
  private final Expression left;

  private final BinaryOperatorKind operator;

  private final Expression right;

  private final EdmType type;

  public BinaryImpl(final Expression left, final BinaryOperatorKind operator, final Expression right, final EdmType type) {
    this.left = left;
    this.operator = operator;
    this.right = right;
    this.type = type;
  }

  @Override public BinaryOperatorKind getOperator() {
    return operator;
  }

  @Override public Expression getLeftOperand() {
    return left;
  }

  @Override public Expression getRightOperand() {
    return right;
  }

  public EdmType getType() {
    return type;
  }

  @Override public <T extends java.lang.Object> T accept(final ExpressionVisitor<T> visitor) throws ExpressionVisitException, ODataApplicationException {
    T left = this.left.accept(visitor);
    T right = this.right.accept(visitor);
    return visitor.visitBinaryOperator(operator, left, right);
  }

  @Override public String toString() {
    return "{" + left + " " + operator.name() + " " + right + '}';
  }
}