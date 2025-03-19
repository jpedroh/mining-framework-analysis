package com.cronutils;
import org.junit.Before;
import org.junit.Test;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class Issue58UnixCronAsStringIntegrationTest {
  private CronParser cronParser;

  @Before public void setup() {
    final CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX);
    cronParser = new CronParser(cronDefinition);
  }

  @Test public void everyEvenHourShouldBeParsedCorrectly() {
    final Cron cron = cronParser.parse("0 0/1 * * *");
    assertThat(cron.asString(), anyOf(is("0 0/1 * * *"), is("0 /1 * * *"), is("0 0 * * *")));
  }

  @Test public void everyOddHourShouldBeParsedCorrectly() {
    final Cron cron = cronParser.parse("0 1/2 * * *");
    assertThat(cron.asString(), is("0 1/2 * * *"));
  }

  @Test public void everyEvenMinuteShouldBeParsedCorrectly() {
    final Cron cron = cronParser.parse("0/1 * * * *");
    assertThat(cron.asString(), anyOf(is("0/1 * * * *"), is("/1 * * * *"), is("0 * * * *")));
  }

  @Test public void everyOddMinuteShouldBeParsedCorrectly() {
    final Cron cron = cronParser.parse("1/2 * * * *");
    assertThat(cron.asString(), is("1/2 * * * *"));
  }
}