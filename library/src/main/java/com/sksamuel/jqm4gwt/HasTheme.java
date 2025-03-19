package com.sksamuel.jqm4gwt;

/**
 * @author Stephen K Samuel samspade79@gmail.com 5 May 2011 10:36:07
<<<<<<< /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasTheme.java/left.java
 *         <br>
||||||| /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasTheme.java/base.java
 *         <p/>
=======
 *
>>>>>>> /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasTheme.java/right.java
 *         Interface for elements that are themeable.
 * see http://jquerymobile.com/demos/1.0b1/#/demos/1.0b1/docs/api/themes.html
 */
public interface HasTheme<T> {

    /**
     * Returns the value of the data-theme attribute
     */
    String getTheme();

    /**
     * Sets the value of the data-theme attribute. Should be a value definined
     * by the accompanying CSS stylesheet.
<<<<<<< /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasTheme.java/left.java
     * <br>
||||||| /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasTheme.java/base.java
     * <p/>
=======
     *
>>>>>>> /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasTheme.java/right.java
     * JQM by default defines styles A-E. User styles will typically be
     * defined as F onwards.
     */
    void setTheme(String themeName);

    /**
     * Sets the value of the data-theme attribute. Should be a value definined
     * by the accompanying CSS stylesheet.
<<<<<<< /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasTheme.java/left.java
     * <br>
||||||| /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasTheme.java/base.java
     * <p/>
=======
     *
>>>>>>> /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasTheme.java/right.java
     * JQM by default defines styles A-E. User styles will typically be
     * defined as F onwards.
     */
    T withTheme(String themeName);
}
