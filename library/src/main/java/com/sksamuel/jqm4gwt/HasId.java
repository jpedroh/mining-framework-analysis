package com.sksamuel.jqm4gwt;

/**
 * @author Stephen K Samuel samspade79@gmail.com 11 Jul 2011 22:05:22
<<<<<<< /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasId.java/left.java
 *         <br>
||||||| /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasId.java/base.java
 *         <p/>
=======
 *
>>>>>>> /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasId.java/right.java
 *         Widgets implementing this interface had a user definable id
 */
public interface HasId<T> {

    /**
     * Returns the currently set ID
     */
    String getId();

    /**
     * Change the ID to the given value
     */
    void setId(String id);

    /**
     * Change the ID to the given value
     */
    T withId(String id);
}
