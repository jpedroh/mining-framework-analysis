package com.sksamuel.jqm4gwt.html;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.sksamuel.jqm4gwt.HasHTML;
import com.sksamuel.jqm4gwt.HasText;


/**
<<<<<<< LEFT
 * An implemenation of a &lt;abbr> element exposed as a widget.
 * <br> The &lt;abbr> tag indicates an abbreviation or an acronym, like "WWW" or "NATO".
=======
 * An implementation of a &lt;abbr&gt; element exposed as a widget.
 *  The &lt;abbr&gt; tag indicates an abbreviation or an acronym, like "WWW" or "NATO".
>>>>>>> RIGHT
 *
 * @author slavap
 *
 */
public class Abbr extends Widget implements HasText<Abbr> , HasHTML<Abbr> {
    public Abbr() {
        Element elt = DOM.createElement("abbr");
        setElement(elt);
    }

    @Override
    public String getText() {
        return getElement().getInnerText();
    }

    @Override
    public void setText(String text) {
        getElement().setInnerText(text);
    }

    @Override
    public Abbr withText(String text) {
        setText(text);
        return this;
    }

    @Override
    public String getHTML() {
        return getElement().getInnerHTML();
    }

    @Override
    public void setHTML(String html) {
        getElement().setInnerHTML(html);
    }

    @Override
    public Abbr withHTML(String html) {
        setHTML(html);
        return this;
    }
}