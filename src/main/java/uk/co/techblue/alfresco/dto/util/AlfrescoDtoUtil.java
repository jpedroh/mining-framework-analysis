package uk.co.techblue.alfresco.dto.util;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * The Class AlfrescoDtoUtil.
 */
public class AlfrescoDtoUtil {
  /** The Constant ISO8601_PATTERN. */
  private static final String ISO8601_PATTERN = "yyyy-MM-dd\'T\'HH:mm:ss.SSSz";

  private AlfrescoDtoUtil() {
  }

  /**
     * Parses the ISO8601 date string.
     * 
     * @param dateString the date string
     * @return the date
     * @throws ParseException the parse exception
     */
  public static Date parseISO8601Date(String dateString) throws ParseException {
    final SimpleDateFormat sdf = new SimpleDateFormat(ISO8601_PATTERN);
    if (dateString.endsWith("0000Z")) {
      dateString = dateString.substring(0, dateString.indexOf("0000Z")) + "GMT-00:00";
    } else {
      if (dateString.endsWith("Z")) {
        dateString = dateString.substring(0, dateString.length() - 1) + "GMT-00:00";
      } else {
        final int inset = 6;
        final String strPrefix = dateString.substring(0, dateString.length() - inset);
        final String strSuffix = dateString.substring(dateString.length() - inset, dateString.length());
        dateString = strPrefix + "GMT" + strSuffix;
      }
    }
    return sdf.parse(dateString);
  }

  /**
     * Format is o8601 date.
     * 
     * @param date the date
     * @return the string
     * @throws ParseException the parse exception
     */
  public static String formatISO8601Date(final Date date) throws ParseException {
    return formatDate(date, "yyyy-MM-dd\'T\'HH:mm:ss.SSS\'0000Z\'", true);
  }

  /**
     * Format date to string using specified pattern.
     * 
     * @param date the date
     * @param pattern the pattern
     * @param toUTC the to utc
     * @return the string
     * @throws ParseException the parse exception
     */
  public static String formatDate(final Date date, final String pattern, final boolean toUTC) throws ParseException {
    final DateFormat df = new SimpleDateFormat(pattern);
    if (toUTC) {
      final TimeZone tz = TimeZone.getTimeZone("UTC");
      df.setTimeZone(tz);
    }
    return df.format(date);
  }

  /**
     * Gets the class field value.
     * 
     * @param object the object
     * @param fieldName the field name
     * @return the class field value
     * @throws IllegalArgumentException the illegal argument exception
     * @throws IllegalAccessException the illegal access exception
     */
  public static Object getClassFieldValue(final Object object, final String fieldName) throws IllegalAccessException {
    for (final Field classField : object.getClass().getDeclaredFields()) {
      if (classField.getName().equals(fieldName)) {
        classField.setAccessible(true);
        return classField.get(object);
      }
    }
    return null;
  }
}