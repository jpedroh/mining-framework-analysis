package com.sksamuel.jqm4gwt;



/**
 *
 *
 * @author Stephen K Samuel samspade79@gmail.com 5 May 2011 10:59:06
<p/>
Interface for classes that enable switching between native and jqm
rendering modes.
 */
public interface HasNative<T> {
    public abstract boolean isNative();

    public abstract void setNative(boolean b);

    public abstract T withNative(boolean b);
}