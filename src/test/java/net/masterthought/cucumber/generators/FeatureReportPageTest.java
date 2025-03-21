package net.masterthought.cucumber.generators;

import net.masterthought.cucumber.generators.integrations.PageTest;
import net.masterthought.cucumber.json.Element;
import net.masterthought.cucumber.json.Embedding;
import net.masterthought.cucumber.json.Feature;
import net.masterthought.cucumber.json.Step;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class FeatureReportPageTest extends PageTest {

    @Before
    public void setUp() {
        setUpWithJson(SAMPLE_JSON);
    }

    @Test
    public void getWebPage_ReturnsFeatureFileName() {

        // given
        Feature feature = features.get(1);
        page = new FeatureReportPage(reportResult, configuration, feature);

        // when
        String fileName = page.getWebPage();

        // then
        assertThat(fileName).isEqualTo(feature.getReportFileName());
    }

    @Test
    public void prepareReport_AddsCustomProperties() {

        // given
        Feature feature = features.get(1);
        page = new FeatureReportPage(reportResult, configuration, feature);

        // when
        page.prepareReport();

        // then
        VelocityContext context = page.context;
<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/FeatureReportPageTest.java/left.java
        assertThat(context.getKeys()).hasSize(13);
||||||| /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/FeatureReportPageTest.java/base.java
        assertThat(context.getKeys()).hasSize(12);
=======
        assertThat(context.getKeys()).hasSize(11);
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/FeatureReportPageTest.java/right.java
        assertThat(context.get("feature")).isEqualTo(feature);
    }

    @Test
    public void getMimeType_OnEmbeddingFromV2CucumberReportFile_SupportsScreenshots() {
        // given
        Feature feature = features.get(0);
        Element element = feature.getElements()[0];
        Step step = element.getSteps()[0];

        // when
        Embedding[] embeddings = step.getEmbeddings();

        // then
        assertThat(embeddings[0].getMimeType()).isEqualTo("image/url");
    }

    @Test
    public void getMimeType_OnEmbeddingFromV3CucumberReportFile_SupportsScreenshots() {
        // given
        Feature feature = features.get(0);
        Element element = feature.getElements()[0];
        Step step = element.getSteps()[0];

        // when
        Embedding[] embeddings = step.getEmbeddings();

        // then
        assertThat(embeddings[1].getMimeType()).isEqualTo("text/plain");
    }
}
