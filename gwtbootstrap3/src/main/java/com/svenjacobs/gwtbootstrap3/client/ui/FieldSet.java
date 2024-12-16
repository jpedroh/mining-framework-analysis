package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.HasEnabled;
import com.svenjacobs.gwtbootstrap3.client.ui.base.ComplexWidget;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;


/**
 * @author Sven Jacobs
 * @author Joshua Godi
 * @see Form
 */
public class FieldSet extends ComplexWidget implements HasEnabled , HasResponsiveness {
    public FieldSet() {
        setElement(Document.get().createFieldSetElement());
    }

    @Override
    public void setEnabled(final boolean enabled) {
        getElement().setPropertyBoolean("disabled", !enabled);
    }

    @Override
    public boolean isEnabled() {
        return !getElement().getPropertyBoolean("disabled");
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