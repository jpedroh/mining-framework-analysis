package com.mitchellbosecke.pebble.lexer;
import java.util.regex.Pattern;

public final class Syntax {
  private final String delimiterCommentOpen;

  private final String delimiterCommentClose;

  private final String delimiterExecuteOpen;

  private final String delimiterExecuteClose;

  private final String delimiterPrintOpen;

  private final String delimiterPrintClose;

  private final String delimiterInterpolationOpen;

  private final String delimiterInterpolationClose;

  private final String whitespaceTrim;

  private final Pattern regexPrintClose;

  private final Pattern regexExecuteClose;

  private final Pattern regexCommentClose;

  private final Pattern regexStartDelimiters;

  private final Pattern regexLeadingWhitespaceTrim;

  private final Pattern regexTrailingWhitespaceTrim;

  private final Pattern regexInterpolationOpen;

  private final Pattern regexInterpolationClose;

  private final Pattern regexVerbatimStart;

  private final Pattern regexVerbatimEnd;

  private static final String POSSIBLE_NEW_LINE = "(\r\n|\n\r|\r|\n|\u0085|\u2028|\u2029)?";

  public Syntax(final String delimiterCommentOpen, final String delimiterCommentClose, final String delimiterExecuteOpen, final String delimiterExecuteClose, final String delimiterPrintOpen, final String delimiterPrintClose, final String delimiterInterpolationOpen, final String delimiterInterpolationClose, final String whitespaceTrim) {
    this.delimiterCommentClose = delimiterCommentClose;
    this.delimiterCommentOpen = delimiterCommentOpen;
    this.delimiterExecuteOpen = delimiterExecuteOpen;
    this.delimiterExecuteClose = delimiterExecuteClose;
    this.delimiterPrintOpen = delimiterPrintOpen;
    this.delimiterPrintClose = delimiterPrintClose;
    this.whitespaceTrim = whitespaceTrim;
    this.delimiterInterpolationClose = delimiterInterpolationClose;
    this.delimiterInterpolationOpen = delimiterInterpolationOpen;
    this.regexPrintClose = Pattern.compile("^\\s*" + Pattern.quote(whitespaceTrim) + "?" + Pattern.quote(delimiterPrintClose) + POSSIBLE_NEW_LINE);
    this.regexExecuteClose = Pattern.compile("^\\s*" + Pattern.quote(whitespaceTrim) + "?" + Pattern.quote(delimiterExecuteClose) + POSSIBLE_NEW_LINE);
    this.regexCommentClose = Pattern.compile(Pattern.quote(delimiterCommentClose) + POSSIBLE_NEW_LINE);
    this.regexStartDelimiters = Pattern.compile(Pattern.quote(delimiterPrintOpen) + "|" + Pattern.quote(delimiterExecuteOpen) + "|" + Pattern.quote(delimiterCommentOpen));
    this.regexVerbatimStart = Pattern.compile("^\\s*verbatim\\s*(" + Pattern.quote(whitespaceTrim) + ")?" + Pattern.quote(delimiterExecuteClose) + POSSIBLE_NEW_LINE);
    this.regexVerbatimEnd = Pattern.compile(Pattern.quote(delimiterExecuteOpen) + "(" + Pattern.quote(whitespaceTrim) + ")?" + "\\s*endverbatim\\s*(" + Pattern.quote(whitespaceTrim) + ")?" + Pattern.quote(delimiterExecuteClose) + POSSIBLE_NEW_LINE);
    this.regexLeadingWhitespaceTrim = Pattern.compile(Pattern.quote(whitespaceTrim) + "\\s+");
    this.regexTrailingWhitespaceTrim = Pattern.compile("^\\s*" + Pattern.quote(whitespaceTrim) + "(" + Pattern.quote(delimiterPrintClose) + "|" + Pattern.quote(delimiterExecuteClose) + "|" + Pattern.quote(delimiterCommentClose) + ")");
    this.regexInterpolationOpen = Pattern.compile("^" + Pattern.quote(delimiterInterpolationOpen));
    this.regexInterpolationClose = Pattern.compile("^\\s*" + Pattern.quote(delimiterInterpolationClose));
  }

