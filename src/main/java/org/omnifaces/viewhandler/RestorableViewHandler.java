package org.omnifaces.viewhandler;
import static java.lang.Boolean.TRUE;
import static org.omnifaces.util.Components.buildView;
import static org.omnifaces.util.FacesLocal.getApplicationAttribute;
import java.io.IOException;
import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import org.omnifaces.taghandler.EnableRestorableView;

/**
 * This view handler implementation will recreate the entire view state whenever the view has apparently been expired,
 * i.e. whenever {@link #restoreView(FacesContext, String)} returns <code>null</code> and the current request is a
 * postback and the view in question has <code>&lt;enableRestorableView&gt;</code> in the metadata. This effectively
 * prevents the {@link ViewExpiredException} on the view.
 *
 * @author Bauke Scholtz
 * @since 1.3
 * @see EnableRestorableView
 */
public class RestorableViewHandler extends ViewHandlerWrapper {
  private ViewHandler wrapped;

  /**
	 * Construct a new restorable view handler around the given wrapped view handler.
	 * @param wrapped The wrapped view handler.
	 */
  public RestorableViewHandler(ViewHandler wrapped) {
    this.wrapped = wrapped;
  }

  /**
	 * If the <code>&lt;o:enableRestoreView&gt;</code> is used once in the application, and the restored view is null
	 * and the current request is a postback, then recreate and rebuild the view from scratch. If it indeed contains the
	 * <code>&lt;o:enableRestoreView&gt;</code>, then return the newly created view, else return <code>null</code>.
	 */
  @Override public UIViewRoot restoreView(FacesContext context, String viewId) {
    UIViewRoot restoredView = super.restoreView(context, viewId);
    if (!(isRestorableViewEnabled(context) && restoredView == null && context.isPostback())) {
      return restoredView;
    }
    try {
      UIViewRoot createdView = buildView(viewId);
      return isRestorableView(createdView) ? createdView : null;
    } catch (IOException e) {
      throw new FacesException(e);
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * Restore only the view root state. This ensures that the view scope map and all view root component system event
	 * listeners are also restored. Calling <code>super.restoreView()</code> would implicitly also build the entire view
	 * and restore state of all other components in the tree. This is unnecessary during an unload request.
	 */
  @SuppressWarnings(value = { "unchecked" }) private boolean restoreViewRootState(FacesContext context, ResponseStateManager manager, UIViewRoot view) {
    Object state = manager.getState(context, view.getViewId());
    if (state == null || !(state instanceof Object[]) || ((Object[]) state).length < 2 || !(((Object[]) state)[1] instanceof Map)) {
      return false;
    }
    Map<String, Object> states = (Map<String, Object>) ((Object[]) state)[1];
    if (view.getId() == null) {
      view.setId(view.createUniqueId(context, null));
    }
    Object viewRootState = states.get(view.getClientId(context));
    view.restoreState(context, viewRootState);
    context.setViewRoot(view);
    return true;
  }
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/6421cc8340e71622018047b422b5a3c42230d06e/src/main/java/org/omnifaces/viewhandler/RestorableViewHandler.java/right.java


  private boolean isRestorableViewEnabled(FacesContext context) {
    return TRUE.equals(getApplicationAttribute(context, EnableRestorableView.class.getName()));
  }

  private boolean isRestorableView(UIViewRoot view) {
    return TRUE.equals(view.getAttributes().get(EnableRestorableView.class.getName()));
  }

  @Override public ViewHandler getWrapped() {
    return wrapped;
  }
}