package bibliothek.gui.dock.station;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.support.CombinerTarget;

/**
 * A description of what would happen if the user dropped a {@link Dockable} over a {@link DockStation}.
 * @author Benjamin Sigg
 */
public interface StationDropOperation {
  /**
	 * Informs this operation that it is the current candidate, and that it should paint markings onto
	 * the screen. 
	 */
  public void draw();

  /**
	 * Informs this operation that it is no longer used and that it should release any resources
	 * it has acquired.
	 */
  public void destroy(StationDropOperation next);

  /**
	 * Tells whether this operation is a move operation. A move operation is an operation where
	 * the {@link Dockable}s parent does not change.
	 * @return <code>true</code> if the {@link Dockable}s parent remains the same
	 */
  public boolean isMove();

  /**
	 * Executes this operation. There are no limitations of what the operation may do.
	 * @throws IllegalStateException if this operation was {@link #destroy() destroied}, if the
	 * tree of {@link DockElement}s changed since creation, or if this method was already executed 
	 */
  public void execute();

  /**
	 * Gets the target of the operation, this is the {@link DockStation} that created this object.
	 * @return the target of the operation, not <code>null</code>
	 */
  public DockStation getTarget();

  /**
	 * Gets the item that will be dropped onto {@link #getTarget() the target}.
	 * @return the item that will be dropped, not <code>null</code>
	 */
  public Dockable getItem();

  /**
	 * Most {@link DockStation}s will use a {@link Combiner} to merge two {@link Dockable}s into one. This method
	 * returns the information that was provided by the {@link Combiner}.
	 * @return the combiner information or <code>null</code>, <code>null</code> is always a valid result 
	 */
  public CombinerTarget getCombination();

  /**
	 * Some {@link DockStation}s may use the combining feature of {@link DockableDisplayer}s
	 * ({@link DockableDisplayer#prepareCombination(bibliothek.gui.dock.station.support.CombinerSource, bibliothek.gui.dock.station.support.Enforcement)})
	 * to combine some {@link Dockable}s. This method returns the information that was provided by the displayer.<br>
	 * If {@link #getCombination()} does not return <code>null</code>, then the result of 
	 * {@link CombinerTarget#getDisplayerCombination()} and this method should be the same.
	 * @return the information or <code>null</code>, <code>null</code> is always a valid result
	 */
  public DisplayerCombinerTarget getDisplayerCombination();
}