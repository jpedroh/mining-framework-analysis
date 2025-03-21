package com.willwinder.ugs.nbp.core.actions;
import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.ugs.nbp.lib.services.LocalizingService;
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
import java.awt.*;
import java.awt.event.ActionEvent;

@ActionID(category = LocalizingService.StopCategory, id = LocalizingService.StopActionId) @ActionRegistration(iconBase = StopAction.ICON_BASE, displayName = "resources.MessagesBundle#" + LocalizingService.StopTitleKey, lazy = false) @ActionReferences(value = { @ActionReference(path = LocalizingService.StopWindowPath, position = 1010), @ActionReference(path = "Toolbars/StartPauseStop", position = 1010) }) public final class StopAction extends AbstractAction implements UGSEventListener {
  public static final String ICON_BASE = "resources/icons/stop.svg";

  private BackendAPI backend;

  public StopAction() {
    this.backend = CentralLookup.getDefault().lookup(BackendAPI.class);
    this.backend.addUGSEventListener(this);
    putValue("iconBase", ICON_BASE);
    putValue(SMALL_ICON, ImageUtilities.loadImageIcon(ICON_BASE, false));
    putValue("menuText", LocalizingService.StopTitle);
    putValue(NAME, LocalizingService.StopTitle);
    setEnabled(isEnabled());
  }

  @Override public void UGSEvent(UGSEvent cse) {
    if (cse instanceof ControllerStateEvent) {
      EventQueue.invokeLater(() -> setEnabled(isEnabled()));
    }
  }

  @Override public boolean isEnabled() {
    return backend.canCancel();
  }

  @Override public void actionPerformed(ActionEvent e) {
    try {
      backend.cancel();
    } catch (Exception ex) {
      GUIHelpers.displayErrorDialog(ex.getLocalizedMessage());
    }
  }
}