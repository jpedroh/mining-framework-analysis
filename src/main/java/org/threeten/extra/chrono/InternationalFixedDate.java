package org.threeten.extra.chrono;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

/**
 * A date in the International fixed calendar system.
 * <p>
 * Implements a pure International Fixed calendar (also known as the Cotsworth plan, the Eastman plan,
 * the 13 Month calendar or the Equal Month calendar) a solar calendar proposal for calendar reform designed by
 * Moses B. Cotsworth, who presented it in 1902.</p>
 * <p>
 * It provides for a year of 13 months of 28 days each, with one or two days a year belonging to no month or week.
 * It is therefore a perennial calendar, with every date fixed always on the same weekday.
 * Though it was never officially adopted in any country, it was the official calendar of the Eastman Kodak Company
 * from 1928 to 1989.</p>
 * <p>
 * This date operates using the {@linkplain InternationalFixedChronology International fixed calendar}.
 * This calendar system is a proposed reform calendar system, and is not in common use.
 * The International fixed differs from the Gregorian in terms of month count and length, and the leap year rule.
 * Dates are aligned such that {@code 0001-01-01 (International fixed)} is {@code 0000-12-31 (ISO)}.</p>
 * <p>
 * More information is available in the <a href='https://en.wikipedia.org/wiki/International_Fixed_Calendar'>International fixed Calendar</a> Wikipedia article.</p>
 * <p>
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.</p>
 */
public final class InternationalFixedDate extends AbstractDate implements ChronoLocalDate, Serializable {
  /**
     * Serialization version UID.
     */
  private static final long serialVersionUID = 
<<<<<<< /usr/src/app/output/threeten/threeten-extra/e5a3e5ebfa32bb843cf0b90c76354f8eeead3ebe/src/main/java/org/threeten/extra/chrono/InternationalFixedDate.java/left.java
  -7473722012599657263L
=======
  -5501342824322148215L
>>>>>>> /usr/src/app/output/threeten/threeten-extra/e5a3e5ebfa32bb843cf0b90c76354f8eeead3ebe/src/main/java/org/threeten/extra/chrono/InternationalFixedDate.java/right.java
  ;

  /**
     * The days per 400 year cycle.
     */
  private static final int LEAP_DAY_AS_DAY_OF_YEAR = 6 * InternationalFixedChronology.DAYS_IN_MONTH + 1;

  /**
    /**
     * Number of years in a decade.
     */
  private static final int YEARS_IN_DECADE = 10;

  /**
     * Number of years in a century.
     */
  private static final int YEARS_IN_CENTURY = 100;

  /**
     * Number of years in a millennium.
     */
  private static final int YEARS_IN_MILLENNIUM = 1000;

  /**
     * The proleptic year.
     */
  private final int prolepticYear;

  /**
     * The month.
     */
  private final int month;

  /**
     * The day.
     */
  private final int day;

