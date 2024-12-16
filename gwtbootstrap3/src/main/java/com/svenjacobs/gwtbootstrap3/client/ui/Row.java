package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.svenjacobs.gwtbootstrap3.client.ui.base.ComplexWidget;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * A row of Bootstrap's fluid grid system.
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 * @see Column
 */
public class Row extends ComplexWidget implements HasResponsiveness {
    public Row() {
        setElement(Document.get().createDivElement());
        setStyleName(Styles.ROW);
    }

    public void setMarginTop(final int marginTop) {
        getElement().getStyle().setMarginTop(marginTop, Style.Unit.PX);
    }

    public void setMarginBottom(final int marginBottom) {
        getElement().getStyle().setMarginBottom(marginBottom, Style.Unit.PX);
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