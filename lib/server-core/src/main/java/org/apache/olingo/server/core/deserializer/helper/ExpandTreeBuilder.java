package org.apache.olingo.server.core.deserializer.helper;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.UriResourceNavigationPropertyImpl;
import org.apache.olingo.server.core.uri.queryoption.ExpandItemImpl;

public abstract class ExpandTreeBuilder {
  public abstract ExpandTreeBuilder expand(EdmNavigationProperty edmNavigationProperty);

  protected ExpandItemImpl buildExpandItem(final EdmNavigationProperty edmNavigationProperty) {
    return new ExpandItemImpl().setResourcePath(new UriInfoImpl().addResourcePart(new UriResourceNavigationPropertyImpl(edmNavigationProperty)));
  }
}