package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Document;
import com.svenjacobs.gwtbootstrap3.client.ui.base.ComplexWidget;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * @author Joshua Godi
 */
public class UnorderedList extends ComplexWidget implements HasResponsiveness {
    /**
     * Creates an empty list.
     */
    public UnorderedList() {
        setElement(Document.get().createULElement());
    }

    /**
     * Creates a list and adds the given widgets.
     *
     * @param widgets widgets to be added
     */
    public UnorderedList(final ListItem... widgets) {
        this();
        for (final ListItem li : widgets) {
            add(li);
        }
    }

    public void setUnstyled(final boolean unstyled) {
        setStyleName(Styles.UNSTYLED, unstyled);
    }

    public void setInline(final boolean inline) {
        if (inline) {
            addStyleName(Styles.LIST_INLINE);
        }
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