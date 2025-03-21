package bibliothek.gui.dock.station.split;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.security.GlassedPane;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * The default implementation of {@link SplitDividerStrategy} 
 * @author Benjamin Sigg
 */
public class DefaultSplitDividerStrategy implements SplitDividerStrategy {
  private Map<SplitDockStation, Handler> handlers = new HashMap<SplitDockStation, Handler>();

  public void install(SplitDockStation station, Component container) {
    Handler handler = createHandlerFor(station);
    handler.install(container);
    handlers.put(station, handler);
  }

  public void uninstall(SplitDockStation station) {
    Handler handler = handlers.remove(station);
    if (handler != null) {
      handler.destroy();
    }
  }

  public void paint(SplitDockStation station, Graphics g) {
    Handler handler = handlers.get(station);
    if (handler != null) {
      handler.paint(g);
    }
  }

  /**
	 * Creates a new {@link Handler} for <code>station</code>.
	 * @param station the station which is to be monitored
	 * @return the new handler, not <code>null</code>
	 */
  protected Handler createHandlerFor(SplitDockStation station) {
    return new Handler(station);
  }

  public static class Handler extends MouseAdapter implements MouseListener, MouseMotionListener, AWTEventListener, DockHierarchyListener {
    private PropertyValue<Boolean> restricted = new PropertyValue<Boolean>(DockController.RESTRICTED_ENVIRONMENT) {
      @Override protected void valueChanged(Boolean oldValue, Boolean newValue) {
        updateEventListener();
      }
    };

    /** the currently known {@link DockController} */
    private DockController controller;

    /** the node of the currently selected divider */
    private Divideable current;

    /** the current location of the divider */
    private double divider;

    /** the current state of the mouse: pressed or not pressed */
    private boolean pressed = false;

    /** Will be set to true when mouse is over divider, and set to false when exited. (see AWTListener method below for more details). */
    private boolean withinBounds = false;

    /** Flag indicating if AWTEventListener is registered successfully. */
    private boolean awtListenerEnabled = false;

    /** the current bounds of the divider */
    private Rectangle bounds = new Rectangle();

    /** 
		 * A small modification of the position of the mouse. The modification
		 * is the distance to the center of the divider.
		 */
    private int deltaX;

    /** 
		 * A small modification of the position of the mouse. The modification
		 * is the distance to the center of the divider.
		 */
    private int deltaY;

    /** The station which is monitored by this strategy */
    private SplitDockStation station;

    /** The component to which this strategy added a {@link MouseListener} */
    private Component container;

    /**
		 * Creates a new strategy that will monitor <code>station</code>.
		 * @param station the station to monitor
		 */
    public Handler(SplitDockStation station) {
      this.station = station;
    }

    /**
		 * Gets the station which is monitored by this strategy
		 * @return the owner of this strategy
		 */
    public SplitDockStation getStation() {
      return station;
    }

    public void install(Component container) {
      if (this.container != null) {
        throw new IllegalStateException("already initialized");
      }
      this.container = container;
      container.addMouseListener(this);
      container.addMouseMotionListener(this);
      station.addDockHierarchyListener(this);
      setController(station.getController());
    }

    public void hierarchyChanged(DockHierarchyEvent event) {
    }

    public void controllerChanged(DockHierarchyEvent event) {
      setController(station.getController());
    }

    private void setController(DockController controller) {
      if (this.controller != controller) {
        this.controller = controller;
        restricted.setProperties(controller);
        updateEventListener();
      }
    }

