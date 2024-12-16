package com.sksamuel.jqm4gwt;

import com.sksamuel.jqm4gwt.form.elements.JQMTextArea;


/**
 *
 *
 * @author Stephen K Samuel samspade79@gmail.com 11 May 2011 13:54:02
<p/>
This interface is for widgets that have two dimensions, eg a
{@link JQMTextArea}
 */
public interface HasGridDimensions<T> {
    /**
     * Returns the number of columns
     */
    public abstract int getColumns();

    /**
     * Returns the number of rows
     */
    public abstract int getRows();

    public abstract void setColumns(int columns);

    public abstract T withColumns(int columns);

    public abstract void setRows(int rows);

    public abstract T withRows(int rows);
}