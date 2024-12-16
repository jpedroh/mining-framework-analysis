package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Event;
import com.svenjacobs.gwtbootstrap3.client.shared.event.AlertCloseEvent;
import com.svenjacobs.gwtbootstrap3.client.shared.event.AlertClosedEvent;
import com.svenjacobs.gwtbootstrap3.client.ui.base.button.CloseButton;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.AlertType;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.ButtonDismiss;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * Alert block.
 * <p/>
 * Use {@link #setDismissable(boolean)} to add a close ("x") button.
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 * @see AlertCloseEvent
 * @see AlertClosedEvent
 */
public class Alert extends HTMLPanel implements HasType<AlertType> , HasResponsiveness {
    private final CloseButton closeButton = new CloseButton();

    public Alert(final String html) {
        super(html);
        setStyleName(Styles.ALERT);
        setType(AlertType.WARNING);
        closeButton.setDismiss(ButtonDismiss.ALERT);
        bindJavaScriptEvents(getElement());
    }

    public Alert(final String html, final AlertType type) {
        this(html);
        setType(type);
    }

    public Alert(final SafeHtml safeHtml) {
        this(safeHtml.asString());
    }

    public Alert(final SafeHtml safeHtml, final AlertType type) {
        this(safeHtml.asString(), type);
    }

    /**
     * Sets alert type.
     *
     * @param type Alert type
     * @see AlertType
     */
    @Override
    public void setType(final AlertType type) {
        StyleHelper.addUniqueEnumStyleName(this, AlertType.class, type);
    }

    @Override
    public AlertType getType() {
        return AlertType.fromStyleName(getStyleName());
    }

    /**
     * Adds a close button to the alert
     *
     * @param dismissable Adds close button when {@code true}
     */
    public void setDismissable(final boolean dismissable) {
        if (dismissable) {
            insert(closeButton, getElement(), 0, true);
            addStyleName(Styles.ALERT_DISMISSABLE);
        } else {
            closeButton.removeFromParent();
            removeStyleName(Styles.ALERT_DISMISSABLE);
        }
    }

    public boolean isDismissable() {
        return closeButton.getParent() != null;
    }

    /**
     * Closes alert.
     */
    public void close() {
        alert(getElement(), "close");
    }

    protected void onClose(final Event evt) {
        fireEvent(new AlertCloseEvent(evt));
    }

    protected void onClosed(final Event evt) {
        fireEvent(new AlertClosedEvent(evt));
    }

    @Override
    public void setVisibleOn(final String deviceSizeString) {
        StyleHelper.setVisibleOn(this, deviceSizeString);
    }

    @Override
    public void setHiddenOn(final String deviceSizeString) {
        StyleHelper.setHiddenOn(this, deviceSizeString);
    }

    // @formatter:off
    /*-{
        $wnd.jQuery(e).alert(arg);
    }-*/
    private native void alert(final Element e, final String arg);

    /*-{
        var target = this;
        var $alert = $wnd.jQuery(e);

        $alert.on('close.bs.alert', function (evt) {
            target.@com.svenjacobs.gwtbootstrap3.client.ui.Alert::onClose(Lcom/google/gwt/user/client/Event;)(evt);
        });

        $alert.on('closed.bs.alert', function (evt) {
            target.@com.svenjacobs.gwtbootstrap3.client.ui.Alert::onClosed(Lcom/google/gwt/user/client/Event;)(evt);
        });
    }-*/
    private native void bindJavaScriptEvents(final Element e);
}