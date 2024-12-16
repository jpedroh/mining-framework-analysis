package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.svenjacobs.gwtbootstrap3.client.ui.base.TextBoxBase;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 *
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 * @author Pontus Enmark
 */
public class TextBox extends TextBoxBase {
    public TextBox() {
        this(DOM.createInputText());
    }

    public TextBox(final Element element) {
        super(element);
        setStyleName(Styles.FORM_CONTROL);
    }

    public void clear() {
        super.setValue(null);
    }
}