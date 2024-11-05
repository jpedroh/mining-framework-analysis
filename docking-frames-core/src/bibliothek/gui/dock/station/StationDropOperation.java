package bibliothek.gui.dock.station;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.support.CombinerTarget;

public interface StationDropOperation {
  public void draw();

  public void destroy();

  public boolean isMove();

  public void execute();

  public DockStation getTarget();

  public Dockable getItem();

  public CombinerTarget getCombination();

  public DisplayerCombinerTarget getDisplayerCombination();
}