package org.apache.olingo.server.core.uri;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;

public class UriResourcePrimitivePropertyImpl extends UriResourceTypedImpl implements UriResourcePrimitiveProperty {
  private final EdmProperty property;

  public UriResourcePrimitivePropertyImpl(final EdmProperty property) {
    super(UriResourceKind.primitiveProperty);
    this.property = property;
  }

  @Override public EdmProperty getProperty() {
    return property;
  }

  @Override public EdmType getType() {
    return property.getType();
  }

  @Override public boolean isCollection() {
    return property.isCollection();
  }

  @Override public String getSegmentValue() {
    return property.getName();
  }
}