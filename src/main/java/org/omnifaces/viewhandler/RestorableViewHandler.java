package org.omnifaces.viewhandler;
import static java.lang.Boolean.TRUE;
import static org.omnifaces.util.Faces.normalizeViewId;
import static org.omnifaces.util.Faces.setContext;
import static org.omnifaces.util.FacesLocal.getApplicationAttribute;
import java.io.IOException;
import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.ViewExpiredException;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
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
	 * First try to restore the view. If the <code>&lt;o:enableRestoreView&gt;</code> is used once in the application,
	 * and the restored view returns null and the current request is a postback, then recreate and build the view.
	 * If it contains the <code>&lt;o:enableRestoreView&gt;</code>, then return the newly created view, else
	 * return <code>null</code>.
	 */
  @Override public UIViewRoot restoreView(FacesContext context, String viewId) {
    UIViewRoot restoredView = super.restoreView(context, viewId);
    if (!(isEnabled(context) && restoredView == null && context.isPostback())) {
      return restoredView;
    }
    String normalizedViewId = normalizeViewId(viewId);
    UIViewRoot createdView = createView(context, normalizedViewId);
    FacesContext temporaryContext = new TemporaryViewFacesContext(context, createdView);
    try {
      setContext(temporaryContext);
      getViewDeclarationLanguage(temporaryContext, normalizedViewId).buildView(temporaryContext, createdView);
    } catch (IOException e) {
      throw new FacesException(e);
    } finally {
      setContext(context);
    }
    if (TRUE.equals(createdView.getAttributes().get(EnableRestorableView.class.getName()))) {
      return createdView;
    } else {
      return null;
    }
  }

  private boolean isEnabled(FacesContext context) {
    return TRUE.equals(getApplicationAttribute(context, 
<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/viewhandler/RestorableViewHandler.java/left.java
    RestorableViewHandler
=======
    EnableRestorableView
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/viewhandler/RestorableViewHandler.java/right.java
    .class.getName()));
  }

  @Override public ViewHandler getWrapped() {
    return wrapped;
  }

  private static class TemporaryViewFacesContext extends FacesContextWrapper {
    private FacesContext wrapped;

    private UIViewRoot temporaryView;

    public TemporaryViewFacesContext(FacesContext wrapped, UIViewRoot temporaryView) {
      this.wrapped = wrapped;
      this.temporaryView = temporaryView;
    }

    @Override public UIViewRoot getViewRoot() {
      return temporaryView;
    }

    @Override public RenderKit getRenderKit() {
      return ((RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY)).getRenderKit(this, temporaryView.getRenderKitId());
    }

    @Override public FacesContext getWrapped() {
      return wrapped;
    }
  }
}