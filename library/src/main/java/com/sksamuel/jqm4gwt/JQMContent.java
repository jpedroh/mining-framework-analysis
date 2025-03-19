package com.sksamuel.jqm4gwt;

import com.google.gwt.dom.client.Document;
import com.sksamuel.jqm4gwt.panel.JQMPanel;

/**
 * @author Stephen K Samuel samspade79@gmail.com 4 May 2011 23:55:27
 *
<<<<<<< /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/JQMContent.java/left.java
 * <br> A panel that is used for all the main content widgets.
 * <br> This maps to the &lt;div class="ui-content" role="main" /> element in the page.
||||||| /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/JQMContent.java/base.java
 * <p/> A panel that is used for all the main content widgets.
 * <p/> This maps to the &lt;div class="ui-content" role="main" /> element in the page.
=======
 *  A panel that is used for all the main content widgets.
 *  This maps to the &lt;div class="ui-content" role="main" /&gt; element in the page.
>>>>>>> /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/JQMContent.java/right.java
 *
 */
public class JQMContent extends JQMPanel {

	JQMContent() {
		super(Document.get().createDivElement(), null/*dataRole*/, "ui-content");
		JQMCommon.setRole(getElement(), "main");
	}

}
