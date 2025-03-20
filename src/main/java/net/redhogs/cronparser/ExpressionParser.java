package net.redhogs.cronparser;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import java.text.ParseException;
import java.util.Locale;

/**
 * @author grhodes
 * @since 10 Dec 2012 10:58:21
 */
class ExpressionParser {
  private ExpressionParser() {
  }

  public static String[] parse(String expression) throws ParseException {
    String[] parsed = new String[6];
    if (StringUtils.isEmpty(expression)) {
      throw new IllegalArgumentException(I18nMessages.get("expression_empty_exception"));
    }
    String[] expressionParts = expression.split(" ");
    if (expressionParts.length < 5) {
      throw new ParseException(expression, 0);
    } else {
      if (expressionParts.length > 6) {
        throw new ParseException(expression, 6);
      } else {
        if (expressionParts.length == 5) {
          parsed[0] = StringUtils.EMPTY;
          System.arraycopy(expressionParts, 0, parsed, 1, 5);
        } else {
          parsed = expressionParts;
        }
      }
    }
    normaliseExpression(parsed);
    return parsed;
  }

  /**
     * @param expressionParts
     */
  private static void normaliseExpression(String[] expressionParts) {
    expressionParts[3] = expressionParts[3].replace('?', '*');
    expressionParts[5] = expressionParts[5].replace('?', '*');
    expressionParts[0] = expressionParts[0].startsWith("0/") ? expressionParts[0].replace("0/", "*/") : expressionParts[0];
    expressionParts[1] = expressionParts[1].startsWith("0/") ? expressionParts[1].replace("0/", "*/") : expressionParts[1];
    expressionParts[2] = expressionParts[2].startsWith("0/") ? expressionParts[2].replace("0/", "*/") : expressionParts[2];
    expressionParts[3] = expressionParts[3].startsWith("1/") ? expressionParts[3].replace("1/", "*/") : expressionParts[3];
    expressionParts[4] = expressionParts[4].startsWith("1/") ? expressionParts[4].replace("1/", "*/") : expressionParts[4];
    expressionParts[5] = expressionParts[5].startsWith("1/") ? expressionParts[5].replace("1/", "*/") : expressionParts[5];
    for (int i = 0; i <= 5; i++) {
      if ("*/1".equals(expressionParts[i])) {
        expressionParts[i] = "*";
      }
    }
    if (!StringUtils.isNumeric(expressionParts[5])) {
      for (int i = 0; i <= 6; i++) {
        expressionParts[5] = expressionParts[5].replace(DateAndTimeUtils.getDayOfWeekName(i + 1), String.valueOf(i));
      }
    }
    if (!StringUtils.isNumeric(expressionParts[4])) {
      for (int i = 1; i <= 12; i++) {
        DateTime currentMonth = new DateTime().withDayOfMonth(1).withMonthOfYear(i);
        String currentMonthDescription = currentMonth.toString("MMM", Locale.ENGLISH).toUpperCase();
        expressionParts[4] = expressionParts[4].replace(currentMonthDescription, String.valueOf(i));
      }
    }
    if ("0".equals(expressionParts[0])) {
      expressionParts[0] = StringUtils.EMPTY;
    }
  }
}