package com.univocity.parsers.csv;
import java.io.*;
import com.univocity.parsers.common.*;

/**
 * A powerful and flexible CSV writer implementation.
 *
 * @see CsvFormat
 * @see CsvWriterSettings
 * @see CsvParser
 * @see AbstractWriter
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class CsvWriter extends AbstractWriter<CsvWriterSettings> {
  private final char separator;

  private final char quotechar;

  private final char escapechar;

  private final char escapeEscape;

  private final boolean ignoreLeading;

  private final boolean ignoreTrailing;

  private final boolean quoteAllFields;

  private final boolean escapeUnquoted;

  private final boolean inputNotEscaped;

  private final char newLine;

  /**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 * <p><strong>Important: </strong> by not providing an instance of {@link java.io.Writer} to this constructor, only the operations that write to Strings are available.</p>
	 * @param settings the CSV writer configuration
	 */
  public CsvWriter(CsvWriterSettings settings) {
    this(null, settings);
  }

  /**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 * @param writer the output resource that will receive CSV records produced by this class.
	 * @param settings the CSV writer configuration
	 */
  public CsvWriter(Writer writer, CsvWriterSettings settings) {
    super(writer, settings);
    CsvFormat format = settings.getFormat();
    this.separator = format.getDelimiter();
    this.quotechar = format.getQuote();
    this.escapechar = format.getQuoteEscape();
    this.escapeEscape = settings.getFormat().getCharToEscapeQuoteEscaping();
    this.newLine = format.getNormalizedNewline();
    this.quoteAllFields = settings.getQuoteAllFields();
    this.ignoreLeading = settings.getIgnoreLeadingWhitespaces();
    this.ignoreTrailing = settings.getIgnoreTrailingWhitespaces();
    this.escapeUnquoted = settings.isEscapeUnquotedValues();
    this.inputNotEscaped = !settings.isInputEscaped();
  }

  /**
	 * {@inheritDoc}
	 */
  @Override protected void processRow(Object[] row) {
    for (int i = 0; i < row.length; i++) {
      if (i != 0) {
        appendToRow(separator);
      }
      String nextElement = getStringValue(row[i]);
      boolean isElementQuoted = quoteElement(nextElement);
      if (isElementQuoted) {
        appender.append(quotechar);
      }
      int originalLength = appender.length();
      append(isElementQuoted, nextElement);
      if (appender.length() == originalLength && nullValue != null && !nullValue.isEmpty()) {
        if (isElementQuoted) {
          append(true, emptyValue);
        } else {
          append(false, nullValue);
        }
      }
      if (isElementQuoted) {
        appendValueToRow();
        appendToRow(quotechar);
      } else {
        appendValueToRow();
      }
    }
  }

  private boolean quoteElement(String nextElement) {
    if (quoteAllFields) {
      return true;
    }
    if (nextElement == null) {
      return false;
    }
    int start = 0;
    if (ignoreLeading) {
      start = skipLeadingWhitespace(nextElement);
    }
    if (start < nextElement.length() && nextElement.charAt(start) == quotechar) {
      return true;
    }
    for (int j = start; j < nextElement.length(); j++) {
      char nextChar = nextElement.charAt(j);
      if (nextChar == separator || nextChar == newLine) {
        return true;
      }
    }
    return false;
  }

  private void append(boolean isElementQuoted, String element) {
    if (element == null) {
      element = nullValue;
    }
    if (element == null) {
      return;
    }
    int start = 0;
    if (this.ignoreLeading) {
      start = skipLeadingWhitespace(element);
    }
    if (this.ignoreTrailing) {
      for (int i = start; i < element.length(); i++) {
        char nextChar = element.charAt(i);
        if (nextChar == quotechar && (isElementQuoted || escapeUnquoted) && inputNotEscaped) {
          appender.appendIgnoringWhitespace(escapechar);
        } else {
          if (nextChar == escapechar && inputNotEscaped && escapeEscape != '\u0000' && (isElementQuoted || escapeUnquoted)) {
            appender.appendIgnoringWhitespace(escapeEscape);
          }
        }
        appender.appendIgnoringWhitespace(nextChar);
      }
    } else {
      for (int i = start; i < element.length(); i++) {
        char nextChar = element.charAt(i);
        if (nextChar == quotechar && (isElementQuoted || escapeUnquoted) && inputNotEscaped) {
          appender.append(escapechar);
        } else {
          if (nextChar == escapechar && inputNotEscaped && escapeEscape != '\u0000' && (isElementQuoted || escapeUnquoted)) {
            appender.appendIgnoringWhitespace(escapeEscape);
          }
        }
        appender.append(nextChar);
      }
    }
  }
}