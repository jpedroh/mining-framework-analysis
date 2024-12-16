package com.svenjacobs.gwtbootstrap3.client.ui.base;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.svenjacobs.gwtbootstrap3.client.ui.HasId;
import com.svenjacobs.gwtbootstrap3.client.ui.HasResponsiveness;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.base.mixin.IdMixin;


/**
 * Base class for {@link Widget} that just contains text.
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 */
public abstract class AbstractTextWidget extends Widget implements HasId , HasText , HasResponsiveness {
    private final IdMixin<AbstractTextWidget> idMixin = new IdMixin<AbstractTextWidget>(this);

    protected AbstractTextWidget(final Element element) {
        setElement(element);
    }

    @Override
    public void setId(final String id) {
        idMixin.setId(id);
    }

    @Override
    public String getId() {
        return idMixin.getId();
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