package com.sksamuel.jqm4gwt;



/**
 *
 *
 * @author Stephen K Samuel samspade79@gmail.com 11 Jul 2011 22:05:22
<p/>
Widgets implementing this interface had a user definable id
 */
public interface HasId<T> {
    /**
     * Returns the currently set ID
     */
    public abstract String getId();

    /**
     * Change the ID to the given value
     */
    public abstract void setId(String id);

    /**
     * Change the ID to the given value
     */
    public abstract T withId(String id);
}