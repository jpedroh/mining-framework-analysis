package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Document;
import com.svenjacobs.gwtbootstrap3.client.ui.base.ComplexWidget;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;


/**
 * @author Joshua Godi
 */
public class Hr extends ComplexWidget implements HasResponsiveness {
    public Hr() {
        setElement(Document.get().createHRElement());
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