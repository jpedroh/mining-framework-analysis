package org.fusesource.restygwt.client;
import org.fusesource.restygwt.client.dispatcher.DefaultDispatcher;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.TimeZone;

/**
 * Provides ability to set the default date format and service root (defaults to
 * GWT.getModuleBaseURL()).
 *
 *
 * @author <a href="http://www.acuedo.com">Dave Finch</a>
 *
 */
public class Defaults {
  private static Dispatcher dispatcher = DefaultDispatcher.INSTANCE;

  private static String serviceRoot = GWT.getModuleBaseURL();

  private static String dateFormat = "yyyy-MM-dd\'T\'HH:mm:ss.SSSZ";

  private static boolean ignoreJsonNulls = false;


<<<<<<< /usr/src/app/output/resty-gwt/resty-gwt/27a136b4b15c58e80ba693c17cc3bcf093cbe7e9/restygwt/src/main/java/org/fusesource/restygwt/client/Defaults.java/left.java
  private static ExceptionMapper exceptionMapper = new ExceptionMapper();
=======
  private static boolean dateFormatHasTimeZone = true;
>>>>>>> /usr/src/app/output/resty-gwt/resty-gwt/27a136b4b15c58e80ba693c17cc3bcf093cbe7e9/restygwt/src/main/java/org/fusesource/restygwt/client/Defaults.java/right.java


  private static TimeZone timeZone = null;

  private static int requestTimeout = -1;

  private static boolean byteArraysToBase64 = false;

  public static String getServiceRoot() {
    return serviceRoot;
  }

  /**
     * sets the URL prepended to the value of Path annotations.
     *
     * @param serviceRoot
     */
  public static void setServiceRoot(String serviceRoot) {
    if (!serviceRoot.endsWith("/")) {
      serviceRoot += "/";
    }
    Defaults.serviceRoot = serviceRoot;
  }

  public static String getDateFormat() {
    return dateFormat;
  }

  /**
     * Sets the format used when encoding and decoding Dates.
     *
     * @param dateFormat
     */
  public static void setDateFormat(String dateFormat) {
    Defaults.dateFormat = dateFormat;
    dateFormatHasTimeZone = false;
    if (dateFormat != null) {
      for (int i = 0; i < dateFormat.length(); i++) {
        char ch = dateFormat.charAt(i);
        if (ch == 'Z' || ch == 'z' || ch == 'V' || ch == 'v') {
          dateFormatHasTimeZone = true;
          break;
        }
      }
    }
  }

  static boolean dateFormatHasTimeZone() {
    return dateFormatHasTimeZone;
  }

  /**
     * Gets the timezone used when encoding and decoding Dates.
     * <p>
     * The timezone is only taken into consideration if the date format string
     * does not contain a timezone field. If the timezone is set to
     * {@code null}, the browser's default (local) timezone will be used.
     * 
     * @return the date format timezone (null for local timezone)
     */
  public static TimeZone getTimeZone() {
    return timeZone;
  }

  /**
     * Gets the timezone used when encoding and decoding Dates.
     * <p>
     * The timezone is only taken into consideration if the date format string
     * does not contain a timezone field. If the timezone is set to null, the
     * browser's default (local) timezone will be used.
     * 
     * @param timeZone the new timezone (use null for local timezone)
     */
  public static void setDateFormat(TimeZone timeZone) {
    Defaults.timeZone = timeZone;
  }

  /**
     * Indicates whether or not nulls will be ignored during JSON marshalling.
     */
  public static boolean doesIgnoreJsonNulls() {
    return ignoreJsonNulls;
  }

  public static void ignoreJsonNulls() {
    ignoreJsonNulls = true;
  }

  public static void dontIgnoreJsonNulls() {
    ignoreJsonNulls = false;
  }

  public static final int getRequestTimeout() {
    return requestTimeout;
  }

  public static final void setRequestTimeout(int requestTimeout) {
    Defaults.requestTimeout = requestTimeout;
  }

  /**
    * @return the byteArraysToBase64
    */
  public static boolean isByteArraysToBase64() {
    return byteArraysToBase64;
  }

  /**
    * @param byteArraysToBase64 the byteArraysToBase64 to set
    */
  public static void setByteArraysToBase64(boolean byteArraysToBase64) {
    Defaults.byteArraysToBase64 = byteArraysToBase64;
  }

  /**
     * Sets the default dispatcher used by Method instances.
     *
     * @param value
     */
  public static void setDispatcher(Dispatcher value) {
    dispatcher = value;
  }

  /**
     * Returns the default dispatcher.
     *
     * @return
     */
  public static Dispatcher getDispatcher() {
    return dispatcher;
  }

  /**
     * Gets the default ExceptionMapper
     * @return
     */
  public static ExceptionMapper getExceptionMapper() {
    return exceptionMapper;
  }

  /**
     * Sets the default ExceptionMapper
     * @param exceptionMapper the new ExceptionMapper to be used by all requests
     */
  public static void setExceptionMapper(ExceptionMapper exceptionMapper) {
    Defaults.exceptionMapper = exceptionMapper;
  }
}