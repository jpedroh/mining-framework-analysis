package net.masterthought.cucumber;

import java.util.List;
import java.util.regex.Pattern;
import net.masterthought.cucumber.json.Feature;
import net.masterthought.cucumber.json.support.StepObject;
import net.masterthought.cucumber.json.support.TagObject;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class ReportResultTest extends ReportGenerator {
    @Before
    public void setUp() {
        setUpWithJson(SAMPLE_JSON);
    }

    @Test
    public void getAllFeatures_ReturnsFeatures() {

        // given
        // from @Before

        // when
        List<Feature> features = reportResult.getAllFeatures();

        // then
        assertThat(features).hasSize(2);
    }

    @Test
    public void getTags_ReturnsTags() {

        // given
        // from @Before

        // when
        List<TagObject> tags = reportResult.getAllTags();

        // then
        assertThat(tags).hasSize(3);
    }

    @Test
    public void getAllSteps_ReturnsSteps() {

        // given
        // from @Before

        // when
        List<StepObject> tags = reportResult.getAllSteps();

        // then
        assertThat(tags).hasSize(16);
    }

    @Test
    public void getTagReport_ReturnsTagReport() {
        // given
        // from @Before
        // when
        Reportable reportable = reportResult.getTagReport();
        // then
        assertThat(reportable.getDuration()).isEqualTo(509064334L);
    }

    @Test
    public void getAllXXXFeatures_ReturnsFeaturesByStatus() {
        // given
        // from @Before
        // when
        int passingFeatures = reportResult.getFeatureReport().getPassedFeatures();
        int failedFeatures = reportResult.getFeatureReport().getFailedFeatures();
        int pendingFeatures = reportResult.getFeatureReport().getPendingFeatures();
        int undefinedFeatures = reportResult.getFeatureReport().getUndefinedFeatures();
        // then
        assertThat(passingFeatures).isEqualTo(1);
        assertThat(failedFeatures).isEqualTo(1);
        assertThat(pendingFeatures).isEqualTo(0);
        assertThat(undefinedFeatures).isEqualTo(0);
    }

    @Test
    public void getBuildTime_ReturnsFormattedBuildTime() {

        // given
        // from @Before

        // when
        String time = reportResult.getBuildTime();

        // then
        // validate only format such as "17 lip 2016, 18:40" (dot for month because it can have local no ASCII characters
        assertThat(time).containsPattern(Pattern.compile("^\\d{0,2} .{3} \\d{4}, \\d{1,2}:\\d{1,2}$"));
    }
}