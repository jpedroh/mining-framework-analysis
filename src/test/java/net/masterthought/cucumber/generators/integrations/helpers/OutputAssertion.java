package net.masterthought.cucumber.generators.integrations.helpers;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class OutputAssertion extends ReportAssertion {
  public void hasMessages(String[] messages) {
    WebAssertion[] outputMessages = allBySelector(
<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/test/java/net/masterthought/cucumber/generators/integrations/helpers/OutputAssertion.java/left.java
    "span"
=======
    "p"
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/test/java/net/masterthought/cucumber/generators/integrations/helpers/OutputAssertion.java/right.java
    , WebAssertion.class);
    assertThat(outputMessages).hasSameSizeAs(messages);
    for (int i = 0; i < messages.length; i++) {
      assertThat(outputMessages[i].text()).isEqualTo(messages[i]);
    }
  }
}