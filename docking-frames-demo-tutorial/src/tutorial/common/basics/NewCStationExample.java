package tutorial.common.basics;
import java.awt.Color;
import tutorial.support.ColorSingleCDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.group.CGroupBehavior;
import bibliothek.gui.dock.common.intern.AbstractDockableCStation;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.intern.station.CommonDockStation;
import bibliothek.gui.dock.common.intern.station.CommonDockStationFactory;
import bibliothek.gui.dock.common.mode.CNormalModeArea;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.common.perspective.CStationPerspective;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.ModeAreaListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.Path;

@Tutorial(id = "NewCStation", title = "Custom CStation") public class NewCStationExample {
  public static void main(String[] args) {
    JTutorialFrame frame = new JTutorialFrame(NewCStationExample.class);
    CControl control = new CControl(frame);
    frame.destroyOnClose(control);
    control.setGroupBehavior(CGroupBehavior.TOPMOST);
    CContentArea contentArea = control.getContentArea();
    frame.add(contentArea);
    CStack stack = new CStack("stack");
    control.addStation(stack, true);
    ColorSingleCDockable white = new ColorSingleCDockable("White", Color.WHITE);
    ColorSingleCDockable gray = new ColorSingleCDockable("Gray", Color.GRAY);
    ColorSingleCDockable black = new ColorSingleCDockable("Black", Color.BLACK);
    ColorSingleCDockable red = new ColorSingleCDockable("Red", Color.RED);
    ColorSingleCDockable green = new ColorSingleCDockable("Green", Color.GREEN);
    ColorSingleCDockable blue = new ColorSingleCDockable("Blue", Color.BLUE);
    CGrid grid = new CGrid(control);
    grid.add(0, 0, 1, 0.25, red);
    grid.add(0, 0.25, 0.25, 0.25, green);
    grid.add(0, 0.5, 0.25, 0.5, blue);
    grid.add(0.25, 0.25, 0.75, 0.75, stack);
    contentArea.deploy(grid);
    control.addDockable(white);
    control.addDockable(gray);
    control.addDockable(black);
    white.setWorkingArea(stack);
    gray.setWorkingArea(stack);
    black.setWorkingArea(stack);
    stack.getStation().add(white.intern(), 0);
    stack.getStation().add(gray.intern(), 1);
    stack.getStation().add(black.intern(), 2);
    frame.setVisible(true);
  }

  private static class CStack extends AbstractDockableCStation<CStackDockStation> implements CNormalModeArea {
    public CStack(String id) {
      CStackDockStation delegate = new CStackDockStation(this);
      CLocation stationLocation = new CLocation() {
        @Override public CLocation getParent() {
          return null;
        }

        @Override public String findRoot() {
          return getUniqueId();
        }

        @Override public DockableProperty findProperty(DockableProperty successor) {
          return successor;
        }

        @Override public ExtendedMode findMode() {
          return ExtendedMode.NORMALIZED;
        }

        @Override public CLocation aside() {
          return this;
        }
      };
      init(delegate, id, stationLocation, delegate);
    }

    protected void install(CControlAccess access) {
      access.getLocationManager().getNormalMode().add(this);
    }

    protected void uninstall(CControlAccess access) {
      access.getLocationManager().getNormalMode().remove(getUniqueId());
    }

    public CStationPerspective createPerspective() {
      throw new IllegalStateException("not implemented");
    }

    public boolean isNormalModeChild(Dockable dockable) {
      return isChild(dockable);
    }

    public DockableProperty getLocation(Dockable child) {
      return DockUtilities.getPropertyChain(getStation(), child);
    }

    public void setLocation(Dockable dockable, DockableProperty location, AffectedSet set) {
      set.add(dockable);
      if (isChild(dockable)) {
        getStation().move(dockable, location);
      } else {
        if (!getStation().drop(dockable, location)) {
          getStation().drop(dockable);
        }
      }
    }

    public void addModeAreaListener(ModeAreaListener listener) {
    }

    public Path getTypeId() {
      return null;
    }

    public boolean autoDefaultArea() {
      return true;
    }

    public boolean isChild(Dockable dockable) {
      return dockable.getDockParent() == getStation();
    }

    public void removeModeAreaListener(ModeAreaListener listener) {
    }

    public void setController(DockController controller) {
    }

    public void setMode(LocationMode mode) {
    }

    public CLocation getCLocation(Dockable dockable) {
      DockableProperty property = DockUtilities.getPropertyChain(getStation(), dockable);
      return getStationLocation().expandProperty(getStation().getController(), property);
    }

    public CLocation getCLocation(Dockable dockable, Location location) {
      DockableProperty property = location.getLocation();
      if (property == null) {
        return getStationLocation();
      }
      return getStationLocation().expandProperty(getStation().getController(), property);
    }

    public boolean respectWorkingAreas() {
      return true;
    }

    public boolean isCloseable() {
      return false;
    }

    public boolean isExternalizable() {
      return false;
    }

    public boolean isMaximizable() {
      return false;
    }

    public boolean isMinimizable() {
      return false;
    }

    public boolean isStackable() {
      return false;
    }

    public boolean isWorkingArea() {
      return true;
    }

    public DockActionSource[] getSources() {
      return new DockActionSource[] { getClose() };
    }
  }

  private static class CStackDockStation extends StackDockStation implements CommonDockStation<StackDockStation, CStackDockStation>, CommonDockable {
    private CStack delegate;

    public CStackDockStation(CStack stack) {
      this.delegate = stack;
    }

    @Override public String getFactoryID() {
      return CommonDockStationFactory.FACTORY_ID;
    }

    public String getConverterID() {
      return super.getFactoryID();
    }

    public CDockable getDockable() {
      return delegate;
    }

    public DockActionSource[] getSources() {
      return delegate.getSources();
    }

    public CStation<CStackDockStation> getStation() {
      return delegate;
    }

    public StackDockStation getDockStation() {
      return this;
    }

    @Override public CStackDockStation asDockStation() {
      return this;
    }

    @Override public CommonDockable asDockable() {
      return this;
    }
  }
}