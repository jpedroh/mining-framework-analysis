package org.apache.olingo.server.core.uri;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceKind;

/**
 * Implementation of the {@link UriResourceAction} interface. This class does not extend
 * {@link org.apache.olingo.server.core.uri.UriResourceTypedImpl UriResourceTypedImpl}
 * since that would allow type filters and subsequent path segments.
 */
public class UriResourceActionImpl extends UriResourceImpl implements UriResourceAction {
  private final EdmActionImport actionImport;

  private final EdmAction action;

  public UriResourceActionImpl(final EdmActionImport actionImport) {
    super(UriResourceKind.action);
    this.actionImport = actionImport;
    this.action = actionImport.getUnboundAction();
  }

  public UriResourceActionImpl(final EdmAction action) {
    super(UriResourceKind.action);
    this.actionImport = null;
    this.action = action;
  }

  @Override public EdmAction getAction() {
    return action;
  }

  @Override public EdmActionImport getActionImport() {
    return actionImport;
  }

  @Override public boolean isCollection() {
    return action.getReturnType() != null && action.getReturnType().isCollection();
  }

  @Override public EdmType getType() {
    return action.getReturnType() == null ? null : action.getReturnType().getType();
  }

  @Override public String getSegmentValue(final boolean includeFilters) {
    return actionImport == null ? (action == null ? "" : action.getName()) : actionImport.getName();
  }

  @Override public String getSegmentValue() {
    return getSegmentValue(false);
  }

  @Override public String toString(final boolean includeFilters) {
    return getSegmentValue(includeFilters);
  }
}