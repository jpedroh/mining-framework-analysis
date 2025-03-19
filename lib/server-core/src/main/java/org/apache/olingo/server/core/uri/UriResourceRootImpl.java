package org.apache.olingo.server.core.uri;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourceRoot;

public class UriResourceRootImpl extends UriResourceWithKeysImpl implements UriResourceRoot {
  private final EdmType type;

  private final boolean isCollection;

  public UriResourceRootImpl(final EdmType type, final boolean isCollection) {
    super(UriResourceKind.root);
    this.type = type;
    this.isCollection = isCollection;
  }

  @Override public EdmType getType() {
    return type;
  }

  @Override public boolean isCollection() {
    return keyPredicates == null && isCollection;
  }

  @Override public String getSegmentValue() {
    return "$root";
  }
}