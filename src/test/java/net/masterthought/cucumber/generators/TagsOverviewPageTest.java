package net.masterthought.cucumber.generators;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import mockit.Deencapsulation;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import net.masterthought.cucumber.generators.integrations.PageTest;
import net.masterthought.cucumber.json.support.TagObject;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class TagsOverviewPageTest extends PageTest {
  @Before public void setUp() {
    setUpWithJson(SAMPLE_JSON);
  }

  @Test public void getWebPage_ReturnsFeatureFileName() {
    page = new TagsOverviewPage(reportResult, configuration);
    String fileName = page.getWebPage();
    assertThat(fileName).isEqualTo("tag-overview.html");
  }

  @Test public void prepareReportAddsCustomProperties() {
    page = new TagsOverviewPage(reportResult, configuration);
    page.prepareReport();
    VelocityContext context = Deencapsulation.getField(page, "context");
    assertThat(context.getKeys()).hasSize(9);
    assertThat(context.get("all_tags")).isEqualTo(tags);
    assertThat(context.get("report_summary")).isEqualTo(reportResult.getTagReport());
    assertThat(context.get("chart_categories")).isEqualTo(TagsOverviewPage.generateTagLabels(tags));
    assertThat(context.get("chart_data")).isEqualTo(TagsOverviewPage.generateTagValues(tags));
  }

  @Test public void prepareReport_setTagsToExcludeFromChart_ReturnsFilteredTags() {
    page = new TagsOverviewPage(reportResult, configuration);
    configuration.setTagsToExcludeFromChart("@checkout", "@feature.*");
    page.prepareReport();
    VelocityContext context = Deencapsulation.getField(page, "context");
    assertThat(context.get("chart_categories")).isEqualTo("[\"@fast\"]");
    assertThat(context.get("chart_data")).isEqualTo(asList("[57.14]", "[0.00]", "[0.00]", "[28.57]", "[14.29]", "[0.00]"));
  }

  @Test public void generateTagLabels_ReturnsTags() {
    List<TagObject> allTags = this.tags;
    String labels = TagsOverviewPage.generateTagLabels(allTags);
    assertThat(labels).isEqualTo("[\"@checkout\",\"@fast\",\"@featureTag\"]");
  }

  @Test public void generateTagValues_ReturnsTagValues() {
    List<TagObject> allTags = this.tags;
    List<String> labels = TagsOverviewPage.generateTagValues(allTags);
    assertThat(labels).containsExactly("[50.00,57.14,57.14]", "[0.00,0.00,0.00]", "[25.00,0.00,0.00]", "[12.50,28.57,28.57]", "[6.25,14.29,14.29]", "[6.25,0.00,0.00]");
  }

  @Test public void format_ReturnsFormatedValue() {
    final int[][] values = { { 1, 3 }, { 2, 2 }, { 1, 5 }, { 0, 5 } };
    String[] formatted = { "33.33", "100.00", "20.00", "0.00" };
    for (int i = 0; i < values.length; i++) {
      assertThat(TagsOverviewPage.format(values[i][0], values[i][1])).isEqualTo(formatted[i]);
    }
  }
}