    private void updateEventListener() {
      boolean expected = controller != null && !controller.isRestrictedEnvironment();
      if (expected != awtListenerEnabled) {
        awtListenerEnabled = expected;
        if (expected) {
          Toolkit.getDefaultToolkit().addAWTEventListener(Handler.this, AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
        } else {
          Toolkit.getDefaultToolkit().removeAWTEventListener(Handler.this);
        }
      }
    }

    /**
		 * AWT event listener.
		 * Used to reset the mouse cursor when divider was changed and mouse exited event had not occurred normally.
		 * @param event
		 */
    public void eventDispatched(AWTEvent event) {
      if (event.getID() == MouseEvent.MOUSE_MOVED || event.getID() == MouseEvent.MOUSE_RELEASED) {
        MouseEvent mev = (MouseEvent) event;
        if (mev.getSource() != Handler.this.container && withinBounds) {
          if (mev.getSource() instanceof GlassedPane.GlassPane) {
            checkMousePositionAsync();
          } else {
            Point p = SwingUtilities.convertPoint(mev.getComponent(), mev.getPoint(), station);
            if (station.getBounds().contains(p)) {
              setCursor(null);
              withinBounds = false;
            }
          }
        }
      }
    }

    /**
		 * Gets the {@link Component} with which this strategy was {@link #install(Component) initialized}.
		 * @return the argument from {@link #install(Component)}
		 */
    public Component getContainer() {
      return container;
    }

    /**
		 * Disposes all resources that are used by this handler.
		 */
    public void destroy() {
      if (container != null) {
        setCursor(null);
        current = null;
        container.removeMouseListener(this);
        container.removeMouseMotionListener(this);
        container = null;
        try {
          java.awt.Toolkit.getDefaultToolkit().removeAWTEventListener(this);
        } catch (Throwable e) {
          e.printStackTrace();
        }
        setController(null);
        station.removeDockHierarchyListener(this);
      }
    }

    /**
		 * Changes the cursor of {@link #getContainer() the base component}. Subclasses may override this
		 * method to use custom cursors.
		 * @param cursor the cursor to set, may be <code>null</code>
		 */
    protected void setCursor(Cursor cursor) {
      container.setCursor(cursor);
    }

    /**
		 * Repaints parts of the {@link #getContainer() base component}.
		 * @param x x coordinate
		 * @param y y coordinate
		 * @param width the width of the are to repaint
		 * @param height the height of the are to repaint
		 */
    protected void repaint(int x, int y, int width, int height) {
      container.repaint(x, y, width, height);
    }

    /**
		 * Gets the node whose dividier contains <code>x, y</code>.
		 * @param x the x coordinate
		 * @param y the y coordinate
		 * @return the node containing <code>x, y</code>
		 */
    protected Divideable getDividerNode(int x, int y) {
      return station.getRoot().getDividerNode(x, y);
    }

    /**
		 * Asynchronously checks the current position of the mouse and updates the cursor
		 * if necessary.
		 */
    protected void checkMousePositionAsync() {
      DockController controller = station.getController();
      if (controller != null && !awtListenerEnabled) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            if (container != null) {
              PointerInfo p = MouseInfo.getPointerInfo();
              Point e = p.getLocation();
              SwingUtilities.convertPointFromScreen(e, container);
              current = getDividerNode(e.x, e.y);
              if (current == null) {
                mouseExited(null);
              } else {
                if (bounds.width > 2 && bounds.height > 2) {
                  if (e.x <= bounds.x || e.x >= bounds.x + bounds.width - 1 || e.y <= bounds.y || e.y >= bounds.y + bounds.height - 1) {
                    mouseExited(null);
                  }
                }
              }
            }
          }
        });
      }
    }

    @Override public void mousePressed(MouseEvent e) {
      if (station.isResizingEnabled() && !station.isDisabled()) {
        if (!pressed) {
          pressed = true;
          mouseMoved(e);
          if (current != null) {
            divider = current.getDividerAt(e.getX() + deltaX, e.getY() + deltaY);
            repaint(bounds.x, bounds.y, bounds.width, bounds.height);
            bounds = current.getDividerBounds(divider, bounds);
            repaint(bounds.x, bounds.y, bounds.width, bounds.height);
          }
        }
      }
    }

    public void mouseDragged(MouseEvent e) {
      if (station.isResizingEnabled() && !station.isDisabled()) {
        if (pressed && current != null) {
          divider = current.getDividerAt(e.getX() + deltaX, e.getY() + deltaY);
          divider = current.validateDivider(divider);
          repaint(bounds.x, bounds.y, bounds.width, bounds.height);
          bounds = current.getDividerBounds(divider, bounds);
          repaint(bounds.x, bounds.y, bounds.width, bounds.height);
          if (station.isContinousDisplay() && current != null) {
            setDivider(current, divider);
            station.updateBounds();
          }
        }
      }
    }

    @Override public void mouseReleased(MouseEvent e) {
      if (pressed) {
        pressed = false;
        if (current != null) {
          setDivider(current, divider);
          repaint(bounds.x, bounds.y, bounds.width, bounds.height);
          station.updateBounds();
        }
        setCursor(null);
        mouseMoved(e);
        if (controller != null && !controller.isRestrictedEnvironment() && awtListenerEnabled) {
          eventDispatched(e);
        } else {
          checkMousePositionAsync();
        }
      }
    }

    /**
		 * Called if the dividier of <code>node</code> needs to be changed.
		 * @param node the node whose divider changes
		 * @param dividier the new divider
		 */
    protected void setDivider(Divideable node, double dividier) {
      node.setDivider(dividier);
    }

    public void mouseMoved(MouseEvent e) {
      if (station.isResizingEnabled() && !station.isDisabled()) {
        current = getDividerNode(e.getX(), e.getY());
        if (current == null) {
          setCursor(null);
        } else {
          if (current.getOrientation() == Orientation.HORIZONTAL) {
            setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
          } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
          }
        }
        if (current != null) {
          bounds = current.getDividerBounds(current.getDivider(), bounds);
          deltaX = bounds.width / 2 + bounds.x - e.getX();
          deltaY = bounds.height / 2 + bounds.y - e.getY();
          withinBounds = true;
        } else {
          withinBounds = false;
        }
      }
    }

    @Override public void mouseExited(MouseEvent e) {
      if (!pressed) {
        current = null;
        setCursor(null);
        withinBounds = false;
      }
    }

    /**
		 * Paints a line at the current location of the divider.
		 * @param g the Graphics used to paint
		 */
    public void paint(Graphics g) {
      if (station.isResizingEnabled() && !station.isDisabled()) {
        if (current != null && pressed) {
          station.getPaint().drawDivider(g, bounds);
        }
      }
    }
  }
}