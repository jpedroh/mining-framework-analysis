package de.deepamehta.core.impl;
import de.deepamehta.core.osgi.PluginContext;
import de.deepamehta.core.service.DeepaMehtaEvent;
import de.deepamehta.core.service.DeepaMehtaService;
import de.deepamehta.core.service.EventListener;
import de.deepamehta.core.service.accesscontrol.AccessControlException;
import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class EventManager {
  /**
     * The registered event listeners (key: event class name, value: event listeners).
     */
  private Map<String, List<EventListener>> listenerRegistry = new HashMap();

  private DeepaMehtaService dms;

  EventManager(DeepaMehtaService dms) {
    this.dms = dms;
    new CoreEvent();
  }

  void addListener(DeepaMehtaEvent event, EventListener listener) {
    List<EventListener> listeners = getListeners(event);
    if (listeners == null) {
      listeners = new ArrayList();
      putListeners(event, listeners);
    }
    listeners.add(listener);
  }

  void removeListener(DeepaMehtaEvent event, EventListener listener) {
    List<EventListener> listeners = getListeners(event);
    if (!listeners.remove(listener)) {
      throw new RuntimeException("Removing " + listener + " from " + event + " event listeners failed: not found in " + listeners);
    }
  }

  void fireEvent(DeepaMehtaEvent event, Object... params) {
    List<EventListener> listeners = getListeners(event);
    if (listeners != null) {
      for (EventListener listener : listeners) {
        deliverEvent(listener, event, params);
      }
    }
  }

  /**
     * Delivers an event to a particular plugin.
     * If the plugin is not a listener for that event nothing is performed.
     */
  void deliverEvent(PluginImpl plugin, DeepaMehtaEvent event, Object... params) {
    PluginContext pluginContext = plugin.getContext();
    if (!isListener(pluginContext, event)) {
      return;
    }
    deliverEvent((EventListener) pluginContext, event, params);
  }

  /**
     * Delivers an event to a particular plugin.
     * If the plugin is not a listener for that event nothing is performed.
     * <p>
     * Convenience method that takes a plugin URI.
     */
  void deliverEvent(String pluginUri, DeepaMehtaEvent event, Object... params) {
    deliverEvent((PluginImpl) dms.getPlugin(pluginUri), event, params);
  }

  private void deliverEvent(EventListener listener, DeepaMehtaEvent event, Object... params) {
    try {
      event.deliver(listener, params);
    } catch (WebApplicationException e) {
      throw e;
    } catch (AccessControlException e) {
      throw e;
    } catch (Throwable e) {
      throw new RuntimeException("Processing event by " + listener + " failed (event=" + event + ")", e);
    }
  }

  /**
     * Returns true if the given plugin is a listener for the given event.
     */
  private boolean isListener(PluginContext pluginContext, DeepaMehtaEvent event) {
    return event.getListenerInterface().isAssignableFrom(pluginContext.getClass());
  }

  private List<EventListener> getListeners(DeepaMehtaEvent event) {
    return listenerRegistry.get(event.getClass().getName());
  }

  private void putListeners(DeepaMehtaEvent event, List<EventListener> listeners) {
    listenerRegistry.put(event.getClass().getName(), listeners);
  }
}