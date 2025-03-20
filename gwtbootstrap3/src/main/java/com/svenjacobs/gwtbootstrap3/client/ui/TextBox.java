package com.svenjacobs.gwtbootstrap3.client.ui;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.svenjacobs.gwtbootstrap3.client.ui.base.TextBoxBase;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.Styles;

/**
 * @author Sven Jacobs
 * @author Joshua Godi
 * @author Pontus Enmark
 */
public class TextBox extends TextBoxBase {
  public TextBox() {
    this(DOM.createInputText());
  }

  public TextBox(final Element element) {
    super(element);
    setStyleName(Styles.FORM_CONTROL);
  }

  public void clear() {
    super.setValue(null);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public void setVisibleOn(final String deviceSizeString) {
    StyleHelper.setVisibleOn(this, deviceSizeString);
  }
>>>>>>> /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/client/ui/TextBox.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public void setHiddenOn(final String deviceSizeString) {
    StyleHelper.setHiddenOn(this, deviceSizeString);
  }
>>>>>>> /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/client/ui/TextBox.java/right.java
}