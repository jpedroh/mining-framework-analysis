package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * @author Joshua Godi
 */
public class IconStack extends ComplexPanel implements HasResponsiveness {
    public IconStack() {
        setElement(Document.get().createSpanElement());
        getElement().addClassName(Styles.ICON_STACK);
    }

    /**
     * Adds an icon onto the icon stack
     *
     * @param icon Icon
     * @param base Bottom icon or not
     */
    public void add(final Icon icon, final boolean base) {
        icon.setStackBase(base);
        add(icon);
    }

    @Override
    public void add(final Widget child) {
        if (!(child instanceof Icon)) {
            throw new IllegalArgumentException("An IconStack can only have children that are of type Icon.");
        }

        add(child, getElement());
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