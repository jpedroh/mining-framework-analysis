package net.masterthought.cucumber.json.support;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import org.apache.commons.lang.NotImplementedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import net.masterthought.cucumber.ValidationException;
import net.masterthought.cucumber.generators.integrations.PageTest;
import net.masterthought.cucumber.json.Element;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class TagObjectTest extends PageTest {
  @Rule public ExpectedException thrown = ExpectedException.none();

  @Before public void setUp() {
    setUpWithJson(SAMPLE_JSON);
  }

  @Test public void TagObject_OnNullTagName_ThrowsException() {
    thrown.expect(ValidationException.class);
    new TagObject(null);
  }

  @Test public void getName_ReturnsTagName() {
    final String refName = "yourName";
    TagObject tag = new TagObject(refName);
    String name = tag.getName();
    assertThat(name).isEqualTo(refName);
  }

  @Test public void getReportFileName_ReturnsFileName() {
    TagObject tag = new TagObject("@client:output");
    String fileName = tag.getReportFileName();
    assertThat(fileName).isEqualTo("report-tag_client-output.html");
  }

  @Test public void getElements_ReturnsExactAddedElement() {
    TagObject tag = new TagObject("@checkout");
    Element[] elements = this.features.get(0).getElements();
    for (Element element : elements) {
      tag.addElement(element);
    }
    assertThat(tag.getElements().toArray()).hasSameElementsAs(Arrays.asList(elements));
  }

  @Test public void getFeatures_ThrowsException() {
    TagObject tag = new TagObject("@checkout");
    thrown.expect(NotImplementedException.class);
    tag.getFeatures();
  }

  @Test public void getPassedFeatures_ThrowsException() {
    TagObject tag = new TagObject("@checkout");
    thrown.expect(NotImplementedException.class);
    tag.getPassedFeatures();
  }

  @Test public void getFailedFeatures_ThrowsException() {
    TagObject tag = new TagObject("@checkout");
    thrown.expect(NotImplementedException.class);
    tag.getFailedFeatures();
  }

  @Test public void getScenarios_ReturnsSumOfScenarios() {
    TagObject tag = new TagObject("@checkout");
    Element[] elements = this.features.get(0).getElements();
    for (Element element : elements) {
      tag.addElement(element);
    }
    assertThat(tag.getScenarios()).isEqualTo(elements.length - 1);
  }

  @Test public void getPassedScenarios_ReturnsSumOfPassingScenarios() {
    TagObject tag = new TagObject("@checkout");
    Element[] elements = this.features.get(0).getElements();
    for (Element element : elements) {
      tag.addElement(element);
    }
    assertThat(tag.getPassedScenarios()).isEqualTo(2);
  }

  @Test public void getFailedScenarios_ReturnsSumOfFailedScenarios() {
    TagObject tag = new TagObject("@checkout");
    Element[] elements = this.features.get(0).getElements();
    for (Element element : elements) {
      tag.addElement(element);
    }
    assertThat(tag.getFailedScenarios()).isEqualTo(0);
  }

  @Test public void getDuration_ReturnsDuration() {
    TagObject tag = new TagObject("@checkout");
    Element[] elements = this.features.get(0).getElements();
    for (Element element : elements) {
      tag.addElement(element);
    }
    assertThat(tag.getDuration()).isEqualTo(99263122889L);
    assertThat(tag.getFormattedDuration()).isEqualTo("1:39.263");
  }

  @Test public void getSteps_ReturnsSumOfSteps() {
    TagObject tag = new TagObject("@checkout");
    Element[] elements = this.features.get(0).getElements();
    for (Element element : elements) {
      tag.addElement(element);
    }
    assertThat(tag.getSteps()).isEqualTo(10);
  }

  @Test public void getNumberOfStatus_OnStatus__ReturnsSumOfStatuses() {
    TagObject tag = new TagObject("@checkout");
    Element[] elements = this.features.get(0).getElements();
    for (Element element : elements) {
      tag.addElement(element);
    }
    assertThat(tag.getNumberOfStatus(Status.PASSED)).isEqualTo(10);
    assertThat(tag.getNumberOfStatus(Status.FAILED)).isEqualTo(0);
    assertThat(tag.getNumberOfStatus(Status.PENDING)).isEqualTo(0);
  }

  @Test public void getStatus_ReturnsStatus() {
    TagObject tag = new TagObject("hello");
    tag.addElement(this.features.get(0).getElements()[0]);
    Status status = tag.getStatus();
    assertThat(status).isEqualTo(Status.PASSED);
  }

  @Test public void getRawStatus_ReturnsRawOfFinalStatus() {
    TagObject tag = new TagObject("@checkout");
    Element[] elements = this.features.get(0).getElements();
    for (Element element : elements) {
      tag.addElement(element);
    }
    assertThat(tag.getRawStatus()).isEqualTo(Status.PASSED.getRawName());
  }

  @Test public void getDeviceName_ThrowsException() {
    TagObject tag = new TagObject("@checkout");
    thrown.expect(NotImplementedException.class);
    tag.getDeviceName();
  }
}