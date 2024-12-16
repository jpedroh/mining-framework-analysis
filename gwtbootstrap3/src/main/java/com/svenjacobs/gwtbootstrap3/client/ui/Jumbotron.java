package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Document;
import com.svenjacobs.gwtbootstrap3.client.ui.base.ComplexWidget;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * A lightweight, flexible component to showcase key content.
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 */
public class Jumbotron extends ComplexWidget implements HasResponsiveness {
    public Jumbotron() {
        setElement(Document.get().createDivElement());
        setStyleName(Styles.JUMBOTRON);
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