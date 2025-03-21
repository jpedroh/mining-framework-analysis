package net.masterthought.cucumber.generators;

import net.masterthought.cucumber.generators.integrations.PageTest;
import net.masterthought.cucumber.json.support.StepObject;
import net.masterthought.cucumber.util.Util;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class StepsOverviewPageTest extends PageTest {

    @Before
    public void setUp() {
        setUpWithJson(SAMPLE_JSON);
    }

    @Test
    public void getWebPage_ReturnsStepsOverviewFileName() {

        // given
        page = new StepsOverviewPage(reportResult, configuration);

        // when
        String fileName = page.getWebPage();

        // then
        assertThat(fileName).isEqualTo(StepsOverviewPage.WEB_PAGE);
    }

    @Test
    public void prepareReport_AddsCustomProperties() {

        // given
        page = new StepsOverviewPage(reportResult, configuration);

        // when
        page.prepareReport();

        // then
        VelocityContext context = page.context;
<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/StepsOverviewPageTest.java/left.java
        assertThat(context.getKeys()).hasSize(17);
||||||| /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/StepsOverviewPageTest.java/base.java
        assertThat(context.getKeys()).hasSize(16);
=======
        assertThat(context.getKeys()).hasSize(15);
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/StepsOverviewPageTest.java/right.java
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
