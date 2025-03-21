package net.masterthought.cucumber.generators;
import static org.assertj.core.api.Assertions.assertThat;
import net.masterthought.cucumber.generators.integrations.PageTest;
import org.apache.velocity.VelocityContext;
import net.masterthought.cucumber.json.support.StepObject;
import org.junit.Before;
import net.masterthought.cucumber.util.Util;
import org.junit.Test;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class StepsOverviewPageTest extends PageTest {
  @Before public void setUp() {
    setUpWithJson(SAMPLE_JSON);
  }

  @Test public void getWebPage_ReturnsStepsOverviewFileName() {
    page = new StepsOverviewPage(reportResult, configuration);
    String fileName = page.getWebPage();
    assertThat(fileName).isEqualTo(StepsOverviewPage.WEB_PAGE);
  }

  @Test public void prepareReport_AddsCustomProperties() {
    page = new StepsOverviewPage(reportResult, configuration);
    page.prepareReport();
    VelocityContext context = page.context;
    assertThat(context.getKeys()).hasSize(
<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/StepsOverviewPageTest.java/left.java
    17
=======
    15
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/StepsOverviewPageTest.java/right.java
    );
    assertThat(context.get("all_steps")).isEqualTo(steps);
    int allOccurrences = 0;
    long allDurations = 0;
    long maxDuration = 0;
    for (StepObject stepObject : reportResult.getAllSteps()) {
      allOccurrences += stepObject.getTotalOccurrences();
      allDurations += stepObject.getDuration();
      if (stepObject.getDuration() > maxDuration) {
        maxDuration = stepObject.getMaxDuration();
      }
    }
    assertThat(context.get("all_occurrences")).isEqualTo(allOccurrences);
    long average = allDurations / (allOccurrences == 0 ? 1 : allOccurrences);
    assertThat(context.get("all_average_duration")).isEqualTo(Util.formatDuration(average));
    assertThat(context.get("all_max_duration")).isEqualTo(Util.formatDuration(maxDuration));
    assertThat(context.get("all_durations")).isEqualTo(Util.formatDuration(allDurations));
  }
}