  public String getCommentOpenDelimiter() {
    return delimiterCommentOpen;
  }

  public String getCommentCloseDelimiter() {
    return delimiterCommentClose;
  }

  public String getExecuteOpenDelimiter() {
    return delimiterExecuteOpen;
  }

  public String getExecuteCloseDelimiter() {
    return delimiterExecuteClose;
  }

  public String getPrintOpenDelimiter() {
    return delimiterPrintOpen;
  }

  public String getPrintCloseDelimiter() {
    return delimiterPrintClose;
  }

  public String getInterpolationOpenDelimiter() {
    return delimiterInterpolationOpen;
  }

  public String getInterpolationCloseDelimiter() {
    return delimiterInterpolationClose;
  }

  public String getWhitespaceTrim() {
    return whitespaceTrim;
  }

  Pattern getRegexPrintClose() {
    return regexPrintClose;
  }

  Pattern getRegexExecuteClose() {
    return regexExecuteClose;
  }

  Pattern getRegexCommentClose() {
    return regexCommentClose;
  }

  Pattern getRegexStartDelimiters() {
    return regexStartDelimiters;
  }

  Pattern getRegexLeadingWhitespaceTrim() {
    return regexLeadingWhitespaceTrim;
  }

  Pattern getRegexTrailingWhitespaceTrim() {
    return regexTrailingWhitespaceTrim;
  }

  Pattern getRegexVerbatimEnd() {
    return regexVerbatimEnd;
  }

  Pattern getRegexVerbatimStart() {
    return regexVerbatimStart;
  }

  Pattern getRegexInterpolationOpen() {
    return regexInterpolationOpen;
  }

  Pattern getRegexInterpolationClose() {
    return regexInterpolationClose;
  }

  public static class Builder {
    private String delimiterCommentOpen = "{#";

    private String delimiterCommentClose = "#}";

    private String delimiterExecuteOpen = "{%";

    private String delimiterExecuteClose = "%}";

    private String delimiterPrintOpen = "{{";

    private String delimiterPrintClose = "}}";

    private String delimiterInterpolationOpen = "#{";

    private String delimiterInterpolationClose = "}";

    private String whitespaceTrim = "-";

    public String getCommentOpenDelimiter() {
      return delimiterCommentOpen;
    }

    public void setCommentOpenDelimiter(String commentOpenDelimiter) {
      this.delimiterCommentOpen = commentOpenDelimiter;
    }

    public String getCommentCloseDelimiter() {
      return delimiterCommentClose;
    }

    public void setCommentCloseDelimiter(String commentCloseDelimiter) {
      this.delimiterCommentClose = commentCloseDelimiter;
    }

    public String getExecuteOpenDelimiter() {
      return delimiterExecuteOpen;
    }

    public void setExecuteOpenDelimiter(String executeOpenDelimiter) {
      this.delimiterExecuteOpen = executeOpenDelimiter;
    }

    public String getExecuteCloseDelimiter() {
      return delimiterExecuteClose;
    }

    public void setExecuteCloseDelimiter(String executeCloseDelimiter) {
      this.delimiterExecuteClose = executeCloseDelimiter;
    }

    public String getPrintOpenDelimiter() {
      return delimiterPrintOpen;
    }

    public void setPrintOpenDelimiter(String printOpenDelimiter) {
      this.delimiterPrintOpen = printOpenDelimiter;
    }

    public String getPrintCloseDelimiter() {
      return delimiterPrintClose;
    }

    public void setPrintCloseDelimiter(String printCloseDelimiter) {
      this.delimiterPrintClose = printCloseDelimiter;
    }

    public String getWhitespaceTrim() {
      return whitespaceTrim;
    }

