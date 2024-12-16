package com.svenjacobs.gwtbootstrap3.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.svenjacobs.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import com.svenjacobs.gwtbootstrap3.client.ui.base.mixin.IdMixin;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;


/**
 * Page header with optional subtext
 * <p/>
 * <h3>UiBinder example</h3>
 *
 * <pre>
 * {@code <b:PageHeader subText="Some subtext">Page header title</b:PageHeader>}
 * </pre>
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 */
public class PageHeader extends Widget implements HasText , HasId , HasResponsiveness {
    private final IdMixin<PageHeader> idMixin = new IdMixin<PageHeader>(this);

    private String heading;

    private String subText;

    public PageHeader() {
        setElement(Document.get().createDivElement());
        setStyleName(Styles.PAGE_HEADER);
    }

    public void setSubText(final String subText) {
        this.subText = subText;
        render();
    }

    @Override
    public void setText(final String text) {
        heading = text;
        render();
    }

    @Override
    public String getText() {
        return heading;
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
    public void setVisibleOn(final String deviceSizeString) {
        StyleHelper.setVisibleOn(this, deviceSizeString);
    }

    @Override
    public void setHiddenOn(final String deviceSizeString) {
        StyleHelper.setHiddenOn(this, deviceSizeString);
    }

    private void render() {
        final SafeHtmlBuilder builder = new SafeHtmlBuilder();

        builder.appendHtmlConstant("<h1>");
        builder.appendEscaped(heading == null ? "" : heading);

        if (subText != null && !subText.isEmpty()) {
            builder.appendEscaped(" ");
            builder.appendHtmlConstant("<small>");
            builder.appendEscaped(subText);
            builder.appendHtmlConstant("</small>");
        }

        builder.appendHtmlConstant("</h1>");

        getElement().setInnerSafeHtml(builder.toSafeHtml());
    }
}