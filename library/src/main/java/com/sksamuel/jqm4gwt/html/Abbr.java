package com.sksamuel.jqm4gwt.html;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.sksamuel.jqm4gwt.HasHTML;
import com.sksamuel.jqm4gwt.HasText;

/**
<<<<<<< /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/html/Abbr.java/left.java
 * An implemenation of a &lt;abbr> element exposed as a widget.
 * <br> The &lt;abbr> tag indicates an abbreviation or an acronym, like "WWW" or "NATO".
||||||| /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/html/Abbr.java/base.java
 * An implemenation of a &lt;abbr> element exposed as a widget.
 * <p/> The &lt;abbr> tag indicates an abbreviation or an acronym, like "WWW" or "NATO".
=======
 * An implementation of a &lt;abbr&gt; element exposed as a widget.
 *  The &lt;abbr&gt; tag indicates an abbreviation or an acronym, like "WWW" or "NATO".
>>>>>>> /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/html/Abbr.java/right.java
 *
 * @author slavap
 *
 */
public class Abbr extends Widget implements HasText<Abbr>, HasHTML<Abbr> {

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
