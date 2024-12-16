package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;
import com.svenjacobs.gwtbootstrap3.client.ui.base.ComplexWidget;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * @author Joshua Godi
 */
public class Description extends ComplexWidget implements HasResponsiveness {
    public Description() {
        setElement(Document.get().createDLElement());
    }

    public void setHorizontal(final boolean horizontal) {
        setStyleName(Styles.DL_HORIZONTAL, horizontal);
    }

    @Override
    public void add(final Widget child) {
        if (!(child instanceof DescriptionComponent)) {
            throw new IllegalArgumentException("Description can only have children of type DescriptionData and DescriptionTitle");
        }
        super.add(child);
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