package com.sksamuel.jqm4gwt;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Stephen K Samuel samspade79@gmail.com 9 Jul 2011 14:23:25
 *
<<<<<<< /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/DataIcon.java/left.java
 * <br>This enum represents the default iconset available in jquery mobile.
||||||| /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/DataIcon.java/base.java
 * <p/>This enum represents the default iconset available in jquery mobile.
=======
 * This enum represents the default iconset available in jquery mobile.
>>>>>>> /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/DataIcon.java/right.java
 *
<<<<<<< /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/DataIcon.java/left.java
 * <br>To see what the icon set looks like, visit the following site:
||||||| /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/DataIcon.java/base.java
 * <p/>To see what the icon set looks like, visit the following site:
=======
 * To see what the icon set looks like, visit the following site:
>>>>>>> /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/DataIcon.java/right.java
 *
 * <p><a href="http://demos.jquerymobile.com/1.4.5/icons/">JQM Icons</a></p>
 *
 */
public enum DataIcon {

    ACTION("action"),
    ALERT("alert"),

    DOWN("arrow-d"),
    DOWNLEFT("arrow-d-l"),
    DOWNRIGHT("arrow-d-r"),
    LEFT("arrow-l"),
    RIGHT("arrow-r"),
    UP("arrow-u"),
    UPLEFT("arrow-u-l"),
    UPRIGHT("arrow-u-r"),

    AUDIO("audio"),
    BACK("back"),
    BARS("bars"),
    BULLETS("bullets"),
    CALENDAR("calendar"),
    CAMERA("camera"),

    DOWNCARAT("carat-d"),
    LEFTCARAT("carat-l"),
    RIGHTCARAT("carat-r"),
    UPCARAT("carat-u"),

    CHECK("check"),
    CLOCK("clock"),
    CLOUD("cloud"),
    COMMENT("comment"),
    DELETE("delete"),
    EDIT("edit"),
    EYE("eye"),
    FORBIDDEN("forbidden"),
    FORWARD("forward"),
    GEAR("gear"),
    GRID("grid"),
    HEART("heart"),
    HOME("home"),
    INFO("info"),
    LOCATION("location"),
    LOCK("lock"),
    MAIL("mail"),
    MINUS("minus"),
    NAVIGATION("navigation"),
    PHONE("phone"),
    PLUS("plus"),
    POWER("power"),
    RECYCLE("recycle"),
    REFRESH("refresh"),
    SEARCH("search"),
    SHOP("shop"),
    STAR("star"),
    TAG("tag"),
    USER("user"),
    VIDEO("video"),

    CUSTOM("custom"),
    NONE("false");

    private static final Map<String, DataIcon> jqmToIcon = new HashMap<String, DataIcon>();

    static {
        for (DataIcon i : DataIcon.values()) {
            jqmToIcon.put(i.getJqmValue(), i);
        }
    }

    private final String jqmValue;

    private DataIcon(String jqmValue) {
        this.jqmValue = jqmValue;
    }

    /**
     * Returns the string value that JQM expects
     */
    public String getJqmValue() {
        return jqmValue;
    }

    public static DataIcon fromJqmValue(String jqmValue) {
        if (jqmValue == null || jqmValue.isEmpty()) return null;
        return jqmToIcon.get(jqmValue);
    }
}
