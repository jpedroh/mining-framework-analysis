package com.univocity.parsers.csv;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.input.*;

/**
 * A very fast CSV parser implementation.
 *
 * @see CsvFormat
 * @see CsvParserSettings
 * @see CsvWriter
 * @see AbstractParser
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class CsvParser extends AbstractParser<CsvParserSettings> {
  private final boolean ignoreTrailingWhitespace;

  private final boolean ignoreLeadingWhitespace;

  private final boolean parseUnescapedQuotes;

  private final boolean dontEscapeUnquotedValues;

  private final boolean keepEscape;

  private char delimiter;

  private char quote;

  private char quoteEscape;

  private final char escapeEscape;

  private final char newLine;

  private final DefaultCharAppender whitespaceAppender;

  /**
	 * The CsvParser supports all settings provided by {@link CsvParserSettings}, and requires this configuration to be properly initialized.
	 * @param settings the parser configuration
	 */
  public CsvParser(CsvParserSettings settings) {
    super(settings);
    ignoreTrailingWhitespace = settings.getIgnoreTrailingWhitespaces();
    ignoreLeadingWhitespace = settings.getIgnoreLeadingWhitespaces();
    parseUnescapedQuotes = settings.isParseUnescapedQuotes();
    dontEscapeUnquotedValues = !settings.isEscapeUnquotedValues();
    keepEscape = settings.isKeepEscapeSequences();
    CsvFormat format = settings.getFormat();
    delimiter = format.getDelimiter();
    quote = format.getQuote();
    quoteEscape = format.getQuoteEscape();
    escapeEscape = format.getCharToEscapeQuoteEscaping();
    newLine = format.getNormalizedNewline();
    whitespaceAppender = new DefaultCharAppender(settings.getMaxCharsPerColumn(), "");
  }

  /**
	 * {@inheritDoc}
	 */
  @Override protected void parseRecord() {
    if (ch <= ' ' && ignoreLeadingWhitespace) {
      skipWhitespace();
    }
    while (ch != newLine) {
      parseField();
      if (ch != newLine) {
        ch = input.nextChar();
        if (ch == newLine) {
          output.emptyParsed();
        }
      }
    }
  }

  private void parseValue() {
    if (ignoreTrailingWhitespace) {
      while (ch != delimiter && ch != newLine) {
        output.appender.appendIgnoringWhitespace(ch);
        ch = input.nextChar();
      }
    } else {
      while (ch != delimiter && ch != newLine) {
        output.appender.append(ch);
        ch = input.nextChar();
      }
    }
  }

  private void parseValueProcessingEscape() {
    char prev = '\u0000';
    if (ignoreTrailingWhitespace) {
      while (ch != delimiter && ch != newLine) {
        if (ch != quote && ch != quoteEscape) {
          output.appender.appendIgnoringWhitespace(ch);
          prev = ch;
        } else {
          if (ch == quoteEscape && prev == escapeEscape && escapeEscape != '\u0000') {
            if (keepEscape) {
              output.appender.appendIgnoringWhitespace(escapeEscape);
            }
            output.appender.appendIgnoringWhitespace(quoteEscape);
            prev = '\u0000';
          } else {
            if (prev == quoteEscape) {
              if (ch == quote) {
                if (keepEscape) {
                  output.appender.appendIgnoringWhitespace(quoteEscape);
                }
                output.appender.appendIgnoringWhitespace(quote);
                prev = '\u0000';
              } else {
                output.appender.appendIgnoringWhitespace(prev);
              }
            } else {
              if (ch == quote) {
                output.appender.appendIgnoringWhitespace(quote);
              }
              prev = ch;
            }
          }
        }
        ch = input.nextChar();
      }
    } else {
      while (ch != delimiter && ch != newLine) {
        if (ch != quote && ch != quoteEscape) {
          output.appender.append(ch);
          prev = ch;
        } else {
          if (ch == quoteEscape && prev == escapeEscape && escapeEscape != '\u0000') {
            if (keepEscape) {
              output.appender.appendIgnoringWhitespace(escapeEscape);
            }
            output.appender.append(quoteEscape);
            prev = '\u0000';
          } else {
            if (prev == quoteEscape) {
              if (ch == quote) {
                if (keepEscape) {
                  output.appender.append(quoteEscape);
                }
                output.appender.append(quote);
                prev = '\u0000';
              } else {
                output.appender.append(prev);
              }
            } else {
              if (ch == quote) {
                output.appender.append(quote);
              }
              prev = ch;
            }
          }
        }
        ch = input.nextChar();
      }
    }
  }

  private void parseQuotedValue(char prev) {
    ch = input.nextChar();
    while (!(prev == quote && (ch <= ' ' || ch == delimiter || ch == newLine))) {
      if (ch != quote && ch != quoteEscape) {
        if (prev == quote) {
          if (parseUnescapedQuotes) {
            output.appender.append(quote);
            output.appender.append(ch);
            parseQuotedValue(ch);
            break;
          } else {
            throw new TextParsingException(context, "Unescaped quote character \'" + quote + "\' inside quoted value of CSV field. To allow unescaped quotes, set \'parseUnescapedQuotes\' to \'true\' in the CSV parser settings. Cannot parse CSV input.");
          }
        }
        output.appender.append(ch);
        prev = ch;
      } else {
        if (ch == quoteEscape && prev == escapeEscape && escapeEscape != '\u0000') {
          if (keepEscape) {
            output.appender.append(escapeEscape);
          }
          output.appender.append(quoteEscape);
          prev = '\u0000';
        } else {
          if (prev == quoteEscape) {
            if (ch == quote) {
              if (keepEscape) {
                output.appender.append(quoteEscape);
              }
              output.appender.append(quote);
              prev = '\u0000';
            } else {
              output.appender.append(prev);
            }
          } else {
            prev = ch;
          }
        }
      }
      ch = input.nextChar();
    }
    if (ch != delimiter && ch != newLine && ch <= ' ') {
      whitespaceAppender.reset();
      do {
        whitespaceAppender.append(ch);
        ch = input.nextChar();
        if (ch == newLine) {
          return;
        }
      } while(ch <= ' ');
      if (ch != delimiter && parseUnescapedQuotes) {
        if (output.appender instanceof DefaultCharAppender) {
          output.appender.append(quote);
          ((DefaultCharAppender) output.appender).append(whitespaceAppender);
        }
        if (ch != quoteEscape) {
          output.appender.append(ch);
        }
        parseQuotedValue(ch);
      }
    }
    if (ch != delimiter && ch != newLine) {
      throw new TextParsingException(context, "Unexpected character \'" + ch + "\' following quoted value of CSV field. Expecting \'" + delimiter + "\'. Cannot parse CSV input.");
    }
  }

  private void parseField() {
    if (ch <= ' ' && ignoreLeadingWhitespace) {
      skipWhitespace();
    }
    if (ch == delimiter) {
      output.emptyParsed();
    } else {
      if (ch == quote) {
        parseQuotedValue('\u0000');
      } else {
        if (dontEscapeUnquotedValues) {
          parseValue();
        } else {
          parseValueProcessingEscape();
        }
      }
      output.valueParsed();
    }
  }

  private void skipWhitespace() {
    while (ch <= ' ' && ch != delimiter && ch != newLine) {
      ch = input.nextChar();
    }
  }

  /**
	 * {@inheritDoc}
	 */
  @Override protected InputAnalysisProcess getInputAnalysisProcess() {
    if (settings.isDelimiterDetectionEnabled() || settings.isQuoteDetectionEnabled()) {
      return new CsvFormatDetector(20, settings) {
        @Override void apply(char delimiter, char quote, char quoteEscape) {
          if (settings.isDelimiterDetectionEnabled()) {
            CsvParser.this.delimiter = delimiter;
          }
          if (settings.isQuoteDetectionEnabled()) {
            CsvParser.this.quote = quote;
            CsvParser.this.quoteEscape = quoteEscape;
          }
        }
      };
    }
    return null;
  }
}