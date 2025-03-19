package com.sksamuel.jqm4gwt;

import com.sksamuel.jqm4gwt.form.elements.JQMTextArea;

/**
 * @author Stephen K Samuel samspade79@gmail.com 11 May 2011 13:54:02
<<<<<<< /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasGridDimensions.java/left.java
 *         <br>
||||||| /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasGridDimensions.java/base.java
 *         <p/>
=======
 *
>>>>>>> /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasGridDimensions.java/right.java
 *         This interface is for widgets that have two dimensions, eg a
 *         {@link JQMTextArea}
 */
public interface HasGridDimensions<T> {

    /**
     * Returns the number of columns
     */
    int getColumns();

    /**
     * Returns the number of rows
     */
    int getRows();

    void setColumns(int columns);

    T withColumns(int columns);

    void setRows(int rows);

    T withRows(int rows);
}
