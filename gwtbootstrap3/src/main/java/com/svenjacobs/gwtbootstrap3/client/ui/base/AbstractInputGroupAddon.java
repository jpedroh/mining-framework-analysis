package com.svenjacobs.gwtbootstrap3.client.ui.base;

import com.google.gwt.dom.client.Document;
import com.svenjacobs.gwtbootstrap3.client.ui.HasResponsiveness;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;


/**
 * Base class for {@link com.svenjacobs.gwtbootstrap3.client.ui.InputGroupAddon}
 * and {@link com.svenjacobs.gwtbootstrap3.client.ui.InputGroupButton}
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 * @see com.svenjacobs.gwtbootstrap3.client.ui.InputGroupAddon
 * @see com.svenjacobs.gwtbootstrap3.client.ui.InputGroupButton
 */
public abstract class AbstractInputGroupAddon extends ComplexWidget implements HasResponsiveness {
    protected AbstractInputGroupAddon(final String styleName) {
        setElement(Document.get().createSpanElement());
        setStyleName(styleName);
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