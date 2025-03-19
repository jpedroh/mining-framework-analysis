package org.apache.olingo.server.core.uri;
import java.util.Collections;
import java.util.List;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceKind;

/**
 * Covers Function imports and BoundFunction in URI
 */
public class UriResourceFunctionImpl extends UriResourceWithKeysImpl implements UriResourceFunction {
  private final EdmFunctionImport functionImport;

  private final List<UriParameter> parameters;

  private final EdmFunction function;

  public UriResourceFunctionImpl(final EdmFunctionImport edmFunctionImport, final EdmFunction function, final List<UriParameter> parameters) {
    super(UriResourceKind.function);
    this.functionImport = edmFunctionImport;
    this.function = function;
    this.parameters = parameters;
  }

  @Override public List<UriParameter> getParameters() {
    return parameters == null ? Collections.<UriParameter>emptyList() : Collections.unmodifiableList(parameters);
  }

  @Override public EdmFunction getFunction() {
    return function;
  }

  @Override public EdmFunctionImport getFunctionImport() {
    return functionImport;
  }

  @Override public EdmType getType() {
    return function.getReturnType().getType();
  }

  @Override public boolean isCollection() {
    return keyPredicates == null && function.getReturnType().isCollection();
  }

  @Override public String getSegmentValue() {
    return functionImport == null ? (function == null ? "" : function.getName()) : functionImport.getName();
  }
}