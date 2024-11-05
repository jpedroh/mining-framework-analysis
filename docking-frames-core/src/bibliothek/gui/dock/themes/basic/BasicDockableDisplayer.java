package bibliothek.gui.dock.themes.basic;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.displayer.DisplayerBackgroundComponent;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.displayer.DisplayerDockBorder;
import bibliothek.gui.dock.displayer.DisplayerFocusTraversalPolicy;
import bibliothek.gui.dock.displayer.DockableDisplayerHints;
import bibliothek.gui.dock.displayer.SingleTabDecider;
import bibliothek.gui.dock.event.SingleTabDeciderListener;
import bibliothek.gui.dock.focus.DockFocusTraversalPolicy;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.stack.action.DockActionDistributor;
import bibliothek.gui.dock.station.stack.action.DockActionDistributorSource;
import bibliothek.gui.dock.station.stack.action.DockActionDistributor.Target;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.Enforcement;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.border.BorderForwarder;
import bibliothek.gui.dock.title.ActionsDockTitleEvent;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.gui.dock.util.UIValue;

public class BasicDockableDisplayer extends ConfiguredBackgroundPanel implements DockableDisplayer {
  private Dockable dockable;

  private DockTitle title;

  private Location location;

  private DockStation station;

  private DockController controller;

  private Hints hints = new Hints();

  private boolean respectBorderHint = false;

  private boolean defaultBorderHint = true;

  private boolean singleTabShowInnerBorder = true;

  private boolean singleTabShowOuterBorder = true;

  private List<DockableDisplayerListener> listeners = new ArrayList<DockableDisplayerListener>();

  private Background background = new Background();

  private DisplayerBorder baseBorder;

  private DisplayerBorder contentBorder;

  private SingleTabDeciderListener singleTabListener = new SingleTabDeciderListener() {
    public void showSingleTabChanged(SingleTabDecider source, Dockable dockable) {
      if (dockable == BasicDockableDisplayer.this.dockable) {
        updateDecorator();
      }
    }
  };

  private PropertyValue<SingleTabDecider> decider = new PropertyValue<SingleTabDecider>(SingleTabDecider.SINGLE_TAB_DECIDER) {
    @Override protected void valueChanged(SingleTabDecider oldValue, SingleTabDecider newValue) {
      if (oldValue != null) {
        oldValue.removeSingleTabDeciderListener(singleTabListener);
      }
      if (newValue != null) {
        newValue.addSingleTabDeciderListener(singleTabListener);
      }
      updateDecorator();
    }
  };

  private BasicDockableDisplayerDecorator decorator;

  private boolean singleTabShowing;

  private boolean pendingForcedUpdateDecorator = false;

  private boolean stacked = false;

  private DisplayerContentPane content = new ConfiguredBackgroundPanel(null, Transparency.TRANSPARENT) {
    @Override public void doLayout() {
      BasicDockableDisplayer.this.doLayout(content);
    }

    @Override public Dimension getMinimumSize() {
      return getContentMinimumSize();
    }

    @Override public Dimension getPreferredSize() {
      return getContentPreferredSize();
    }

    @Override public Dimension getMaximumSize() {
      return getContentMaximumSize();
    }
  };

  public BasicDockableDisplayer(DockStation station) {
    this(station, null, null);
  }

  public BasicDockableDisplayer(DockStation station, Dockable dockable, DockTitle title) {
    this(station, dockable, title, Location.TOP);
  }

  public BasicDockableDisplayer(DockStation station, Dockable dockable, DockTitle title, Location location) {
    super(new GridLayout(1, 1), Transparency.DEFAULT);
    init(station, dockable, title, location);
  }

  protected BasicDockableDisplayer(DockStation station, boolean initialize) {
    super(new GridLayout(1, 1), Transparency.DEFAULT);
    if (initialize) {
      init(station, null, null, Location.TOP);
    }
  }

  protected void init(DockStation station, Dockable dockable, DockTitle title, Location location) {
    content = createContentPane();
    content.setBackground(background);
    setDecorator(new MinimalDecorator());
    setBackground(background);
    setTitleLocation(location);
    setDockable(dockable);
    setTitle(title);
    setFocusable(true);
    setFocusCycleRoot(true);
    setFocusTraversalPolicy(new DockFocusTraversalPolicy(new DisplayerFocusTraversalPolicy(this), true));
    baseBorder = new DisplayerBorder(this, "basic.base");
    contentBorder = new DisplayerBorder(content, "basic.content");
  }

