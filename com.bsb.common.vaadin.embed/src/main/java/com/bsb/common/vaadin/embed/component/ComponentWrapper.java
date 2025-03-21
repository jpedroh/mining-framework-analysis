package com.bsb.common.vaadin.embed.component;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

/**
 * Wraps a component into an actual application.
 *
 * @author Stephane Nicoll
 */
public class ComponentWrapper {
  private static final int SPLIT_POSITION = 20;

  private final ComponentBasedVaadinServer server;

  /**
     * Creates a new instance.
     *
     * @param server the server handling this application
     */
  public ComponentWrapper(ComponentBasedVaadinServer server) {
    this.server = server;
  }

  /**
     * Wraps the specified {@link Component} into a Vaadin application.
     *
     * @param component the component to wrap
     * @return an application displaying that component
     * @see #wrapLayout(com.vaadin.ui.Layout)
     * @see #wrapWindow(com.vaadin.ui.Window)
     */
  public UI wrap(Component component) {
    if (component instanceof UI) {
      return (UI) component;
    }
    if (component instanceof Window) {
      return wrapWindow((Window) component);
    }
    if (component instanceof Layout) {
      return wrapLayout((Layout) component);
    }
    final VerticalLayout layout = new VerticalLayout();
    layout.setMargin(true);
    layout.setSizeFull();
    layout.addComponent(component);
    layout.setExpandRatio(component, 1);
    return wrapLayout(layout);
  }

  /**
     * Wraps a {@link Layout} into a Vaadin application.
     *
     * @param layout the layout to wrap
     * @return an application displaying that layout
     */
  public UI wrapLayout(Layout layout) {
    if (server.getConfig().isDevelopmentHeader()) {
      final VerticalSplitPanel mainLayout = new VerticalSplitPanel();
      mainLayout.setSizeFull();
      mainLayout.setSplitPosition(SPLIT_POSITION, Sizeable.Unit.PIXELS);
      mainLayout.setLocked(true);
      final DevApplicationHeader header = new DevApplicationHeader(server);
      header.setSpacing(true);
      mainLayout.setFirstComponent(header);
      mainLayout.setSecondComponent(layout);
      return new DevUI(mainLayout);
    } else {
      return new DevUI(layout);
    }
  }

  /**
     * Wraps a {@link Window} into a Vaadin application.
     *
     * @param window the window to wrap
     * @return an application using that window as primary window
     */
  public UI wrapWindow(Window window) {
    final UI ui = wrapLayout(new VerticalLayout());
    ui.addWindow(window);
    return ui;
  }

  @SuppressWarnings(value = { "serial" }) static class DevUI extends UI {
    /**
         * Creates a new instance.
         *
         * @param content the content of the UI
         */
    public DevUI(ComponentContainer content) {
      setContent(content);
    }

    @Override protected void init(VaadinRequest vaadinRequest) {
    }
  }
}