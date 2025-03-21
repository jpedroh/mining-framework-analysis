package bibliothek.gui.dock.title;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import javax.swing.Icon;
import javax.swing.JComponent;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionPopup;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.disable.DisablingStrategy;
import bibliothek.gui.dock.disable.DisablingStrategyListener;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.swing.OrientedLabel;

/**
 * An abstract implementation of {@link DockTitle}. This title can have
 * an icon, a title-text and some small buttons to display {@link DockAction actions}.
 * The icon is at the top or left edge, the text in the middle, and the actions
 * at the lower or the right edge of the title. If the orientation of the
 * title is set to {@link DockTitle.Orientation vertical}, the text will be rotated
 * by 90 degrees.<br>
 * This title has also an {@link ActionPopup} which will appear when the user
 * presses the right mouse-button. The popup shows a list of all actions known
 * to this title.<br>
 * The whole logic a {@link DockTitle} needs is implemented in this class,
 * but subclasses may add graphical features - like a border or another
 * background.<br>
 * Subclasses may override {@link #getInnerInsets()} to add a space between
 * border and contents of this title.
 * 
 * @author Benjamin Sigg
 *
 */
public class AbstractDockTitle extends AbstractMultiDockTitle {
  /** A panel that displays the action-buttons of this title */
  private ButtonPanel itemPanel;

  /** The actions that were suggested to this title */
  private DockActionSource suggestedSource;

  /** The disabled version of {@link #icon} */
  private Icon disabledIcon;

  /** whether this title should react to user input */
  private boolean disabled = false;

  /** all the listeners that were added to this title */
  private List<MouseInputListener> mouseInputListeners = new ArrayList<MouseInputListener>();

  /** tells whether this title has to be disabled */
  private PropertyValue<DisablingStrategy> disablingStrategy = new PropertyValue<DisablingStrategy>(DisablingStrategy.STRATEGY) {
    @Override protected void valueChanged(DisablingStrategy oldValue, DisablingStrategy newValue) {
      if (oldValue != null) {
        oldValue.removeDisablingStrategyListener(disablingStrategyListener);
      }
      if (newValue != null) {
        newValue.addDisablingStrategyListener(disablingStrategyListener);
        setDisabled(newValue.isDisabled(getDockable(), AbstractDockTitle.this));
      } else {
        setDisabled(false);
      }
    }
  };

  /** a listener added to the current {@link DisablingStrategy} */
  private DisablingStrategyListener disablingStrategyListener = new DisablingStrategyListener() {
    public void changed(DockElement item) {
      setDisabled(disablingStrategy.getValue().isDisabled(getDockable(), AbstractDockTitle.this));
    }
  };

  /**
     * Constructs a new title
     * @param dockable the Dockable which is the owner of this title
     * @param origin the version which was used to create this title
     */
  public AbstractDockTitle(Dockable dockable, DockTitleVersion origin) {
    init(dockable, origin, true);
  }

  /**
     * Standard constructor
     * @param dockable The Dockable whose title this will be
     * @param origin The version which was used to create this title
     * @param showMiniButtons <code>true</code> if the actions of the Dockable
     * should be shown, <code>false</code> if they should not be visible
     */
  public AbstractDockTitle(Dockable dockable, DockTitleVersion origin, boolean showMiniButtons) {
    init(dockable, origin, showMiniButtons);
  }

  /**
     * Constructor which does not do anything. Subclasses should call
     * {@link #init(Dockable, DockTitleVersion, boolean)} to initialize
     * the title.
     */
  protected AbstractDockTitle() {
  }

  /**
     * Initializer called by the constructor.
     * @param dockable The Dockable whose title this will be
     * @param origin The version which was used to create this title
     * @param showMiniButtons <code>true</code> if the actions of the Dockable
     * should be shown, <code>false</code> if they should not be visible
     */
  protected void init(Dockable dockable, DockTitleVersion origin, boolean showMiniButtons) {
    super.init(dockable, origin);
    setShowMiniButtons(showMiniButtons);
  }

