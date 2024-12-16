package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * Header element within {@link DropDownMenu}
 * <p/>
 * <h3>UiBinder example</h3>
 *
 * <pre>
 * {@code <b:DropDownMenu>
 * <b:DropDownHeader>Header 1</b:DropDownHeader>
 * <b:ListItem>Action 1</b:ListItem>
 * <b:ListItem>Action 2</b:ListItem>
 * <b:DropDownHeader>Header 2</b:DropDownHeader>
 * <b:ListItem>Action 3</b:ListItem>
 * <b:ListItem>Action 4</b:ListItem>
 * </b:DropDownMenu>}
 * </pre>
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 */
public class DropDownHeader extends Widget implements HasText , HasResponsiveness {
    public DropDownHeader() {
        setElement(Document.get().createLIElement());
        setStyleName(Styles.DROPDOWN_HEADER);
    }

    @Override
    public void setText(final String text) {
        getElement().setInnerText(text);
    }

    @Override
    public String getText() {
        return getElement().getInnerText();
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