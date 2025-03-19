package com.cronutils;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import org.junit.Test;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

public class OpenIssuesTest {
  private final DateTimeFormatter dfSimple = DateTimeFormatter.ofPattern("hh:mm:ss MM/dd/yyyy a X", Locale.US);

  private final DateTimeFormatter df = DateTimeFormatter.ofPattern("hh:mm:ss EEE, MMM dd yyyy a X", Locale.US);

  @Test public void testBasicCron() {
    printDate("03:15:00 11/20/2015 PM Z");
    printDate("03:15:00 11/27/2015 PM Z");
  }

  private void printDate(final String startDate) {
    final ZonedDateTime now = ZonedDateTime.parse(startDate, dfSimple);
    System.out.println("Starting: " + df.format(now));
    printNextDate(now, "0 6 * * 0");
    printNextDate(now, "0 6 * * 1");
    printNextDate(now, "0 6 * * 2");
    printNextDate(now, "0 6 * * 3");
    printNextDate(now, "0 6 * * 4");
    printNextDate(now, "0 6 * * 5");
    printNextDate(now, "0 6 * * 6");
  }

  private void printNextDate(final ZonedDateTime now, final String cronString) {
    final ZonedDateTime date = nextSchedule(cronString, now);
    System.out.println("Next time: " + df.format(date));
  }

  private static ZonedDateTime nextSchedule(final String cronString, final ZonedDateTime lastExecution) {
    final CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
    final Cron cron = cronParser.parse(cronString);
    final ExecutionTime executionTime = ExecutionTime.forCron(cron);

<<<<<<< /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/OpenIssuesTest.java/left.java
    final Optional<ZonedDateTime> nextExecution = executionTime.nextExecution(lastExecution);
=======
    return executionTime.nextExecution(lastExecution).isPresent() ? executionTime.nextExecution(lastExecution).get() : null;
>>>>>>> /usr/src/app/output/jmrozanec/cron-utils/219633080b459601437e67784e60a42ca6d53e10/src/test/java/com/cronutils/OpenIssuesTest.java/right.java

    if (nextExecution.isPresent()) {
      return nextExecution.get();
    } else {
      throw new NullPointerException("next execution is not present");
    }
  }
}