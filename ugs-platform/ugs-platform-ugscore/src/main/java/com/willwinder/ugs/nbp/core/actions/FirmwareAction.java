package com.willwinder.ugs.nbp.core.actions;
import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.ugs.nbp.lib.services.LocalizingService;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.listeners.ControllerState;
import com.willwinder.universalgcodesender.listeners.UGSEventListener;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.events.ControllerStateEvent;
import com.willwinder.universalgcodesender.model.events.SettingChangedEvent;
import com.willwinder.universalgcodesender.utils.FirmwareUtils;
import static com.willwinder.universalgcodesender.utils.GUIHelpers.displayErrorDialog;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import java.awt.*;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;

@ActionID(category = LocalizingService.ConnectionFirmwareToolbarCategory, id = LocalizingService.ConnectionFirmwareToolbarActionId) @ActionRegistration(iconBase = FirmwareAction.ICON_BASE, displayName = "resources.MessagesBundle#" + LocalizingService.ConnectionFirmwareToolbarTitleKey, lazy = false) @ActionReferences(value = { @ActionReference(path = "Toolbars/Connection", position = 980) }) public class FirmwareAction extends CallableSystemAction implements UGSEventListener {
  public static final String ICON_BASE = "resources/icons/firmware.svg";

  private final BackendAPI backend;

  private Component c;

  private JComboBox<String> firmwareCombo;

  public FirmwareAction() {
    this.backend = CentralLookup.getDefault().lookup(BackendAPI.class);
    this.backend.addUGSEventListener(this);
    putValue(SMALL_ICON, ImageUtilities.loadImageIcon(ICON_BASE, false));
    putValue(NAME, LocalizingService.ConnectionFirmwareToolbarTitle);
  }

  private void setFirmware() {
    String firmware = firmwareCombo.getSelectedItem().toString();
    backend.getSettings().setFirmwareVersion(firmware);
  }

  private void firmwareUpdated() {
    firmwareCombo.setSelectedItem(backend.getSettings().getFirmwareVersion());
  }

  @Override public void performAction() {
    backend.getSettings().setFirmwareVersion(firmwareCombo.getSelectedItem() + "");
  }

  @Override public HelpCtx getHelpCtx() {
    return null;
  }

  @Override public Component getToolbarPresenter() {
    if (c == null) {
      firmwareCombo = new JComboBox<>();
      JPanel panel = new JPanel(new FlowLayout());
      panel.add(new JLabel(Localization.getString("mainWindow.swing.firmwareLabel")));
      panel.add(firmwareCombo);
      c = panel;
      loadFirmwareSelector();
      firmwareCombo.addActionListener((a) -> setFirmware());
      loadFirmwareSelector();
    }
    return c;
  }

  @Override public String getName() {
    return LocalizingService.ConnectionFirmwareToolbarTitle;
  }

  @Override public void UGSEvent(com.willwinder.universalgcodesender.model.UGSEvent evt) {
    if (c == null) {
      return;
    }
    if (evt instanceof SettingChangedEvent) {
      firmwareUpdated();
    } else {
      if (evt instanceof ControllerStateEvent) {
        c.setVisible(backend.getControllerState() == ControllerState.DISCONNECTED);
      }
    }
  }

  private void loadFirmwareSelector() {
    firmwareCombo.removeAllItems();
    java.util.List<String> firmwareList = FirmwareUtils.getFirmwareList();
    if (firmwareList.size() < 1) {
      displayErrorDialog(Localization.getString("mainWindow.error.noFirmware"));
    } else {
      firmwareList.forEach(firmwareCombo::addItem);
    }
    firmwareUpdated();
  }
}