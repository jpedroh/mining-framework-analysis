package com.sksamuel.jqm4gwt;

/**
 * @author Stephen K Samuel samspade79@gmail.com 5 May 2011 10:59:06
<<<<<<< /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasNative.java/left.java
 *         <br>
||||||| /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasNative.java/base.java
 *         <p/>
=======
 *
>>>>>>> /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasNative.java/right.java
 *         Interface for classes that enable switching between native and jqm
 *         rendering modes.
 */
public interface HasNative<T> {

    boolean isNative();

    void setNative(boolean b);

    T withNative(boolean b);
}
