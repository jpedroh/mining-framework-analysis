package org.omnifaces.util;
import static org.omnifaces.util.Faces.getApplication;
import static org.omnifaces.util.Faces.getViewRoot;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import org.omnifaces.eventlistener.CallbackPhaseListener;
import org.omnifaces.eventlistener.DefaultPhaseListener;
import org.omnifaces.eventlistener.DefaultViewEventListener;

public final class Events {
  private Events() {
  }

  public static void subscribeToApplicationEvent(Class<? extends SystemEvent> type, SystemEventListener listener) {
    getApplication().subscribeToEvent(type, listener);
  }

  public static void subscribeToApplicationEvent(Class<? extends SystemEvent> type, Callback.SerializableVoid callback) {
    subscribeToApplicationEvent(type, createSystemEventListener(Events.<SystemEvent>wrap(callback)));
  }

  public static void subscribeToApplicationEvent(Class<? extends SystemEvent> type, Callback.SerializableWithArgument<SystemEvent> callback) {
    subscribeToApplicationEvent(type, createSystemEventListener(callback));
  }

  public static void subscribeToViewEvent(Class<? extends SystemEvent> type, SystemEventListener listener) {
    getViewRoot().subscribeToViewEvent(type, listener);
  }

  public static void subscribeToViewEvent(Class<? extends SystemEvent> type, Callback.SerializableVoid callback) {
    subscribeToViewEvent(type, createSystemEventListener(Events.<SystemEvent>wrap(callback)));
  }

  public static void subscribeToViewEvent(Class<? extends SystemEvent> type, Callback.SerializableWithArgument<SystemEvent> callback) {
    subscribeToViewEvent(type, createSystemEventListener(callback));
  }

  public static void addViewPhaseListener(PhaseListener listener) {
    getViewRoot().addPhaseListener(listener);
  }

  public static void subscribeToViewBeforePhase(PhaseId phaseId, Callback.Void callback) {
    addViewPhaseListener(createBeforePhaseListener(phaseId, Events.<PhaseEvent>wrap(callback)));
  }

  public static void subscribeToViewBeforePhase(PhaseId phaseId, Callback.WithArgument<PhaseEvent> callback) {
    addViewPhaseListener(createBeforePhaseListener(phaseId, callback));
  }

  public static void subscribeToViewAfterPhase(PhaseId phaseId, Callback.Void callback) {
    addViewPhaseListener(createAfterPhaseListener(phaseId, Events.<PhaseEvent>wrap(callback)));
  }

  public static void subscribeToViewAfterPhase(PhaseId phaseId, Callback.WithArgument<PhaseEvent> callback) {
    addViewPhaseListener(createAfterPhaseListener(phaseId, callback));
  }

  public static void addRequestPhaseListener(PhaseListener listener) {
    CallbackPhaseListener.add(listener);
  }

  public static void subscribeToRequestBeforePhase(PhaseId phaseId, Callback.Void callback) {
    addRequestPhaseListener(createBeforePhaseListener(phaseId, Events.<PhaseEvent>wrap(callback)));
  }

  public static void subscribeToRequestBeforePhase(PhaseId phaseId, Callback.WithArgument<PhaseEvent> callback) {
    addRequestPhaseListener(createBeforePhaseListener(phaseId, callback));
  }

  public static void subscribeToRequestAfterPhase(PhaseId phaseId, Callback.Void callback) {
    addRequestPhaseListener(createAfterPhaseListener(phaseId, Events.<PhaseEvent>wrap(callback)));
  }

  public static void subscribeToRequestAfterPhase(PhaseId phaseId, Callback.WithArgument<PhaseEvent> callback) {
    addRequestPhaseListener(createAfterPhaseListener(phaseId, callback));
  }

  private static <A extends java.lang.Object> Callback.WithArgument<A> wrap(final Callback.Void callback) {
    return new Callback.WithArgument<A>() {
      @Override public void invoke(A argument) {
        callback.invoke();
      }
    };
  }

  private static <A extends java.lang.Object> Callback.SerializableWithArgument<A> wrap(final Callback.SerializableVoid callback) {
    return new Callback.SerializableWithArgument<A>() {
      private static final long serialVersionUID = 1L;

      @Override public void invoke(A argument) {
        callback.invoke();
      }
    };
  }

  private static SystemEventListener createSystemEventListener(final Callback.SerializableWithArgument<SystemEvent> callback) {
    return new DefaultViewEventListener() {
      @Override public void processEvent(SystemEvent event) throws AbortProcessingException {
        callback.invoke(event);
      }
    };
  }

  private static PhaseListener createBeforePhaseListener(PhaseId phaseId, final Callback.WithArgument<PhaseEvent> callback) {
    return new DefaultPhaseListener(phaseId) {
      private static final long serialVersionUID = 1L;

      @Override public void beforePhase(PhaseEvent event) {
        callback.invoke(event);
      }
    };
  }

  private static PhaseListener createAfterPhaseListener(PhaseId phaseId, final Callback.WithArgument<PhaseEvent> callback) {
    return new DefaultPhaseListener(phaseId) {
      private static final long serialVersionUID = 1L;

      @Override public void afterPhase(PhaseEvent event) {
        callback.invoke(event);
      }
    };
  }
}