package org.apache.olingo.server.core.uri.queryoption.expression;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.server.api.uri.queryoption.expression.Method;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.TypeLiteral;
import org.apache.olingo.server.core.ODataImpl;

public class MethodImpl implements Method {
  private final MethodKind method;

  private final List<Expression> parameters;

  public MethodImpl(final MethodKind method, final List<Expression> parameters) {
    this.method = method;
    this.parameters = parameters;
  }

  @Override public MethodKind getMethod() {
    return method;
  }

  public EdmType getType() {
    EdmPrimitiveTypeKind kind = null;
    switch (method) {
      case CONTAINS:
      case STARTSWITH:
      case ENDSWITH:
      kind = EdmPrimitiveTypeKind.Boolean;
      break;
      case LENGTH:
      case INDEXOF:
      kind = EdmPrimitiveTypeKind.Int32;
      break;
      case SUBSTRING:
      case TOLOWER:
      case TOUPPER:
      case TRIM:
      case CONCAT:
      kind = EdmPrimitiveTypeKind.String;
      break;
      case YEAR:
      case MONTH:
      case DAY:
      case HOUR:
      case MINUTE:
      case SECOND:
      kind = EdmPrimitiveTypeKind.Int32;
      break;
      case FRACTIONALSECONDS:
      case TOTALSECONDS:
      kind = EdmPrimitiveTypeKind.Decimal;
      break;
      case DATE:
      kind = EdmPrimitiveTypeKind.Date;
      break;
      case TIME:
      kind = EdmPrimitiveTypeKind.TimeOfDay;
      break;
      case TOTALOFFSETMINUTES:
      kind = EdmPrimitiveTypeKind.Int32;
      break;
      case MINDATETIME:
      case MAXDATETIME:
      case NOW:
      kind = EdmPrimitiveTypeKind.DateTimeOffset;
      break;
      case ROUND:
      case FLOOR:
      case CEILING:
      kind = EdmPrimitiveTypeKind.Double;
      break;
      case GEODISTANCE:
      case GEOLENGTH:
      kind = EdmPrimitiveTypeKind.Double;
      break;
      case GEOINTERSECTS:
      kind = EdmPrimitiveTypeKind.Boolean;
      break;
      case CAST:
      return ((TypeLiteral) parameters.get(parameters.size() - 1)).getType();
      case ISOF:
      kind = EdmPrimitiveTypeKind.Boolean;
      break;
    }
    return new ODataImpl().createPrimitiveTypeInstance(kind);
  }

  @Override public List<Expression> getParameters() {
    return parameters == null ? Collections.<Expression>emptyList() : Collections.unmodifiableList(parameters);
  }

  @Override public <T extends java.lang.Object> T accept(final ExpressionVisitor<T> visitor) throws ExpressionVisitException, ODataApplicationException {
    List<T> userParameters = new ArrayList<T>();
    if (parameters != null) {
      for (final Expression parameter : parameters) {
        userParameters.add(parameter.accept(visitor));
      }
    }
    return visitor.visitMethodCall(method, userParameters);
  }

  @Override public String toString() {
    return "{" + method + " " + parameters + "}";
  }
}