package org.apache.olingo.server.core.uri;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.uri.UriResourceKind;

public class UriResourceStartingTypeFilterImpl extends UriResourceWithKeysImpl {
  private final EdmType type;

  private final boolean isCollection;

  public UriResourceStartingTypeFilterImpl(final EdmType type, final boolean isCollection) {
    super(null);
    this.type = type;
    this.isCollection = isCollection;
  }

  @Override public UriResourceKind getKind() {
    return kind;
  }

  @Override public EdmType getType() {
    return type;
  }

  @Override public boolean isCollection() {
    return keyPredicates == null && isCollection;
  }

  @Override public String getSegmentValue() {
    return type.getFullQualifiedName().getFullQualifiedNameAsString();
  }
}