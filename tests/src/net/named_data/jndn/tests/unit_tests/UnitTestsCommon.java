package net.named_data.jndn.tests.unit_tests;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import net.named_data.jndn.util.Common;

/**
 * UnitTestsCommon has static methods to help in unit tests.
 */
public class UnitTestsCommon {
  /**
   * Convert a UNIX timestamp to ISO time representation with the "T" in the middle.
   * @param msSince1970 Timestamp as milliseconds since Jan 1, 1970 UTC.
   * @return The string representation.
   */
  public static String toIsoString(double msSince1970) {
    return dateFormat.format(Common.millisecondsSince1970ToDate((long) Math.round(msSince1970)));
  }

  /**
   * Convert an ISO time representation with the "T" in the middle to a UNIX
   * timestamp.
   * @param timeString The ISO time representation.
   * @return The timestamp as milliseconds since Jan 1, 1970 UTC.
   */
  public static double fromIsoString(String timeString) throws ParseException {
    return (double) Common.dateToMillisecondsSince1970(dateFormat.parse(timeString));
  }

  private static SimpleDateFormat getDateFormat() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd\'T\'HHmmss");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return dateFormat;
  }

  static SimpleDateFormat dateFormat = getDateFormat();

  private static Common dummyCommon_ = new Common();
}