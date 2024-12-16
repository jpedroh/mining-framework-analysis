package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Document;
import com.svenjacobs.gwtbootstrap3.client.ui.base.ComplexWidget;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * Div element that automatically centers contents.
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 * @see Row
 * @see Column
 */
public class Container extends ComplexWidget implements HasResponsiveness {
    public Container() {
        setElement(Document.get().createDivElement());
        setStyleName(Styles.CONTAINER);
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