  /**
     * Creates an instance from validated data.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @param month         the International fixed month, from 1 to 13
     * @param dayOfMonth    the International fixed day-of-month, from 1 to 28, the 29th is only legal for leap day and year day
     */
  private InternationalFixedDate(final int prolepticYear, final int month, final int dayOfMonth) {

<<<<<<< /usr/src/app/output/threeten/threeten-extra/e5a3e5ebfa32bb843cf0b90c76354f8eeead3ebe/src/main/java/org/threeten/extra/chrono/InternationalFixedDate.java/left.java
    ChronoField.YEAR.checkValidValue(prolepticYear);
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/threeten/threeten-extra/e5a3e5ebfa32bb843cf0b90c76354f8eeead3ebe/src/main/java/org/threeten/extra/chrono/InternationalFixedDate.java/left.java
    InternationalFixedChronology.MONTH_OF_YEAR_RANGE.checkValidValue(month, ChronoField.MONTH_OF_YEAR);
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/threeten/threeten-extra/e5a3e5ebfa32bb843cf0b90c76354f8eeead3ebe/src/main/java/org/threeten/extra/chrono/InternationalFixedDate.java/left.java
    InternationalFixedChronology.DAY_OF_MONTH_RANGE.checkValidValue(dayOfMonth, ChronoField.DAY_OF_MONTH);
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/threeten/threeten-extra/e5a3e5ebfa32bb843cf0b90c76354f8eeead3ebe/src/main/java/org/threeten/extra/chrono/InternationalFixedDate.java/left.java
    if (1 > prolepticYear) {
      throw new DateTimeException("Invalid date, year must be at least 1: " + prolepticYear + '-' + month + '-' + dayOfMonth);
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.

    this.prolepticYear = prolepticYear;
    this.month = month;
    this.day = dayOfMonth;
  }

  /**
     * Creates an instance from validated data.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @param dayOfYear     the day of the year
     * @return the International fixed date
     */
  private InternationalFixedDate(final int prolepticYear, final int dayOfYear) {
    boolean isLeapYear = getChronology().isLeapYear(prolepticYear);
    boolean isYearDay = dayOfYear == InternationalFixedChronology.DAYS_IN_YEAR + (isLeapYear ? 1 : 0);
    boolean isLeapDay = isLeapYear && dayOfYear == LEAP_DAY_AS_DAY_OF_YEAR;
    int doy = isLeapYear && dayOfYear > LEAP_DAY_AS_DAY_OF_YEAR ? dayOfYear - 1 : dayOfYear;
    this.prolepticYear = prolepticYear;
    this.month = isYearDay ? 0 : isLeapDay ? -1 : 1 + ((doy - 1) / InternationalFixedChronology.DAYS_IN_MONTH);
    this.day = isYearDay ? 0 : isLeapDay ? -1 : 1 + ((doy - 1) % InternationalFixedChronology.DAYS_IN_MONTH);
  }

  /**
     * Obtains the current {@code InternationalFixedDate} from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
  public static InternationalFixedDate now() {
    return now(Clock.systemDefaultZone());
  }

  /**
     * Obtains the current {@code InternationalFixedDate} from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone the zone ID to use, not null
     * @return the current date using the system clock, not null
     */
  public static InternationalFixedDate now(final ZoneId zone) {
    return now(Clock.system(zone));
  }

  /**
     * Obtains the current {@code InternationalFixedDate} from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param clock the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
  public static InternationalFixedDate now(final Clock clock) {
    LocalDate now = LocalDate.now(clock);
    return InternationalFixedDate.ofEpochDay(now.toEpochDay());
  }

  /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the proleptic-year, month-of-year and day-of-month fields.
     * <p>
     * This returns a {@code InternationalFixedDate} with the specified fields.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @param month         the International fixed month-of-year, from 1 to 13
     * @param dayOfMonth    the International fixed day-of-month, from 1 to 28
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-month is invalid for the month-year
     */
  public static InternationalFixedDate of(final int prolepticYear, final int month, final int dayOfMonth) {
    return create(prolepticYear, month, dayOfMonth);
  }

  /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the proleptic-year, for the month-less days of Leap Day, which follows the last day in June and precedes Sol 1.
     * <p/>
     * This returns a {@code InternationalFixedDate} with the specified fields.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-month is invalid for the month-year
     */
  public static InternationalFixedDate leapDay(final int prolepticYear) {
    return createLeapDay(prolepticYear);
  }

  /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the proleptic-year, for the month-less days of Year Day, which follows the last day in December.
     * <p/>
     * This returns a {@code InternationalFixedDate} with the specified fields.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-month is invalid for the month-year
     */
  public static InternationalFixedDate yearDay(final int prolepticYear) {
    return createYearDay(prolepticYear);
  }

  /**
     * Obtains a {@code InternationalFixedDate} from a temporal object.
     * <p>
     * This obtains a date in the International fixed calendar system based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code InternationalFixedDate}.
     * <p>
     * The conversion typically uses the {@link ChronoField#EPOCH_DAY EPOCH_DAY}
     * field, which is standardized across calendar systems.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code InternationalFixedDate::from}.
     *
     * @param temporal the temporal object to convert, not null
     * @return the date in the International fixed calendar system, not null
     * @throws DateTimeException if unable to convert to a {@code InternationalFixedDate}
     */
  public static InternationalFixedDate from(final TemporalAccessor temporal) {
    if (temporal instanceof InternationalFixedDate) {
      return (InternationalFixedDate) temporal;
    }
    return InternationalFixedDate.ofEpochDay(temporal.getLong(ChronoField.EPOCH_DAY));
  }

  /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the proleptic-year and day-of-year fields.
     * <p>
     * This returns a {@code InternationalFixedDate} with the specified fields.
     * The day must be valid for the year, otherwise an exception will be thrown.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @param dayOfYear     the International fixed day-of-year, from 1 to 371
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-year is invalid for the year
     */
  static InternationalFixedDate ofYearDay(final int prolepticYear, final int dayOfYear) {

<<<<<<< /usr/src/app/output/threeten/threeten-extra/e5a3e5ebfa32bb843cf0b90c76354f8eeead3ebe/src/main/java/org/threeten/extra/chrono/InternationalFixedDate.java/left.java
    ChronoField.YEAR.checkValidValue(prolepticYear)
=======
    InternationalFixedChronology.YEAR_RANGE.checkValidValue(prolepticYear, ChronoField.YEAR_OF_ERA)
>>>>>>> /usr/src/app/output/threeten/threeten-extra/e5a3e5ebfa32bb843cf0b90c76354f8eeead3ebe/src/main/java/org/threeten/extra/chrono/InternationalFixedDate.java/right.java
    ;
    InternationalFixedChronology.DAY_OF_YEAR_RANGE.checkValidValue(dayOfYear, ChronoField.DAY_OF_YEAR);
    if (dayOfYear == InternationalFixedChronology.DAYS_IN_YEAR + 1 && !InternationalFixedChronology.INSTANCE.isLeapYear(prolepticYear)) {
      throw new DateTimeException("Invalid Year Day: " + prolepticYear + '/' + dayOfYear);
    }
    return new InternationalFixedDate(prolepticYear, dayOfYear);
  }

  /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the epoch-day.
     *
     * @param epochDay the epoch day to convert based on 1970-01-01 (ISO)
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the epoch-day is out of range
     */
  static InternationalFixedDate ofEpochDay(final long epochDay) {

<<<<<<< /usr/src/app/output/threeten/threeten-extra/e5a3e5ebfa32bb843cf0b90c76354f8eeead3ebe/src/main/java/org/threeten/extra/chrono/InternationalFixedDate.java/left.java
    ChronoField.EPOCH_DAY.range().checkValidValue(epochDay, ChronoField.EPOCH_DAY)
=======
    InternationalFixedChronology.EPOCH_DAY_RANGE.checkValidValue(epochDay, ChronoField.EPOCH_DAY)
>>>>>>> /usr/src/app/output/threeten/threeten-extra/e5a3e5ebfa32bb843cf0b90c76354f8eeead3ebe/src/main/java/org/threeten/extra/chrono/InternationalFixedDate.java/right.java
    ;
    long zeroDay = epochDay + InternationalFixedChronology.DAYS_0000_TO_1970;
    if (zeroDay < 0) {
      throw new DateTimeException("Invalid epoch: " + epochDay);
    }
    long year = (400 * zeroDay) / InternationalFixedChronology.DAYS_PER_CYCLE;
    long doy = zeroDay - (InternationalFixedChronology.DAYS_IN_YEAR * year + InternationalFixedChronology.getLeapYearsBefore(year));
    boolean isLeapYear = InternationalFixedChronology.INSTANCE.isLeapYear(year);
    if (doy == (InternationalFixedChronology.DAYS_IN_YEAR + 1) && !isLeapYear) {
      year += 1;
      doy = 1;
    }
    if (doy == 0) {
      year -= 1;
      doy = InternationalFixedChronology.DAYS_IN_YEAR + (isLeapYear ? 1 : 0);
    }

<<<<<<< /usr/src/app/output/threeten/threeten-extra/e5a3e5ebfa32bb843cf0b90c76354f8eeead3ebe/src/main/java/org/threeten/extra/chrono/InternationalFixedDate.java/left.java
    int year = ChronoField.YEAR.checkValidIntValue(yearEst);
=======
>>>>>>> Unknown file: This is a bug in JDime.

    return ofYearDay((int) year, (int) doy);
  }

  private static InternationalFixedDate resolvePreviousValid(final int prolepticYear, final int month, final int day) {
    if (month == 0 && day == 0) {
      return createYearDay(prolepticYear);
    }
    if (month == -1 && day == -1 && InternationalFixedChronology.INSTANCE.isLeapYear(prolepticYear)) {
      return createLeapDay(prolepticYear);
    }
    int monthR = month == -1 ? 7 : month == 0 ? InternationalFixedChronology.MONTHS_IN_YEAR : Math.min(month, InternationalFixedChronology.MONTHS_IN_YEAR);
    int dayR = day == -1 ? 1 : day == 0 ? InternationalFixedChronology.DAYS_IN_MONTH : Math.min(day, InternationalFixedChronology.DAYS_IN_MONTH);
    return of(prolepticYear, monthR, dayR);
  }

  /**
     * Factory method, validates the given triplet year, month and dayOfMonth.
     * Special values are required for Year Day (N/0/0) and Leap Day (N/-1/-1).
     *
     * @param prolepticYear the International fixed proleptic-year
     * @param month         the International fixed month, from -1 to 13 (-1 for Leap Day, 0 for Year Day)
     * @param dayOfMonth    the International fixed day-of-month, from -1 to 28 (-1 for Leap Day, 0 for Year Day)
     * @return the International fixed date
     * @throws DateTimeException if the date is invalid
     */
  static InternationalFixedDate create(final int prolepticYear, final int month, final int dayOfMonth) {
    InternationalFixedChronology.YEAR_RANGE.checkValidValue(prolepticYear, ChronoField.YEAR_OF_ERA);
    InternationalFixedChronology.MONTH_OF_YEAR_RANGE.checkValidValue(month, ChronoField.MONTH_OF_YEAR);
    InternationalFixedChronology.DAY_OF_MONTH_RANGE.checkValidValue(dayOfMonth, ChronoField.DAY_OF_MONTH);
    if (((month < 1 || dayOfMonth < 1) && (dayOfMonth != month)) || ((month == -1) && (dayOfMonth == -1) && !InternationalFixedChronology.INSTANCE.isLeapYear(prolepticYear))) {
      throw new DateTimeException("Invalid date: " + prolepticYear + '/' + month + '/' + dayOfMonth);
    }
    return new InternationalFixedDate(prolepticYear, month, dayOfMonth);
  }

  /**
     * Factory method, validates the given year, accepts only valid leap-years.
     * Leap Day is a month-less day between end of June and beginning of Sol.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @return the International fixed date
     * @throws DateTimeException if the date is invalid
     */
  static InternationalFixedDate createLeapDay(final int prolepticYear) {
    InternationalFixedChronology.YEAR_RANGE.checkValidValue(prolepticYear, ChronoField.YEAR_OF_ERA);
    if (!InternationalFixedChronology.INSTANCE.isLeapYear(prolepticYear)) {
      throw new DateTimeException("Invalid Leap Day for year: " + prolepticYear);
    }
    return create(prolepticYear, -1, -1);
  }

  /**
     * Factory method, accepts any year, will be validated further down.
     * Year Day is a month-less day following the last day of December.
     *
     * @param prolepticYear the International fixed proleptic-year
     * @return the International fixed date
     * @throws DateTimeException if the date is invalid
     */
  static InternationalFixedDate createYearDay(final int prolepticYear) {
    InternationalFixedChronology.YEAR_RANGE.checkValidValue(prolepticYear, ChronoField.YEAR_OF_ERA);
    return create(prolepticYear, 0, 0);
  }

  /**
     * Validates the object.
     *
     * @return the resolved date, not null
     */
  private Object readResolve() {
    return InternationalFixedDate.of(prolepticYear, month, day);
  }

  @Override int getProlepticYear() {
    return prolepticYear;
  }

  public boolean isLeapDay() {
    return month == -1;
  }

  public boolean isYearDay() {
    return month == 0;
  }

  @Override int getMonth() {
    return month;
  }

  /**
     * For calculation purposes, consider Leap Day to be associated with month Sol.
     * In the same spirit, associate Year Day with the last month of the year.
     * @return
     */
  private int getCalculatedMonth() {
    return isYearDay() ? InternationalFixedChronology.MONTHS_IN_YEAR : isLeapDay() ? 7 : getMonth();
  }

  @Override int getDayOfMonth() {
    return day;
  }

  private int getCalculatedDayOfMonth() {
    return isYearDay() ? InternationalFixedChronology.DAYS_IN_MONTH + 1 : isLeapDay() ? 0 : getDayOfMonth();
  }

  @Override public int getDayOfYear() {
    return isLeapDay() ? LEAP_DAY_AS_DAY_OF_YEAR : isYearDay() ? InternationalFixedChronology.DAYS_IN_YEAR + (isLeapYear() ? 1 : 0) : (month - 1) * InternationalFixedChronology.DAYS_IN_MONTH + day + (isLeapYear() && month > 6 ? 1 : 0);
  }

  private int getDayOfYearAdjusted() {
    int d = getDayOfYear();
    return isLeapYear() && month > 6 ? d - 1 : d;
  }

  @Override InternationalFixedDate withDayOfYear(final int value) {
    return ofYearDay(getProlepticYear(), value);
  }

  @Override int lengthOfYearInMonths() {
    return InternationalFixedChronology.MONTHS_IN_YEAR;
  }

  @Override ValueRange rangeAlignedWeekOfMonth() {
    return month > 0 ? InternationalFixedChronology.WEEK_OF_MONTH_RANGE : InternationalFixedChronology.EMPTY_RANGE;
  }

  @Override InternationalFixedDate resolvePrevious(final int newYear, final int newMonth, final int dayOfMonth) {
    return resolvePreviousValid(newYear, newMonth, dayOfMonth);
  }

  @Override public ValueRange range(final TemporalField field) {
    boolean special = day < 1;
    if (field instanceof ChronoField) {
      if (isSupported(field)) {
        ChronoField f = (ChronoField) field;
        switch (f) {
          case ALIGNED_DAY_OF_WEEK_IN_MONTH:
          case DAY_OF_WEEK:
          return special ? InternationalFixedChronology.EMPTY_RANGE : ValueRange.of(1, InternationalFixedChronology.DAYS_IN_WEEK);
          case ALIGNED_DAY_OF_WEEK_IN_YEAR:
          return InternationalFixedChronology.ALIGNED_DAY_OF_WEEK_RANGE;
          case ALIGNED_WEEK_OF_MONTH:
          return special ? InternationalFixedChronology.EMPTY_RANGE : ValueRange.of(1, InternationalFixedChronology.WEEKS_IN_MONTH);
          case ALIGNED_WEEK_OF_YEAR:
          return special ? InternationalFixedChronology.EMPTY_RANGE : InternationalFixedChronology.WEEK_OF_YEAR_RANGE;
          case DAY_OF_MONTH:
          return isYearDay() ? InternationalFixedChronology.EMPTY_RANGE : isLeapDay() ? ValueRange.of(-1, -1) : ValueRange.of(1, InternationalFixedChronology.DAYS_IN_MONTH);
          case DAY_OF_YEAR:
          return isLeapYear() ? InternationalFixedChronology.DAY_OF_YEAR_LEAP_RANGE : InternationalFixedChronology.DAY_OF_YEAR_NORMAL_RANGE;
          case EPOCH_DAY:
          return InternationalFixedChronology.EPOCH_DAY_RANGE;
          case ERA:
          return InternationalFixedChronology.ERA_RANGE;
          case MONTH_OF_YEAR:
          return InternationalFixedChronology.MONTH_OF_YEAR_RANGE;
          default:
          break;
        }
      } else {
        throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
      }
    }
    return super.range(field);
  }

  /**
     * Gets the chronology of this date, which is the International fixed calendar system.
     * <p/>
     * The {@code Chronology} represents the calendar system in use.
     * The era and other fields in {@link ChronoField} are defined by the chronology.
     *
     * @return the International fixed chronology, not null
     */
  @Override public InternationalFixedChronology getChronology() {
    return InternationalFixedChronology.INSTANCE;
  }

  /**
     * Gets the era applicable at this date.
     * <p>
     * The International fixed calendar system only has one era, 'CE',
     * defined by {@link InternationalFixedEra}.
     *
     * @return the era applicable at this date, not null
     */
  @Override public InternationalFixedEra getEra() {
    return InternationalFixedEra.CE;
  }

  /**
     * Returns the length of the month represented by this date.
     * <p>
     * This returns the length of the month in days.
     * Month lengths do not match those of the ISO calendar system.
     *
     * @return the length of the month in days, 28 or 29
     */
  @Override public int lengthOfMonth() {
    return month > 0 ? InternationalFixedChronology.DAYS_IN_MONTH : 1;
  }

  @Override public int lengthOfYear() {
    return InternationalFixedChronology.DAYS_IN_YEAR + (isLeapYear() ? 1 : 0);
  }

  @Override public InternationalFixedDate with(final TemporalAdjuster adjuster) {
    return (InternationalFixedDate) adjuster.adjustInto(this);
  }

  @Override public InternationalFixedDate with(final TemporalField field, final long newValue) {
    if (field instanceof ChronoField) {
      ChronoField f = (ChronoField) field;
      getChronology().range(f).checkValidValue(newValue, f);
      if (f == ChronoField.DAY_OF_MONTH || f == ChronoField.MONTH_OF_YEAR) {
        if (newValue == 0) {
          return createYearDay(getProlepticYear());
        }
        if (newValue == -1) {
          return createLeapDay(getProlepticYear());
        }
      }
      int dom = isYearDay() ? 21 : (getCalculatedDayOfMonth() / 7) * 7;
      int d = getDayOfYearAdjusted() % 7;
      int nval = (int) newValue;
      switch (f) {
        case DAY_OF_WEEK:
        case ALIGNED_DAY_OF_WEEK_IN_MONTH:
        case ALIGNED_DAY_OF_WEEK_IN_YEAR:
        if (newValue == 0) {
          return this;
        }
        return resolvePreviousValid(getProlepticYear(), getMonth(), dom + nval);
        case ALIGNED_WEEK_OF_MONTH:
        if (newValue == 0) {
          return this;
        }
        return resolvePreviousValid(getProlepticYear(), getMonth(), (nval - 1) * 7 + d);
        case ALIGNED_WEEK_OF_YEAR:
        if (newValue == 0) {
          return this;
        }
        return ofYearDay(getProlepticYear(), (nval - 1) * 7 + d);
        default:
        break;
      }
    }
    return (InternationalFixedDate) super.with(field, newValue);
  }

  @Override public InternationalFixedDate plus(final TemporalAmount amount) {
    return (InternationalFixedDate) amount.addTo(this);
  }

  @Override public InternationalFixedDate plus(final long amountToAdd, final TemporalUnit unit) {
    if (unit instanceof ChronoUnit) {
      ChronoUnit f = (ChronoUnit) unit;
      switch (f) {
        case WEEKS:
        return plusWeeks(amountToAdd);
        case MONTHS:
        return plusMonths(amountToAdd);
        default:
        break;
      }
    }
    return (InternationalFixedDate) super.plus(amountToAdd, unit);
  }


<<<<<<< /usr/src/app/output/threeten/threeten-extra/e5a3e5ebfa32bb843cf0b90c76354f8eeead3ebe/src/main/java/org/threeten/extra/chrono/InternationalFixedDate.java/left.java
  /**
     * Returns a copy of this {@code InternationalFixedDate} with the specified period in years added.
     * <p>
     * This method adds the specified amount to the years field in two steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>If necessary, shift the index to account for the inserted/deleted leap-month.</li>
     * </ol>
     * <p>
     * In the International fixed Calendar, the month of December is 13 in non-leap-years, and 14 in leap years.
     * Shifting the index of the month thus means the month would still be the same.
     * <p>
     * In the case of moving from the inserted leap-month (destination year is non-leap), the month index is retained.
     * This has the effect of retaining the same day-of-year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param yearsToAdd the years to add, may be negative
     * @return a {@code InternationalFixedDate} based on this date with the years added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
  @Override InternationalFixedDate plusYears(final long yearsToAdd) {
    if (yearsToAdd == 0) {
      return this;
    }
    int newYear = ChronoField.YEAR.checkValidIntValue(getProlepticYear() + yearsToAdd);
    return resolvePreviousValid(newYear, month, day);
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  private InternationalFixedDate plusWeeks(final long weeks) {
    if (weeks == 0) {
      return this;
    }
    if (weeks % InternationalFixedChronology.WEEKS_IN_MONTH == 0) {
      return plusMonths(weeks / InternationalFixedChronology.WEEKS_IN_MONTH);
    }
    int dayOfWeek = getCalculatedDayOfWeek();
    long epoch = toEpochDay() + InternationalFixedChronology.DAYS_IN_WEEK * weeks;
    InternationalFixedDate newDate = InternationalFixedDate.ofEpochDay(epoch);
    int newDayOfWeek = newDate.getCalculatedDayOfWeek();
    if (dayOfWeek == newDayOfWeek) {
      return newDate;
    }
    epoch += Math.signum(weeks);
    return InternationalFixedDate.ofEpochDay(epoch);
  }

  /**
     * Returns a copy of this {@code InternationalFixedDate} with the specified period in months added.
     * <p>
     * This method adds the specified amount to the months field in three steps:
     * <ol>
     * <li>Add the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2006-12-13 plus one month would result in the invalid date 2006-13-13.
     * Instead of returning an invalid result, the last valid day of the month, 2006-13-07, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthsToAdd the months to add, may be negative
     * @return a {@code InternationalFixedDate} based on this date with the months added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
  @Override public InternationalFixedDate plusMonths(final long months) {
    if (months == 0) {
      return this;
    }
    if (months % InternationalFixedChronology.MONTHS_IN_YEAR == 0) {
      return (InternationalFixedDate) plusYears(months / InternationalFixedChronology.MONTHS_IN_YEAR);
    }
    int newDay = isLeapDay() ? 1 : getCalculatedDayOfMonth();
    int 
<<<<<<< /usr/src/app/output/threeten/threeten-extra/e5a3e5ebfa32bb843cf0b90c76354f8eeead3ebe/src/main/java/org/threeten/extra/chrono/InternationalFixedDate.java/left.java
    newYear = ChronoField.YEAR.checkValidIntValue(Math.floorDiv(calcMonths, InternationalFixedChronology.MONTHS_IN_YEAR))
=======
    newMonth = (int) Math.addExact(getProlepticMonth(), months)
>>>>>>> /usr/src/app/output/threeten/threeten-extra/e5a3e5ebfa32bb843cf0b90c76354f8eeead3ebe/src/main/java/org/threeten/extra/chrono/InternationalFixedDate.java/right.java
    ;
    int newYear = newMonth / InternationalFixedChronology.MONTHS_IN_YEAR;
    newMonth = 1 + (newMonth % InternationalFixedChronology.MONTHS_IN_YEAR);
    return resolvePreviousValid(newYear, newMonth, newDay);
  }

  /**
     * {@inheritDoc}
     */
  @Override public int getAlignedDayOfWeekInMonth() {
    if (day < 1) {
      return 0;
    }
    return ((getDayOfMonth() - 1) % lengthOfWeek()) + 1;
  }

  /**
     * {@inheritDoc}
     */
  @Override int getAlignedDayOfWeekInYear() {
    if (day < 1) {
      return 0;
    }
    return ((getDayOfYearAdjusted() - 1) % lengthOfWeek()) + 1;
  }

  /**
     * {@inheritDoc}
     */
  @Override int getAlignedWeekOfMonth() {
    if (day < 1) {
      return 0;
    }
    return ((getDayOfMonth() - 1) / lengthOfWeek()) + 1;
  }

  /**
     * {@inheritDoc}
     */
  @Override int getAlignedWeekOfYear() {
    if (day < 1) {
      return 0;
    }
    return 1 + (month - 1) * InternationalFixedChronology.WEEKS_IN_MONTH + ((day - 1) / InternationalFixedChronology.DAYS_IN_WEEK);
  }

  /**
     * Returns the day of the week represented by this date.
     * <p/>
     * Leap Day and Year Day are not considered week-days, thus return 0.
     *
     * @return the day of the week: between 1 and 7, or 0 (Leap Day, Year Day)
     */
  @Override public int getDayOfWeek() {
    if (day < 1) {
      return 0;
    }
    return 1 + ((day - 1) % InternationalFixedChronology.DAYS_IN_WEEK);
  }

  private int getCalculatedDayOfWeek() {
    return (day == 0) ? 7 : day == -1 ? 1 : 1 + (day - 1) % InternationalFixedChronology.DAYS_IN_WEEK;
  }

  /**
     * {@inheritDoc}
     */
  @Override long getProlepticMonth() {
    return getProlepticYear() * lengthOfYearInMonths() + getCalculatedMonth() - 1;
  }

  long getProlepticWeek() {
    return ((long) prolepticYear) * InternationalFixedChronology.WEEKS_IN_YEAR + getCalculatedMonth() * InternationalFixedChronology.WEEKS_IN_MONTH + ((getCalculatedDayOfMonth() - 1) / InternationalFixedChronology.DAYS_IN_WEEK) - 1;
  }

  @Override public InternationalFixedDate minus(final TemporalAmount amount) {
    return (InternationalFixedDate) amount.subtractFrom(this);
  }

  @Override public InternationalFixedDate minus(final long amountToSubtract, final TemporalUnit unit) {
    return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
  }

  @Override @SuppressWarnings(value = { "unchecked" }) public ChronoLocalDateTime<InternationalFixedDate> atTime(final LocalTime localTime) {
    return (ChronoLocalDateTime<InternationalFixedDate>) ChronoLocalDate.super.atTime(localTime);
  }

  @Override public long until(final Temporal endExclusive, final TemporalUnit unit) {
    return until(InternationalFixedDate.from(endExclusive), unit);
  }

  long until(final InternationalFixedDate end, final TemporalUnit unit) {
    if (unit instanceof ChronoUnit) {
      switch ((ChronoUnit) unit) {
        case WEEKS:
        return weeksUntil(end);
        case MONTHS:
        return monthsUntil(end);
        default:
        break;
      }
    }
    return super.until(end, unit);
  }

  /**
     * Get the number of years from this date to the given day.
     *
     * @param end The end date.
     * @return The number of years from this date to the given day.
     */
  private long yearsUntil(final InternationalFixedDate end) {
    long startYear = getProlepticYear() * 512L + getDayOfYear();
    long endYear = end.getProlepticYear() * 512L + end.getDayOfYear();
    return (endYear - startYear) / 512L;
  }

  @Override public ChronoPeriod until(final ChronoLocalDate endDateExclusive) {
    InternationalFixedDate end = InternationalFixedDate.from(endDateExclusive);
    int years = Math.toIntExact(yearsUntil(end));
    InternationalFixedDate sameYearEnd = (InternationalFixedDate) end.plusYears(years);
    int months = (int) monthsUntil(sameYearEnd);
    int days = (int) daysUntil(sameYearEnd.plusMonths(months));
    return getChronology().period(years, months, days);
  }

  private long weeksUntil(final InternationalFixedDate fixed) {
    int offset = (day < 1 || fixed.day < 1) && isLeapYear() && fixed.isLeapYear() ? (this.isBefore(fixed) ? 1 : -1) : 0;
    long start = this.getProlepticWeek() * 8L + this.getDayOfWeek();
    long end = fixed.getProlepticWeek() * 8L + fixed.getDayOfWeek();
    return (end - start - offset) / 8L;
  }

  /**
     * {@inheritDoc}
     */
  @Override long monthsUntil(final AbstractDate end) {
    InternationalFixedDate date = InternationalFixedDate.from(end);
    long monthStart = this.getProlepticMonth() * 32L + this.getCalculatedDayOfMonth();
    long monthEnd = date.getProlepticMonth() * 32L + date.getCalculatedDayOfMonth();
    return (monthEnd - monthStart) / 32L;
  }

  @Override public long toEpochDay() {
    long epochDay = ((long) getProlepticYear()) * InternationalFixedChronology.DAYS_IN_YEAR + InternationalFixedChronology.getLeapYearsBefore(getProlepticYear()) + getDayOfYear();
    return epochDay - InternationalFixedChronology.DAYS_0000_TO_1970;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
     * Display the date in human-readable format.
     * Note: Leap Day and Year Day are not part of any month; Leap Day is displayed as "N/-1/-1", Year Day as "N/0/0".
     *
     * @return The number of years from this date to the given day.
     */
  @Override public String toString() {
    StringBuilder buf = new StringBuilder(30);
    return buf.append(getChronology().toString()).append(' ').append(getEra()).append(' ').append(getYearOfEra()).append(month < 10 && month > 0 ? "/0" : '/').append(month).append(day < 10 && day > 0 ? "/0" : '/').append(day).toString();
  }
>>>>>>> /usr/src/app/output/threeten/threeten-extra/e5a3e5ebfa32bb843cf0b90c76354f8eeead3ebe/src/main/java/org/threeten/extra/chrono/InternationalFixedDate.java/right.java
}