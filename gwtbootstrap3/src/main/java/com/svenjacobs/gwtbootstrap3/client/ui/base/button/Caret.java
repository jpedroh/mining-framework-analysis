package com.svenjacobs.gwtbootstrap3.client.ui.base.button;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;
import com.svenjacobs.gwtbootstrap3.client.ui.HasResponsiveness;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * @author Sven Jacobs
 * @author Joshua Godi
 */
class Caret extends Widget implements HasResponsiveness {
    public Caret() {
        setElement(Document.get().createSpanElement());
        setStyleName(Styles.CARET);
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