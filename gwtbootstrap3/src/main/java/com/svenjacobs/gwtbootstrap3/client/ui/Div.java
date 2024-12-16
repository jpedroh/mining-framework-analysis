package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Document;
import com.svenjacobs.gwtbootstrap3.client.ui.base.ComplexWidget;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;


/**
 * Simple {@code <div>} tag
 *
 * @author Joshua Godi
 */
public class Div extends ComplexWidget implements HasResponsiveness {
    public Div() {
        setElement(Document.get().createDivElement());
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