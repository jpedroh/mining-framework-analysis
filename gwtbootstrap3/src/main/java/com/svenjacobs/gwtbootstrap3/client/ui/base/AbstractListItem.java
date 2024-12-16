package com.svenjacobs.gwtbootstrap3.client.ui.base;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.svenjacobs.gwtbootstrap3.client.ui.HasActive;
import com.svenjacobs.gwtbootstrap3.client.ui.HasPull;
import com.svenjacobs.gwtbootstrap3.client.ui.HasResponsiveness;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.base.mixin.ActiveMixin;
import com.svenjacobs.gwtbootstrap3.client.ui.base.mixin.PullMixin;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Pull;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * Base class for list items.
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 * @see com.svenjacobs.gwtbootstrap3.client.ui.ListItem
 * @see com.svenjacobs.gwtbootstrap3.client.ui.ListDropDown
 */
public abstract class AbstractListItem extends ComplexPanel implements HasEnabled , HasPull , HasActive , HasResponsiveness {
    private final ActiveMixin<AbstractListItem> activeMixin = new ActiveMixin<AbstractListItem>(this);

    private final PullMixin<AbstractListItem> pullMixin = new PullMixin<AbstractListItem>(this);

    protected AbstractListItem() {
        setElement(Document.get().createLIElement());
    }

    @Override
    public void setEnabled(final boolean enabled) {
        if (enabled) {
            removeStyleName(Styles.DISABLED);
        } else {
            addStyleName(Styles.DISABLED);
        }
    }

    @Override
    public boolean isEnabled() {
        return !StyleHelper.containsStyle(getStyleName(), Styles.DISABLED);
    }

    @Override
    public void setPull(final Pull pull) {
        pullMixin.setPull(pull);
    }

    @Override
    public Pull getPull() {
        return pullMixin.getPull();
    }

    @Override
    public void setActive(final boolean active) {
        activeMixin.setActive(active);
    }

    @Override
    public boolean isActive() {
        return activeMixin.isActive();
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