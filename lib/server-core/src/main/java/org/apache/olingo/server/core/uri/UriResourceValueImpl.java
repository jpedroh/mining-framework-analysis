package org.apache.olingo.server.core.uri;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourceValue;

public class UriResourceValueImpl extends UriResourceImpl implements UriResourceValue {
  public UriResourceValueImpl() {
    super(UriResourceKind.value);
  }

  @Override public String getSegmentValue() {
    return "$value";
  }
}