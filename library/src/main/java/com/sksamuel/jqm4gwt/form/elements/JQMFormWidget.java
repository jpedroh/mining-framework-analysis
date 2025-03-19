package com.sksamuel.jqm4gwt.form.elements;

import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Stephen K Samuel samspade79@gmail.com 12 Jul 2011 22:24:12
 *
 */
public interface JQMFormWidget extends IsWidget, HasValue<String>, HasBlurHandlers {

    /**
     * Optional, can return null, then JQMForm will create label by itself.
<<<<<<< /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/form/elements/JQMFormWidget.java/left.java
     * <br> Create and return a new Label for displaying errors and attach to the form widget.
||||||| /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/form/elements/JQMFormWidget.java/base.java
     * <p/> Create and return a new Label for displaying errors and attach to the form widget.
=======
     *  Create and return a new Label for displaying errors and attach to the form widget.
>>>>>>> /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/form/elements/JQMFormWidget.java/right.java
     **/
    Label addErrorLabel();
}
