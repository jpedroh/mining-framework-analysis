package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * Divider used within {@link DropDownMenu} between {@link ListItem} elements.
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 */
public class Divider extends Widget implements HasResponsiveness {
    public Divider() {
        setElement(Document.get().createLIElement());
        setStyleName(Styles.DIVIDER);
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