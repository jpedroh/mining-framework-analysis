package bibliothek.gui.dock;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.HierarchyDockActionSource;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.control.relocator.Merger;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.displayer.DockableDisplayerHints;
import bibliothek.gui.dock.dockable.DockHierarchyObserver;
import bibliothek.gui.dock.dockable.DockableStateListener;
import bibliothek.gui.dock.dockable.DockableStateListenerManager;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.event.DoubleClickListener;
import bibliothek.gui.dock.event.SplitDockListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.security.SecureContainer;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockStationIcon;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.StationBackgroundComponent;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.split.DefaultSplitDividerStrategy;
import bibliothek.gui.dock.station.split.DefaultSplitLayoutManager;
import bibliothek.gui.dock.station.split.DockableSplitDockTree;
import bibliothek.gui.dock.station.split.Leaf;
import bibliothek.gui.dock.station.split.Node;
import bibliothek.gui.dock.station.split.Placeholder;
import bibliothek.gui.dock.station.split.PutInfo;
import bibliothek.gui.dock.station.split.PutInfo.Put;
import bibliothek.gui.dock.station.split.Root;
import bibliothek.gui.dock.station.split.SplitDividerStrategy;
import bibliothek.gui.dock.station.split.SplitDockAccess;
import bibliothek.gui.dock.station.split.SplitDockCombinerSource;
import bibliothek.gui.dock.station.split.SplitDockFullScreenProperty;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.station.split.SplitDockPathProperty;
import bibliothek.gui.dock.station.split.SplitDockPlaceholderProperty;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import bibliothek.gui.dock.station.split.SplitDockStationFactory;
import bibliothek.gui.dock.station.split.SplitDockTree;
import bibliothek.gui.dock.station.split.SplitDockTreeFactory;
import bibliothek.gui.dock.station.split.SplitDropTreeException;
import bibliothek.gui.dock.station.split.SplitFullScreenAction;
import bibliothek.gui.dock.station.split.SplitLayoutManager;
import bibliothek.gui.dock.station.split.SplitNode;
import bibliothek.gui.dock.station.split.SplitNodeVisitor;
import bibliothek.gui.dock.station.split.SplitPlaceholderConverter;
import bibliothek.gui.dock.station.split.SplitPlaceholderSet;
import bibliothek.gui.dock.station.split.SplitTreeFactory;
import bibliothek.gui.dock.station.split.layer.SideSnapDropLayer;
import bibliothek.gui.dock.station.split.layer.SplitOverrideDropLayer;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.station.support.DockStationListenerManager;
import bibliothek.gui.dock.station.support.DockableShowingManager;
import bibliothek.gui.dock.station.support.Enforcement;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.PlaceholderStrategyListener;
import bibliothek.gui.dock.station.support.RootPlaceholderStrategy;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.themes.StationCombinerValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.title.ActivityDockTitleEvent;
import bibliothek.gui.dock.title.ControllerTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.gui.dock.util.icon.DockIcon;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.util.Path;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;
import bibliothek.gui.dock.station.NoStationDropOperation;

public class SplitDockStation extends SecureContainer implements Dockable, DockStation {
  public static final String TITLE_ID = "split";

  public static final String DISPLAYER_ID = "split";

  public static final PropertyKey<KeyStroke> MAXIMIZE_ACCELERATOR = new PropertyKey<KeyStroke>("SplitDockStation maximize accelerator");

  public static final PropertyKey<SplitLayoutManager> LAYOUT_MANAGER = new PropertyKey<SplitLayoutManager>("SplitDockStation layout manager", new ConstantPropertyFactory<SplitLayoutManager>(new DefaultSplitLayoutManager()), true);

  public static final PropertyKey<SplitDividerStrategy> DIVIDER_STRATEGY = new PropertyKey<SplitDividerStrategy>("SplitDockStation divider strategy", new ConstantPropertyFactory<SplitDividerStrategy>(new DefaultSplitDividerStrategy()), true);

  private DockStation parent;

  private VisibleListener visibleListener = new VisibleListener();

  private DockController controller;

  private DockTheme theme;

  private StationCombinerValue combiner;

  private DockTitleVersion title;

  private List<DockableListener> dockableListeners = new ArrayList<DockableListener>();

  private DockableStateListenerManager dockableStateListeners;

  private DockHierarchyObserver hierarchyObserver;

  private List<SplitDockListener> splitListeners = new ArrayList<SplitDockListener>();

  private DockableShowingManager visibility;

  private List<DockTitle> titles = new LinkedList<DockTitle>();

  private HierarchyDockActionSource globalSource;

  protected DockStationListenerManager dockStationListeners = new DockStationListenerManager(this);

  private PropertyValue<String> titleText = new PropertyValue<String>(PropertyKey.DOCK_STATION_TITLE) {
    @Override protected void valueChanged(String oldValue, String newValue) {
      if (oldValue == null) {
        oldValue = "";
      }
      if (newValue == null) {
        newValue = "";
      }
      for (DockableListener listener : dockableListeners.toArray(new DockableListener[dockableListeners.size()])) {
        listener.titleTextChanged(SplitDockStation.this, oldValue, newValue);
      }
    }
  };

  private DockIcon titleIcon;

  private PropertyValue<String> titleToolTip = new PropertyValue<String>(PropertyKey.DOCK_STATION_TOOLTIP) {
    @Override protected void valueChanged(String oldValue, String newValue) {
      for (DockableListener listener : dockableListeners.toArray(new DockableListener[dockableListeners.size()])) {
        listener.titleToolTipChanged(SplitDockStation.this, oldValue, newValue);
      }
    }
  };

  private PropertyValue<SplitLayoutManager> layoutManager = new PropertyValue<SplitLayoutManager>(LAYOUT_MANAGER) {
    @Override protected void valueChanged(SplitLayoutManager oldValue, SplitLayoutManager newValue) {
      if (oldValue != null) {
        oldValue.uninstall(SplitDockStation.this);
      }
      if (newValue != null) {
        newValue.install(SplitDockStation.this);
      }
    }
  };

  private PropertyValue<SplitDividerStrategy> dividerStrategy = new PropertyValue<SplitDividerStrategy>(DIVIDER_STRATEGY) {
    @Override protected void valueChanged(SplitDividerStrategy oldValue, SplitDividerStrategy newValue) {
      if (oldValue != null) {
        oldValue.uninstall(SplitDockStation.this);
      }
      if (newValue != null && content != null) {
        newValue.install(SplitDockStation.this, getContentPane());
      }
    }
  };

  private PropertyValue<PlaceholderStrategy> placeholderStrategyProperty = new PropertyValue<PlaceholderStrategy>(PlaceholderStrategy.PLACEHOLDER_STRATEGY) {
    @Override protected void valueChanged(PlaceholderStrategy oldValue, PlaceholderStrategy newValue) {
      placeholderStrategy.setStrategy(newValue);
    }
  };

  private RootPlaceholderStrategy placeholderStrategy = new RootPlaceholderStrategy(this);

  private boolean expandOnDoubleclick = true;

  private FullScreenListener fullScreenListener = new FullScreenListener();

  private List<StationChildHandle> dockables = new ArrayList<StationChildHandle>();

  private Dockable frontDockable;

  private StationChildHandle fullScreenDockable;

  private ListeningDockAction fullScreenAction;

  private int dividerSize = 4;

  private float sideSnapSize = 1 / 4f;

  private int borderSideSnapSize = 25;

  private boolean allowSideSnap = true;

  private Access access = new Access();

  private Root root;

  private SplitPlaceholderSet placeholderSet;

  private int treeLock = 0;

  private PutInfo putInfo;

  private DefaultStationPaintValue paint;

  private DefaultDisplayerFactoryValue displayerFactory;

  private DisplayerCollection displayers;

  private boolean resizingEnabled = true;

  private boolean continousDisplay = false;

  private DockableDisplayerHints hints;

  private Content content;

  private Background background = new Background();

  public SplitDockStation() {
    this(true);
  }

