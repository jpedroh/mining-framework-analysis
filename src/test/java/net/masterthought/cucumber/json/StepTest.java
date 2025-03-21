package net.masterthought.cucumber.json;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import net.masterthought.cucumber.generators.integrations.PageTest;
import net.masterthought.cucumber.json.support.Status;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class StepTest extends PageTest {
  @Before public void setUp() {
    setUpWithJson(SAMPLE_JSON);
  }

  @Test public void getRows_ReturnsRows() {
    Step step = features.get(0).getElements()[0].getSteps()[2];
    Row[] rows = step.getRows();
    assertThat(rows).hasSize(5);
    assertThat(rows[0].getCells()).containsOnlyOnce("M\u00fcller", "Deutschland");
  }

  @Test public void getRows_OnArguments_ReturnsRows() {
    Step step = features.get(0).getElements()[1].getSteps()[5];
    Row[] rows = step.getRows();
    assertThat(rows).hasSize(2);
    assertThat(rows[0].getCells()).containsOnlyOnce("max", "min");
  }

  @Test public void getName_ReturnsName() {
    Step step = features.get(0).getElements()[0].getSteps()[0];
    String name = step.getName();
    assertThat(name).isEqualTo("I have a new credit card");
  }

  @Test public void getKeyword_ReturnsKeyword() {
    Step step = features.get(0).getElements()[0].getSteps()[1];
    String keyword = step.getKeyword();
    assertThat(keyword).isEqualTo("And");
  }

  @Test public void getOutput_ReturnsOutput() {
    Step step = features.get(1).getElements()[0].getSteps()[7];
    Output[] outputs = step.getOutputs();
    assertThat(getMessages(outputs)).containsOnlyOnce("Could not connect to the server @Rocky@", "Could not connect to the server @Mike@");
  }

  @Test public void getMatch_ReturnsMatch() {
    Step step = features.get(1).getElements()[0].getSteps()[4];
    Match match = step.getMatch();
    assertThat(match.getLocation()).isEqualTo("ATMScenario.checkMoney(int)");
  }

  @Test public void getEmbeddings_ReturnsEmbeddings() {
    Step step = features.get(1).getElements()[0].getSteps()[6];
    Embedding[] embeddings = step.getEmbeddings();
    assertThat(embeddings).hasSize(1);
    assertThat(embeddings[0].getMimeType()).isEqualTo("application/json");
  }

  @Test public void getResult_ReturnResult() {
    Step step = features.get(1).getElements()[0].getSteps()[5];
    Result result = step.getResult();
    assertThat(result.getErrorMessage()).contains("java.lang.AssertionError");
  }

  @Test public void getStatus_ReturnsStatus() {
    Step step = features.get(1).getElements()[0].getSteps()[5];
    Status status = step.getResult().getStatus();
    assertThat(status).isEqualTo(Status.SKIPPED);
  }

  @Test public void getDuration_ReturnsDuration() {
    Step step = features.get(1).getElements()[0].getSteps()[1];
    long duration = step.getDuration();
    assertThat(duration).isEqualTo(13000L);
  }

  @Test public void getDuration_OnMissingDuration_ReturnsZero() {
    Step step = features.get(1).getElements()[0].getSteps()[0];
    long duration = step.getDuration();
    assertThat(duration).isZero();
  }

  @Test public void getResult_OnMissingResult_ReturnsEmptyResult() {
    Step step = features.get(1).getElements()[2].getSteps()[0];
    Result result = step.getResult();
    assertThat(result.getStatus()).isEqualTo(Status.UNDEFINED);
    assertThat(result.getDuration()).isEqualTo(0L);
    assertThat(result.getErrorMessage()).isNull();
  }
}