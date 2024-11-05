package org.omnifaces.context;
import static org.omnifaces.util.Faces.getContext;
import static org.omnifaces.util.Faces.responseReset;
import static org.omnifaces.util.Faces.setContextAttribute;
import static org.omnifaces.util.FacesLocal.getContextAttribute;
import static org.omnifaces.util.FacesLocal.getRequestAttribute;
import static org.omnifaces.util.FacesLocal.getViewId;
import static org.omnifaces.util.FacesLocal.normalizeViewId;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;
import org.omnifaces.config.WebXml;
import org.omnifaces.exceptionhandler.FullAjaxExceptionHandler;
import org.omnifaces.util.Hacks;
import org.omnifaces.util.Json;
import static org.omnifaces.util.FacesLocal.getRequest;
import static org.omnifaces.util.FacesLocal.getResponse;
import static org.omnifaces.util.Servlets.facesRedirect;
import javax.faces.event.PhaseId;

public class OmniPartialViewContext extends PartialViewContextWrapper {
  private static final String AJAX_DATA = "var OmniFaces=OmniFaces||{};OmniFaces.Ajax={data:%s};";

  private static final String ERROR_NO_OMNI_PVC = "There is no current OmniPartialViewContext instance.";

  private PartialViewContext wrapped;

  private Map<String, Object> arguments;

  private List<String> callbackScripts;

  private OmniPartialResponseWriter writer;

  public OmniPartialViewContext(PartialViewContext wrapped) {
    this.wrapped = wrapped;
    setCurrentInstance(this);
  }

  @Override public PartialResponseWriter getPartialResponseWriter() {
    if (writer == null) {
      writer = new OmniPartialResponseWriter(this, super.getPartialResponseWriter());
    }
    return writer;
  }

  @Override public void setPartialRequest(boolean partialRequest) {
    getWrapped().setPartialRequest(partialRequest);
  }

  @Override public PartialViewContext getWrapped() {
    return wrapped;
  }

  public void addArgument(String name, Object value) {
    if (arguments == null) {
      arguments = new HashMap<String, Object>();
    }
    arguments.put(name, value);
  }

  public void addCallbackScript(String callbackScript) {
    if (callbackScripts == null) {
      callbackScripts = new ArrayList<String>();
    }
    callbackScripts.add(callbackScript);
  }

  public void resetPartialResponse() {
    if (writer != null) {
      writer.reset();
    }
    arguments = null;
    callbackScripts = null;
  }

  public void closePartialResponse() {
    if (writer != null && writer.updating) {
      try {
        writer.endUpdate();
        writer.endDocument();
      } catch (IOException e) {
        throw new FacesException(e);
      }
    }
  }

  public static OmniPartialViewContext getCurrentInstance() {
    return getCurrentInstance(getContext());
  }

  public static OmniPartialViewContext getCurrentInstance(FacesContext context) {
    OmniPartialViewContext instance = getContextAttribute(context, OmniPartialViewContext.class.getName());
    if (instance != null) {
      return instance;
    }
    instance = unwrap(context.getPartialViewContext());
    if (instance != null) {
      setCurrentInstance(instance);
      return instance;
    }
    if (Hacks.isRichFacesInstalled()) {
      PartialViewContext pvc = Hacks.getRichFacesWrappedPartialViewContext();
      if (pvc != null) {
        instance = unwrap(pvc);
        if (instance != null) {
          setCurrentInstance(instance);
          return instance;
        }
      }
    }
    throw new IllegalStateException(ERROR_NO_OMNI_PVC);
  }

  private static void setCurrentInstance(OmniPartialViewContext instance) {
    setContextAttribute(OmniPartialViewContext.class.getName(), instance);
  }

  private static OmniPartialViewContext unwrap(PartialViewContext context) {
    PartialViewContext unwrappedContext = context;
    while (!(unwrappedContext instanceof OmniPartialViewContext) && unwrappedContext instanceof PartialViewContextWrapper) {
      unwrappedContext = ((PartialViewContextWrapper) unwrappedContext).getWrapped();
    }
    if (unwrappedContext instanceof OmniPartialViewContext) {
      return (OmniPartialViewContext) unwrappedContext;
    } else {
      return null;
    }
  }

  private static class OmniPartialResponseWriter extends PartialResponseWriter {
    private OmniPartialViewContext context;

    private PartialResponseWriter wrapped;

    private boolean updating;

    public OmniPartialResponseWriter(OmniPartialViewContext context, PartialResponseWriter wrapped) {
      super(wrapped);
      this.wrapped = wrapped;
      this.context = context;
    }

    @Override public void startUpdate(String targetId) throws IOException {
      updating = true;
      wrapped.startUpdate(targetId);
    }

    @Override public void endUpdate() throws IOException {
      updating = false;
      wrapped.endUpdate();
    }

    @Override public void endDocument() throws IOException {
      if (updating) {
        endCDATA();
        endUpdate();
      } else {
        if (context.arguments != null) {
          startEval();
          write(String.format(AJAX_DATA, Json.encode(context.arguments)));
          endEval();
        }
        if (context.callbackScripts != null) {
          for (String callbackScript : context.callbackScripts) {
            startEval();
            write(callbackScript);
            endEval();
          }
        }
      }
      wrapped.endDocument();
    }

    public void reset() {
      try {
        if (updating) {
          endCDATA();
          endUpdate();
          wrapped.endDocument();
        }
      } catch (IOException e) {
        throw new FacesException(e);
      } finally {
        responseReset();
      }
    }

    @Override public void startError(String errorName) throws IOException {
      wrapped.startError(errorName);
    }

    @Override public void startEval() throws IOException {
      wrapped.startEval();
    }

    @Override public void startExtension(Map<String, String> attributes) throws IOException {
      wrapped.startExtension(attributes);
    }

    @Override public void startInsertAfter(String targetId) throws IOException {
      wrapped.startInsertAfter(targetId);
    }

    @Override public void startInsertBefore(String targetId) throws IOException {
      wrapped.startInsertBefore(targetId);
    }

    @Override public void endError() throws IOException {
      wrapped.endError();
    }

    @Override public void endEval() throws IOException {
      wrapped.endEval();
    }

    @Override public void endExtension() throws IOException {
      wrapped.endExtension();
    }

    @Override public void endInsert() throws IOException {
      wrapped.endInsert();
    }

    @Override public void delete(String targetId) throws IOException {
      wrapped.delete(targetId);
    }

    @Override public void redirect(String url) throws IOException {
      wrapped.redirect(url);
    }

    @Override public void updateAttributes(String targetId, Map<String, String> attributes) throws IOException {
      wrapped.updateAttributes(targetId, attributes);
    }
  }

  @Override public void processPartial(PhaseId phaseId) {
    if (phaseId == PhaseId.RENDER_RESPONSE) {
      String loginURL = WebXml.INSTANCE.getFormLoginPage();
      if (loginURL != null) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String loginViewId = normalizeViewId(facesContext, loginURL);
        if (loginViewId.equals(getViewId(facesContext))) {
          String originalURL = getRequestAttribute(facesContext, "javax.servlet.forward.request_uri");
          if (originalURL != null) {
            try {
              facesRedirect(getRequest(facesContext), getResponse(facesContext), originalURL);
            } catch (IOException e) {
              throw new FacesException(e);
            }
            return;
          }
        }
      }
    }
    super.processPartial(phaseId);
  }
}