    public void setWhitespaceTrim(String whitespaceTrim) {
      this.whitespaceTrim = whitespaceTrim;
    }

    public String getInterpolationOpenDelimiter() {
      return delimiterInterpolationOpen;
    }

    public void setInterpolationOpenDelimiter(String delimiterInterpolationOpen) {
      this.delimiterInterpolationOpen = delimiterInterpolationOpen;
    }

    public String getInterpolationCloseDelimiter() {
      return delimiterInterpolationClose;
    }

    public void setInterpolationCloseDelimiter(String delimiterInterpolationClose) {
      this.delimiterInterpolationClose = delimiterInterpolationClose;
    }

    public Syntax build() {
      return new Syntax(delimiterCommentOpen, delimiterCommentClose, delimiterExecuteOpen, delimiterExecuteClose, delimiterPrintOpen, delimiterPrintClose, delimiterInterpolationOpen, delimiterInterpolationClose, enableNewLineTrimming);
    }

    private boolean enableNewLineTrimming = true;

    public boolean isEnableNewLineTrimming() {
      return enableNewLineTrimming;
    }

    public Builder setEnableNewLineTrimming(boolean enableNewLineTrimming) {
      this.enableNewLineTrimming = enableNewLineTrimming;
      return this;
    }
  }

  public Syntax(final String delimiterCommentOpen, final String delimiterCommentClose, final String delimiterExecuteOpen, final String delimiterExecuteClose, final String delimiterPrintOpen, final String delimiterPrintClose, final String whitespaceTrim, final boolean enableNewLineTrimming) {
    this.delimiterCommentClose = delimiterCommentClose;
    this.delimiterCommentOpen = delimiterCommentOpen;
    this.delimiterExecuteOpen = delimiterExecuteOpen;
    this.delimiterExecuteClose = delimiterExecuteClose;
    this.delimiterPrintOpen = delimiterPrintOpen;
    this.delimiterPrintClose = delimiterPrintClose;
    this.whitespaceTrim = whitespaceTrim;
    String newlineRegexSuffix = enableNewLineTrimming ? POSSIBLE_NEW_LINE : "";
    this.regexPrintClose = Pattern.compile("^\\s*" + Pattern.quote(whitespaceTrim) + "?" + Pattern.quote(delimiterPrintClose) + newlineRegexSuffix);
    this.regexExecuteClose = Pattern.compile("^\\s*" + Pattern.quote(whitespaceTrim) + "?" + Pattern.quote(delimiterExecuteClose) + newlineRegexSuffix);
    this.regexCommentClose = Pattern.compile(Pattern.quote(delimiterCommentClose) + newlineRegexSuffix);
    this.regexStartDelimiters = Pattern.compile(Pattern.quote(delimiterPrintOpen) + "|" + Pattern.quote(delimiterExecuteOpen) + "|" + Pattern.quote(delimiterCommentOpen));
    this.regexVerbatimStart = Pattern.compile("^\\s*verbatim\\s*(" + Pattern.quote(whitespaceTrim) + ")?" + Pattern.quote(delimiterExecuteClose) + newlineRegexSuffix);
    this.regexVerbatimEnd = Pattern.compile(Pattern.quote(delimiterExecuteOpen) + "(" + Pattern.quote(whitespaceTrim) + ")?" + "\\s*endverbatim\\s*(" + Pattern.quote(whitespaceTrim) + ")?" + Pattern.quote(delimiterExecuteClose) + newlineRegexSuffix);
    this.regexLeadingWhitespaceTrim = Pattern.compile(Pattern.quote(whitespaceTrim) + "\\s+");
    this.regexTrailingWhitespaceTrim = Pattern.compile("^\\s*" + Pattern.quote(whitespaceTrim) + "(" + Pattern.quote(delimiterPrintClose) + "|" + Pattern.quote(delimiterExecuteClose) + "|" + Pattern.quote(delimiterCommentClose) + ")");
  }
}