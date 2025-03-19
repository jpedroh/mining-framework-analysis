package com.sksamuel.jqm4gwt;

import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Widget;

/**
 * See <a href="http://demos.jquerymobile.com/1.4.5/pages-dialog/">Dialogs</a>
 *
<<<<<<< /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/JQMDialog.java/left.java
 * <br> Also see <a href="http://vernonkesner.com/blog/2013/04/10/jquery-mobile-dialogs-and-popups/">
||||||| /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/JQMDialog.java/base.java
 * <p/> Also see <a href="http://vernonkesner.com/blog/2013/04/10/jquery-mobile-dialogs-and-popups/">
=======
 *  Also see <a href="http://vernonkesner.com/blog/2013/04/10/jquery-mobile-dialogs-and-popups/">
>>>>>>> /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/JQMDialog.java/right.java
 * jQuery Mobile: Dialogs and Popups</a>
 *
 * @author SlavaP
 *
 */
public class JQMDialog extends JQMPage {

    public JQMDialog() {
        super();
        JQMCommon.setDataDialog(this, true);
    }

    /**
     * Creates a {@link JQMDialog} with the given id
     *
     * @param containerId the id to use as this dialog's id
     */
    public @UiConstructor JQMDialog(String containerId) {
        super(containerId);
        JQMCommon.setDataDialog(this, true);
    }

    /**
     * Create a new {@link JQMDialog} with an automatically assigned dialog id,
     * and then add the given widgets serially to the dialog layout.
     */
    public JQMDialog(Widget... widgets) {
        super(widgets);
        JQMCommon.setDataDialog(this, true);
    }

}
