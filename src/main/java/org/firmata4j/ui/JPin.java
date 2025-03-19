package org.firmata4j.ui;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
import org.firmata4j.PinEventListener;

/**
 * Displays representation of a {@link Pin}.<br/>
 * Currently it supports displaying of pins in
 * {@link org.firmata4j.Pin.Mode#INPUT},
 * {@link org.firmata4j.Pin.Mode#OUTPUT} and
 * {@link org.firmata4j.Pin.Mode#ANALOG} modes as well as disabled pins, i.e., 
 * pins without supported modes.
 *
 * @author Oleg Kurbatov &lt;o.v.kurbatov@gmail.com&gt;
 */
public class JPin extends JLabel implements PinEventListener {
  private Pin model;

  private JPopupMenu modesMenu;

  private Runnable refreshRoutine = new Runnable() {
    @Override public void run() {
      refreshIcon();
    }
  };

  private static final Map<Pin.Mode, Map<String, Icon>> ICONS = new HashMap<>();

  private static final Icon DISABLED_OFF;

  private static final Icon DISABLED_ON;

  static {
    ClassLoader classLoader = JPin.class.getClassLoader();
    Map<String, Icon> iconset = new HashMap<>();
    ICONS.put(Pin.Mode.INPUT, iconset);
    iconset.put("on", new ImageIcon(classLoader.getResource("img/green-on.png")));
    iconset.put("off", new ImageIcon(classLoader.getResource("img/green-off.png")));
    ICONS.put(Pin.Mode.PULLUP, iconset);
    iconset.put("on", new ImageIcon(classLoader.getResource("img/green-on.png")));
    iconset.put("off", new ImageIcon(classLoader.getResource("img/green-off.png")));
    iconset = new HashMap<>();
    ICONS.put(Pin.Mode.OUTPUT, iconset);
    iconset.put("on", new ImageIcon(classLoader.getResource("img/blue-on.png")));
    iconset.put("off", new ImageIcon(classLoader.getResource("img/blue-off.png")));
    DISABLED_ON = new ImageIcon(classLoader.getResource("img/gray-on.png"));
    DISABLED_OFF = new ImageIcon(classLoader.getResource("img/gray-off.png"));
  }

  public JPin(Pin pin) {
    setHorizontalAlignment(JLabel.CENTER);
    modesMenu = new JPopupMenu(String.valueOf(pin.getIndex()));
    setModel(pin);
    addMouseListener(new MouseAdapter() {
      @Override public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          if (model.getMode() == Pin.Mode.OUTPUT) {
            long newValue = (model.getValue() == 0 ? 1 : 0);
            try {
              model.setValue(newValue);
            } catch (IOException ex) {
              JOptionPane.showMessageDialog(JPin.this, ex.getLocalizedMessage(), "", JOptionPane.ERROR_MESSAGE);
            }
          }
        }
      }

      @Override public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
          modesMenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }

      @Override public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          modesMenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });
  }

  @Override public void onModeChange(IOEvent event) {
    SwingUtilities.invokeLater(refreshRoutine);
  }

  @Override public void onValueChange(IOEvent event) {
    SwingUtilities.invokeLater(refreshRoutine);
  }

  public final void setModel(final Pin model) {
    if (this.model != null) {
      this.model.removeEventListener(this);
      modesMenu.removeAll();
    }
    this.model = model;
    ButtonGroup group = new ButtonGroup();
    for (Pin.Mode mode : model.getSupportedModes()) {
      Action action = new AbstractAction(mode.name()) {
        @Override public void actionPerformed(ActionEvent e) {
          try {
            model.setMode((Pin.Mode) getValue("mode"));
          } catch (IOException ex) {
            JOptionPane.showMessageDialog(JPin.this, ex.getLocalizedMessage(), "", JOptionPane.ERROR_MESSAGE);
          }
        }
      };
      action.putValue("mode", mode);
      JMenuItem item = new JRadioButtonMenuItem(action);
      item.setSelected(model.getMode() == mode);
      group.add(item);
      modesMenu.add(item);
    }
    model.addEventListener(this);
    refreshIcon();
  }

  private void refreshIcon() {
    Pin.Mode mode = model.getMode();
    setToolTipText(String.valueOf(mode));
    if (mode == null) {
      setIcon(DISABLED_OFF);
      setToolTipText("disabled");
    } else {
      if (ICONS.containsKey(mode)) {
        String key = (model.getValue() == 0 ? "off" : "on");
        setIcon(ICONS.get(model.getMode()).get(key));
        setText(null);
      } else {
        if (mode == Pin.Mode.ANALOG) {
          setText(String.valueOf(model.getValue()));
          setIcon(null);
        } else {
          setIcon(model.getValue() == 0 ? DISABLED_OFF : DISABLED_ON);
          setText(mode.name());
        }
      }
    }
  }
}