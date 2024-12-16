package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Widget;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.IconFlip;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.IconRotate;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.IconSize;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.IconType;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * Simple put, an icon.
 *
 * @author Sven Jacobs
 * @see IconType
 */
public class Icon extends Widget implements HasType<IconType> , HasResponsiveness {
    public Icon() {
        setElement(Document.get().createElement("i"));
        addStyleName(Styles.FONT_AWESOME_BASE);
    }

    @UiConstructor
    public Icon(final IconType type) {
        this();
        setType(type);
    }

    @Override
    public void setType(final IconType type) {
        StyleHelper.addUniqueEnumStyleName(this, IconType.class, type);
    }

    @Override
    public IconType getType() {
        return IconType.fromStyleName(getStyleName());
    }

    @Override
    public void setVisibleOn(final String deviceSizeString) {
        StyleHelper.setVisibleOn(this, deviceSizeString);
    }

    @Override
    public void setHiddenOn(final String deviceSizeString) {
        StyleHelper.setHiddenOn(this, deviceSizeString);
    }

    public void setLight(final boolean light) {
        StyleHelper.toggleStyleName(this, light, Styles.ICON_LIGHT);
    }

    public void setMuted(final boolean muted) {
        StyleHelper.toggleStyleName(this, muted, Styles.ICON_MUTED);
    }

    public void setBorder(final boolean border) {
        StyleHelper.toggleStyleName(this, border, Styles.ICON_BORDER);
    }

    public void setStackBase(final boolean stackBase) {
        StyleHelper.toggleStyleName(this, stackBase, Styles.ICON_STACK_BASE);
    }

    public void setFixedWidth(final boolean fixedWidth) {
        StyleHelper.toggleStyleName(this, fixedWidth, Styles.ICON_FIXED_WIDTH);
    }

    public void setStackTop(final boolean stackTop) {
        StyleHelper.toggleStyleName(this, stackTop, Styles.ICON_STACK_TOP);
    }

    public void setSpin(final boolean spin) {
        StyleHelper.toggleStyleName(this, spin, Styles.ICON_SPIN);
    }

    public void setRotate(final IconRotate iconRotate) {
        if ((iconRotate == null) || (iconRotate == IconRotate.NONE)) {
            return;
        }
        StyleHelper.addUniqueEnumStyleName(this, IconRotate.class, iconRotate);
    }

    public void setFlip(final IconFlip iconFlip) {
        if ((iconFlip == null) || (iconFlip == IconFlip.NONE)) {
            return;
        }
        StyleHelper.addUniqueEnumStyleName(this, IconFlip.class, iconFlip);
    }

    public void setSize(final IconSize iconSize) {
        if ((iconSize == null) || (iconSize == IconSize.NONE)) {
            return;
        }
        StyleHelper.addUniqueEnumStyleName(this, IconSize.class, iconSize);
    }
}