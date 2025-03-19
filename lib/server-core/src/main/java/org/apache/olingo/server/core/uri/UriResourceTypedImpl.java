package org.apache.olingo.server.core.uri;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;

public abstract class UriResourceTypedImpl extends UriResourceImpl implements UriResourcePartTyped {
  protected EdmType typeFilter = null;

  public UriResourceTypedImpl(final UriResourceKind kind) {
    super(kind);
  }

  public EdmType getTypeFilter() {
    return typeFilter;
  }

  public UriResourceTypedImpl setTypeFilter(final EdmStructuredType typeFilter) {
    this.typeFilter = typeFilter;
    return this;
  }

  @Override public String getSegmentValue(final boolean includeFilters) {
    return includeFilters && typeFilter != null ? getSegmentValue() + "/" + typeFilter.getFullQualifiedName().getFullQualifiedNameAsString() : getSegmentValue();
  }

  @Override public String toString(final boolean includeFilters) {
    return getSegmentValue(includeFilters);
  }
}