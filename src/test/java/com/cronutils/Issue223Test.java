package com.cronutils;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.Test;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Issue223Test {
  /**
     * Issue #223: for dayOfWeek value == 3 && division of day, nextExecution do not return correct results.
     */
  @Test public void testEveryWednesdayOfEveryDayNextExecution() {
    final CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX);
    final CronParser parser = new CronParser(cronDefinition);
    final Cron myCron = parser.parse("* * * * 3");
    ZonedDateTime time = ZonedDateTime.parse("2017-09-05T11:31:55.407-05:00");
    final Optional<ZonedDateTime> 
<<<<<<< /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue223Test.java/left.java
    nextExecution = ExecutionTime.forCron(myCron).nextExecution(time)
=======
    onext = ExecutionTime.forCron(myCron).nextExecution(time)
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue223Test.java/right.java
    ;

<<<<<<< /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue223Test.java/left.java
    if (nextExecution.isPresent()) {
      assertEquals(ZonedDateTime.parse("2017-09-06T00:00-05:00"), nextExecution.get());
    } else {
      fail("next execution was not present");
    }
=======
    ZonedDateTime next = onext.orElse(null);
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue223Test.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    assertEquals(ZonedDateTime.parse("2017-09-06T00:00-05:00"), next);
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue223Test.java/right.java

    final Cron myCron2 = parser.parse("* * */1 * 3");
    time = ZonedDateTime.parse("2017-09-05T11:31:55.407-05:00");
    final Optional<ZonedDateTime> 
<<<<<<< /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue223Test.java/left.java
    nextExecution2 = ExecutionTime.forCron(myCron2).nextExecution(time)
=======
    onext2 = ExecutionTime.forCron(myCron2).nextExecution(time)
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue223Test.java/right.java
    ;

<<<<<<< /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue223Test.java/left.java
    if (nextExecution2.isPresent()) {
      assertEquals(ZonedDateTime.parse("2017-09-06T00:00-05:00"), nextExecution2.get());
    } else {
      fail("next execution was not present");
    }
=======
    ZonedDateTime next2 = onext2.orElse(null);
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue223Test.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    assertEquals(ZonedDateTime.parse("2017-09-06T00:00-05:00"), next2);
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/Issue223Test.java/right.java
  }
}