  /**
     * Tells whether this titel is able to show any {@link DockAction}. 
     * @return <code>true</code> if {@link DockAction}s are enabled
     * @see #setShowMiniButtons(boolean)
     */
  public boolean isShowMiniButtons() {
    return itemPanel != null;
  }

  /**
     * Enables or disables {@link DockAction}s for this title.
     * @param showMiniButtons whether to show actions or not
     */
  public void setShowMiniButtons(boolean showMiniButtons) {
    if (showMiniButtons) {
      if (itemPanel == null) {
        itemPanel = new ButtonPanel(true) {
          @Override protected BasicTitleViewItem<JComponent> createItemFor(DockAction action, Dockable dockable) {
            return AbstractDockTitle.this.createItemFor(action, dockable);
          }
        };
        itemPanel.setOpaque(false);
        itemPanel.setOrientation(getOrientation());
        itemPanel.setToolTipText(getToolTipText());
        add(itemPanel);
        if (isBound()) {
          itemPanel.setController(getDockable().getController());
          itemPanel.set(getDockable(), getActionSourceFor(getDockable()));
        }
      }
    } else {
      if (itemPanel != null) {
        itemPanel.set(null);
        remove(itemPanel);
      }
    }
  }

  /**
     * Tells this title whether it should be disabled or not. This method is called when the {@link DisablingStrategy}
     * changes. A disabled title should react to any {@link InputEvent}, and should be painted differently than an
     * enabled title.
     * @param disabled whether this title is disabled
     * @see #isDisabled()
     */
  protected void setDisabled(boolean disabled) {
    if (this.disabled != disabled) {
      this.disabled = disabled;
      label.setEnabled(!disabled);
      setEnabled(!disabled);
      if (disabled) {
        for (MouseInputListener listener : mouseInputListeners) {
          doRemoveMouseInputListener(listener);
        }
      } else {
        for (MouseInputListener listener : mouseInputListeners) {
          doAddMouseInputListener(listener);
        }
      }
    }
  }

