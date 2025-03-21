package net.masterthought.cucumber.generators;

import java.util.ArrayList;
import java.util.List;

import net.masterthought.cucumber.generators.integrations.PageTest;
import net.masterthought.cucumber.json.Element;
import net.masterthought.cucumber.json.Feature;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class FailuresOverviewPageTest extends PageTest {

    @Before
    public void setUp() {
        setUpWithJson(SAMPLE_JSON);
    }

    @Test
    public void getWebPage_ReturnsFailureReportFileName() {

        // given
        page = new FailuresOverviewPage(reportResult, configuration);

        // when
        String fileName = page.getWebPage();

        // then
        assertThat(fileName).isEqualTo(FailuresOverviewPage.WEB_PAGE);
    }

    @Test
    public void prepareReport_AddsCustomProperties() {

        // given
        page = new FailuresOverviewPage(reportResult, configuration);
        // this page only has failed scenarios (elements) so extract them into
        // a list to compare
        List<Element> failures = new ArrayList<>();
        for (Feature feature : features) {
            if (feature.getStatus().isPassed()) {
                continue;
            }

            for (Element element : feature.getElements()) {
                if (element.getStepsStatus().isPassed())
                    continue;

                failures.add(element);
            }
        }

        // when
        page.prepareReport();

        // then
        VelocityContext context = page.context;
<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/FailuresOverviewPageTest.java/left.java
        assertThat(context.getKeys()).hasSize(13);
||||||| /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/FailuresOverviewPageTest.java/base.java
        assertThat(context.getKeys()).hasSize(12);
=======
        assertThat(context.getKeys()).hasSize(11);
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/FailuresOverviewPageTest.java/right.java

        List<Element> elements = (List<Element>) context.get("failures");
        assertThat(elements).hasSameElementsAs(failures);
    }
}
