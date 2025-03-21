package com.willwinder.ugs.nbp.core.actions;
import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.ugs.nbp.lib.services.LocalizingService;
import com.willwinder.universalgcodesender.listeners.ControllerState;
import com.willwinder.universalgcodesender.listeners.UGSEventListener;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.UGSEvent;
import com.willwinder.universalgcodesender.model.events.ControllerStateEvent;
import com.willwinder.universalgcodesender.utils.GUIHelpers;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import javax.swing.*;
import java.awt.event.ActionEvent;

@ActionID(category = LocalizingService.CheckModeCategory, id = LocalizingService.CheckModeActionId) @ActionRegistration(iconBase = CheckModeAction.ICON_BASE, displayName = "resources.MessagesBundle#" + LocalizingService.CheckModeTitleKey, lazy = false) @ActionReferences(value = { @ActionReference(path = LocalizingService.CheckModeWindowPath, position = 1040) }) public final class CheckModeAction extends AbstractAction implements UGSEventListener {
  public static final String ICON_BASE = "resources/icons/check.svg";

  private final BackendAPI backend;

  public CheckModeAction() {
    this.backend = CentralLookup.getDefault().lookup(BackendAPI.class);
    this.backend.addUGSEventListener(this);
    putValue("iconBase", ICON_BASE);
    putValue(SMALL_ICON, ImageUtilities.loadImageIcon(ICON_BASE, false));
    putValue("menuText", LocalizingService.CheckModeTitle);
    putValue(NAME, LocalizingService.CheckModeTitle);
    setEnabled(isEnabled());
  }

  @Override public void UGSEvent(UGSEvent cse) {
    if (cse instanceof ControllerStateEvent) {
      java.awt.EventQueue.invokeLater(() -> setEnabled(isEnabled()));
    }
  }

  @Override public boolean isEnabled() {
    return backend.isConnected() && backend.isIdle() && backend.getController().getCapabilities().hasCheckMode();
  }

  @Override public void actionPerformed(ActionEvent e) {
    try {
      backend.toggleCheckMode();
    } catch (Exception ex) {
      GUIHelpers.displayErrorDialog(ex.getLocalizedMessage());
    }
  }
}