  protected void setDecorator(BasicDockableDisplayerDecorator decorator) {
    if (decorator == null) {
      throw new IllegalArgumentException("decorator must not be null");
    }
    if (this.decorator != null) {
      this.decorator.setDockable(null, null);
      this.decorator.setController(null);
    }
    this.decorator = decorator;
    decorator.setController(controller);
    resetDecorator();
    if (title != null) {
      title.changed(new ActionsDockTitleEvent(dockable, decorator.getActionSuggestion()));
    }
    revalidate();
    repaint();
  }

  protected void updateDecorator() {
    updateDecorator(false);
  }

  protected void updateDecorator(boolean force) {
    if (force) {
      pendingForcedUpdateDecorator = true;
    }
    if (dockable != null && station != null) {
      boolean decision = decider.getValue().showSingleTab(station, dockable);
      if (pendingForcedUpdateDecorator || decision != singleTabShowing) {
        pendingForcedUpdateDecorator = false;
        singleTabShowing = decision;
        if (singleTabShowing) {
          setDecorator(createTabDecorator());
        } else {
          if (isStacked()) {
            setDecorator(createStackedDecorator());
          } else {
            setDecorator(createMinimalDecorator());
          }
        }
      }
      updateBorder();
    }
  }

  public void setStacked(boolean stacked) {
    if (this.stacked != stacked) {
      this.stacked = stacked;
      updateDecorator(true);
    }
  }

  public boolean isStacked() {
    return stacked;
  }

  protected BasicDockableDisplayerDecorator createMinimalDecorator() {
    return new MinimalDecorator();
  }

  protected BasicDockableDisplayerDecorator createStackedDecorator() {
    return createMinimalDecorator();
  }

  protected BasicDockableDisplayerDecorator createStackedDecorator(final PropertyKey<DockActionDistributor> distributor) {
    return new MinimalDecorator() {
      private DockActionDistributorSource source = new DockActionDistributorSource(Target.TITLE, distributor);

      @Override public void setDockable(Component content, Dockable dockable) {
        super.setDockable(content, dockable);
        source.setDockable(dockable);
      }

      @Override public DockActionSource getActionSuggestion() {
        return source;
      }
    };
  }

  protected BasicDockableDisplayerDecorator createTabDecorator() {
    return new TabDecorator(station, null);
  }

  public void setController(DockController controller) {
    this.controller = controller;
    decider.setProperties(controller);
    decorator.setController(controller);
    background.setController(controller);
    baseBorder.setController(controller);
    contentBorder.setController(controller);
    resetDecorator();
  }

  public DockController getController() {
    return controller;
  }

  public void addDockableDisplayerListener(DockableDisplayerListener listener) {
    listeners.add(listener);
  }

  public void removeDockableDisplayerListener(DockableDisplayerListener listener) {
    listeners.remove(listener);
  }

  protected DockableDisplayerListener[] listeners() {
    return listeners.toArray(new DockableDisplayerListener[listeners.size()]);
  }

  public void setStation(DockStation station) {
    this.station = station;
    updateDecorator();
  }

  public DockStation getStation() {
    return station;
  }

  public Dockable getDockable() {
    return dockable;
  }

  public void setDockable(Dockable dockable) {
    if (this.dockable != null) {
      this.dockable.configureDisplayerHints(null);
    }
    updateDecorator();
    resetDecorator();
    hints.setShowBorderHint(null);
    this.dockable = dockable;
    if (dockable != null) {
      this.dockable.configureDisplayerHints(hints);
    }
    revalidate();
  }

  public Location getTitleLocation() {
    return location;
  }

  public void setTitleLocation(Location location) {
    if (location == null) {
      location = Location.TOP;
    }
    this.location = location;
    content.setTitleLocation(location);
    if (title != null) {
      title.setOrientation(orientation(location));
    }
    revalidate();
  }

  protected DockTitle.Orientation orientation(Location location) {
    switch (location) {
      case TOP:
      return DockTitle.Orientation.NORTH_SIDED;
      case BOTTOM:
      return DockTitle.Orientation.SOUTH_SIDED;
      case LEFT:
      return DockTitle.Orientation.WEST_SIDED;
      case RIGHT:
      return DockTitle.Orientation.EAST_SIDED;
    }
    return null;
  }