  /**
     * Tells whether this title is disabled, a disabled title does not react to any user input.
     * @return whether the title is disabled
     * @set {@link #setDisabled(boolean)}
     */
  protected boolean isDisabled() {
    return disabled;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
     * Paints the icon (if there is any)
     * @param g the graphics context to use
     * @param component the {@link Component} which represents this title
     */
  protected void paintIcon(Graphics g, JComponent component) {
    Icon icon = this.icon;
    if (icon != null) {
      if (isDisabled()) {
        if (disabledIcon == null) {
          disabledIcon = DockUtilities.disabledIcon(component, icon);
        }
        icon = disabledIcon;
      }
      if (icon != null) {
        Insets insets = titleInsets();
        if (orientation.isVertical()) {
          int width = getWidth() - insets.left - insets.right;
          icon.paintIcon(this, g, insets.left + (width - icon.getIconWidth()) / 2, insets.top);
        } else {
          int height = getHeight() - insets.top - insets.bottom;
          icon.paintIcon(this, g, insets.left, insets.top + (height - icon.getIconHeight()) / 2);
        }
      }
    }
  }
>>>>>>> /usr/src/app/output/benoker/dockingframes/7792989b18d9aa22f844dd8a0ba8b56c8021d1e5/docking-frames-core/src/bibliothek/gui/dock/title/AbstractDockTitle.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
     * Sets the icon of this title. The icon is shown on the top or the left
     * edge.
     * @param icon the icon, can be <code>null</code>
     */
  protected void setIcon(Icon icon) {
    this.icon = icon;
    disabledIcon = null;
    revalidate();
    repaint();
  }
>>>>>>> /usr/src/app/output/benoker/dockingframes/7792989b18d9aa22f844dd8a0ba8b56c8021d1e5/docking-frames-core/src/bibliothek/gui/dock/title/AbstractDockTitle.java/right.java


  /**
     * Sets the tooltip that will be shown on this title.
     * @param text the new tooltip, can be <code>null</code>
     */
  protected void setTooltip(String text) {
    super.setToolTipText(text);
    if (itemPanel != null) {
      itemPanel.setToolTipText(text);
    }
  }

  public void setOrientation(Orientation orientation) {
    if (itemPanel != null) {
      itemPanel.setOrientation(orientation);
    }
    super.setOrientation(orientation);
  }

  @Override protected void doTitleLayout() {
    Insets insets = titleInsets();
    int x = insets.left;
    int y = insets.top;
    int width = getWidth() - insets.left - insets.right;
    int height = getHeight() - insets.top - insets.bottom;
    OrientedLabel label = getLabel();
    Orientation orientation = getOrientation();
    Icon icon = getIcon();
    int iconTextGap = getIconTextGap();
    Dimension labelPreferred;
    String text = getText();
    if (text == null || text.length() == 0) {
      labelPreferred = new Dimension(5, 5);
    } else {
      labelPreferred = label.getPreferredSize();
    }
    if (orientation.isHorizontal()) {
      if (icon != null) {
        x += icon.getIconWidth() + iconTextGap;
        width -= icon.getIconWidth() + iconTextGap;
      }
      if (itemPanel != null && itemPanel.getItemCount() > 0) {
        Dimension[] buttonPreferred = itemPanel.getPreferredSizes();
        int remaining = width - labelPreferred.width;
        int count = buttonPreferred.length - 1;
        while (count > 0 && buttonPreferred[count].width > remaining) {
          count--;
        }
        itemPanel.setVisibleActions(count);
        int buttonWidth = buttonPreferred[count].width;
        int buttonX = width - buttonWidth;
        label.setBounds(x, y, buttonX, height);
        itemPanel.setBounds(x + buttonX, y, width - buttonX, height);
      } else {
        label.setBounds(x, y, width, height);
      }
    } else {
      if (icon != null) {
        y += icon.getIconWidth() + iconTextGap;
        height -= icon.getIconWidth() + iconTextGap;
      }
      if (itemPanel != null && itemPanel.getItemCount() > 0) {
        Dimension[] buttonPreferred = itemPanel.getPreferredSizes();
        int remaining = height - labelPreferred.height;
        int count = buttonPreferred.length - 1;
        while (count > 0 && buttonPreferred[count].height > remaining) {
          count--;
        }
        itemPanel.setVisibleActions(count);
        int buttonHeight = buttonPreferred[count].height;
        int buttonY = height - buttonHeight;
        label.setBounds(x, y, width, buttonY);
        itemPanel.setBounds(x, y + buttonY, width, height - buttonY);
      } else {
        label.setBounds(x, y, width, height);
      }
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void addMouseInputListener(MouseInputListener listener) {
    mouseInputListeners.add(listener);
    if (!isDisabled()) {
      doAddMouseInputListener(listener);
    }
  }
>>>>>>> /usr/src/app/output/benoker/dockingframes/7792989b18d9aa22f844dd8a0ba8b56c8021d1e5/docking-frames-core/src/bibliothek/gui/dock/title/AbstractDockTitle.java/right.java


  private void doAddMouseInputListener(MouseInputListener listener) {
    addMouseListener(listener);
    addMouseMotionListener(listener);
    label.addMouseListener(listener);
    label.addMouseMotionListener(listener);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void removeMouseInputListener(MouseInputListener listener) {
    mouseInputListeners.remove(listener);
    if (!isDisabled()) {
      doRemoveMouseInputListener(listener);
    }
  }
>>>>>>> /usr/src/app/output/benoker/dockingframes/7792989b18d9aa22f844dd8a0ba8b56c8021d1e5/docking-frames-core/src/bibliothek/gui/dock/title/AbstractDockTitle.java/right.java


  private void doRemoveMouseInputListener(MouseInputListener listener) {
    removeMouseListener(listener);
    removeMouseMotionListener(listener);
    label.removeMouseListener(listener);
    label.removeMouseMotionListener(listener);
  }

  public Point getPopupLocation(Point click, boolean popupTrigger) {
    if (popupTrigger) {
      return click;
    }
    boolean restrained = getText() == null || getText().length() == 0;
    Rectangle icon = getIconBounds();
    if (icon != null) {
      if (icon.contains(click)) {
        if (restrained) {
          int size = getWidth() * getHeight();
          if (itemPanel != null) {
            size -= itemPanel.getWidth() * itemPanel.getHeight();
          }
          if (size <= 2 * icon.width * icon.height) {
            return null;
          }
        }
        if (getOrientation().isHorizontal()) {
          return new Point(icon.x, icon.y + icon.height);
        } else {
          return new Point(icon.x + icon.width, icon.y);
        }
      }
    }
    return null;
  }

  @Override public void changed(DockTitleEvent event) {
    super.changed(event);
    if (event instanceof ActionsDockTitleEvent) {
      suggestActions(((ActionsDockTitleEvent) event).getSuggestions());
    }
  }

  @Override public Dimension getPreferredSize() {
    Dimension size = super.getPreferredSize();
    if (itemPanel != null) {
      Dimension items = itemPanel.getPreferredSize();
      Insets insets = titleInsets();
      if (getOrientation().isHorizontal()) {
        size.width += items.width;
        size.height = Math.max(size.height, items.height + insets.top + insets.bottom);
      } else {
        size.height += items.height;
        size.width = Math.max(size.width, items.width + insets.left + insets.right);
      }
    }
    if (size.width < 10) {
      size.width = 10;
    }
    if (size.height < 10) {
      size.height = 10;
    }
    return size;
  }

  /**
     * Gets a list of all actions which will be shown on this title.
     * @param dockable the owner of the actions
     * @return the list of actions
     */
  protected DockActionSource getActionSourceFor(Dockable dockable) {
    if (suggestedSource != null) {
      return suggestedSource;
    }
    return dockable.getGlobalActionOffers();
  }

  /**
     * Called if a module using the {@link DockTitle} suggests using a specific set of {@link DockAction}s. It is
     * up to the {@link DockTitle} to follow the suggestions or to ignore them. The default behavior of this
     * {@link AbstractDockTitle} is to set the result of {@link #getActionSourceFor(Dockable)} equal to
     * <code>actions</code> and update the {@link #itemPanel} if necessary.
     * @param actions the set of actions that should be used
     */
  protected void suggestActions(DockActionSource actions) {
    if (suggestedSource != actions) {
      suggestedSource = actions;
      if (isShowMiniButtons()) {
        Dockable dockable = getDockable();
        itemPanel.set(dockable, getActionSourceFor(dockable));
      }
    }
  }

  /**
     * Gets the {@link DockActionSource} that was {@link #suggestActions(DockActionSource) suggested} to this
     * title.
     * @return the source, can be <code>null</code>
     */
  protected DockActionSource getSuggestedSource() {
    return suggestedSource;
  }

  @Override public void bind() {
    DockController controller = getDockable().getController();
    if (itemPanel != null) {
      Dockable dockable = getDockable();
      itemPanel.set(dockable, getActionSourceFor(dockable));
      itemPanel.setController(controller);
    }

<<<<<<< Unknown file: This is a bug in JDime.
=======
    if (controller != null) {
      for (AbstractDockColor color : colors) {
        color.connect(controller);
      }
      for (AbstractDockFont font : fonts) {
        font.connect(controller);
      }
      orientationConverter.setProperties(controller);
      disablingStrategy.setProperties(controller);
    }
>>>>>>> /usr/src/app/output/benoker/dockingframes/7792989b18d9aa22f844dd8a0ba8b56c8021d1e5/docking-frames-core/src/bibliothek/gui/dock/title/AbstractDockTitle.java/right.java

    super.bind();
  }

  @Override public void unbind() {
    if (itemPanel != null) {
      itemPanel.set(null);
      itemPanel.setController(null);
    }
    disablingStrategy.setProperties((DockProperties) null);
    super.unbind();
  }
}