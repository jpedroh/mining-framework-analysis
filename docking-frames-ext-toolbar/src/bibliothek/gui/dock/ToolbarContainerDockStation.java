package bibliothek.gui.dock;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.Position;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.gui.dock.station.AbstractDockableStation;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.OrientationObserver;
import bibliothek.gui.dock.station.OrientedDockStation;
import bibliothek.gui.dock.station.OrientingDockStation;
import bibliothek.gui.dock.station.OrientingDockStationEvent;
import bibliothek.gui.dock.station.OrientingDockStationListener;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.StationBackgroundComponent;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.StationDragOperation;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.DockableShowingManager;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.toolbar.DefaultToolbarContainerConverter;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerConverter;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerConverterCallback;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerDropInfo;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerLayoutManager;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.station.toolbar.layer.ToolbarContainerDropLayer;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.BasicDockTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.SilentPropertyValue;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.util.Path;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A {@link Dockable} and a {@link DockStation} which stands for a group of
 * {@link ToolbarGroupDockStation}. As dockable it can be put in every
 * {@link DockStation}. As DockStation it accepts only
 * {@link ToolbarElementInterface}s. When ToolbarElement are added, all the
 * ComponentDockable extracted from the element are merged together and wrapped
 * in a {@link ToolbarGroupDockStation} before to be added.
 * 
 * @author Herve Guillaume
 */
public class ToolbarContainerDockStation extends AbstractDockableStation implements OrientingDockStation, OrientedDockStation {
  /** the id of the {@link DockTitleFactory} used with this station */
  public static final String TITLE_ID = "toolbar.container";

  /**
	 * This id is forwarded to {@link Extension}s which load additional
	 * {@link DisplayerFactory}s
	 */
  public static final String DISPLAYER_ID = "toolbar.container";

  public static final Orientation DEFAULT_ORIENTATION = Orientation.VERTICAL;

  /** the orientation of the station */
  private Orientation orientation = DEFAULT_ORIENTATION;

  /** the number of dockables <code>this</code> station will accept */
  private int dockablesMaxNumber = 1;

  /** The containerPane */
  private JPanel containerPanel;

  /** The background of {@link #containerPanel} and in return of this entire station */
  private Background background = new Background();

  /**
	 * The graphical representation of this station: the pane which contains
	 * toolbars
	 */
  protected OverpaintablePanelBase mainPanel;

  /** dockables associate with the container pane */
  private DockablePlaceholderList<StationChildHandle> dockables = new DockablePlaceholderList<StationChildHandle>();

  /** the {@link DockableDisplayer} shown */
  private final DisplayerCollection displayer;

  /** factory for {@link DockTitle}s used for the main panel */
  private DockTitleVersion title;

  /** factory for creating new {@link DockableDisplayer}s */
  private final DefaultDisplayerFactoryValue displayerFactory;

  /** A paint to draw lines */
  private final DefaultStationPaintValue paint;

  /** the index of the closest dockable above the mouse */
  private int indexBeneathMouse = -1;

  /** closest side of the the closest dockable above the mouse */
  private Position sideAboveMouse = null;

  /**
	 * Tells if this station is in prepareDrop state and should draw something
	 * accordingly
	 */
  boolean prepareDropDraw = false;

  /** The dockable that is about to be dragged away from this station */
  private Dockable removal = null;

  /** all registered {@link OrientingDockStationListener}s. */
  private final List<OrientingDockStationListener> orientingListeners = new ArrayList<OrientingDockStationListener>();

  /** current {@link PlaceholderStrategy} */
  private final PropertyValue<PlaceholderStrategy> placeholderStrategy = new PropertyValue<PlaceholderStrategy>(PlaceholderStrategy.PLACEHOLDER_STRATEGY) {
    @Override protected void valueChanged(PlaceholderStrategy oldValue, PlaceholderStrategy newValue) {
      dockables.setStrategy(newValue);
    }
  };

  /** a manager to inform listeners about changes in the visibility state */
  private DockableShowingManager visibility;

  /** added to the current parent of this dockable */
  private VisibleListener visibleListener;

