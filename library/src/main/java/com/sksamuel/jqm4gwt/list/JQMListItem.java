package com.sksamuel.jqm4gwt.list;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.FieldSetElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sksamuel.jqm4gwt.DataIcon;
import com.sksamuel.jqm4gwt.HasText;
import com.sksamuel.jqm4gwt.IconPos;
import com.sksamuel.jqm4gwt.JQMCommon;
import com.sksamuel.jqm4gwt.panel.JQMControlGroup;

/**
 * @author Stephen K Samuel samspade79@gmail.com 5 May 2011 11:21:29
 */
public class JQMListItem extends Widget implements HasText<JQMListItem>, HasClickHandlers {

    /**
     * Element to hold the count bubble
     */
    private Element countElem;

    /**
     * Element to hold the image (thumbnail or icon)
     */
    private Element imageElem;

    /**
     * The element that contains the link, if any
     */
    private Element anchor;

    /** Split button element */
    private Element split;
    private String splitTheme;

    /**
     * The element that holds the aside content
     */
    private Element asideElem;

    /**
     * The element that holds the content of the "main" text
     */
    private Element headerElem;

    private JQMList list;

    public class LiControlGroup extends JQMControlGroup {

        protected LiControlGroup(Element element, String styleName) {
            super(element, styleName);
        }
    }

    private LiControlGroup controlGroup;
    private Element controlGroupRootElem;
    private TextBox checkBoxInput;


    /**
     * Create empty {@link JQMListItem}
     */
    @UiConstructor
    public JQMListItem() {
        setElement(Document.get().createLIElement());
        setStyleName("jqm4gwt-listitem");
        setId();
    }

    /**
     * Create {@link JQMListItem} with the initial content set to the value of the text.
     */
    public JQMListItem(String text) {
        this();
        if (text == null) throw new RuntimeException("Cannot create list item with null text");
        setText(text);
    }

    /**
     * Create a linked {@link JQMListItem} with the inital content set to the
     * value of the param text and the link set to the value of the param url.
     */
    public JQMListItem(String text, String url) {
        this(text);
        if (url != null) setUrl(url);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        setUrl("#");
        return addDomHandler(handler, ClickEvent.getType());
    }

    private boolean isSplitClicked(Element elt) {
        if (split == null || elt == null) return false;
        Element element = elt;
        while (element != null) {
            if (element == split) return true;
            element = element.getParentElement();
        }
        return false;
    }

    public boolean isSplitClicked(ClickEvent event) {
        if (event == null) return false;
        EventTarget target = event.getNativeEvent().getEventTarget();
        Element element = Element.as(target);
        return isSplitClicked(element);
    }

    /**
     * Adds a header element containing the given text.
     *
     * @param n    the Hn element to use, eg if n is 2 then a <h2>element is
     *             created
     * @param html the value to set as the inner html of the <hn> element
     */
    public JQMListItem addHeaderText(int n, String html) {
        Element e = Document.get().createHElement(n);
        e.setInnerHTML(html);
        attachChild(e);
        return this;
    }

    /**
     * Adds a paragraph element containing the given text
     *
     * @param html the value to set as the inner html of the p element
     */
    public JQMListItem addText(String html) {
        Element e = Document.get().createPElement();
        e.setInnerHTML(html);
        attachChild(e);
        return this;
    }

    private void attachChild(Element elem) {
        if (anchor == null) getElement().appendChild(elem);
        else if (controlGroup != null) controlGroup.getElement().appendChild(elem);
        else anchor.appendChild(elem);
    }

    private void removeChild(Element elem) {
        if (anchor == null) getElement().removeChild(elem);
        else if (controlGroup != null) controlGroup.getElement().removeChild(elem);
        else anchor.removeChild(elem);
    }

    // Probably this method is not needed, at least it's not raised in Chrome with "Emulate touch events".
    private native void bind(String id, JQMListItem item) /*-{
        $wnd.$(document).on("tap", "#"+id, function(event) {
            item.@com.sksamuel.jqm4gwt.list.JQMListItem::onTap(Lcom/google/gwt/core/client/JavaScriptObject;)(event.target);
        })
    }-*/;

    private native void unbind(String id) /*-{
        $wnd.$(document).off("tap", "#"+id)
    }-*/;

    @Override
    protected void onLoad() {
        bind(getElement().getId(), this);
    }

    @Override
    protected void onUnload() {
    	unbind(getElement().getId());
    }

    private void createAndAttachAsideElem() {
        asideElem = Document.get().createPElement();
        asideElem.setClassName("ui-li-aside");
        attachChild(asideElem);
    }

    private void createAndAttachCountElement() {
        countElem = Document.get().createSpanElement();
        countElem.setClassName("ui-li-count");
        attachChild(countElem);
    }

