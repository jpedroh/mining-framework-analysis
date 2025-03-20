package net.named_data.jndn.tests.unit_tests;
import java.text.ParseException;
import net.named_data.jndn.encrypt.Interval;
import static net.named_data.jndn.tests.unit_tests.UnitTestsCommon.toIsoString;
import static net.named_data.jndn.tests.unit_tests.UnitTestsCommon.fromIsoString;
import net.named_data.jndn.util.Common;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class TestInterval {
  @Test public void testConstruction() throws ParseException {
    Interval interval1 = new Interval(fromIsoString("20150825T120000"), fromIsoString("20150825T160000"));
    assertEquals("20150825T120000", toIsoString(interval1.getStartTime()));
    assertEquals("20150825T160000", toIsoString(interval1.getEndTime()));
    assertEquals(true, interval1.isValid());
    Interval interval2 = new Interval();
    assertEquals(false, interval2.isValid());
    Interval interval3 = new Interval(true);
    assertEquals(true, interval3.isValid());
    assertEquals(true, interval3.isEmpty());
  }

  @Test public void testCoverTimePoint() throws ParseException {
    Interval interval = new Interval(fromIsoString("20150825T120000"), fromIsoString("20150825T160000"));
    double timePoint1 = fromIsoString("20150825T120000");
    double timePoint2 = fromIsoString("20150825T130000");
    double timePoint3 = fromIsoString("20150825T170000");
    double timePoint4 = fromIsoString("20150825T110000");
    assertEquals(true, interval.covers(timePoint1));
    assertEquals(true, interval.covers(timePoint2));
    assertEquals(false, interval.covers(timePoint3));
    assertEquals(false, interval.covers(timePoint4));
  }

  @Test public void testIntersectionAndUnion() throws ParseException, Interval.Error {
    Interval interval1 = new Interval(fromIsoString("20150825T030000"), fromIsoString("20150825T050000"));
    Interval interval2 = new Interval(fromIsoString("20150825T050000"), fromIsoString("20150825T070000"));
    Interval interval3 = new Interval(fromIsoString("20150825T060000"), fromIsoString("20150825T070000"));
    Interval interval4 = new Interval(fromIsoString("20150825T010000"), fromIsoString("20150825T040000"));
    Interval interval5 = new Interval(fromIsoString("20150825T030000"), fromIsoString("20150825T040000"));
    Interval interval6 = new Interval(fromIsoString("20150825T010000"), fromIsoString("20150825T050000"));
    Interval interval7 = new Interval(true);
    Interval tempInterval = new Interval(interval1);
    tempInterval.intersectWith(interval2);
    assertEquals(true, tempInterval.isEmpty());
    tempInterval = new Interval(interval1);
    boolean gotError = true;
    try {
      tempInterval.unionWith(interval2);
      gotError = false;
    } catch (Throwable ex) {
    }
    if (!gotError) {
      fail("Expected error in unionWith(interval2)");
    }
    tempInterval = new Interval(interval1);
    tempInterval.intersectWith(interval3);
    assertEquals(true, tempInterval.isEmpty());
    tempInterval = new Interval(interval1);
    gotError = true;
    try {
      tempInterval.unionWith(interval3);
      gotError = false;
    } catch (Interval.Error ex) {
    }
    if (!gotError) {
      fail("Expected error in unionWith(interval3)");
    }
    tempInterval = new Interval(interval1);
    tempInterval.intersectWith(interval4);
    assertEquals(false, tempInterval.isEmpty());
    assertEquals("20150825T030000", toIsoString(tempInterval.getStartTime()));
    assertEquals("20150825T040000", toIsoString(tempInterval.getEndTime()));
    tempInterval = new Interval(interval1);
    tempInterval.unionWith(interval4);
    assertEquals(false, tempInterval.isEmpty());
    assertEquals("20150825T010000", toIsoString(tempInterval.getStartTime()));
    assertEquals("20150825T050000", toIsoString(tempInterval.getEndTime()));
    tempInterval = new Interval(interval1);
    tempInterval.intersectWith(interval5);
    assertEquals(false, tempInterval.isEmpty());
    assertEquals("20150825T030000", toIsoString(tempInterval.getStartTime()));
    assertEquals("20150825T040000", toIsoString(tempInterval.getEndTime()));
    tempInterval = new Interval(interval1);
    tempInterval.unionWith(interval5);
    assertEquals(false, tempInterval.isEmpty());
    assertEquals("20150825T030000", toIsoString(tempInterval.getStartTime()));
    assertEquals("20150825T050000", toIsoString(tempInterval.getEndTime()));
    tempInterval = new Interval(interval1);
    tempInterval.intersectWith(interval6);
    assertEquals(false, tempInterval.isEmpty());
    assertEquals("20150825T030000", toIsoString(tempInterval.getStartTime()));
    assertEquals("20150825T050000", toIsoString(tempInterval.getEndTime()));
    tempInterval = new Interval(interval1);
    tempInterval.unionWith(interval6);
    assertEquals(false, tempInterval.isEmpty());
    assertEquals("20150825T010000", toIsoString(tempInterval.getStartTime()));
    assertEquals("20150825T050000", toIsoString(tempInterval.getEndTime()));
    tempInterval = new Interval(interval1);
    tempInterval.intersectWith(interval7);
    assertEquals(true, tempInterval.isEmpty());
    tempInterval = new Interval(interval1);
    tempInterval.unionWith(interval7);
    assertEquals(false, tempInterval.isEmpty());
    assertEquals("20150825T030000", toIsoString(tempInterval.getStartTime()));
    assertEquals("20150825T050000", toIsoString(tempInterval.getEndTime()));
  }

  private static Common dummyCommon_ = new Common();
}