  /** This {@link LayoutManager} is responsible for updating the boundaries of all {@link Dockable}s and keeping track of {@link Span}s */
  private ToolbarContainerLayoutManager layoutManager;

  /** the number of pixels outside this station where a drag and drop operation is still possible */
  private int sideSnapSize = 10;

  /**
	 * Constructs a new ContainerLineStation
	 */
  public ToolbarContainerDockStation(Orientation orientation) {
    this(orientation, 1);
  }

  /**
	 * Creates a new station
	 * @param orientation the orientation of the content
	 * @param maxNumberOfDockables the maximum number of children or -1
	 */
  public ToolbarContainerDockStation(Orientation orientation, int maxNumberOfDockables) {
    this.orientation = orientation;
    setDockablesMaxNumber(maxNumberOfDockables);
    mainPanel = new OverpaintablePanelBase();
    paint = new DefaultStationPaintValue(ThemeManager.STATION_PAINT + ".toolbar", this);
    displayerFactory = new DefaultDisplayerFactoryValue(ThemeManager.DISPLAYER_FACTORY + ".toolbar.container", this);
    displayer = new DisplayerCollection(this, displayerFactory, DISPLAYER_ID);
    final DockableDisplayerListener listener = new DockableDisplayerListener() {
      @Override public void discard(DockableDisplayer displayer) {
        ToolbarContainerDockStation.this.discard(displayer);
      }

      @Override public void moveableElementChanged(DockableDisplayer displayer) {
      }
    };
    displayer.addDockableDisplayerListener(listener);
    setTitleIcon(null);
    new OrientationObserver(this) {
      @Override protected void orientationChanged(Orientation current) {
        if (current != null) {
          setOrientation(current);
        }
      }
    };
    visibility = new DockableShowingManager(listeners);
    visibleListener = new VisibleListener();
    getComponent().addHierarchyListener(new HierarchyListener() {
      public void hierarchyChanged(HierarchyEvent e) {
        if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
          if (getDockParent() == null) {
            getDockableStateListeners().checkShowing();
          }
          visibility.fire();
        }
      }
    });
  }

  /**
	 * Create a pane for this dock station
	 */
  private JPanel createPanel() {
    ConfiguredBackgroundPanel panel = new ConfiguredBackgroundPanel(Transparency.DEFAULT);
    panel.setBackground(background);
    layoutManager = new ToolbarContainerLayoutManager(panel, ToolbarContainerDockStation.this);
    panel.setLayout(layoutManager);
    panel.setBorder(new EmptyBorder(new Insets(3, 3, 3, 3)));
    return panel;
  }

  @Override public int getDockableCount() {
    return dockables.dockables().size();
  }

  @Override public Dockable getDockable(int index) {
    return dockables.dockables().get(index).getDockable();
  }

  @Override public Dockable getFrontDockable() {
    return null;
  }

  @Override public void setFrontDockable(Dockable dockable) {
  }

  @Override public PlaceholderMap getPlaceholders() {
    return createConverter().getPlaceholders(this);
  }

  /**
	 * Gets the layout of this station encoded as {@link PlaceholderMap}.
	 * 
	 * @param children
	 *            identifiers for the children
	 * @return the encoded layout, not <code>null</code>
	 */
  public PlaceholderMap getPlaceholders(Map<Dockable, Integer> children) {
    return createConverter().getPlaceholders(this, children);
  }

  @Override public void setPlaceholders(PlaceholderMap placeholders) {
    createConverter().setPlaceholders(this, placeholders);
  }

  /**
	 * Sets the layout of this station using the encoded layout from
	 * <code>placeholders</code>
	 * 
	 * @param placeholders
	 *            the placeholders to read
	 * @param children
	 *            the children to add
	 */
  public void setPlaceholders(PlaceholderMap placeholders, Map<Integer, Dockable> children) {
    createConverter().setPlaceholders(this, new ToolbarContainerConverterCallback() {
      int index = 0;

      @Override public StationChildHandle wrap(Dockable dockable) {
        return new StationChildHandle(ToolbarContainerDockStation.this, displayer, dockable, title);
      }

      @Override public void adding(StationChildHandle handle) {
        listeners.fireDockableAdding(handle.getDockable());
      }

      @Override public void added(StationChildHandle handle) {
        handle.updateDisplayer();
        insertAt(handle, index++);
        handle.getDockable().setDockParent(ToolbarContainerDockStation.this);
        listeners.fireDockableAdded(handle.getDockable());
      }

      @Override public void setDockables(DockablePlaceholderList<StationChildHandle> list) {
        ToolbarContainerDockStation.this.setDockables(list, false);
      }

      @Override public void finished(DockablePlaceholderList<StationChildHandle> list) {
        if (getController() != null) {
          list.bind();
          list.setStrategy(getPlaceholderStrategy());
        }
      }
    }, placeholders, children);
  }

  /**
	 * Creates a {@link ToolbarContainerConverter} which will be used for one
	 * call.
	 * 
	 * @return the new converter, not <code>null</code>
	 */
  protected ToolbarContainerConverter createConverter() {
    return new DefaultToolbarContainerConverter();
  }

  /**
	 * Gets the {@link PlaceholderStrategy} that is currently in use.
	 * 
	 * @return the current strategy, may be <code>null</code>
	 */
  public PlaceholderStrategy getPlaceholderStrategy() {
    return placeholderStrategy.getValue();
  }

  /**
	 * Sets the {@link PlaceholderStrategy} to use, <code>null</code> will set
	 * the default strategy.
	 * 
	 * @param strategy
	 *            the new strategy, can be <code>null</code>
	 */
  public void setPlaceholderStrategy(PlaceholderStrategy strategy) {
    placeholderStrategy.setValue(strategy);
  }

  @Override public DockableProperty getDockableProperty(Dockable child, Dockable target) {
    final int index = indexOf(child);
    Path placeholder = null;
    PlaceholderStrategy strategy = getPlaceholderStrategy();
    if (strategy != null) {
      if (target != null) {
        placeholder = strategy.getPlaceholderFor(target);
      } else {
        placeholder = strategy.getPlaceholderFor(child);
      }
      if (placeholder != null) {
        dockables.dockables().addPlaceholder(index, placeholder);
      }
    }
    return new ToolbarContainerProperty(index, placeholder);
  }

  @Override @Todo(compatibility = Compatibility.COMPATIBLE, priority = Priority.ENHANCEMENT, target = Version.VERSION_1_1_2, description = "Implement this feature") public void aside(AsideRequest request) {
  }

  @Override public StationDragOperation prepareDrag(Dockable dockable) {
    removal = dockable;
    getComponent().repaint();
    return new StationDragOperation() {
      @Override public void succeeded() {
        removal = null;
        getComponent().repaint();
      }

      @Override public void canceled() {
        removal = null;
        getComponent().repaint();
      }
    };
  }

  /**
	 * Sets the number of pixels outside the station where a drag and drop
	 * operation can still start.
	 * @param sideSnapSize the size in pixels
	 */
  public void setSideSnapSize(int sideSnapSize) {
    this.sideSnapSize = sideSnapSize;
  }

  /**
	 * Gets the number of pixels outside the station where a drag and drop 
	 * operation can still start
	 * @return the size in pixels
	 */
  public int getSideSnapSize() {
    return sideSnapSize;
  }

  @Override public DockStationDropLayer[] getLayers() {
    return new DockStationDropLayer[] { new DefaultDropLayer(this) {
      @Override public Component getComponent() {
        return ToolbarContainerDockStation.this.getComponent();
      }
    }, new ToolbarContainerDropLayer(this) };
  }

  @Override public StationDropOperation prepareDrop(StationDropItem item) {
    final DockController controller = getController();
    Dockable dockable = item.getDockable();
    if (this.accept(dockable) && dockable.accept(this)) {
      if (controller != null) {
        if (!controller.getAcceptance().accept(this, dockable)) {
          return null;
        }
      }
      if (!getToolbarStrategy().isToolbarPart(dockable)) {
        return null;
      }
      final ToolbarContainerDropInfo result = new ToolbarContainerDropInfo(dockable, this, dockables, item.getMouseX(), item.getMouseY()) {
        @Override public void execute() {
          drop(this);
        }

        @Override public void destroy(StationDropOperation next) {
          if (next == null || next.getTarget() != getTarget()) {
            layoutManager.setDrawing(null);
          }
          indexBeneathMouse = -1;
          sideAboveMouse = null;
          prepareDropDraw = false;
          mainPanel.repaint();
        }

        @Override public int getIndex() {
          return indexOf(getDockableBeneathMouse());
        }

        @Override public void draw() {
          boolean effect = true;
          indexBeneathMouse = getIndex();
          if (isMove()) {
            int target = moveIndex(this, indexBeneathMouse);
            int current = indexOf(getItem());
            effect = target != current && target != current - 1;
          }
          if (effect) {
            layoutManager.setDrawing(this);
            prepareDropDraw = true;
          } else {
            layoutManager.setDrawing(null);
            prepareDropDraw = false;
          }
          sideAboveMouse = getSideDockableBeneathMouse();
          mainPanel.repaint();
        }
      };
      return result;
    } else {
      return null;
    }
  }

  @Override public void addOrientingDockStationListener(OrientingDockStationListener listener) {
    orientingListeners.add(listener);
  }

  @Override public void removeOrientingDockStationListener(OrientingDockStationListener listener) {
    orientingListeners.remove(listener);
  }

  @Override public Orientation getOrientationOf(Dockable child) {
    return orientation;
  }

  /**
	 * Sets the number of dockables that this station will accept (max = -1
	 * indicates that there's no limit).
	 * 
	 * @param max
	 *            the number of dockables accepted
	 */
  public void setDockablesMaxNumber(int max) {
    dockablesMaxNumber = max;
  }

  /**
	 * Gets the number of dockables that this station will accept.
	 * 
	 * @return the number of dockables accepted (-1 if there is no maximum
	 *         number)
	 */
  public int getDockablesMaxNumber() {
    return dockablesMaxNumber;
  }

  /**
	 * Fires an {@link OrientingDockStationEvent}.
	 * 
	 * @param dockables
	 *            the items whose orientation changed
	 */
  protected void fireOrientingEvent() {
    final OrientingDockStationEvent event = new OrientingDockStationEvent(this);
    for (final OrientingDockStationListener listener : orientingListeners.toArray(new OrientingDockStationListener[orientingListeners.size()])) {
      listener.changed(event);
    }
  }

  private void drop(ToolbarContainerDropInfo dropInfo) {
    if (getDockables().dockables().size() == 0) {
      drop(dropInfo.getItem(), 0);
    }
    if (dropInfo.getItemPositionVSBeneathDockable() != Position.CENTER) {
      final int indexBeneathMouse = indexOf(dropInfo.getDockableBeneathMouse());
      int dropIndex;
      if (dropInfo.isMove()) {
        move(dropInfo.getItem(), moveIndex(dropInfo, indexBeneathMouse));
      } else {
        int increment = 0;
        if ((dropInfo.getSideDockableBeneathMouse() == Position.SOUTH) || (dropInfo.getSideDockableBeneathMouse() == Position.EAST)) {
          increment++;
        }
        dropIndex = indexBeneathMouse + increment;
        drop(dropInfo.getItem(), dropIndex);
      }
    }
  }

  private int moveIndex(ToolbarContainerDropInfo dropInfo, int indexBeneathMouse) {
    switch (getOrientation()) {
      case VERTICAL:
      if (dropInfo.getItemPositionVSBeneathDockable() == Position.SOUTH) {
        if (dropInfo.getSideDockableBeneathMouse() == Position.SOUTH) {
          return indexBeneathMouse + 1;
        } else {
          return indexBeneathMouse;
        }
      } else {
        if (dropInfo.getSideDockableBeneathMouse() == Position.SOUTH) {
          return indexBeneathMouse;
        } else {
          return indexBeneathMouse - 1;
        }
      }
      case HORIZONTAL:
      if (dropInfo.getItemPositionVSBeneathDockable() == Position.EAST) {
        if (dropInfo.getSideDockableBeneathMouse() == Position.EAST) {
          return indexBeneathMouse + 1;
        } else {
          return indexBeneathMouse;
        }
      } else {
        if (dropInfo.getSideDockableBeneathMouse() == Position.EAST) {
          return indexBeneathMouse;
        } else {
          return indexBeneathMouse - 1;
        }
      }
      default:
      throw new IllegalStateException("unknown orientation: " + getOrientation());
    }
  }

  @Override public boolean drop(Dockable dockable, DockableProperty property) {
    if (property instanceof ToolbarContainerProperty) {
      final ToolbarContainerProperty toolbar = (ToolbarContainerProperty) property;
      Path placeholder = toolbar.getPlaceholder();
      boolean hasPlaceholder = false;
      int index = -1;
      StationChildHandle presetHandle = null;
      if (placeholder != null) {
        hasPlaceholder = dockables.hasPlaceholder(placeholder);
        if (hasPlaceholder) {
          index = dockables.getDockableIndex(placeholder);
          presetHandle = dockables.getDockableAt(placeholder);
        }
      }
      if (index == -1) {
        index = toolbar.getIndex();
      }
      if (toolbar.getSuccessor() != null) {
        final DockablePlaceholderList<StationChildHandle> list = getDockables();
        Dockable preset = null;
        if (presetHandle != null) {
          preset = presetHandle.asDockable();
        } else {
          if (!hasPlaceholder && index >= 0 && index < list.dockables().size()) {
            preset = list.dockables().get(index).getDockable();
          }
        }
        if ((preset != null) && (preset.asDockStation() != null)) {
          return preset.asDockStation().drop(dockable, property.getSuccessor());
        }
      }
      final int max = getDockables().dockables().size();
      if (hasPlaceholder && presetHandle == null && toolbar.getSuccessor() != null) {
        Dockable replacement = getToolbarStrategy().ensureToolbarLayer(this, dockable);
        DockController controller = getController();
        if (controller != null) {
          controller.freezeLayout();
        }
        try {
          add(replacement, -1, placeholder);
          if (replacement != dockable) {
            if (!replacement.asDockStation().drop(dockable, toolbar.getSuccessor())) {
              replacement.asDockStation().drop(dockable);
            }
          }
          return true;
        }  finally {
          if (controller != null) {
            controller.meltLayout();
          }
        }
      } else {
        return drop(dockable, Math.max(0, Math.min(max, index)));
      }
    }
    return false;
  }

  /**
	 * Adds <code>dockable</code> to this station. The dockable must be a
	 * {@link ToolbarElementInterface} : if not, do nothing. The dockable is
	 * added at the last position.
	 * 
	 * @param dockable
	 *            a new child
	 */
  @Override public void drop(Dockable dockable) {
    this.drop(dockable, getDockables().dockables().size());
  }

  /**
	 * Inserts <code>dockable</code> to this station at the given index. The
	 * dockable must be a {@link ToolbarElementInterface}: if not, do nothing.
	 * 
	 * @param dockable
	 *            a new child
	 * @param index the group of the child  
	 * @return <code>true</code> if dropping was successfull
	 */
  private boolean drop(Dockable dockable, int index) {
    return add(dockable, index);
  }

  private void move(Dockable dockable, int indexWhereInsert) {
    if (getToolbarStrategy().isToolbarPart(dockable)) {
      final DockController controller = getController();
      try {
        if (controller != null) {
          controller.freezeLayout();
        }
        add(dockable, indexWhereInsert);
      }  finally {
        if (controller != null) {
          controller.meltLayout();
        }
      }
    }
  }

  @Override public void move(Dockable dockable, DockableProperty property) {
  }

  @Override public boolean canDrag(Dockable dockable) {
    return true;
  }

  @Override public void drag(Dockable dockable) {
    if (dockable.getDockParent() != this) {
      throw new IllegalArgumentException("The dockable cannot be dragged, it is not child of this station.");
    }
    remove(dockable);
  }

  @Override public boolean canReplace(Dockable old, Dockable next) {
    if (old.getClass() == next.getClass()) {
      return true;
    } else {
      return false;
    }
  }

  @Override public void replace(Dockable old, Dockable next) {
    DockUtilities.checkLayoutLocked();
    final DockController controller = getController();
    if (controller != null) {
      controller.freezeLayout();
    }
    final int index = indexOf(old);
    remove(old);
    add(next, index);
    controller.meltLayout();
  }

  @Override public void replace(DockStation old, Dockable next) {
    replace(old.asDockable(), next);
  }

  @Override public String getFactoryID() {
    return ToolbarContainerDockStationFactory.ID;
  }

  @Override public Component getComponent() {
    return mainPanel;
  }

  @Override protected void callDockUiUpdateTheme() throws IOException {
    DockUI.updateTheme(this, new ToolbarContainerDockStationFactory());
  }

  /**
	 * Gets the panel which contains dockables
	 * 
	 * @return the panel which contains dockables
	 */
  public JPanel getContainerPanel() {
    return containerPanel;
  }

  /**
	 * Gets the {@link ToolbarStrategy} that is currently used by this station.
	 * 
	 * @return the strategy, never <code>null</code>
	 */
  public ToolbarStrategy getToolbarStrategy() {
    final SilentPropertyValue<ToolbarStrategy> value = new SilentPropertyValue<ToolbarStrategy>(ToolbarStrategy.STRATEGY, getController());
    final ToolbarStrategy result = value.getValue();
    value.setProperties((DockController) null);
    return result;
  }

  @Override public boolean accept(Dockable child) {
    if (dockablesMaxNumber == -1) {
      return true;
    } else {
      if (dockables.dockables().size() >= dockablesMaxNumber) {
        return false;
      } else {
        return true;
      }
    }
  }

  @Override public boolean accept(DockStation station) {
    return true;
  }

  @Override public String toString() {
    return this.getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
  }

  /**
	 * Updates one of the lists containing dockables.
	 * 
	 * @param list
	 *            the new list
	 * @param bind
	 *            whether the new list should be
	 *            {@link DockablePlaceholderList#bind() bound}
	 */
  private void setDockables(DockablePlaceholderList<StationChildHandle> list, boolean bind) {
    if (getController() != null) {
      final DockablePlaceholderList<StationChildHandle> oldList = getDockables();
      oldList.setStrategy(null);
      oldList.unbind();
    }
    dockables = list;
    if ((getController() != null) && bind) {
      list.bind();
      list.setStrategy(getPlaceholderStrategy());
    }
  }

  /**
	 * Gets the dockables in the station
	 * 
	 * @return the dockables associated with the station
	 */
  public DockablePlaceholderList<StationChildHandle> getDockables() {
    return dockables;
  }

  /**
	 * Gets the orientation of dockables in the station
	 * 
	 * @return the orientation
	 */
  @Override public Orientation getOrientation() {
    return orientation;
  }

  /**
	 * Sets the orientation of dockables in the station
	 * 
	 * @param orientation
	 *            the orientation
	 */
  @Override public void setOrientation(Orientation orientation) {
    this.orientation = orientation;
    fireOrientingEvent();
  }

  /**
	 * Gets the index of a child.
	 * 
	 * @param dockable
	 *            the child which is searched
	 * @return the index of <code>dockable</code> or -1 if it was not found
	 */
  private int indexOf(Dockable dockable) {
    for (int i = 0; i < dockables.dockables().size(); i++) {
      if (dockables.dockables().get(i).getDockable() == dockable) {
        return i;
      }
    }
    return -1;
  }

  /**
	 * Removes the child with the given <code>index</code> from this station.<br>
	 * Note: clients may need to invoke {@link DockController#freezeLayout()}
	 * and {@link DockController#meltLayout()} to ensure noone else adds or
	 * removes <code>Dockable</code>s.
	 * 
	 * @param index
	 *            the index of the child that will be removed
	 */
  private void remove(Dockable dockable) {
    DockUtilities.checkLayoutLocked();
    final DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking(this, dockable);
    try {
      final int index = indexOf(dockable);
      final DockablePlaceholderList.Filter<StationChildHandle> dockables = getDockables().dockables();
      listeners.fireDockableRemoving(dockable);
      dockable.setDockParent(null);
      final StationChildHandle childHandle = dockables.get(index);
      getDockables().remove(index);
      getContainerPanel().remove(childHandle.getDisplayer().getComponent());
      childHandle.destroy();
      mainPanel.getContentPane().revalidate();
      mainPanel.getContentPane().repaint();
      listeners.fireDockableRemoved(dockable);
      fireDockablesRepositioned(index);
    }  finally {
      token.release();
    }
  }

  /**
	 * Add one dockable at the index position. The dockable can be a
	 * {@link ToolbarItemDockable}, {@link ToolbarDockStation} or a
	 * {@link ToolbarGroupDockStation} (see method accept()). All the
	 * ComponentDockable extracted from the element are merged together and
	 * wrapped in a {@link ToolbarDockStation} before to be added at index
	 * position
	 * 
	 * @param dockable
	 *            Dockable to add
	 * @param index
	 *            Index where add dockable
	 * @return <code>true</code> if dropping was successfull
	 */
  protected boolean add(Dockable dockable, int index) {
    return add(dockable, index, null);
  }

  protected boolean add(Dockable dockable, int index, Path placeholder) {
    DockUtilities.ensureTreeValidity(this, dockable);
    DockUtilities.checkLayoutLocked();
    final ToolbarStrategy strategy = getToolbarStrategy();
    if (strategy.isToolbarPart(dockable)) {
      Dockable replacement = strategy.ensureToolbarLayer(this, dockable);
      if (replacement != dockable) {
        replacement.setController(getController());
        replacement.asDockStation().drop(dockable);
        replacement.setController(null);
        dockable = replacement;
      }
      final DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking(this, dockable);
      try {
        listeners.fireDockableAdding(dockable);
        final DockablePlaceholderList.Filter<StationChildHandle> dockables = getDockables().dockables();
        final StationChildHandle handle = new StationChildHandle(this, displayer, dockable, title);
        if (placeholder != null) {
          index = getDockables().put(placeholder, handle);
        } else {
          dockables.add(index, handle);
        }
        handle.updateDisplayer();
        insertAt(handle, index);
        dockable.setDockParent(this);
        listeners.fireDockableAdded(dockable);
        fireDockablesRepositioned(index + 1);
      }  finally {
        token.release();
      }
      mainPanel.revalidate();
      mainPanel.repaint();
      return true;
    }
    return false;
  }

  private void insertAt(StationChildHandle handle, int index) {
    final Dockable dockable = handle.getDockable();
    dockable.setDockParent(this);
    getContainerPanel().add(handle.getDisplayer().getComponent(), index);
    mainPanel.getContentPane().revalidate();
    mainPanel.getContentPane().repaint();
  }

  /**
	 * Replaces <code>displayer</code> with a new {@link DockableDisplayer}.
	 * 
	 * @param displayer
	 *            the displayer to replace, must actually be shown on this
	 *            station
	 */
  protected void discard(DockableDisplayer displayer) {
    final int index = indexOf(displayer.getDockable());
    final StationChildHandle handle = getDockables().dockables().get(index);
    getContainerPanel().remove(displayer.getComponent());
    handle.updateDisplayer();
    insertAt(handle, index);
  }

  protected class OverpaintablePanelBase extends OverpaintablePanel {
    /**
		 * Creates a new panel
		 */
    public OverpaintablePanelBase() {
      setBackground(new Color(100, 100, 100));
      containerPanel = createPanel();
      setBasePane(containerPanel);
      setContentPane(containerPanel);
      setSolid(true);
      getContentPane().revalidate();
      getContentPane().repaint();
    }

    @Override protected void paintOverlay(Graphics g) {
      paintRemoval(g);
      final DefaultStationPaintValue paint = getPaint();
      Rectangle rectangleAreaBeneathMouse;
      if (prepareDropDraw) {
        if (indexBeneathMouse != -1) {
          final Rectangle rectComponentBeneathMouse = getDockables().dockables().get(indexBeneathMouse).getDisplayer().getComponent().getBounds();
          rectangleAreaBeneathMouse = getContainerPanel().getBounds();
          rectComponentBeneathMouse.translate(rectangleAreaBeneathMouse.x, rectangleAreaBeneathMouse.y);
          switch (getOrientation()) {
            case VERTICAL:
            int y;
            if (sideAboveMouse == Position.NORTH) {
              y = rectComponentBeneathMouse.y;
            } else {
              y = rectComponentBeneathMouse.y + rectComponentBeneathMouse.height;
            }
            paint.drawInsertionLine(g, rectComponentBeneathMouse.x, y, rectComponentBeneathMouse.x + rectComponentBeneathMouse.width, y);
            break;
            case HORIZONTAL:
            int x;
            if (sideAboveMouse == Position.WEST) {
              x = rectComponentBeneathMouse.x;
            } else {
              x = rectComponentBeneathMouse.x + rectComponentBeneathMouse.width;
            }
            paint.drawInsertionLine(g, x, rectComponentBeneathMouse.y, x, rectComponentBeneathMouse.y + rectComponentBeneathMouse.height);
          }
        } else {
          paint.drawDivider(g, getContainerPanel().getBounds());
        }
      }
    }

    private void paintRemoval(Graphics g) {
      if (removal != null) {
        for (StationChildHandle handle : dockables.dockables()) {
          if (handle.getDockable() == removal) {
            Rectangle bounds = handle.getDisplayer().getComponent().getBounds();
            getPaint().drawRemoval(g, bounds, bounds);
            break;
          }
        }
      }
    }

    @Override public String toString() {
      return this.getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
    }
  }

  /**
	 * Gets a {@link StationPaint} which is used to paint some lines onto this
	 * station. Use a {@link DefaultStationPaintValue#setDelegate(StationPaint)
	 * delegate} to exchange the paint.
	 * 
	 * @return the paint
	 */
  public DefaultStationPaintValue getPaint() {
    return paint;
  }

  @Override public void setDockParent(DockStation station) {
    DockStation old = getDockParent();
    if (old != null) {
      old.removeDockStationListener(visibleListener);
    }
    super.setDockParent(station);
    if (station != null) {
      station.addDockStationListener(visibleListener);
    }
    visibility.fire();
  }

  @Override public void setController(DockController controller) {
    if (getController() != controller) {
      if (getController() != null) {
        unbind(dockables);
      }
      super.setController(controller);
      paint.setController(controller);
      displayerFactory.setController(controller);
      layoutManager.setController(controller);
      background.setController(controller);
      if (controller == null) {
        title = null;
      } else {
        title = controller.getDockTitleManager().getVersion(TITLE_ID, BasicDockTitleFactory.FACTORY);
      }
      displayer.setController(controller);
      placeholderStrategy.setProperties(controller);
      if (controller != null) {
        bind(dockables, title);
      }
      visibility.fire();
    }
  }

  private void unbind(DockablePlaceholderList<StationChildHandle> list) {
    list.unbind();
    for (final StationChildHandle handle : list.dockables()) {
      handle.setTitleRequest(null);
    }
  }

  private void bind(DockablePlaceholderList<StationChildHandle> list, DockTitleVersion title) {
    list.bind();
    for (final StationChildHandle handle : list.dockables()) {
      handle.setTitleRequest(title, true);
    }
  }

  private class Background extends BackgroundAlgorithm implements StationBackgroundComponent {
    public Background() {
      super(StationBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".station.toolbar.container");
    }

    @Override public Component getComponent() {
      return ToolbarContainerDockStation.this.getComponent();
    }

    @Override public DockStation getStation() {
      return ToolbarContainerDockStation.this;
    }
  }

  private class VisibleListener extends DockStationAdapter {
    @Override public void dockableShowingChanged(DockStation station, Dockable dockable, boolean visible) {
      if (dockable == ToolbarContainerDockStation.this) {
        visibility.fire();
      }
    }
  }
}