    /**
     * Returns the value of the count bubble or null if no count has been set
     */
    public Integer getCount() {
        if (countElem == null)
            return null;
        return Integer.parseInt(countElem.getInnerText());
    }

    /**
     * Returns the value of the "main" text element
     */
    @Override
    public String getText() {
        return headerElem != null ? headerElem.getInnerText() : null;
    }

    private void moveAnchorChildrenTo(Element elt) {
        for (int k = 0; k < anchor.getChildCount(); k++) {
            Node node = anchor.getChild(k);
            anchor.removeChild(node);
            elt.appendChild(node);
        }
    }

    private void moveAnchorChildrenToThis() {
        moveAnchorChildrenTo(getElement());
    }

    private void moveThisChildrenToAnchor() {
        for (int k = 0; k < getElement().getChildCount(); k++) {
            Node node = getElement().getChild(k);
            getElement().removeChild(node);
            anchor.appendChild(node);
        }
    }

    protected void onTap(JavaScriptObject jsObj) {
        Element elt = (Element) jsObj;
        list.setClickItem(this, isSplitClicked(elt));
    }

    /**
     * Removes the value of the aside element, if any. It is safe to call this
     * method regardless of if an aside has been set or not.
     */
    public JQMListItem removeAside() {
        if (asideElem != null) {
            getElement().removeChild(asideElem);
            asideElem = null;
        }
        return this;
    }

    /**
     * Removes the value of the count element if any. It is safe to call this
     * method regardless of if a count has been set or not.
     */
    public JQMListItem removeCount() {
        if (countElem != null) {
            getElement().removeChild(countElem);
            countElem = null;
        }
        return this;
    }

    /**
     * Removes the value of the image element if any. It is safe to call this
     * method regardless of if an image has been set or not.
     */
    public JQMListItem removeImage() {
        if (imageElem != null) {
            getElement().removeChild(imageElem);
            imageElem = null;
        }
        return this;
    }

    /**
     * Remove the url from this list item changing the item into a read only
     * item.
     */
    public JQMListItem removeUrl() {
        if (anchor == null)
            return null;
        moveAnchorChildrenToThis();
        getElement().removeChild(anchor);
        anchor = null;
        setSplitHref(null);
        return this;
    }

    /**
     * Sets the content of the aside. The aside is supplemental content that
     * is positioned to the right of the main content.
     */
    public JQMListItem setAside(String text) {
        if (text == null)
            throw new RuntimeException("Cannot set aside to null. Call removeAside() if you wanted to remove the aside text");

        if (asideElem == null)
            createAndAttachAsideElem();
        asideElem.setInnerText(text);
        return this;
    }

    /**
     * Set the count bubble value. If null this will throw a runtime
     * exception. To remove a count bubble call removeCount()
     */
    public JQMListItem setCount(Integer count) {
        if (count == null)
            throw new RuntimeException("Cannot set count to null. Call removeCount() if you wanted to remove the bubble");

        if (countElem == null)
            createAndAttachCountElement();
        countElem.setInnerText(count.toString());

        return this;
    }

    /**
     * Sets the image to be used to the given src. The image will be set as an
     * icon.
     */
    public JQMListItem setIcon(String src) {
        setImage(src, true);
        return this;
    }

    private JQMListItem setId() {
        getElement().setId(Document.get().createUniqueId());
        return this;
    }

    /**
     * Sets the image on this list item to the image at the given src. If icon
     * is true then the image will be set as an icon, otherwise it will be set
     * as a thumbnail.
     */
    public JQMListItem setImage(String src, boolean icon) {
        if (src == null)
            throw new RuntimeException("Cannot set image to null. Call removeImage() if you wanted to remove the image");

        if (imageElem == null) {
            imageElem = Document.get().createImageElement();
            attachChild(imageElem);
        }
        imageElem.setAttribute("src", src);

        if (icon)
            imageElem.setAttribute("class", "ui-li-icon");
        else
            imageElem.removeAttribute("class");
        return this;
    }

    protected JQMListItem setList(JQMList jqmList) {
        this.list = jqmList;
        return this;
    }

    /**
     * Sets the content of the "main" text to the given value.
     */
    @Override
    public void setText(String text) {
        if (text == null) {
            if (headerElem != null) {
                removeChild(headerElem);
                headerElem = null;
            }
            return;
        }

        if (headerElem == null) {
            headerElem = Document.get().createHElement(3);
            attachChild(headerElem);
        }
        headerElem.setInnerText(text);
    }

    /**
     * Sets the image element to type thumbnail and uses the @param src for
     * the source url of the image
     */
    public JQMListItem setThumbnail(String src) {
        setImage(src, false);
        return this;
    }

