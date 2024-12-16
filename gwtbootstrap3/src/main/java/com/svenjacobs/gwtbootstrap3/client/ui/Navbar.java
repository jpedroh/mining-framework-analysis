package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Document;
import com.svenjacobs.gwtbootstrap3.client.ui.base.ComplexWidget;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.NavbarPosition;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.NavbarType;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * @author Sven Jacobs
 * @author Joshua Godi
 * @see NavbarBrand
 * @see NavbarNav
 * @see NavbarForm
 * @see NavbarText
 */
public class Navbar extends ComplexWidget implements HasType<NavbarType> , HasResponsiveness {
    public Navbar() {
        setElement(Document.get().createElement("nav"));
        setStyleName(Styles.NAVBAR);
        setType(NavbarType.DEFAULT);
        getElement().setAttribute("role", "navigation");
    }

    @Override
    public void setType(final NavbarType type) {
        StyleHelper.addUniqueEnumStyleName(this, NavbarType.class, type);
    }

    @Override
    public NavbarType getType() {
        return NavbarType.fromStyleName(getStyleName());
    }

    @Override
    public void setVisibleOn(final String deviceSizeString) {
        StyleHelper.setVisibleOn(this, deviceSizeString);
    }

    @Override
    public void setHiddenOn(final String deviceSizeString) {
        StyleHelper.setHiddenOn(this, deviceSizeString);
    }

    public void setPosition(final NavbarPosition type) {
        StyleHelper.addUniqueEnumStyleName(this, NavbarPosition.class, type);
    }

    public NavbarPosition getPosition() {
        return NavbarPosition.fromStyleName(getStyleName());
    }
}