  public SplitDockStation(boolean createFullScreenAction) {
    content = new Content();
    content.setBackground(background);
    setBasePane(content);
    hierarchyObserver = new DockHierarchyObserver(this);
    placeholderSet = new SplitPlaceholderSet(access);
    dockableStateListeners = new DockableStateListenerManager(this);
    paint = new DefaultStationPaintValue(ThemeManager.STATION_PAINT + ".split", this);
    combiner = new StationCombinerValue(ThemeManager.COMBINER + ".split", this);
    displayerFactory = new DefaultDisplayerFactoryValue(ThemeManager.DISPLAYER_FACTORY + ".split", this);
    displayers = new DisplayerCollection(this, displayerFactory, DISPLAYER_ID);
    displayers.addDockableDisplayerListener(new DockableDisplayerListener() {
      public void discard(DockableDisplayer displayer) {
        SplitDockStation.this.discard(displayer);
      }
    });
    if (createFullScreenAction) {
      fullScreenAction = createFullScreenAction();
    }
    visibility = new DockableShowingManager(dockStationListeners);
    dividerStrategy.getValue().install(this, getContentPane());
    globalSource = new HierarchyDockActionSource(this);
    globalSource.bind();
    titleIcon = new DockStationIcon("dockStation.default", this) {
      protected void changed(Icon oldValue, Icon newValue) {
        for (DockableListener listener : dockableListeners.toArray(new DockableListener[dockableListeners.size()])) {
          listener.titleIconChanged(SplitDockStation.this, oldValue, newValue);
        }
      }
    };
    addDockStationListener(new DockStationAdapter() {
      @Override public void dockableAdded(DockStation station, Dockable dockable) {
        updateConfigurableDisplayerHints();
      }

      @Override public void dockableRemoved(DockStation station, Dockable dockable) {
        updateConfigurableDisplayerHints();
      }
    });
    placeholderStrategy.addListener(new PlaceholderStrategyListener() {
      public void placeholderInvalidated(Set<Path> placeholders) {
        removePlaceholders(placeholders);
      }
    });
    addHierarchyListener(new HierarchyListener() {
      public void hierarchyChanged(HierarchyEvent e) {
        if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
          if (getDockParent() == null) {
            dockableStateListeners.checkShowing();
          }
          visibility.fire();
        }
      }
    });
  }

  protected Root createRoot(SplitDockAccess access) {
    return new Root(access);
  }

  protected final Root root() {
    if (root == null) {
      root = createRoot(access);
    }
    return root;
  }

  @Override public String toString() {
    if (root == null) {
      return super.toString();
    } else {
      return root.toString();
    }
  }

  @Override public Dimension getMinimumSize() {
    Insets insets = getInsets();
    Dimension base = getRoot().getMinimumSize();
    if (insets != null) {
      base = new Dimension(base.width + insets.left + insets.right, base.height + insets.top + insets.bottom);
    }
    return base;
  }

  public DockTheme getTheme() {
    return theme;
  }

  public void updateTheme() {
    DockController controller = getController();
    if (controller != null) {
      DockTheme newTheme = controller.getTheme();
      if (newTheme != theme) {
        theme = newTheme;
        try {
          callDockUiUpdateTheme();
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      }
    }
  }

  protected void callDockUiUpdateTheme() throws IOException {
    DockUI.updateTheme(this, new SplitDockStationFactory());
  }

  protected ListeningDockAction createFullScreenAction() {
    return new SplitFullScreenAction(this);
  }

  public void setFullScreenAction(ListeningDockAction fullScreenAction) {
    if (this.fullScreenAction != null) {
      throw new IllegalStateException("The fullScreenAction can only be set once");
    }
    this.fullScreenAction = fullScreenAction;
  }

  public void setExpandOnDoubleclick(boolean expandOnDoubleclick) {
    this.expandOnDoubleclick = expandOnDoubleclick;
  }

  public boolean isExpandOnDoubleclick() {
    return expandOnDoubleclick;
  }

  public void setResizingEnabled(boolean resizingEnabled) {
    this.resizingEnabled = resizingEnabled;
  }

  public boolean isResizingEnabled() {
    return resizingEnabled;
  }

  public void setDockParent(DockStation station) {
    if (this.parent != null) {
      this.parent.removeDockStationListener(visibleListener);
    }
    parent = station;
    if (station != null) {
      station.addDockStationListener(visibleListener);
    }
    hierarchyObserver.update();
  }

  public DockStation getDockParent() {
    return parent;
  }

  public void setController(DockController controller) {
    super.setController(controller);
    if (this.controller != controller) {
      if (this.controller != null) {
        this.controller.getDoubleClickController().removeListener(fullScreenListener);
      }
      for (StationChildHandle handle : dockables) {
        handle.setTitleRequest(null);
      }
      this.controller = controller;
      getDisplayers().setController(controller);
      if (fullScreenAction != null) {
        fullScreenAction.setController(controller);
      }
      titleIcon.setController(controller);
      titleText.setProperties(controller);
      layoutManager.setProperties(controller);
      placeholderStrategyProperty.setProperties(controller);
      paint.setController(controller);
      displayerFactory.setController(controller);
      combiner.setController(controller);
      background.setController(controller);
      dividerStrategy.setProperties(controller);
      if (controller != null) {
        title = controller.getDockTitleManager().getVersion(TITLE_ID, ControllerTitleFactory.INSTANCE);
        controller.getDoubleClickController().addListener(fullScreenListener);
      } else {
        title = null;
      }
      for (StationChildHandle handle : dockables) {
        handle.setTitleRequest(title);
      }
      hierarchyObserver.controllerChanged(controller);
      visibility.fire();
    }
  }

  @Override public DockController getController() {
    return controller;
  }

  public void addDockableListener(DockableListener listener) {
    dockableListeners.add(listener);
  }

  public void removeDockableListener(DockableListener listener) {
    dockableListeners.remove(listener);
  }

  public void addDockHierarchyListener(DockHierarchyListener listener) {
    hierarchyObserver.addDockHierarchyListener(listener);
  }

  public void removeDockHierarchyListener(DockHierarchyListener listener) {
    hierarchyObserver.removeDockHierarchyListener(listener);
  }

  public void addMouseInputListener(MouseInputListener listener) {
  }

  public void removeMouseInputListener(MouseInputListener listener) {
  }

  public boolean accept(DockStation station) {
    return true;
  }

  public boolean accept(DockStation base, Dockable neighbour) {
    return true;
  }

  public Component getComponent() {
    return this;
  }

  public DockElement getElement() {
    return this;
  }

  public boolean isUsedAsTitle() {
    return false;
  }

  public boolean shouldFocus() {
    return true;
  }

  public boolean shouldTransfersFocus() {
    return false;
  }

  public Point getPopupLocation(Point click, boolean popupTrigger) {
    if (popupTrigger) {
      return click;
    } else {
      return null;
    }
  }

  public String getTitleText() {
    String text = titleText.getValue();
    if (text == null) {
      return "";
    } else {
      return text;
    }
  }

  public void setTitleText(String titleText) {
    this.titleText.setValue(titleText);
  }

  public String getTitleToolTip() {
    return titleToolTip.getValue();
  }

  public void setTitleToolTip(String text) {
    titleToolTip.setValue(text);
  }

  public Icon getTitleIcon() {
    return titleIcon.value();
  }

  public void setTitleIcon(Icon titleIcon) {
    this.titleIcon.setValue(titleIcon, true);
  }

  public void resetTitleIcon() {
    this.titleIcon.setValue(null);
  }

  public void setSplitLayoutManager(SplitLayoutManager manager) {
    layoutManager.setValue(manager);
  }

  public SplitLayoutManager getSplitLayoutManager() {
    return layoutManager.getOwnValue();
  }

  public SplitLayoutManager getCurrentSplitLayoutManager() {
    return layoutManager.getValue();
  }

  public RootPlaceholderStrategy getPlaceholderStrategy() {
    return placeholderStrategy;
  }

  public void setPlaceholderStrategy(PlaceholderStrategy strategy) {
    placeholderStrategyProperty.setValue(strategy);
  }

  public void setSideSnapSize(float sideSnapSize) {
    if (sideSnapSize < 0) {
      throw new IllegalArgumentException("sideSnapSize must not be less than 0");
    }
    this.sideSnapSize = sideSnapSize;
  }

  public float getSideSnapSize() {
    return sideSnapSize;
  }

  public void setBorderSideSnapSize(int borderSideSnapSize) {
    if (borderSideSnapSize < 0) {
      throw new IllegalArgumentException("borderSideSnapeSize must not be less than 0");
    }
    this.borderSideSnapSize = borderSideSnapSize;
  }

  public int getBorderSideSnapSize() {
    return borderSideSnapSize;
  }

  public void setDividerSize(int dividerSize) {
    if (dividerSize < 0) {
      throw new IllegalArgumentException("dividerSize must not be less than 0");
    }
    this.dividerSize = dividerSize;
    doLayout();
  }

  public int getDividerSize() {
    return dividerSize;
  }

  public void setContinousDisplay(boolean continousDisplay) {
    this.continousDisplay = continousDisplay;
  }

  public boolean isContinousDisplay() {
    return continousDisplay;
  }

  public void setAllowSideSnap(boolean allowSideSnap) {
    this.allowSideSnap = allowSideSnap;
  }

  public boolean isAllowSideSnap() {
    return allowSideSnap;
  }

  public void requestDockTitle(DockTitleRequest request) {
  }

  public void requestDisplayer(DisplayerRequest request) {
  }

  public void changed(Dockable dockable, DockTitle title, boolean active) {
    title.changed(new ActivityDockTitleEvent(this, dockable, active));
  }

  public void requestChildDockTitle(DockTitleRequest request) {
  }

  public void requestChildDisplayer(DisplayerRequest request) {
  }

  public void bind(DockTitle title) {
    if (titles.contains(title)) {
      throw new IllegalArgumentException("Title is already bound");
    }
    titles.add(title);
    for (DockableListener listener : dockableListeners.toArray(new DockableListener[dockableListeners.size()])) {
      listener.titleBound(this, title);
    }
  }

  public void unbind(DockTitle title) {
    if (!titles.contains(title)) {
      throw new IllegalArgumentException("Title is unknown");
    }
    titles.remove(title);
    for (DockableListener listener : dockableListeners.toArray(new DockableListener[dockableListeners.size()])) {
      listener.titleUnbound(this, title);
    }
  }

  public DockTitle[] listBoundTitles() {
    return titles.toArray(new DockTitle[titles.size()]);
  }

  public DockActionSource getLocalActionOffers() {
    return null;
  }

  public DockActionSource getGlobalActionOffers() {
    return globalSource;
  }

  public void configureDisplayerHints(DockableDisplayerHints hints) {
    this.hints = hints;
    updateConfigurableDisplayerHints();
  }

  protected DockableDisplayerHints getConfigurableDisplayerHints() {
    return hints;
  }

  protected void updateConfigurableDisplayerHints() {
    if (hints != null) {
      if (getDockableCount() == 0) {
        hints.setShowBorderHint(Boolean.TRUE);
      } else {
        hints.setShowBorderHint(Boolean.FALSE);
      }
    }
  }

  public DockStation asDockStation() {
    return this;
  }

  public DefaultDockActionSource getDirectActionOffers(Dockable dockable) {
    if (fullScreenAction == null) {
      return null;
    } else {
      DefaultDockActionSource source = new DefaultDockActionSource(new LocationHint(LocationHint.DIRECT_ACTION, LocationHint.VERY_RIGHT));
      source.add(fullScreenAction);
      return source;
    }
  }

  public DockActionSource getIndirectActionOffers(Dockable dockable) {
    if (fullScreenAction == null) {
      return null;
    }
    DockStation parent = dockable.getDockParent();
    if (parent == null) {
      return null;
    }
    if (parent instanceof SplitDockStation) {
      return null;
    }
    dockable = parent.asDockable();
    if (dockable == null) {
      return null;
    }
    parent = dockable.getDockParent();
    if (parent != this) {
      return null;
    }
    DefaultDockActionSource source = new DefaultDockActionSource(fullScreenAction);
    source.setHint(new LocationHint(LocationHint.INDIRECT_ACTION, LocationHint.VERY_RIGHT));
    return source;
  }

  public void addDockStationListener(DockStationListener listener) {
    dockStationListeners.addListener(listener);
  }

  public void removeDockStationListener(DockStationListener listener) {
    dockStationListeners.removeListener(listener);
  }

  public void addDockableStateListener(DockableStateListener listener) {
    dockableStateListeners.addListener(listener);
  }

  public void removeDockableStateListener(DockableStateListener listener) {
    dockableStateListeners.removeListener(listener);
  }

  public void addSplitDockStationListener(SplitDockListener listener) {
    splitListeners.add(listener);
  }

  public void removeSplitDockStationListener(SplitDockListener listener) {
    splitListeners.remove(listener);
  }

  public boolean isChildShowing(Dockable dockable) {
    return isVisible(dockable);
  }

  @Deprecated @Todo(compatibility = Compatibility.BREAK_MAJOR, priority = Priority.ENHANCEMENT, target = Version.VERSION_1_1_3, description = "remove this method") public boolean isVisible(Dockable dockable) {
    return isStationVisible() && (!isFullScreen() || dockable == getFullScreen());
  }

  public boolean isStationShowing() {
    return isStationVisible();
  }

  @Deprecated @Todo(compatibility = Compatibility.BREAK_MAJOR, priority = Priority.ENHANCEMENT, target = Version.VERSION_1_1_3, description = "remove this method") public boolean isStationVisible() {
    return isDockableVisible();
  }

  public boolean isDockableShowing() {
    return isDockableVisible();
  }

  @Deprecated @Todo(compatibility = Compatibility.BREAK_MAJOR, priority = Priority.ENHANCEMENT, target = Version.VERSION_1_1_3, description = "remove this method") public boolean isDockableVisible() {
    DockController controller = getController();
    if (controller == null) {
      return false;
    }
    DockStation parent = getDockParent();
    if (parent != null) {
      return parent.isChildShowing(this);
    }
    return isShowing();
  }

  public int getDockableCount() {
    return dockables.size();
  }

  public Dockable getDockable(int index) {
    return dockables.get(index).getDockable();
  }

  public DockableProperty getDockableProperty(Dockable child, Dockable target) {
    DockableProperty result = getDockablePlaceholderProperty(child, target);
    if (result == null) {
      result = getDockablePathProperty(child);
    }
    return result;
  }

  public SplitDockPathProperty getDockablePathProperty(final Dockable dockable) {
    final SplitDockPathProperty path = new SplitDockPathProperty();
    root().submit(new SplitTreeFactory<Object>() {
      public Object leaf(Dockable check, long id, Path[] placeholders, PlaceholderMap placeholderMap) {
        if (dockable == check) {
          path.setLeafId(id);
          return this;
        }
        return null;
      }

      public Object placeholder(long id, Path[] placeholders, PlaceholderMap placeholderMap) {
        return null;
      }

      public Object root(Object root, long id) {
        return root;
      }

      public Object horizontal(Object left, Object right, double divider, long id, Path[] placeholders, PlaceholderMap placeholderMap, boolean visible) {
        if (left != null) {
          if (visible) {
            path.insert(SplitDockPathProperty.Location.LEFT, divider, 0, id);
          }
          return left;
        }
        if (right != null) {
          if (visible) {
            path.insert(SplitDockPathProperty.Location.RIGHT, 1 - divider, 0, id);
          }
          return right;
        }
        return null;
      }

      public Object vertical(Object top, Object bottom, double divider, long id, Path[] placeholders, PlaceholderMap placeholderMap, boolean visible) {
        if (top != null) {
          if (visible) {
            path.insert(SplitDockPathProperty.Location.TOP, divider, 0, id);
          }
          return top;
        }
        if (bottom != null) {
          if (visible) {
            path.insert(SplitDockPathProperty.Location.BOTTOM, 1 - divider, 0, id);
          }
          return bottom;
        }
        return null;
      }
    });
    return path;
  }

  public SplitDockProperty getDockableLocationProperty(Dockable dockable) {
    Leaf leaf = getRoot().getLeaf(dockable);
    return new SplitDockProperty(leaf.getX(), leaf.getY(), leaf.getWidth(), leaf.getHeight());
  }

  public SplitDockPlaceholderProperty getDockablePlaceholderProperty(Dockable dockable, Dockable target) {
    Leaf leaf = getRoot().getLeaf(dockable);
    if (leaf == null) {
      throw new IllegalArgumentException("dockable not known to this station");
    }
    Path placeholder = getPlaceholderStrategy().getPlaceholderFor(target == null ? dockable : target);
    if (placeholder == null) {
      return null;
    }
    placeholderSet.set(leaf, placeholder);
    return new SplitDockPlaceholderProperty(placeholder, getDockablePathProperty(dockable));
  }

  public Dockable getFrontDockable() {
    if (isFullScreen()) {
      return getFullScreen();
    }
    if (frontDockable == null && dockables.size() > 0) {
      frontDockable = dockables.get(0).getDockable();
    }
    return frontDockable;
  }

  public void setFrontDockable(Dockable dockable) {
    Dockable old = getFrontDockable();
    this.frontDockable = dockable;
    if (isFullScreen() && dockable != null) {
      setFullScreen(dockable);
    }
    if (old != dockable) {
      access.dockableSelected(old);
    }
  }

  public boolean isFullScreen() {
    return fullScreenDockable != null;
  }

  public Dockable getFullScreen() {
    return fullScreenDockable == null ? null : fullScreenDockable.getDockable();
  }

  public boolean hasFullScreenAction() {
    return fullScreenAction != null;
  }

  public void setFullScreen(Dockable dockable) {
    try {
      access.arm();
      dockable = layoutManager.getValue().willMakeFullscreen(this, dockable);
      Dockable oldFullScreen = getFullScreen();
      if (oldFullScreen != dockable) {
        if (dockable != null) {
          access.repositioned.add(dockable);
          Leaf leaf = getRoot().getLeaf(dockable);
          if (leaf == null) {
            throw new IllegalArgumentException("Dockable not child of this station");
          }
          fullScreenDockable = leaf.getDockableHandle();
          updateVisibility();
        } else {
          fullScreenDockable = null;
          updateVisibility();
        }
        if (oldFullScreen != null) {
          access.repositioned.add(oldFullScreen);
        }
        revalidate();
        fireFullScreenChanged(oldFullScreen, getFullScreen());
        visibility.fire();
      }
    }  finally {
      access.fire();
    }
  }

  protected void updateVisibility() {
    StationChildHandle fullscreenHandle = fullScreenDockable;
    if (fullscreenHandle == null) {
      for (StationChildHandle handle : dockables) {
        handle.getDisplayer().getComponent().setVisible(true);
      }
    } else {
      for (StationChildHandle handle : dockables) {
        handle.getDisplayer().getComponent().setVisible(handle == fullscreenHandle);
      }
    }
  }

  public void setNextFullScreen() {
    if (dockables.size() > 0) {
      if (fullScreenDockable == null) {
        setFullScreen(getDockable(0));
      } else {
        int index = indexOfDockable(fullScreenDockable.getDockable());
        index++;
        index %= getDockableCount();
        setFullScreen(getDockable(index));
      }
    }
  }

  public boolean accept(Dockable child) {
    return true;
  }

  public PlaceholderMap getPlaceholders() {
    return createPlaceholderConverter().getPlaceholders();
  }

  public void setPlaceholders(PlaceholderMap placeholders) {
    createPlaceholderConverter().setPlaceholders(placeholders);
  }

  protected SplitPlaceholderConverter createPlaceholderConverter() {
    return new SplitPlaceholderConverter(this);
  }

  public DockStationDropLayer[] getLayers() {
    return new DockStationDropLayer[] { new DefaultDropLayer(this), new SplitOverrideDropLayer(this), new SideSnapDropLayer(this) };
  }

  public StationDropOperation prepareDrop(int x, int y, int titleX, int titleY, Dockable dockable) {
    PutInfo putInfo = null;
    boolean move = dockable.getDockParent() == this;
    if (move) {
      putInfo = layoutManager.getValue().prepareMove(this, x, y, titleX, titleY, dockable);
      if (putInfo != null) {
        if (putInfo.getNode() == null) {
          return new NoStationDropOperation(this, dockable);
        }
        prepareCombine(putInfo, x, y, move);
      }
    } else {
      if (SwingUtilities.isDescendingFrom(getComponent(), dockable.getComponent())) {
        putInfo = null;
      } else {
        putInfo = layoutManager.getValue().prepareDrop(this, x, y, titleX, titleY, dockable);
      }
      if (putInfo != null) {
        prepareCombine(putInfo, x, y, move);
      }
    }
    if (putInfo == null) {
      return null;
    }
    return new SplitDropOperation(putInfo, move);
  }

  public PutInfo getDropInfo() {
    return putInfo;
  }

  private void prepareCombine(PutInfo putInfo, int x, int y, boolean move) {
    if (putInfo.getCombinerSource() == null && putInfo.getCombinerTarget() == null) {
      if (putInfo.getNode() instanceof Leaf) {
        Point mouseOnStation = new Point(x, y);
        SwingUtilities.convertPointFromScreen(mouseOnStation, getComponent());
        SplitDockCombinerSource source = new SplitDockCombinerSource(putInfo, this, mouseOnStation);
        Enforcement force;
        if (putInfo.getPut() == PutInfo.Put.CENTER) {
          force = Enforcement.EXPECTED;
        } else {
          if (putInfo.getPut() == PutInfo.Put.TITLE) {
            force = Enforcement.HARD;
          } else {
            force = Enforcement.WHISHED;
          }
        }
        CombinerTarget target = getCombiner().prepare(source, force);
        if (target == null && putInfo.isCombining() && putInfo.getDockable().asDockStation() != null) {
          DockController controller = getController();
          if (controller != null) {
            Merger merger = controller.getRelocator().getMerger();
            target = getCombiner().prepare(source, Enforcement.HARD);
            putInfo.setCombination(source, target);
            if (!merger.canMerge(new SplitDropOperation(putInfo, move), this, putInfo.getDockable().asDockStation())) {
              putInfo.setCombination(null, null);
            }
          }
        }
        putInfo.setCombination(source, target);
      }
    }
  }

  public void drop(Dockable dockable) {
    addDockable(dockable, null);
  }

  public boolean drop(Dockable dockable, DockableProperty property) {
    if (property instanceof SplitDockProperty) {
      return drop(dockable, (SplitDockProperty) property);
    } else {
      if (property instanceof SplitDockPathProperty) {
        return drop(dockable, (SplitDockPathProperty) property);
      } else {
        if (property instanceof SplitDockPlaceholderProperty) {
          return drop(dockable, (SplitDockPlaceholderProperty) property);
        } else {
          if (property instanceof SplitDockFullScreenProperty) {
            return drop(dockable, (SplitDockFullScreenProperty) property);
          } else {
            return false;
          }
        }
      }
    }
  }

  public boolean drop(Dockable dockable, SplitDockProperty property) {
    return drop(dockable, property, root());
  }

  private boolean drop(final Dockable dockable, final SplitDockProperty property, SplitNode root) {
    try {
      access.arm();
      DockUtilities.checkLayoutLocked();
      if (getDockableCount() == 0) {
        if (!DockUtilities.acceptable(this, dockable)) {
          return false;
        }
        drop(dockable);
        return true;
      }
      updateBounds();
      class DropInfo {
        public Leaf bestLeaf;

        public double bestLeafIntersection;

        public SplitNode bestNode;

        public double bestNodeIntersection = Double.POSITIVE_INFINITY;

        public PutInfo.Put bestNodePut;
      }
      final DropInfo info = new DropInfo();
      root.visit(new SplitNodeVisitor() {
        public void handleLeaf(Leaf leaf) {
          double intersection = leaf.intersection(property);
          if (intersection > info.bestLeafIntersection) {
            info.bestLeafIntersection = intersection;
            info.bestLeaf = leaf;
          }
          handleNeighbour(leaf);
        }

        public void handleNode(Node node) {
          if (node.isVisible()) {
            handleNeighbour(node);
          }
        }

        public void handleRoot(Root root) {
        }

        public void handlePlaceholder(Placeholder placeholder) {
        }

        private void handleNeighbour(SplitNode node) {
          if (DockUtilities.acceptable(SplitDockStation.this, dockable)) {
            double x = node.getX();
            double y = node.getY();
            double width = node.getWidth();
            double height = node.getHeight();
            double left = Math.abs(x - property.getX());
            double right = Math.abs(x + width - property.getX() - property.getWidth());
            double top = Math.abs(y - property.getY());
            double bottom = Math.abs(y + height - property.getY() - property.getHeight());
            double value = left + right + top + bottom;
            value -= Math.max(Math.max(left, right), Math.max(top, bottom));
            double kx = property.getX() + property.getWidth() / 2;
            double ky = property.getY() + property.getHeight() / 2;
            PutInfo.Put put = node.relativeSidePut(kx, ky);
            double px, py;
            if (put == PutInfo.Put.TOP) {
              px = x + 0.5 * width;
              py = y + 0.25 * height;
            } else {
              if (put == PutInfo.Put.BOTTOM) {
                px = x + 0.5 * width;
                py = y + 0.75 * height;
              } else {
                if (put == PutInfo.Put.LEFT) {
                  px = x + 0.25 * width;
                  py = y + 0.5 * height;
                } else {
                  px = x + 0.5 * width;
                  py = y + 0.75 * height;
                }
              }
            }
            double distance = Math.pow((kx - px) * (kx - px) + (ky - py) * (ky - py), 0.25);
            value *= distance;
            if (value < info.bestNodeIntersection) {
              info.bestNodeIntersection = value;
              info.bestNode = node;
              info.bestNodePut = put;
            }
          }
        }
      });
      if (info.bestLeaf != null) {
        DockStation station = info.bestLeaf.getDockable().asDockStation();
        DockableProperty successor = property.getSuccessor();
        if (station != null && successor != null) {
          if (station.drop(dockable, successor)) {
            validate();
            return true;
          }
        }
        if (info.bestLeafIntersection > 0.75) {
          if (station != null && DockUtilities.acceptable(station, dockable)) {
            station.drop(dockable);
            validate();
            return true;
          } else {
            boolean result = dropOver(info.bestLeaf, dockable, property.getSuccessor(), null, null);
            validate();
            return result;
          }
        }
      }
      if (info.bestNode != null) {
        if (!DockUtilities.acceptable(this, dockable)) {
          return false;
        }
        double divider = 0.5;
        if (info.bestNodePut == PutInfo.Put.LEFT) {
          divider = property.getWidth() / info.bestNode.getWidth();
        } else {
          if (info.bestNodePut == PutInfo.Put.RIGHT) {
            divider = 1 - property.getWidth() / info.bestNode.getWidth();
          } else {
            if (info.bestNodePut == PutInfo.Put.TOP) {
              divider = property.getHeight() / info.bestNode.getHeight();
            } else {
              if (info.bestNodePut == PutInfo.Put.BOTTOM) {
                divider = 1 - property.getHeight() / info.bestNode.getHeight();
              }
            }
          }
        }
        divider = Math.max(0, Math.min(1, divider));
        return dropAside(info.bestNode, info.bestNodePut, dockable, null, divider, null);
      }
      repaint();
      return false;
    }  finally {
      access.fire();
    }
  }

  public boolean drop(Dockable dockable, SplitDockPathProperty property) {
    try {
      access.arm();
      DockUtilities.checkLayoutLocked();
      int index = 0;
      SplitNode start = null;
      long leafId = property.getLeafId();
      if (leafId != -1) {
        start = getNode(leafId);
        if (start != null) {
          index = property.size();
        }
      }
      if (start == null) {
        for (index = property.size() - 1; index >= 0; index--) {
          SplitDockPathProperty.Node node = property.getNode(index);
          long id = node.getId();
          if (id != -1) {
            start = getNode(id);
            if (start != null) {
              break;
            }
          }
        }
      }
      if (start == null || index < 0) {
        start = root();
        index = 0;
      }
      updateBounds();
      boolean done = start.insert(property, index, dockable);
      if (done) {
        revalidate();
      }
      return done;
    }  finally {
      access.fire();
    }
  }

  public boolean drop(Dockable dockable, SplitDockPlaceholderProperty property) {
    try {
      access.arm();
      DockUtilities.checkLayoutLocked();
      validate();
      return root().insert(property, dockable);
    }  finally {
      access.fire();
    }
  }

  public boolean drop(Dockable dockable, SplitDockFullScreenProperty property) {
    try {
      access.arm();
      DockUtilities.checkLayoutLocked();
      DockableProperty successor = property.getSuccessor();
      if (dockable.getDockParent() == this) {
        setFullScreen(dockable);
        return true;
      }
      Dockable currentFullScreen = getFullScreen();
      if (currentFullScreen == null) {
        return false;
      }
      DockStation currentFullScreenStation = currentFullScreen.asDockStation();
      if (currentFullScreenStation != null) {
        if (successor != null) {
          if (currentFullScreenStation.drop(dockable, successor)) {
            return true;
          }
        }
        return false;
      } else {
        Leaf leaf = getRoot().getLeaf(currentFullScreen);
        setFullScreen(null);
        if (!dropOver(leaf, dockable, successor, null, null)) {
          return false;
        }
        Dockable last = dockable;
        while (dockable != null && dockable != this) {
          last = dockable;
          DockStation station = dockable.getDockParent();
          dockable = station == null ? null : station.asDockable();
        }
        if (last != null) {
          setFullScreen(last);
        }
        return true;
      }
    }  finally {
      access.fire();
    }
  }

  protected boolean dropOver(Leaf leaf, Dockable dockable, CombinerSource source, CombinerTarget target) {
    return dropOver(leaf, dockable, null, source, target);
  }

  protected boolean dropOver(Leaf leaf, Dockable dockable, DockableProperty property, CombinerSource source, CombinerTarget target) {
    if (!DockUtilities.acceptable(this, leaf.getDockable(), dockable)) {
      return false;
    }
    try {
      access.arm();
      DockUtilities.checkLayoutLocked();
      DockUtilities.ensureTreeValidity(this, dockable);
      if (source == null || target == null) {
        PutInfo info = new PutInfo(leaf, Put.TITLE, dockable, true);
        source = new SplitDockCombinerSource(info, this, null);
        target = combiner.prepare(source, Enforcement.HARD);
      }
      if (leaf.getDockable() != null) {
        Dockable oldDockable = leaf.getDockable();
        DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking(this, oldDockable);
        try {
          dockStationListeners.fireDockableRemoving(oldDockable);
          leaf.setDockable(null, token);
          dockStationListeners.fireDockableRemoved(oldDockable);
        }  finally {
          token.release();
        }
      }
      Dockable combination = combiner.combine(source, target);
      leaf.setPlaceholderMap(null);
      if (property != null) {
        DockStation combinedStation = combination.asDockStation();
        if (combinedStation != null && dockable.getDockParent() == combinedStation) {
          combinedStation.move(dockable, property);
        }
      }
      DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking(this, combination);
      try {
        dockStationListeners.fireDockableAdding(combination);
        leaf.setDockable(combination, token);
        dockStationListeners.fireDockableAdded(combination);
      }  finally {
        token.release();
      }
      revalidate();
      repaint();
      return true;
    }  finally {
      access.fire();
    }
  }

  protected boolean dropAside(SplitNode neighbor, PutInfo.Put put, Dockable dockable, Leaf leaf, double divider, DockHierarchyLock.Token token) {
    if (!DockUtilities.acceptable(this, dockable)) {
      return false;
    }
    try {
      boolean fire = token == null;
      access.arm();
      DockUtilities.checkLayoutLocked();
      if (fire) {
        DockUtilities.ensureTreeValidity(this, dockable);
        token = DockHierarchyLock.acquireLinking(this, dockable);
      }
      try {
        if (fire) {
          dockStationListeners.fireDockableAdding(dockable);
        }
        boolean leafSet = false;
        if (leaf == null) {
          leaf = new Leaf(access);
          leafSet = true;
        }
        SplitNode parent = neighbor.getParent();
        Node node = null;
        updateBounds();
        int location = parent.getChildLocation(neighbor);
        if (put == PutInfo.Put.TOP) {
          node = new Node(access, leaf, neighbor, Orientation.VERTICAL);
        } else {
          if (put == PutInfo.Put.BOTTOM) {
            node = new Node(access, neighbor, leaf, Orientation.VERTICAL);
          } else {
            if (put == PutInfo.Put.LEFT) {
              node = new Node(access, leaf, neighbor, Orientation.HORIZONTAL);
            } else {
              node = new Node(access, neighbor, leaf, Orientation.HORIZONTAL);
            }
          }
        }
        node.setDivider(divider);
        parent.setChild(node, location);
        if (leafSet) {
          leaf.setDockable(dockable, token);
        }
        if (fire) {
          dockStationListeners.fireDockableAdded(dockable);
        }
        revalidate();
        repaint();
      }  finally {
        if (fire) {
          token.release();
        }
      }
    }  finally {
      access.fire();
    }
    return true;
  }

  public void move(Dockable dockable, DockableProperty property) {
  }

  public void dropGrid(SplitDockGrid grid) {
    dropTree(grid.toTree());
  }

  public void dropTree(SplitDockTree<Dockable> tree) {
    dropTree(tree, true);
  }

  public void dropTree(SplitDockTree<Dockable> tree, boolean checkValidity) {
    if (tree == null) {
      throw new IllegalArgumentException("Tree must not be null");
    }
    DockUtilities.checkLayoutLocked();
    DockController controller = getController();
    try {
      access.arm();
      treeLock++;
      if (controller != null) {
        controller.freezeLayout();
      }
      setFullScreen(null);
      removeAllDockables();
      for (Dockable dockable : tree.getDockables()) {
        DockUtilities.ensureTreeValidity(this, dockable);
      }
      SplitDockTree<Dockable>.Key rootKey = tree.getRoot();
      if (rootKey != null) {
        Map<Leaf, Dockable> linksToSet = new HashMap<Leaf, Dockable>();
        root().evolve(rootKey, checkValidity, linksToSet);
        for (Map.Entry<Leaf, Dockable> entry : linksToSet.entrySet()) {
          entry.getKey().setDockable(entry.getValue(), null);
        }
        updateBounds();
      }
    }  finally {
      treeLock--;
      if (controller != null) {
        controller.meltLayout();
      }
      access.fire();
    }
  }

  public DockableSplitDockTree createTree() {
    DockableSplitDockTree tree = new DockableSplitDockTree();
    createTree(new SplitDockTreeFactory(tree));
    return tree;
  }

  public void createTree(SplitDockTreeFactory factory) {
    root().submit(factory);
  }

  public <N extends java.lang.Object> N visit(SplitTreeFactory<N> factory) {
    return root().submit(factory);
  }

  public boolean canDrag(Dockable dockable) {
    return true;
  }

  public void drag(Dockable dockable) {
    if (dockable.getDockParent() != this) {
      throw new IllegalArgumentException("The dockable cannot be dragged, it is not child of this station.");
    }
    removeDockable(dockable);
  }

  protected void fireFullScreenChanged(Dockable oldDockable, Dockable newDockable) {
    for (SplitDockListener listener : splitListeners.toArray(new SplitDockListener[splitListeners.size()])) {
      listener.fullScreenDockableChanged(this, oldDockable, newDockable);
    }
  }

  protected void fireTitleExchanged(DockTitle title) {
    for (DockableListener listener : dockableListeners.toArray(new DockableListener[dockableListeners.size()])) {
      listener.titleExchanged(this, title);
    }
  }

  protected void fireTitleExchanged() {
    DockTitle[] bound = listBoundTitles();
    for (DockTitle title : bound) {
      fireTitleExchanged(title);
    }
    fireTitleExchanged(null);
  }

  public Dockable asDockable() {
    return this;
  }

  public DefaultStationPaintValue getPaint() {
    return paint;
  }

  public DefaultDisplayerFactoryValue getDisplayerFactory() {
    return displayerFactory;
  }

  public DisplayerCollection getDisplayers() {
    return displayers;
  }

  public StationCombinerValue getCombiner() {
    return combiner;
  }

  @Override protected void paintOverlay(Graphics g) {
    if (putInfo != null) {
      DefaultStationPaintValue paint = getPaint();
      if (putInfo.getNode() == null) {
        Rectangle bounds = new Rectangle(0, 0, getWidth(), getHeight());
        paint.drawInsertion(g, bounds, bounds);
      } else {
        CombinerTarget target = putInfo.getCombinerTarget();
        if (target == null) {
          Rectangle bounds = putInfo.getNode().getBounds();
          if (putInfo.getPut() == PutInfo.Put.LEFT) {
            bounds.width = (int) (bounds.width * putInfo.getDivider() + 0.5);
          } else {
            if (putInfo.getPut() == PutInfo.Put.RIGHT) {
              int width = bounds.width;
              bounds.width = (int) (bounds.width * (1 - putInfo.getDivider()) + 0.5);
              bounds.x += width - bounds.width;
            } else {
              if (putInfo.getPut() == PutInfo.Put.TOP) {
                bounds.height = (int) (bounds.height * putInfo.getDivider() + 0.5);
              } else {
                if (putInfo.getPut() == PutInfo.Put.BOTTOM) {
                  int height = bounds.height;
                  bounds.height = (int) (bounds.height * (1 - putInfo.getDivider()) + 0.5);
                  bounds.y += height - bounds.height;
                }
              }
            }
          }
          paint.drawInsertion(g, putInfo.getNode().getBounds(), bounds);
        } else {
          Rectangle bounds = putInfo.getNode().getBounds();
          StationPaint stationPaint = paint.get();
          if (stationPaint != null) {
            target.paint(g, getComponent(), stationPaint, bounds, bounds);
          }
        }
      }
    }
    dividerStrategy.getValue().paint(this, g);
  }

  public void addDockable(Dockable dockable) {
    addDockable(dockable, null);
  }

  private void addDockable(Dockable dockable, DockHierarchyLock.Token token) {
    try {
      boolean fire = token == null;
      access.arm();
      DockUtilities.checkLayoutLocked();
      if (fire) {
        DockUtilities.ensureTreeValidity(this, dockable);
        token = DockHierarchyLock.acquireLinking(this, dockable);
      }
      try {
        if (fire) {
          dockStationListeners.fireDockableAdding(dockable);
        }
        Leaf leaf = new Leaf(access);
        Root root = root();
        if (root.getChild() == null) {
          root.setChild(leaf);
        } else {
          SplitNode child = root.getChild();
          root.setChild(null);
          Node node = new Node(access, leaf, child);
          root.setChild(node);
        }
        leaf.setDockable(dockable, token);
        if (fire) {
          dockStationListeners.fireDockableAdded(dockable);
        }
        revalidate();
      }  finally {
        if (fire) {
          token.release();
        }
      }
    }  finally {
      access.fire();
    }
  }

  public boolean canReplace(Dockable old, Dockable next) {
    return true;
  }

  public void replace(DockStation old, Dockable next) {
    replace(old.asDockable(), next, true);
  }

  public void replace(Dockable previous, Dockable next) {
    replace(previous, next, false);
  }

  private void replace(Dockable previous, Dockable next, boolean station) {
    try {
      access.arm();
      DockUtilities.checkLayoutLocked();
      if (previous == null) {
        throw new NullPointerException("previous must not be null");
      }
      if (next == null) {
        throw new NullPointerException("next must not be null");
      }
      if (previous != next) {
        Leaf leaf = root().getLeaf(previous);
        if (leaf == null) {
          throw new IllegalArgumentException("Previous is not child of this station");
        }
        DockUtilities.ensureTreeValidity(this, next);
        boolean wasFullScreen = isFullScreen() && getFullScreen() == previous;
        leaf.setDockable(next, null, true, station);
        if (wasFullScreen) {
          setFullScreen(next);
        }
        revalidate();
        repaint();
      }
    }  finally {
      access.fire();
    }
  }

  private void addHandle(StationChildHandle handle, DockHierarchyLock.Token token) {
    Dockable dockable = handle.getDockable();
    DockUtilities.ensureTreeValidity(this, dockable);
    boolean fire = token == null;
    if (fire) {
      token = DockHierarchyLock.acquireLinking(this, dockable);
    }
    try {
      if (fire) {
        dockStationListeners.fireDockableAdding(dockable);
      }
      dockables.add(handle);
      dockable.setDockParent(this);
      handle.updateDisplayer();
      DockableDisplayer displayer = handle.getDisplayer();
      getContentPane().add(displayer.getComponent());
      displayer.getComponent().setVisible(!isFullScreen());
      if (fire) {
        dockStationListeners.fireDockableAdded(dockable);
      }
    }  finally {
      if (fire) {
        token.release();
      }
    }
  }

  protected void discard(DockableDisplayer displayer) {
    int index = indexOfDockable(displayer.getDockable());
    if (index < 0) {
      throw new IllegalArgumentException("displayer unknown to this station: " + displayer);
    }
    Dockable dockable = displayer.getDockable();
    boolean visible = displayer.getComponent().isVisible();
    Leaf leaf = root().getLeaf(dockable);
    getContentPane().remove(displayer.getComponent());
    StationChildHandle handle = leaf.getDockableHandle();
    handle.updateDisplayer();
    displayer = handle.getDisplayer();
    getContentPane().add(displayer.getComponent());
    displayer.getComponent().setVisible(visible);
    revalidate();
  }

  public int indexOfDockable(Dockable dockable) {
    for (int i = 0, n = dockables.size(); i < n; i++) {
      if (dockables.get(i).getDockable() == dockable) {
        return i;
      }
    }
    return -1;
  }

  public void removeAllDockables() {
    DockController controller = getController();
    try {
      access.arm();
      DockUtilities.checkLayoutLocked();
      if (controller != null) {
        controller.freezeLayout();
      }
      for (int i = getDockableCount() - 1; i >= 0; i--) {
        removeDisplayer(i, null);
      }
      root().setChild(null);
    }  finally {
      if (controller != null) {
        controller.meltLayout();
      }
      access.fire();
    }
  }

  public void removeDockable(Dockable dockable) {
    try {
      access.arm();
      DockUtilities.checkLayoutLocked();
      Leaf leaf = root().getLeaf(dockable);
      if (leaf != null) {
        leaf.setDockable(null, null, true, dockable.asDockStation() != null);
        leaf.placehold(true);
      }
    }  finally {
      access.fire();
    }
  }

  public void removePlaceholder(Path placeholder) {
    Set<Path> placeholders = new HashSet<Path>();
    placeholders.add(placeholder);
    removePlaceholders(placeholders);
  }

  public void removePlaceholders(final Set<Path> placeholders) {
    if (placeholders.isEmpty()) {
      return;
    }
    final List<SplitNode> nodesToDelete = new ArrayList<SplitNode>();
    root().visit(new SplitNodeVisitor() {
      public void handleRoot(Root root) {
        handle(root);
      }

      public void handlePlaceholder(Placeholder placeholder) {
        handle(root);
      }

      public void handleNode(Node node) {
        handle(root);
      }

      public void handleLeaf(Leaf leaf) {
        handle(root);
      }

      private void handle(SplitNode node) {
        node.removePlaceholders(placeholders);
        if (!node.isOfUse()) {
          nodesToDelete.add(node);
        }
      }
    });
    for (SplitNode node : nodesToDelete) {
      node.delete(true);
    }
  }

  private void removeHandle(StationChildHandle handle, DockHierarchyLock.Token token) {
    int index = dockables.indexOf(handle);
    if (index >= 0) {
      removeDisplayer(index, token);
    }
  }

  private void removeDisplayer(int index, DockHierarchyLock.Token token) {
    StationChildHandle handle = dockables.get(index);
    if (handle == fullScreenDockable) {
      setNextFullScreen();
      if (handle == fullScreenDockable) {
        setFullScreen(null);
      }
    }
    Dockable dockable = handle.getDockable();
    boolean fire = token == null;
    if (fire) {
      token = DockHierarchyLock.acquireUnlinking(this, dockable);
    }
    try {
      if (fire) {
        dockStationListeners.fireDockableRemoving(dockable);
      }
      dockables.remove(index);
      DockableDisplayer displayer = handle.getDisplayer();
      displayer.getComponent().setVisible(true);
      getContentPane().remove(displayer.getComponent());
      handle.destroy();
      if (dockable == frontDockable) {
        setFrontDockable(null);
      }
      dockable.setDockParent(null);
      if (fire) {
        dockStationListeners.fireDockableRemoved(dockable);
      }
    }  finally {
      if (fire) {
        token.release();
      }
    }
  }

  public Root getRoot() {
    return root();
  }

  public SplitNode getNode(final long id) {
    class Visitor implements SplitNodeVisitor {
      private SplitNode result;

      public void handleRoot(Root root) {
        if (root.getId() == id) {
          result = root;
        }
      }

      public void handleLeaf(Leaf leaf) {
        if (leaf.getId() == id) {
          result = leaf;
        }
      }

      public void handlePlaceholder(Placeholder placeholder) {
        if (placeholder.getId() == id) {
          result = placeholder;
        }
      }

      public void handleNode(Node node) {
        if (node.getId() == id) {
          result = node;
        }
      }
    }
    ;
    if (root == null) {
      return null;
    }
    Visitor visitor = new Visitor();
    getRoot().visit(visitor);
    return visitor.result;
  }

  public String getFactoryID() {
    return SplitDockStationFactory.ID;
  }

  public void updateBounds() {
    Insets insets = getBasePane().getInsets();
    double factorW = getWidth() - insets.left - insets.right;
    double factorH = getHeight() - insets.top - insets.bottom;
    SplitLayoutManager manager = layoutManager.getValue();
    if (factorW <= 0 || factorH <= 0) {
      manager.updateBounds(root(), 0, 0, 0, 00);
    } else {
      manager.updateBounds(root(), insets.left / factorW, insets.top / factorH, factorW, factorH);
    }
  }

  private class Background extends BackgroundAlgorithm implements StationBackgroundComponent {
    public Background() {
      super(StationBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".station.split");
    }

    public Component getComponent() {
      return SplitDockStation.this.getComponent();
    }

    public DockStation getStation() {
      return SplitDockStation.this;
    }
  }

  private class Content extends ConfiguredBackgroundPanel {
    public Content() {
      super(Transparency.DEFAULT);
    }

    @Override public void doLayout() {
      updateBounds();
      Insets insets = getInsets();
      if (fullScreenDockable != null) {
        fullScreenDockable.getDisplayer().getComponent().setBounds(insets.left, insets.top, getWidth() - insets.left - insets.right, getHeight() - insets.bottom - insets.top);
      }
    }

    @Override public void setTransparency(Transparency transparency) {
      super.setTransparency(transparency);
      SplitDockStation.this.setSolid(transparency == Transparency.SOLID);
    }
  }

  public enum Orientation {
    HORIZONTAL,
    VERTICAL
  }



  private class VisibleListener extends DockStationAdapter {
    @Override public void dockableShowingChanged(DockStation station, Dockable dockable, boolean visible) {
      visibility.fire();
    }
  }

  private class FullScreenListener implements DoubleClickListener {
    public DockElement getTreeLocation() {
      return SplitDockStation.this;
    }

    public boolean process(Dockable dockable, MouseEvent event) {
      if (event.isConsumed() || !isExpandOnDoubleclick()) {
        return false;
      } else {
        if (dockable == SplitDockStation.this) {
          return false;
        }
        dockable = unwrap(dockable);
        if (dockable != null) {
          if (isFullScreen()) {
            if (getFullScreen() == dockable) {
              setFullScreen(null);
              event.consume();
            }
          } else {
            setFullScreen(dockable);
            event.consume();
          }
          return true;
        }
        return false;
      }
    }

    private Dockable unwrap(Dockable dockable) {
      while (dockable.getDockParent() != SplitDockStation.this) {
        DockStation parent = dockable.getDockParent();
        if (parent == null) {
          return null;
        }
        dockable = parent.asDockable();
        if (dockable == null) {
          return null;
        }
      }
      return dockable;
    }
  }

  protected class SplitDropOperation implements StationDropOperation {
    private PutInfo putInfo;

    private boolean move;

    public SplitDropOperation(PutInfo putInfo, boolean move) {
      this.putInfo = putInfo;
      this.move = move;
    }

    public boolean isMove() {
      return move;
    }

    public void draw() {
      SplitDockStation.this.putInfo = putInfo;
      repaint();
    }

    public void destroy() {
      if (SplitDockStation.this.putInfo == putInfo) {
        SplitDockStation.this.putInfo = null;
        repaint();
      }
    }

    public DockStation getTarget() {
      return SplitDockStation.this;
    }

    public Dockable getItem() {
      return putInfo.getDockable();
    }

    public CombinerTarget getCombination() {
      return putInfo.getCombinerTarget();
    }

    public DisplayerCombinerTarget getDisplayerCombination() {
      CombinerTarget target = getCombination();
      if (target == null) {
        return null;
      }
      return target.getDisplayerCombination();
    }

    public void execute() {
      if (isMove()) {
        move();
      } else {
        drop(null);
      }
    }

    public void move() {
      try {
        access.arm();
        DockUtilities.checkLayoutLocked();
        Root root = root();
        Leaf leaf = root.getLeaf(putInfo.getDockable());
        SplitNode parent = putInfo.getNode();
        if (leaf.getParent() == parent) {
          while (parent != null) {
            if (parent == root) {
              return;
            } else {
              Node node = (Node) parent;
              SplitNode next;
              if (node.getLeft() == leaf) {
                next = node.getRight();
              } else {
                next = node.getLeft();
              }
              if (next.isVisible()) {
                putInfo.setNode(next);
                break;
              }
              parent = parent.getParent();
            }
          }
        }
        putInfo.setLeaf(leaf);
        if (putInfo.getPut() == Put.CENTER) {
          leaf.placehold(false);
        } else {
          leaf.delete(true);
        }
        drop(DockHierarchyLock.acquireFake());
      }  finally {
        access.fire();
      }
    }

    private void drop(DockHierarchyLock.Token token) {
      try {
        boolean fire = token == null;
        access.arm();
        DockUtilities.checkLayoutLocked();
        if (putInfo.getNode() == null) {
          if (fire) {
            DockUtilities.ensureTreeValidity(SplitDockStation.this, putInfo.getDockable());
            token = DockHierarchyLock.acquireLinking(SplitDockStation.this, putInfo.getDockable());
          }
          try {
            if (fire) {
              dockStationListeners.fireDockableAdding(putInfo.getDockable());
            }
            addDockable(putInfo.getDockable(), token);
            if (fire) {
              dockStationListeners.fireDockableAdded(putInfo.getDockable());
            }
          }  finally {
            if (fire) {
              token.release();
            }
          }
        } else {
          boolean finish = false;
          if (putInfo.getCombinerTarget() != null) {
            if (putInfo.getNode() instanceof Leaf) {
              if (putInfo.getLeaf() != null) {
                if (fire) {
                  token = DockHierarchyLock.acquireUnlinking(SplitDockStation.this, putInfo.getLeaf().getDockable());
                }
                try {
                  putInfo.getLeaf().setDockable(null, token);
                  putInfo.setLeaf(null);
                }  finally {
                  if (fire) {
                    token.release();
                  }
                }
              }
              if (dropOver((Leaf) putInfo.getNode(), putInfo.getDockable(), putInfo.getCombinerSource(), putInfo.getCombinerTarget())) {
                finish = true;
              }
            } else {
              putInfo.setPut(PutInfo.Put.TOP);
            }
          }
          if (!finish) {
            updateBounds();
            layoutManager.getValue().calculateDivider(SplitDockStation.this, putInfo, root().getLeaf(putInfo.getDockable()));
            dropAside(putInfo.getNode(), putInfo.getPut(), putInfo.getDockable(), putInfo.getLeaf(), putInfo.getDivider(), token);
          }
        }
        revalidate();
      }  finally {
        access.fire();
      }
    }
  }

  private class Access implements SplitDockAccess {
    private long lastUniqueId = -1;

    private int repositionedArm = 0;

    private Set<Dockable> repositioned = new HashSet<Dockable>();

    private Dockable dockableSelected = null;

    public StationChildHandle getFullScreenDockable() {
      return fullScreenDockable;
    }

    public DockTitleVersion getTitleVersion() {
      return title;
    }

    public SplitDockStation getOwner() {
      return SplitDockStation.this;
    }

    public double validateDivider(double divider, Node node) {
      return layoutManager.getValue().validateDivider(SplitDockStation.this, divider, node);
    }

    public StationChildHandle newHandle(Dockable dockable) {
      return new StationChildHandle(SplitDockStation.this, getDisplayers(), dockable, title);
    }

    public void addHandle(StationChildHandle dockable, DockHierarchyLock.Token token) {
      SplitDockStation.this.addHandle(dockable, token);
    }

    public void removeHandle(StationChildHandle handle, DockHierarchyLock.Token token) {
      SplitDockStation.this.removeHandle(handle, token);
    }

    public boolean drop(Dockable dockable, SplitDockProperty property, SplitNode root) {
      return SplitDockStation.this.drop(dockable, property, root);
    }

    public PutInfo validatePutInfo(PutInfo putInfo) {
      return layoutManager.getValue().validatePutInfo(SplitDockStation.this, putInfo);
    }

    public void repositioned(SplitNode node) {
      arm();
      try {
        node.visit(new SplitNodeVisitor() {
          public void handleRoot(Root root) {
          }

          public void handlePlaceholder(Placeholder placeholder) {
          }

          public void handleNode(Node node) {
          }

          public void handleLeaf(Leaf leaf) {
            Dockable dockable = leaf.getDockable();
            if (dockable != null) {
              repositioned.add(dockable);
            }
          }
        });
      }  finally {
        fire();
      }
    }

    public void dockableSelected(Dockable dockable) {
      arm();
      if (dockableSelected == null) {
        dockableSelected = dockable;
      }
      fire();
    }

    public void arm() {
      repositionedArm++;
    }

    public void fire() {
      repositionedArm--;
      if (repositionedArm == 0) {
        List<Dockable> dockables = new ArrayList<Dockable>();
        for (Dockable dockable : repositioned) {
          if (dockable.getDockParent() == SplitDockStation.this) {
            dockables.add(dockable);
          }
        }
        repositioned.clear();
        if (dockables.size() > 0) {
          dockStationListeners.fireDockablesRepositioned(dockables.toArray(new Dockable[dockables.size()]));
        }
        if (dockableSelected != null) {
          Dockable newDockable = getFrontDockable();
          if (dockableSelected != newDockable) {
            dockStationListeners.fireDockableSelected(dockableSelected, newDockable);
          }
          dockableSelected = null;
        }
      }
    }

    public long uniqueID() {
      long id = System.currentTimeMillis();
      if (id <= lastUniqueId) {
        lastUniqueId++;
        id = lastUniqueId + 1;
      }
      while (getNode(id) != null) {
        id++;
      }
      lastUniqueId = id;
      return id;
    }

    public boolean isTreeAutoCleanupEnabled() {
      return treeLock == 0;
    }

    public SplitPlaceholderSet getPlaceholderSet() {
      return placeholderSet;
    }
  }
}