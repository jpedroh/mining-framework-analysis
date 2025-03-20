package org.gwtbootstrap3.client.ui;
import org.gwtbootstrap3.client.shared.event.HiddenEvent;
import org.gwtbootstrap3.client.shared.event.HiddenHandler;
import org.gwtbootstrap3.client.shared.event.HideEvent;
import org.gwtbootstrap3.client.shared.event.HideHandler;
import org.gwtbootstrap3.client.shared.event.ShowEvent;
import org.gwtbootstrap3.client.shared.event.ShowHandler;
import org.gwtbootstrap3.client.shared.event.ShownEvent;
import org.gwtbootstrap3.client.shared.event.ShownHandler;
import org.gwtbootstrap3.client.ui.constants.Styles;
import com.google.gwt.event.shared.HandlerRegistration;
import org.gwtbootstrap3.client.ui.html.Div;
import com.google.gwt.user.client.Event;

/**
 * @author Grant Slender
 */
public class Collapse extends Div {
  private static final String TOGGLE = "toggle";

  private static final String SHOW = "show";

  private static final String HIDE = "hide";

  private boolean toggle = true;

  public Collapse() {
    setStyleName(Styles.COLLAPSE);
  }

  @Override protected void onLoad() {
    super.onLoad();
    bindJavaScriptEvents(getElement());
    if (toggle) {
      addStyleName(Styles.IN);
    }
  }

  @Override protected void onUnload() {
    super.onUnload();
    unbindJavaScriptEvents(getElement());
  }

  /**
     * Sets the default state to show or hide. Show is true.
     *
     * @param toggle toggle the collapse
     */
  public void setToggle(final boolean toggle) {
    this.toggle = toggle;
  }

  /**
     * Causes the collapse to show or hide
     */
  public void toggle() {
    fireMethod(getElement(), TOGGLE);
  }

  /**
     * Causes the collapse to show
     */
  public void show() {
    fireMethod(getElement(), SHOW);
  }

  /**
     * Causes the collapse to hide
     */
  public void hide() {
    fireMethod(getElement(), HIDE);
  }

  public boolean isShown() {
    return this.getElement().hasClassName(Styles.IN);
  }

  public boolean isHidden() {
    return !isShown();
  }

  public boolean isCollapsing() {
    return this.getElement().hasClassName(Styles.COLLAPSING);
  }

  public HandlerRegistration addShowHandler(final ShowHandler showHandler) {
    return addHandler(showHandler, ShowEvent.getType());
  }

  public HandlerRegistration addShownHandler(final ShownHandler shownHandler) {
    return addHandler(shownHandler, ShownEvent.getType());
  }

  public HandlerRegistration addHideHandler(final HideHandler hideHandler) {
    return addHandler(hideHandler, HideEvent.getType());
  }

  public HandlerRegistration addHiddenHandler(final HiddenHandler hiddenHandler) {
    return addHandler(hiddenHandler, HiddenEvent.getType());
  }

  /**
     * Fired when the collapse is starting to show
     */
  private void onShow(final Event evt) {
    fireEvent(new ShowEvent(evt));
  }

  /**
     * Fired when the collapse has shown
     */
  private void onShown(final Event evt) {
    fireEvent(new ShownEvent(evt));
  }

  /**
     * Fired when the collapse is starting to hide
     */
  private void onHide(final Event evt) {
    fireEvent(new HideEvent(evt));
  }

  /**
     * Fired when the collapse has hidden
     */
  private void onHidden(final Event evt) {
    fireEvent(new HiddenEvent(evt));
  }

  private native void bindJavaScriptEvents(final com.google.gwt.dom.client.Element e);

  private native void unbindJavaScriptEvents(final com.google.gwt.dom.client.Element e);

  private native void collapse(final com.google.gwt.dom.client.Element e, final boolean toggle);

  private native void fireMethod(final com.google.gwt.dom.client.Element e, String method);

  private native void fireMethod(final com.google.gwt.dom.client.Element e, int slideNumber);
}