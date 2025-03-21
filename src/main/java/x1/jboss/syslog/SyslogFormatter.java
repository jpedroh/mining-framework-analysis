package x1.jboss.syslog;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Logging formatter for Syslog
 */
public class SyslogFormatter extends Formatter {
  @Override public String format(LogRecord record) {
    String source;
    if (record.getSourceClassName() != null) {
      source = record.getSourceClassName();
      if (record.getSourceMethodName() != null) {
        source += " " + record.getSourceMethodName();
      }
    } else {
      source = record.getLoggerName();
    }
    String message = formatMessage(record);
    return " [" + source + "] " + message;
  }

  @Override public synchronized String formatMessage(LogRecord record) {
    String format = record.getMessage();
    java.util.ResourceBundle catalog = record.getResourceBundle();
    if (catalog != null) {
      try {
        format = catalog.getString(record.getMessage());
      } catch (java.util.MissingResourceException ex) {
        format = record.getMessage();
      }
    }
    try {
      Object[] parameters = record.getParameters();
      if (parameters == null || parameters.length == 0) {
        return format;
      }
      if (format.indexOf("{0") >= 0 || format.indexOf("{1") >= 0 || format.indexOf("{2") >= 0 || format.indexOf("{3") >= 0) {
        return java.text.MessageFormat.format(format, parameters);
      }
      return String.format(format, record.getParameters());
    } catch (Exception ex) {
      return format;
    }
  }
}