    /**
     * Sets the url to link to for this item. If this item was a read only
     * item it automatically becomes linkable.
     */
    public JQMListItem setUrl(String url) {
        if (url == null)
            throw new RuntimeException("Cannot set URL to null. Call removeUrl() if you wanted to remove the URL");
        if (anchor == null) {
            // need to make anchor and move children to it
            anchor = Document.get().createAnchorElement();
            moveThisChildrenToAnchor();
            getElement().appendChild(anchor);
        }
        anchor.setAttribute("href", url);
        return this;
    }

    /** Can be used in UiBinder */
    public void setHref(String url) {
        setUrl(url);
    }

    public void setSplitHref(String url) {
        if (url == null) {
            if (split != null) {
                getElement().removeChild(split);
                split = null;
                checkSplitPadding();
            }
            return;
        }
        if (split != null) return;
        if (anchor == null) setUrl("#");
        split = Document.get().createAnchorElement();
        split.setAttribute("href", url);
        getElement().appendChild(split);
        setSplitTheme(splitTheme);
        checkSplitPadding();
    }

    public void setSplitIcon(DataIcon icon) {
        JQMCommon.setIcon(getElement(), icon);
    }

    public DataIcon getSplitIcon() {
        return JQMCommon.getIcon(getElement());
    }

    public void setSplitTheme(String theme) {
        splitTheme = theme;
        if (split != null) JQMCommon.setTheme(split, theme);
    }

    public String getSplitTheme() {
        if (split == null) return splitTheme;
        splitTheme = JQMCommon.getTheme(split);
        return splitTheme;
    }

    @Override
    public JQMListItem withText(String text) {
        setText(text);
        return this;
    }

    private void checkSplitPadding() {
        if (anchor == null || controlGroup == null) return;
        anchor.getStyle().setPaddingRight(split == null ? 0 : 42, Unit.PX);
    }

    private void createControlGroup() {
        if (controlGroup != null) return;

        if (anchor == null) setUrl("#");
        anchor.getStyle().setPadding(0, Unit.PX);
        checkSplitPadding();

        LabelElement label = Document.get().createLabelElement();
        setStyleName(label, "jqm4gwt-li-checkbox");
        JQMCommon.setCorners(label, false);
        Style st = label.getStyle();
        st.setBorderWidth(0, Unit.PX);
        st.setMarginTop(0, Unit.PX);
        st.setMarginBottom(0, Unit.PX);

        FieldSetElement fldSet = Document.get().createFieldSetElement();
        LiControlGroup grp = new LiControlGroup(fldSet, "jqm4gwt-li-controls");
        label.appendChild(fldSet);

        moveAnchorChildrenTo(fldSet);
        controlGroupRootElem = label;
        controlGroup = grp;
        anchor.appendChild(controlGroupRootElem);
    }

    /**
     * true - prepare and allow to add widgets to this list box item.
     */
    public void setControlGroup(boolean value) {
        if (value) {
            createControlGroup();
        } else if (controlGroup != null) {
            getElement().removeChild(anchor);
            anchor = null;
            setSplitHref(null);
            controlGroupRootElem = null;
            controlGroup = null;
            checkBoxInput = null;
        }
    }

    public boolean isControlGroup() {
        return controlGroup != null;
    }

    /**
     * CheckBox will be created for this list item.
     * <p>See <a href="http://stackoverflow.com/a/13931919">Checkbox in ListView</a></p>
     */
    public void setCheckBox(IconPos iconPos) {
        if (checkBoxInput != null) {
            if (iconPos == null) {
                controlGroup.remove(checkBoxInput);
                checkBoxInput = null;
            } else {
                JQMCommon.setIconPos(controlGroupRootElem, iconPos);
            }
            return;
        }

        if (iconPos == null) return;
        createControlGroup();
        JQMCommon.setIconPos(controlGroupRootElem, iconPos);
        TextBox cb = new TextBox();
        cb.getElement().setAttribute("type", "checkbox");
        controlGroup.insert(cb, 0);
        checkBoxInput = cb;
    }

    public IconPos getCheckBox() {
        if (checkBoxInput == null) return null;
        return JQMCommon.getIconPos(controlGroupRootElem);
    }

    public boolean isChecked() {
        if (checkBoxInput == null) return false;
        InputElement cb = checkBoxInput.getElement().cast();
        return cb.isChecked();
    }

    private native void setChecked(InputElement e, boolean value) /*-{
        $wnd.$(e).prop('checked', value).checkboxradio('refresh');
    }-*/;

    public void setChecked(boolean value) {
        if (checkBoxInput == null || isChecked() == value) return;
        InputElement cb = checkBoxInput.getElement().cast();
        setChecked(cb, value);
    }

    /**
     * Currently possible only in checkBox and control group modes.
     * <p>See {@link JQMListItem#setCheckBox(IconPos)} and {@link JQMListItem#setControlGroup(boolean)}
     */
    public void addWidget(Widget w) {
        if (w == null || controlGroup == null) return;
        controlGroup.add(w);
    }
}
