package com.svenjacobs.gwtbootstrap3.client.ui;

import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * Container for collapsible items within a {@link Navbar}.
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 * @see NavbarCollapseButton
 */
public class NavbarCollapse extends FlowPanel implements HasResponsiveness {
    public NavbarCollapse() {
        setStyleName(Styles.COLLAPSE);
        addStyleName(Styles.NAVBAR_COLLAPSE);
    }

    @Override
    public void setVisibleOn(final String deviceSizeString) {
        StyleHelper.setVisibleOn(this, deviceSizeString);
    }

    @Override
    public void setHiddenOn(final String deviceSizeString) {
        StyleHelper.setHiddenOn(this, deviceSizeString);
    }
}