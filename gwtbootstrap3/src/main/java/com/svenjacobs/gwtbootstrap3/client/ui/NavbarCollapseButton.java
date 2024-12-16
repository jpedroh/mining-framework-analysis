package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Toggle;


/**
 * Special button to toggle collapsible area of {@link Navbar}.
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 * @see NavbarCollapse
 */
public class NavbarCollapseButton extends Composite implements HasTarget , HasResponsiveness {
    private final Button button;

    public NavbarCollapseButton() {
        button = new Button();
        button.setStyleName(Styles.NAVBAR_TOGGLE);
        button.setToggle(Toggle.COLLAPSE);
        button.add(newBarIcon());
        button.add(newBarIcon());
        button.add(newBarIcon());
        initWidget(button);
    }

    @Override
    public void setTarget(final String target) {
        button.setTarget(target);
    }

    @Override
    public String getTarget() {
        return button.getTarget();
    }

    @Override
    public void setVisibleOn(final String deviceSizeString) {
        StyleHelper.setVisibleOn(this, deviceSizeString);
    }

    @Override
    public void setHiddenOn(final String deviceSizeString) {
        StyleHelper.setHiddenOn(this, deviceSizeString);
    }

    private Span newBarIcon() {
        final Span span = new Span();
        span.setStyleName(Styles.ICON_BAR);
        return span;
    }
}