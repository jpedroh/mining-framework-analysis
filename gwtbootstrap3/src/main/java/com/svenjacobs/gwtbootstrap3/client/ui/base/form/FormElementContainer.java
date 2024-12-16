package com.svenjacobs.gwtbootstrap3.client.ui.base.form;

import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.svenjacobs.gwtbootstrap3.client.ui.HasResponsiveness;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * Base class for {@link com.svenjacobs.gwtbootstrap3.client.ui.Form} related
 * widgets that may contain form input elements.
 * <p/>
 * Input elements styleName is adjusted during
 * {@link #add(com.google.gwt.user.client.ui.Widget)}.
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 */
public abstract class FormElementContainer extends ComplexPanel implements HasResponsiveness {
    @Override
    public void add(final Widget w) {
        if ((w instanceof ListBox) || (w instanceof FileUpload)) {
            w.addStyleName(Styles.FORM_CONTROL);
        }
        add(w, getElement());
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