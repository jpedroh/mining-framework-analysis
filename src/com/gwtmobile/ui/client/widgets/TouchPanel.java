package com.gwtmobile.ui.client.widgets;
import com.gwtmobile.ui.client.event.DragController;
import com.gwtmobile.ui.client.event.DragControllerMobile;

/**
 * A touch panel that allows propagation of touch events to child widgets.
 * Use for displaying maps that allow the user to pan and zoom.
 * 
 * @author Frank Mena
 *
 */
public class TouchPanel extends PanelBase {
  @Override public void onLoad() {
    super.onLoad();
    startTouch();
  }

  @Override public void onUnload() {
    stopTouch();
  }

  /**
     * Start touch. Useful when you have more than one touch target on 
     * different tabs of the same page.
     */
  public void startTouch() {
    DragController drag = DragController.get();
    if (drag instanceof DragControllerMobile) {
      ((DragControllerMobile) drag).setStartPropagation();
    }
  }

  /**
     * Stop touch.
     */
  public void stopTouch() {
    DragController drag = DragController.get();
    if (drag instanceof DragControllerMobile) {
      ((DragControllerMobile) drag).setStopPropagation();
    }
  }
}