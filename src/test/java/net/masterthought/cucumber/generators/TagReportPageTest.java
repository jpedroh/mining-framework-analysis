package net.masterthought.cucumber.generators;

import net.masterthought.cucumber.generators.integrations.PageTest;
import net.masterthought.cucumber.json.support.TagObject;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class TagReportPageTest extends PageTest {

    @Before
    public void setUp() {
        setUpWithJson(SAMPLE_JSON);
    }

    @Test
    public void getWebPage_ReturnsTagReportFileName() {

        // given
        TagObject tag = tags.get(0);
        page = new TagReportPage(reportResult, configuration, tag);

        // when
        String fileName = page.getWebPage();

        // then
        assertThat(fileName).isEqualTo(tag.getReportFileName());
    }

    @Test
    public void prepareReport_AddsCustomProperties() {

        // given
        TagObject tag = tags.get(1);
        page = new TagReportPage(reportResult, configuration, tag);

        // when
        page.prepareReport();

        // then
        VelocityContext context = page.context;
<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/TagReportPageTest.java/left.java
        assertThat(context.getKeys()).hasSize(13);
||||||| /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/TagReportPageTest.java/base.java
        assertThat(context.getKeys()).hasSize(12);
=======
        assertThat(context.getKeys()).hasSize(11);
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/TagReportPageTest.java/right.java
        assertThat(context.get("tag")).isEqualTo(tag);
    }
}