  public DockTitle getTitle() {
    return title;
  }

  public void setTitle(DockTitle title) {
    this.title = title;
    if (title == null) {
      content.setTitle(null);
    } else {
      content.setTitle(getComponent(title));
    }
    if (title != null) {
      title.setOrientation(orientation(location));
      if (decorator != null) {
        title.changed(new ActionsDockTitleEvent(dockable, decorator.getActionSuggestion()));
      }
    }
    revalidate();
  }

  protected Component getComponent(Dockable dockable) {
    return dockable.getComponent();
  }

  protected Component getComponent(DockTitle title) {
    return title.getComponent();
  }

  public boolean titleContains(int x, int y) {
    DockTitle title = getTitle();
    if (title == null) {
      return false;
    }
    Component component = getComponent(title);
    Point point = new Point(x, y);
    point = SwingUtilities.convertPoint(this, point, component);
    point.x -= component.getX();
    point.y -= component.getY();
    return component.contains(point);
  }

  public Component getComponent() {
    return this;
  }

  public Dimension getContentPreferredSize() {
    Dimension base;
    if (title == null && dockable != null) {
      base = getComponent(dockable).getPreferredSize();
    } else {
      if (dockable == null && title != null) {
        base = getComponent(title).getPreferredSize();
      } else {
        if (dockable == null && title == null) {
          base = new Dimension(0, 0);
        } else {
          if (location == Location.LEFT || location == Location.RIGHT) {
            Dimension titleSize = getComponent(title).getPreferredSize();
            base = getComponent(dockable).getPreferredSize();
            base = new Dimension(base.width + titleSize.width, Math.max(base.height, titleSize.height));
          } else {
            Dimension titleSize = getComponent(title).getPreferredSize();
            base = getComponent(dockable).getPreferredSize();
            base = new Dimension(Math.max(titleSize.width, base.width), titleSize.height + base.height);
          }
        }
      }
    }
    Insets insets = getInsets();
    if (insets != null) {
      base = new Dimension(base.width + insets.left + insets.right, base.height + insets.top + insets.bottom);
    }
    return base;
  }

  public Dimension getContentMaximumSize() {
    Dimension base;
    if (title == null && dockable != null) {
      base = getComponent(dockable).getMaximumSize();
    } else {
      if (dockable == null && title != null) {
        base = getComponent(title).getMaximumSize();
      } else {
        if (dockable == null && title == null) {
          base = new Dimension(0, 0);
        } else {
          if (location == Location.LEFT || location == Location.RIGHT) {
            Dimension titleSize = getComponent(title).getMaximumSize();
            base = getComponent(dockable).getMaximumSize();
            base = new Dimension(base.width + titleSize.width, Math.max(base.height, titleSize.height));
          } else {
            Dimension titleSize = getComponent(title).getMaximumSize();
            base = getComponent(dockable).getMaximumSize();
            base = new Dimension(Math.max(titleSize.width, base.width), titleSize.height + base.height);
          }
        }
      }
    }
    Insets insets = getInsets();
    if (insets != null) {
      base = new Dimension(base.width + insets.left + insets.right, base.height + insets.top + insets.bottom);
    }
    return base;
  }

  public Insets getDockableInsets() {
    Insets insets = getInsets();
    if (insets == null) {
      insets = new Insets(0, 0, 0, 0);
    }
    if (title == null && dockable == null) {
      return insets;
    }
    if (title == null) {
      return insets;
    } else {
      if (dockable != null) {
        Dimension preferred = getComponent(title).getPreferredSize();
        if (location == Location.LEFT) {
          insets.left += preferred.width;
        } else {
          if (location == Location.RIGHT) {
            insets.right += preferred.width;
          } else {
            if (location == Location.BOTTOM) {
              insets.bottom += preferred.height;
            } else {
              insets.top += preferred.height;
            }
          }
        }
      }
    }
    return insets;
  }

  protected Hints getHints() {
    return hints;
  }

  public void setRespectBorderHint(boolean respectBorderHint) {
    if (this.respectBorderHint != respectBorderHint) {
      this.respectBorderHint = respectBorderHint;
      updateBorder();
    }
  }

  public boolean isRespectBorderHint() {
    return respectBorderHint;
  }

  public void setDefaultBorderHint(boolean defaultBorderHint) {
    if (this.defaultBorderHint != defaultBorderHint) {
      this.defaultBorderHint = defaultBorderHint;
      updateBorder();
    }
  }

