package com.sksamuel.jqm4gwt.button;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;
import com.sksamuel.jqm4gwt.HasInline;
import com.sksamuel.jqm4gwt.panel.JQMControlGroup;

/**
 * @author Stephen K Samuel samspade79@gmail.com 5 May 2011 18:09:41
<<<<<<< /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/button/JQMButtonGroup.java/left.java
 *         <br>
||||||| /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/button/JQMButtonGroup.java/base.java
 *         <p/>
=======
 *
>>>>>>> /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/button/JQMButtonGroup.java/right.java
 *         Groups a collection of {@link JQMButton} instances together
 * @link http://jquerymobile.com/demos/1.2.0/docs/buttons/buttons-grouped.html
 */
public class JQMButtonGroup extends JQMControlGroup implements HasInline<JQMButtonGroup> {

    public JQMButtonGroup() {
        super(Document.get().createDivElement(), "jqm4gwt-buttongroup");
    }

    public JQMButtonGroup(JQMButton... buttons) {
        this();
        for (JQMButton button : buttons)
            add(button);
    }

    public JQMButtonGroup add(JQMButton button) {
        super.add(button);
        return this;
    }

    @Override
    public void add(Widget w) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return true if this widget is currently rendering inline
     */
    @Override
    public boolean isInline() {
        return "true".equals(getElement().getAttribute("data-inline"));
    }


    /**
     * If inline is true then sets all the buttons in this group to inline,
     * otherwise sets them to not-inline.
<<<<<<< /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/button/JQMButtonGroup.java/left.java
     * <br>
||||||| /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/button/JQMButtonGroup.java/base.java
     * <p/>
=======
     *
>>>>>>> /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/button/JQMButtonGroup.java/right.java
     * If the buttons are in a group then it is best to call this method
     * instead of withInline on each button
     *
     */
    @Override
    public void setInline(boolean inline) {
        getElement().setAttribute("data-inline", "true");
        for (int k = 0; k < getWidgetCount(); k++) {
            JQMButton button = (JQMButton) getWidget(k);
            button.withInline(inline);
        }
    }

    /**
     * If inline is true then sets all the buttons in this group to inline,
     * otherwise sets them to not-inline.
<<<<<<< /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/button/JQMButtonGroup.java/left.java
     * <br>
||||||| /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/button/JQMButtonGroup.java/base.java
     * <p/>
=======
     *
>>>>>>> /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/button/JQMButtonGroup.java/right.java
     * If the buttons are in a group then it is best to call this method
     * instead of withInline on each button
     *
     */
    @Override
    public JQMButtonGroup withInline(boolean inline) {
        setInline(inline);
        return this;
    }
}
