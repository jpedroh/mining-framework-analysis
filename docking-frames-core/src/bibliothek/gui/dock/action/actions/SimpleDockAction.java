package bibliothek.gui.dock.action.actions;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import bibliothek.extension.gui.dock.preference.editor.KeyStrokeEditor;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.KeyboardListener;
import bibliothek.gui.dock.station.LayoutLocked;

public abstract class SimpleDockAction extends AbstractStandardDockAction implements SharingStandardDockAction {
  private Map<ActionContentModifier, Icon> icons = new HashMap<ActionContentModifier, Icon>();

  private String text;

  private String tooltip;

  private boolean enabled = true;

  private KeyStroke accelerator;

  private Dockable representative;

  private Map<Dockable, DockableKeyForwarder> forwarders = new HashMap<Dockable, DockableKeyForwarder>();

  @Override protected void bound(Dockable dockable) {
    super.bound(dockable);
    DockableKeyForwarder forwarder = new DockableKeyForwarder(dockable);
    forwarders.put(dockable, forwarder);
  }

  @Override protected void unbound(Dockable dockable) {
    super.unbound(dockable);
    DockableKeyForwarder forwarder = forwarders.remove(dockable);
    forwarder.destroy();
  }

  public Icon getIcon(Dockable dockable, ActionContentModifier modifier) {
    return icons.get(modifier);
  }

  public String getText(Dockable dockable) {
    return text;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
    fireActionTextChanged(getBoundDockables());
  }

  public String getTooltipText(Dockable dockable) {
    return getTooltipText();
  }

  public String getTooltipText() {
    if (accelerator == null) {
      return tooltip;
    }
    String acceleratorText = KeyStrokeEditor.toString(accelerator, true);
    if (tooltip == null) {
      return acceleratorText;
    } else {
      return tooltip + " (" + acceleratorText + ")";
    }
  }

  public String getTooltip() {
    return tooltip;
  }

  public void setTooltip(String tooltip) {
    this.tooltip = tooltip;
    fireActionTooltipTextChanged(getBoundDockables());
  }

  public boolean isEnabled(Dockable dockable) {
    return enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    if (this.enabled != enabled) {
      this.enabled = enabled;
      fireActionEnabledChanged(getBoundDockables());
    }
  }

  public Icon getIcon() {
    return icons.get(ActionContentModifier.NONE);
  }

  public void setIcon(Icon icon) {
    setIcon(ActionContentModifier.NONE, icon);
  }

  public ActionContentModifier[] getIconContexts(Dockable dockable) {
    return icons.keySet().toArray(new ActionContentModifier[icons.size()]);
  }

  public Icon getDisabledIcon() {
    return icons.get(ActionContentModifier.DISABLED);
  }

  public void setDisabledIcon(Icon icon) {
    setIcon(ActionContentModifier.DISABLED, icon);
  }

  public Icon getIcon(ActionContentModifier modifier) {
    return icons.get(modifier);
  }

  public void setIcon(ActionContentModifier modifier, Icon icon) {
    if (icon == null) {
      icons.remove(modifier);
    } else {
      icons.put(modifier, icon);
    }
    fireActionIconChanged(modifier, getBoundDockables());
  }

  public void setDockableRepresentation(Dockable dockable) {
    if (this.representative != dockable) {
      this.representative = dockable;
      fireActionRepresentativeChanged(getBoundDockables());
    }
  }

  public Dockable getDockableRepresentation(Dockable dockable) {
    return representative;
  }

  public Dockable getDockableRepresentation() {
    return representative;
  }

  public KeyStroke getAccelerator() {
    return accelerator;
  }

  public void setAccelerator(KeyStroke accelerator) {
    this.accelerator = accelerator;
    fireActionTooltipTextChanged(getBoundDockables());
  }

  protected boolean trigger(KeyEvent event, Dockable dockable) {
    return trigger(dockable);
  }

  private @LayoutLocked(locked = false) class DockableKeyForwarder implements KeyboardListener, DockHierarchyListener {
    private Dockable dockable;

    private DockController controller;

    private boolean destroyed = false;

    public DockableKeyForwarder(Dockable dockable) {
      this.dockable = dockable;
      dockable.addDockHierarchyListener(this);
      setController(dockable.getController());
    }

    public void hierarchyChanged(DockHierarchyEvent event) {
    }

    public void controllerChanged(DockHierarchyEvent event) {
      setController(dockable.getController());
    }

    private void setController(DockController controller) {
      if (this.controller != null) {
        this.controller.getKeyboardController().removeListener(this);
      }
      if (destroyed) {
        this.controller = null;
      } else {
        this.controller = controller;
      }
      if (this.controller != null) {
        this.controller.getKeyboardController().addListener(this);
      }
    }

    public void destroy() {
      destroyed = true;
      setController(null);
      dockable.removeDockHierarchyListener(this);
    }

    private boolean forward(DockElement element, KeyEvent event) {
      if (accelerator != null) {
        if (accelerator.equals(KeyStroke.getKeyStrokeForEvent(event))) {
          return trigger(event, dockable);
        }
      }
      return false;
    }

    public boolean keyPressed(DockElement element, KeyEvent event) {
      return forward(element, event);
    }

    public boolean keyReleased(DockElement element, KeyEvent event) {
      return forward(element, event);
    }

    public boolean keyTyped(DockElement element, KeyEvent event) {
      return forward(element, event);
    }

    public DockElement getTreeLocation() {
      return dockable;
    }

    @Override public String toString() {
      return getClass().getSimpleName() + " -> " + dockable.getTitleText() + " -> " + getText();
    }
  }
}