package com.cronutils;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import static org.junit.Assert.fail;

public class Issue143Test {
  private static final String LAST_EXECUTION_NOT_PRESENT_ERROR = "last execution was not present";

  private CronParser parser;

  private ZonedDateTime currentDateTime;

  @Before public void setUp() {
    currentDateTime = ZonedDateTime.of(LocalDateTime.of(2016, 12, 20, 12, 0), ZoneId.systemDefault());
    parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
  }

  @Test public void testCase1() {
    final ExecutionTime et = ExecutionTime.forCron(parser.parse("0 0 12 31 12 ? *"));
    final Optional<ZonedDateTime> 
<<<<<<< /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/left.java
    lastExecution = et.lastExecution(currentDateTime)
=======
    olast = et.lastExecution(currentDateTime)
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/right.java
    ;

<<<<<<< /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/left.java
    if (lastExecution.isPresent()) {
      final ZonedDateTime actual = lastExecution.get();
      final ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2015, 12, 31, 12, 00), ZoneId.systemDefault());
      Assert.assertEquals(expected, actual);
    }
=======
    ZonedDateTime last = olast.orElse(null);
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2015, 12, 31, 12, 0), ZoneId.systemDefault());
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    Assert.assertEquals(expected, last);
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/right.java
  }

  @Test public void testCase2() {
    final ExecutionTime et = ExecutionTime.forCron(parser.parse("0 0 12 ? 12 SAT#5 *"));
    final Optional<ZonedDateTime> 
<<<<<<< /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/left.java
    lastExecution = et.lastExecution(currentDateTime)
=======
    olast = et.lastExecution(currentDateTime)
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/right.java
    ;

<<<<<<< /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/left.java
    if (lastExecution.isPresent()) {
      final ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2012, 12, 29, 12, 00), ZoneId.systemDefault());
      Assert.assertEquals(expected, lastExecution.get());
    } else {
      fail(LAST_EXECUTION_NOT_PRESENT_ERROR);
    }
=======
    ZonedDateTime last = olast.orElse(null);
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2012, 12, 29, 12, 0), ZoneId.systemDefault());
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    Assert.assertEquals(expected, last);
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/right.java
  }

  @Test @Ignore public void testCase3() {
    final ExecutionTime et = ExecutionTime.forCron(parser.parse("0 0 12 31 1/1 ? *"));
    final Optional<ZonedDateTime> 
<<<<<<< /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/left.java
    lastExecution = et.lastExecution(currentDateTime)
=======
    olast = et.lastExecution(currentDateTime)
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/right.java
    ;

<<<<<<< /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/left.java
    if (lastExecution.isPresent()) {
      final ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2015, 12, 31, 12, 00), ZoneId.systemDefault());
      Assert.assertEquals(expected, lastExecution.get());
    } else {
      fail(LAST_EXECUTION_NOT_PRESENT_ERROR);
    }
=======
    ZonedDateTime last = olast.orElse(null);
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2015, 12, 31, 12, 0), ZoneId.systemDefault());
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    Assert.assertEquals(expected, last);
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/right.java
  }

  @Test public void testCase4() {
    final ExecutionTime et = ExecutionTime.forCron(parser.parse("0 0 12 ? 1/1 SAT#5 *"));
    final Optional<ZonedDateTime> 
<<<<<<< /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/left.java
    lastExecution = et.lastExecution(currentDateTime)
=======
    olast = et.lastExecution(currentDateTime)
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/right.java
    ;

<<<<<<< /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/left.java
    if (lastExecution.isPresent()) {
      final ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2016, 10, 29, 12, 00), ZoneId.systemDefault());
      Assert.assertEquals(expected, lastExecution.get());
    } else {
      fail(LAST_EXECUTION_NOT_PRESENT_ERROR);
    }
=======
    ZonedDateTime last = olast.orElse(null);
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2016, 10, 29, 12, 0), ZoneId.systemDefault());
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    Assert.assertEquals(expected, last);
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue143Test.java/right.java
  }
}