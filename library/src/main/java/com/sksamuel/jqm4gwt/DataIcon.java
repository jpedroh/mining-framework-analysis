package com.sksamuel.jqm4gwt;

/**
 * @author Stephen K Samuel samspade79@gmail.com 9 Jul 2011 14:23:25
 *
 * <p/>This enum represents the default iconset available in jquery mobile.
 *
 * <p/>To see what the icon set looks like, visit the following site:
 *
 * <p><a href="http://view.jquerymobile.com/1.3.2/dist/demos/widgets/icons/">JQM Icons</a></p>
 *
 */
public enum DataIcon {

    BARS("bars"), EDIT("edit"), LEFT("arrow-l"), RIGHT("arrow-r"), UP("arrow-u"), DOWN("arrow-d"),
    DELETE("delete"), PLUS("plus"), MINUS("minus"), CHECK("check"), GEAR("gear"), REFRESH("refresh"),
    FORWARD("forward"), BACK("back"), GRID("grid"), STAR("star"), ALERT("alert"), INFO("info"),
    HOME("home"), SEARCH("search");

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
        for (DataIcon i : DataIcon.values()) {
            if (i.getJqmValue().equals(jqmValue)) return i;
        }
        return null;
    }
}
