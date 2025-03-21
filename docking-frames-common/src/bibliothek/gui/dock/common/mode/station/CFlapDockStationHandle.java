package bibliothek.gui.dock.common.mode.station;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.station.CFlapDockStation;
import bibliothek.gui.dock.common.mode.CMinimizedModeArea;
import bibliothek.gui.dock.common.mode.CModeArea;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.station.FlapDockStationHandle;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * Interface between {@link FlapDockStation} and {@link CModeArea}.
 * @author Benjamin Sigg
 */
public class CFlapDockStationHandle extends FlapDockStationHandle implements CMinimizedModeArea {
  /** base location */
  private CLocation location;

  /**
	 * Creates a new handle
	 * @param station the station which is handled by this handle
	 */
  public CFlapDockStationHandle(CStation<CFlapDockStation> station) {
    this(station.getUniqueId(), station.getStation(), station.getStationLocation());
  }

  /**
	 * Creates a new handle
	 * @param id the unique identifier of this station
	 * @param station the station to handle
	 * @param location the location which represents <code>station</code>
	 */
  public CFlapDockStationHandle(String id, CFlapDockStation station, CLocation location) {
    super(id, station);
    if (location == null) {
      throw new IllegalArgumentException("location must not be null");
    }
    this.location = location;
  }

  public CLocation getCLocation(Dockable dockable) {
    DockableProperty property = DockUtilities.getPropertyChain(getStation(), dockable);
    return location.expandProperty(getStation().getController(), property);
  }

  public CLocation getCLocation(Dockable dockable, Location location) {
    DockableProperty property = location.getLocation();
    if (property == null) {
      return this.location;
    }
    return this.location.expandProperty(getStation().getController(), property);
  }
}