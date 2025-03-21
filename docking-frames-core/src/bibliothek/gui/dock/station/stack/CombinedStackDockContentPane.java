package bibliothek.gui.dock.station.stack;
import java.awt.Dimension;
import javax.swing.JPanel;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.focus.DockFocusTraversalPolicy;
import bibliothek.gui.dock.station.stack.tab.TabLayoutManager;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.Transparency;

/**
 * This panel paints the contents of a {@link CombinedStackDockComponent}. It is just a {@link JPanel}. The layout has to be 
 * managed by a {@link TabLayoutManager}. This panel is also a {@link #setFocusTraversalPolicyProvider(boolean)
 * focus traversal policy provider}.
 * @author Benjamin Sigg
 */
public class CombinedStackDockContentPane extends ConfiguredBackgroundPanel {
  private CombinedStackDockComponent<?, ?, ?> parent;

  private boolean paintBackground = true;

  /**
	 * Creates a new content pane
	 * @param parent the owner of this pane, not <code>null</code>
	 */
  public CombinedStackDockContentPane(CombinedStackDockComponent<?, ?, ?> parent) {
    super(null, Transparency.TRANSPARENT);
    if (parent == null) {
      throw new IllegalArgumentException("parent must not be null");
    }
    this.parent = parent;
    setFocusTraversalPolicyProvider(true);
    setFocusTraversalPolicy(new DockFocusTraversalPolicy(new CombinedStackDockFocusTraversalPolicy(this), true));
  }

  /**
	 * Tells this panel whether the background should be painted or not.
	 * @param paintBackground whether to paint a background
	 */
  public void setPaintBackground(boolean paintBackground) {
    this.paintBackground = paintBackground;
    if (paintBackground) {
      setTransparency(Transparency.DEFAULT);
    } else {
      setTransparency(Transparency.TRANSPARENT);
    }
  }

  /**
	 * Tells whether a background should be painted or not
	 * @return whether a background should be painted
	 */
  public boolean isPaintBackground() {
    return paintBackground;
  }

  /**
	 * Gets the owner of this pane.
	 * @return the owner, not <code>null</code>
	 */
  public CombinedStackDockComponent<?, ?, ?> getParentPane() {
    return parent;
  }

  @Override public void updateUI() {
    super.updateUI();
    if (parent != null) {
      parent.discardComponentsAndRebuild();
    }
  }

  @Override public void doLayout() {
    super.doLayout();
    parent.doLayout();
  }

  @Override public Dimension getPreferredSize() {
    return parent.getPreferredSize();
  }

  @Override public Dimension getMinimumSize() {
    Dimension result = new Dimension(5, 5);
    for (Dockable dockable : parent.getDockables()) {
      Dimension size = dockable.getComponent().getMinimumSize();
      result.width = Math.max(result.width, size.width);
      result.height = Math.max(result.height, size.height);
    }
    Dimension parentSize = parent.getMinimumSize();
    if (parent.getDockTabPlacement().isHorizontal()) {
      result.height += parentSize.height;
    } else {
      result.width += parentSize.width;
    }
    return result;
  }
}