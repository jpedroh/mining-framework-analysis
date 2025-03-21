package com.coverity.security;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Filter {
  private static final Pattern OCTAL_REGEX = Pattern.compile("(0+)([0-7]*)");

  private static final Pattern NUMBER_REGEX = Pattern.compile("[-+]?((\\.[0-9]+)|([0-9]+\\.?[0-9]*))");

  private static final Pattern HEX_REGEX = Pattern.compile("0x[0-9a-fA-F]+");

  /**
     * asNumber is useful for outputting dynamic data as a number in a JavaScript
     * context, e.g.
     * &lt;script>
     * var userNum = ${cov:asNumber(param.web)};
     * &lt;/script>
     * 
     * It allows decimal and hex numbers (e.g. 0x41) through unmodified, unless they have
     * leading 0s and may be interpreted as octal numbers, in which case the leading 0s
     * are stripped.
     * 
     * @param number    the potential number to filter
     * @return            a sanitised number or 0 if there is no conversion
     */
  public static String asNumber(String number) {
    return asNumber(number, "0");
  }

  /**
     * Identical to asNumber, except you can provide your own default value
     * @param number        the potential number to filter
     * @param defaultNumber    a default String to return if the number argument is not a Number 
     * @return                a sanitised number or defaultNumber if there is no conversion
     */
  public static String asNumber(String number, String defaultNumber) {
    if (number == null) {
      return null;
    }
    String trimNumber = number.trim();
    Matcher octal = OCTAL_REGEX.matcher(trimNumber);
    if (octal.matches()) {
      return octal.group(2);
    }
    if (NUMBER_REGEX.matcher(trimNumber).matches()) {
      return trimNumber;
    }
    if (HEX_REGEX.matcher(trimNumber).matches()) {
      return trimNumber;
    }
    return defaultNumber;
  }

  private static final Pattern CSS_HEX_COLOR_REGEX = Pattern.compile("#[0-9a-fA-F]{3}([0-9a-fA-F]{3})?");

  private static final Pattern CSS_NAMED_COLOR_REGEX = Pattern.compile("[a-zA-Z]{1,20}");

  /**
     * asCssColor is useful when you need to insert dynamic data into a CSS color context, e.g.
     * &lt;style>
     * .userprofile {
     * background-colo: ${cov:asCssColor(param.web)};
     * }
     * &lt;/style> 
     * 
     * It should be used for colors since it is not possible to specify colors inside
     * CSS strings
     * 
     * This method validates that the parameter is a valid color, or returns the string "invalid".
     * The string invalid was chosen since it is a token that is not valid in this context, so
     * this rule will be ignored by the CSS parser, but additional rules will still be parsed
     * properly.
     * 
     * The effect of this is that it will be as if the CSS rule was never specified.
     * 
     * We have chosen to provide an illegal token instead of a default such as "transparent"
     * or "inherit" since the defaults are different for different color contexts, e.g.
     * background-color defaults to transparent, while color defaults to inherit. This will
     * essentially preserve those semantics.
     * 
     * 
     * @param color    the potential css color to filter
     * @return        the color specified or the string "invalid"
     */
  public static String asCssColor(String color) {
    return asCssColor(color, "invalid");
  }

  /**
     * Identical to asCssColor, except you can provide your own default value
     * @param number        the potential css color to filter
     * @param defaultColor    a default String to return if the color argument is not a potentially valid CSS color 
     * @return                a sanitised color or defaultColor if there is no conversion
     */
  public static String asCssColor(String color, String defaultColor) {
    if (color == null) {
      return null;
    }
    if (CSS_HEX_COLOR_REGEX.matcher(color).matches()) {
      return color;
    }
    if (CSS_NAMED_COLOR_REGEX.matcher(color).matches()) {
      return color;
    }
    return defaultColor;
  }

  private static final Pattern URL_REGEX = Pattern.compile("(/|\\\\\\\\|https?:|ftp:|mailto:).*");

  /**
     * URL filtering to ensure that the URL is a safe non-relative URL or transforms it to a safe relative URL.
     * <p>
     * Specifically, if the URL starts with one of the following it will be unaltered:
     * <ul>
     * <li>/ to allow URLs of the form /path/from/root.jsp</li>
     * <li>\\ to allow UNC paths of the form \\server\some\file.xls</li>
     * <li>http:</li>
     * <li>https:</li>
     * <li>ftp:</li>
     * <li>mailto:</li>
     * </ul>
     * 
     * Our research shows that these URLs will not cause an XSS defect by being accessed, and is intended to
     * be used in cases where having a user point a URL at their own content is intended.
     * 
     * Other URLs are made safe by turning them into URLs relative to the current document, e.g.
     * file.html becomes ./file.html
     * ?query becomse ./?query
     * #hash becomes ./#hash
     * javascript:alert(1) becomes ./javascript:alert(1)
     * 
     * 
     * This methods will not prevent XSS if it is used to show active content such as:
     * <ul>
     * <li>JavaScript src</li>
     * <li>CSS src</li>
     * <li>CSS \@import</li>
     * <li>Embeded Flash files</li>
     * <li>Java Applets</li>
     * <li>Embeded PDFs</li>
     * <li>Pretty much any other plugin</li>
     * <li>etc</li>
     * </ul>
     * 
     * 
     * @param url    The potentially tainted URL to be Filtered
     * @return        a safe version of the URL or <code>null</code> if <code>input</code> is null
     */
  public static String asURL(String url) {
    if (url == null) {
      return null;
    }
    if (url.length() == 0) {
      return url;
    }
    if (URL_REGEX.matcher(url).matches()) {
      return url;
    }
    return "./" + url;
  }

  /**
     * This function should be semantically identical to the above function with the exception
     * of using a scheme blacklist instead of a scheme whitelist.
     * 
     * It disallows javascript, vbscript, data and about URLs and turns these URLs into
     * relative URLs the same way the above does.
     * 
     * It allows all other schemes as long as the scheme name is directly followed by a colon (:)
     * 
     * The complexity of this function is necessary due to the parsing that browsers do when
     * they encounter URLs, e.g. stripping new lines and NUL bytes. 
     * 
     * @param url    The potentially tainted URL to be Filtered
     * @return        a safe version of the URL or <code>null</code> if <code>input</code> is null
     */
  public static String asFlexibleURL(String url) {
    if (url == null) {
      return null;
    }
    int i = 0;
    int length = url.length();
    if (url.startsWith("/")) {
      return url;
    }
    if (url.startsWith("\\\\")) {
      return url;
    }
    for ( ; i < length; i++) {
      char c = url.charAt(i);
      if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || (c == '.') || (c == '+') || (c == '-'))) {
        break;
      }
    }
    if (i == length) {
      return url;
    }
    if (url.charAt(i) == ':' && validateScheme(url.substring(0, i))) {
      return url;
    }
    return "./" + url;
  }

  private static final Pattern SCHEME_REGEX = Pattern.compile("(javascript|vbscript|data|about)", Pattern.CASE_INSENSITIVE);

  private static boolean validateScheme(String scheme) {
    return !SCHEME_REGEX.matcher(scheme).matches();
  }
}