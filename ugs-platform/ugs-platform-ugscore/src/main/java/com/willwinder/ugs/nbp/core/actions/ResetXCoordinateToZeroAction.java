package com.willwinder.ugs.nbp.core.actions;
import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.ugs.nbp.lib.services.LocalizingService;
import com.willwinder.universalgcodesender.listeners.ControllerState;
import com.willwinder.universalgcodesender.listeners.UGSEventListener;
import static com.willwinder.universalgcodesender.model.Axis.X;
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

@ActionID(category = LocalizingService.ResetXZeroCategory, id = LocalizingService.ResetXZeroActionId) @ActionRegistration(iconBase = ResetXCoordinateToZeroAction.ICON_BASE, displayName = "resources.MessagesBundle#" + LocalizingService.ResetXZeroTitleKey, lazy = false) @ActionReferences(value = { @ActionReference(path = LocalizingService.ResetXZeroWindowPath, position = 1030) }) public final class ResetXCoordinateToZeroAction extends AbstractAction implements UGSEventListener {
  public static final String ICON_BASE = "resources/icons/resetzero_x.svg";

  private BackendAPI backend;

  public ResetXCoordinateToZeroAction() {
    this.backend = CentralLookup.getDefault().lookup(BackendAPI.class);
    this.backend.addUGSEventListener(this);
    putValue("iconBase", ICON_BASE);
    putValue(SMALL_ICON, ImageUtilities.loadImageIcon(ICON_BASE, false));
    putValue("menuText", LocalizingService.ResetXZeroTitle);
    putValue(NAME, LocalizingService.ResetXZeroTitle);
    setEnabled(isEnabled());
  }

  @Override public void UGSEvent(UGSEvent cse) {
    if (cse instanceof ControllerStateEvent) {
      java.awt.EventQueue.invokeLater(() -> setEnabled(isEnabled()));
    }
  }

  @Override public boolean isEnabled() {
    return backend.isIdle() && backend.getControllerState() == ControllerState.IDLE;
  }

  @Override public void actionPerformed(ActionEvent e) {
    try {
      backend.resetCoordinateToZero(X);
    } catch (Exception ex) {
      GUIHelpers.displayErrorDialog(ex.getLocalizedMessage());
    }
  }
}