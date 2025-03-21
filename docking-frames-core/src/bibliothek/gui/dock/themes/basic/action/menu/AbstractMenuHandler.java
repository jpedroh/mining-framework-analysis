package bibliothek.gui.dock.themes.basic.action.menu;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.event.StandardDockActionListener;

/**
 * A handler that connects a {@link JMenuItem} with a {@link DockAction}.
 * @param <I> DropDownItemHandle used by this handler
 * @param <D> Action used by this handler
 * @author Benjamin Sigg
 */
public abstract class AbstractMenuHandler<I extends JMenuItem, D extends StandardDockAction> implements MenuViewItem<JComponent> {
  /** the visual representation of the action, may be <code>null</code> */
  protected I item;

  /** the Dockable for which actions are dispatched */
  protected Dockable dockable;

  /** the action shown by the item of this handler, may be <code>null</code> */
  protected D action;

  /** a listener to the action, changes text, icon, etc.. of the item */
  private Listener listener;

  /**
     * Creates a new handler with predefined item.
     * @param action the action to observe
     * @param dockable the dockable for which actions are dispatched
     * @param item the item whose values have to be updated, <code>null</code> is
     * only valid if <code>action</code> is <code>null</code> too.
     */
  public AbstractMenuHandler(D action, Dockable dockable, I item) {
    this.action = action;
    this.dockable = dockable;
    this.item = item;
  }

  /**
     * Gets the element for which actions are dispatched.
     * @return the element
     */
  public Dockable getDockable() {
    return dockable;
  }

  /**
     * Gets the action that is observed by this handler.
     * @return the action, may be <code>null</code>
     */
  public D getAction() {
    return action;
  }

  /**
     * Gets the item whose values are updated by this handler.
     * @return the item, may be <code>null</code>
     */
  public JMenuItem getItem() {
    return item;
  }

  /**
     * Connects this handler to its action.
     */
  public void bind() {
    if (action != null) {
      if (listener == null) {
        action.bind(dockable);
        listener = new Listener();
        action.addDockActionListener(listener);
        if (item != null) {
          item.setEnabled(action.isEnabled(dockable));
          item.setIcon(action.getIcon(dockable));
          item.setDisabledIcon(action.getDisabledIcon(dockable));
          item.setText(action.getText(dockable));
          item.setToolTipText(action.getTooltipText(dockable));
        }

<<<<<<< /usr/src/app/output/benoker/dockingframes/4a829b05935f1ed1955f3ecefa55d0bcedfbd809/docking-frames-core/src/bibliothek/gui/dock/themes/basic/action/menu/AbstractMenuHandler.java/left.java
        item.setIcon(action.getIcon(dockable, ActionContentModifier.NONE_HORIZONTAL));
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/benoker/dockingframes/4a829b05935f1ed1955f3ecefa55d0bcedfbd809/docking-frames-core/src/bibliothek/gui/dock/themes/basic/action/menu/AbstractMenuHandler.java/left.java
        item.setDisabledIcon(action.getIcon(dockable, ActionContentModifier.DISABLED));
=======
>>>>>>> Unknown file: This is a bug in JDime.
      } else {
        throw new IllegalStateException("Handler is already bound");
      }
    }
  }

  /**
     * Disconnects this handler from its action
     */
  public void unbind() {
    if (action != null) {
      if (listener != null) {
        action.unbind(dockable);
        action.removeDockActionListener(listener);
        listener = null;
      } else {
        throw new IllegalStateException("Handler is already unbound");
      }
    }
  }

  private class Listener implements StandardDockActionListener {
    public void actionEnabledChanged(StandardDockAction action, Set<Dockable> dockables) {
      if (item != null) {
        item.setEnabled(action.isEnabled(dockable));
      }
    }

    public void actionIconChanged(StandardDockAction action, ActionContentModifier modifier, Set<Dockable> dockables) {

<<<<<<< /usr/src/app/output/benoker/dockingframes/4a829b05935f1ed1955f3ecefa55d0bcedfbd809/docking-frames-core/src/bibliothek/gui/dock/themes/basic/action/menu/AbstractMenuHandler.java/left.java
      if (modifier == null || modifier == ActionContentModifier.NONE_HORIZONTAL) {
        item.setIcon(action.getIcon(dockable, ActionContentModifier.NONE_HORIZONTAL));
      } else {
        if (modifier == null || modifier == ActionContentModifier.NONE) {
          item.setIcon(action.getIcon(dockable, ActionContentModifier.NONE));
        }
      }
=======
      if (item != null) {
        item.setIcon(action.getIcon(dockable));
      }
>>>>>>> /usr/src/app/output/benoker/dockingframes/4a829b05935f1ed1955f3ecefa55d0bcedfbd809/docking-frames-core/src/bibliothek/gui/dock/themes/basic/action/menu/AbstractMenuHandler.java/right.java

      if (modifier == null || modifier == ActionContentModifier.DISABLED) {
        item.setDisabledIcon(action.getIcon(dockable, ActionContentModifier.DISABLED));
      }
    }


<<<<<<< Unknown file: This is a bug in JDime.
=======
    public void actionDisabledIconChanged(StandardDockAction action, Set<Dockable> dockables) {
      if (item != null) {
        item.setDisabledIcon(action.getDisabledIcon(dockable));
      }
    }
>>>>>>> /usr/src/app/output/benoker/dockingframes/4a829b05935f1ed1955f3ecefa55d0bcedfbd809/docking-frames-core/src/bibliothek/gui/dock/themes/basic/action/menu/AbstractMenuHandler.java/right.java


    public void actionTextChanged(StandardDockAction action, Set<Dockable> dockables) {
      if (item != null) {
        item.setText(action.getText(dockable));
      }
    }

    public void actionTooltipTextChanged(StandardDockAction action, Set<Dockable> dockables) {
      if (item != null) {
        item.setToolTipText(action.getTooltipText(dockable));
      }
    }

    public void actionRepresentativeChanged(StandardDockAction action, Set<Dockable> dockables) {
    }
  }
}