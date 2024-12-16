package com.svenjacobs.gwtbootstrap3.client.ui.base;

import com.google.gwt.dom.client.Element;
import com.svenjacobs.gwtbootstrap3.client.ui.HasResponsiveness;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * @author Sven Jacobs
 * @author Joshua Godi
 */
public class AbstractDropDown extends ComplexWidget implements HasResponsiveness {
    public AbstractDropDown(final Element element) {
        setElement(element);
        setStyleName(Styles.DROPDOWN);
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