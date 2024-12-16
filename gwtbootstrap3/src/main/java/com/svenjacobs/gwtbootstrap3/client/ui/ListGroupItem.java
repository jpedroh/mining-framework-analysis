package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Document;
import com.svenjacobs.gwtbootstrap3.client.ui.base.ComplexWidget;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * @author Joshua Godi
 */
public class ListGroupItem extends ComplexWidget implements HasResponsiveness {
    private final Span span = new Span();

    public ListGroupItem() {
        setElement(Document.get().createLIElement());
        setStyleName(Styles.LIST_GROUP_ITEM);
        add(span);
    }

    public String getText() {
        return span.getText();
    }

    public void setText(final String text) {
        span.setText(text);
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