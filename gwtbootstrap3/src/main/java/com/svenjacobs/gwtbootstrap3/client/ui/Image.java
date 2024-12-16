package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.base.mixin.PullMixin;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.ImageType;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Pull;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * @author Joshua Godi
 */
public class Image extends com.google.gwt.user.client.ui.Image implements HasType<ImageType> , HasResponsiveness , HasPull {
    private final PullMixin<Image> pullMixin = new PullMixin<Image>(this);

    public Image() {
        super();
        setStyleName("");
    }

    public Image(final ImageResource resource) {
        super(resource);
        setStyleName("");
    }

    public Image(final SafeUri url, final int left, final int top, final int width, final int height) {
        super(url, left, top, width, height);
        setStyleName("");
    }

    public Image(final SafeUri url) {
        super(url);
        setStyleName("");
    }

    public Image(final String url, final int left, final int top, final int width, final int height) {
        super(url, left, top, width, height);
        setStyleName("");
    }

    public Image(final String url) {
        super(url);
        setStyleName("");
    }

    @Override
    public void setType(final ImageType type) {
        StyleHelper.addEnumStyleName(this, type);
    }

    @Override
    public ImageType getType() {
        return ImageType.fromStyleName(getStyleName());
    }

    @Override
    public void setVisibleOn(final String deviceSizeString) {
        StyleHelper.setVisibleOn(this, deviceSizeString);
    }

    @Override
    public void setHiddenOn(final String deviceSizeString) {
        StyleHelper.setHiddenOn(this, deviceSizeString);
    }

    public void setResponsive(final boolean responsive) {
        StyleHelper.toggleStyleName(this, responsive, Styles.IMG_RESPONSIVE);
    }

    @Override
    public void setPull(final Pull pull) {
        pullMixin.setPull(pull);
    }

    @Override
    public Pull getPull() {
        return pullMixin.getPull();
    }
}