package org.omnifaces.eventlistener;
import static org.omnifaces.util.Events.subscribeToApplicationEvent;
import static org.omnifaces.util.Utils.isEmpty;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PostValidateEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import org.omnifaces.event.PostInvokeActionEvent;
import org.omnifaces.event.PreInvokeActionEvent;

public class InvokeActionEventListener extends DefaultPhaseListener implements SystemEventListener {
  private static final long serialVersionUID = -7324254442944700095L;

  public InvokeActionEventListener() {
    super(PhaseId.INVOKE_APPLICATION);
    subscribeToApplicationEvent(PostValidateEvent.class, this);
  }

  @Override public boolean isListenerForSource(Object source) {
    if (!(source instanceof UIComponent)) {
      return false;
    }
    UIComponent component = (UIComponent) source;
    return !isEmpty(component.getListenersForEventClass(PreInvokeActionEvent.class)) || !isEmpty(component.getListenersForEventClass(PostInvokeActionEvent.class));
  }

  @Override public void processEvent(SystemEvent event) throws AbortProcessingException {
    FacesContext context = FacesContext.getCurrentInstance();
    if (!context.isValidationFailed()) {
      UIComponent component = (UIComponent) event.getSource();
      checkAndAddComponentWithListeners(context, component, PreInvokeActionEvent.class);
      checkAndAddComponentWithListeners(context, component, PostInvokeActionEvent.class);
    }
  }

  @Override public void beforePhase(PhaseEvent event) {
    publishEvent(event.getFacesContext(), PreInvokeActionEvent.class);
  }

  @Override public void afterPhase(PhaseEvent event) {
    publishEvent(event.getFacesContext(), PostInvokeActionEvent.class);
  }

  @SuppressWarnings(value = { "unchecked" }) private static <T extends SystemEvent> void checkAndAddComponentWithListeners(FacesContext context, UIComponent component, Class<T> type) {
    if (!isEmpty(component.getListenersForEventClass(type))) {
      Set<UIComponent> components = (Set<UIComponent>) context.getAttributes().get(type);
      if (components == null) {
        components = new LinkedHashSet<UIComponent>();
        context.getAttributes().put(type, components);
      }
      components.add(component);
    }
  }

  @SuppressWarnings(value = { "unchecked" }) private static <T extends SystemEvent> void publishEvent(FacesContext context, Class<T> type) {
    Set<UIComponent> components = (Set<UIComponent>) context.getAttributes().get(type);
    if (components != null) {
      for (UIComponent component : components) {
        context.getApplication().publishEvent(context, type, component);
      }
    }
  }
}