  public boolean getDefaultBorderHint() {
    return defaultBorderHint;
  }

  public void setSingleTabShowInnerBorder(boolean singleTabShowInnerBorder) {
    this.singleTabShowInnerBorder = singleTabShowInnerBorder;
    updateBorder();
  }

  public boolean isSingleTabShowInnerBorder() {
    return singleTabShowInnerBorder;
  }

  public void setSingleTabShowOuterBorder(boolean singleTabShowOuterBorder) {
    this.singleTabShowOuterBorder = singleTabShowOuterBorder;
    updateBorder();
  }

  public boolean isSingleTabShowOuterBorder() {
    return singleTabShowOuterBorder;
  }

  @Override public void updateUI() {
    super.updateUI();
    updateBorder();
  }

  protected void updateBorder() {
    if (singleTabShowing) {
      if (singleTabShowInnerBorder) {
        setContentBorder(getDefaultBorder());
      } else {
        setContentBorder(null);
      }
      if (singleTabShowOuterBorder) {
        setBaseBorder(getDefaultBorder());
      } else {
        setBaseBorder(null);
      }
    } else {
      setContentBorder(null);
      if (respectBorderHint) {
        boolean show = hints.getShowBorderHint();
        if (show) {
          setBaseBorder(getDefaultBorder());
        } else {
          setBaseBorder(null);
        }
      } else {
        if (defaultBorderHint) {
          setBaseBorder(getDefaultBorder());
        } else {
          setBaseBorder(null);
        }
      }
    }
  }

  public void setBaseBorder(Border border) {
    if (baseBorder != null) {
      baseBorder.setBorder(border);
    }
  }

  public void setContentBorder(Border border) {
    if (contentBorder != null) {
      contentBorder.setBorder(border);
    }
  }

  public DisplayerCombinerTarget prepareCombination(CombinerSource source, Enforcement force) {
    if (decorator instanceof TabDecorator) {
      TabDisplayerCombinerTarget target = new TabDisplayerCombinerTarget(this, ((TabDecorator) decorator).getStackComponent(), source, force);
      if (target.isValid()) {
        return target;
      }
    }
    return null;
  }

  protected Border getDefaultBorder() {
    return BorderFactory.createBevelBorder(BevelBorder.RAISED);
  }

  protected class Hints implements DockableDisplayerHints {
    private Boolean border;

    public void setShowBorderHint(Boolean border) {
      if (this.border != border) {
        this.border = border;
        updateBorder();
      }
    }

    public boolean getShowBorderHint() {
      if (border != null) {
        return border.booleanValue();
      }
      return defaultBorderHint;
    }
  }

  private class Background extends BackgroundAlgorithm implements DisplayerBackgroundComponent {
    public Background() {
      super(DisplayerBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".displayer");
    }

    public Component getComponent() {
      return BasicDockableDisplayer.this;
    }

    public DockableDisplayer getDisplayer() {
      return BasicDockableDisplayer.this;
    }
  }

  protected class DisplayerBorder extends BorderForwarder implements DisplayerDockBorder {
    public DisplayerBorder(JComponent target, String idSuffix) {
      super(DisplayerDockBorder.KIND, ThemeManager.BORDER_MODIFIER + ".displayer." + idSuffix, target);
    }

    public DockableDisplayer getDisplayer() {
      return BasicDockableDisplayer.this;
    }
  }

  private boolean tabInside = false;

  protected DisplayerContentPane createContentPane() {
    return new DisplayerContentPane();
  }

  protected void resetDecorator() {
    removeAll();
    if (tabInside) {
      if (dockable == null) {
        content.setDockable(null);
        decorator.setDockable(null, null);
      } else {
        content.setDockable(null);
        decorator.setDockable(getComponent(dockable), dockable);
        content.setDockable(decorator.getComponent());
      }
      add(content);
    } else {
      if (dockable == null) {
        content.setDockable(null);
      } else {
        content.setDockable(getComponent(dockable));
      }
      decorator.setDockable(content, dockable);
      Component newComponent = decorator.getComponent();
      if (newComponent != null) {
        add(newComponent);
      }
    }
  }

  public boolean isTabInside() {
    return tabInside;
  }

  public void setTabInside(boolean tabInside) {
    this.tabInside = tabInside;
    resetDecorator();
  }
}