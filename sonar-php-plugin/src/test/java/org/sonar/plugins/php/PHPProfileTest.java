package org.sonar.plugins.php;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.php.checks.CheckList;
import org.sonar.plugins.php.api.Php;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PHPProfileTest {
  @Test public void should_create_sonar_way_profile() {
    ValidationMessages validation = ValidationMessages.create();
    RuleFinder ruleFinder = ruleFinder();
    PHPProfile definition = new PHPProfile(ruleFinder);
    RulesProfile profile = definition.createProfile(validation);
    assertThat(profile.getLanguage()).isEqualTo(Php.KEY);
    assertThat(profile.getName()).isEqualTo(CheckList.SONAR_WAY_PROFILE);
    assertThat(profile.getActiveRulesByRepository(CheckList.REPOSITORY_KEY)).hasSize(64);
    assertThat(validation.hasErrors()).isFalse();
    assertThat(profile.getActiveRules()).hasSize(64);
    assertThat(profile.getActiveRules()).extracting("ruleKey").contains("DuplicatedBlocks");
  }

  static RuleFinder ruleFinder() {
    return when(mock(RuleFinder.class).findByKey(anyString(), anyString())).thenAnswer(new Answer<Rule>() {
      public Rule answer(InvocationOnMock invocation) {
        Object[] arguments = invocation.getArguments();
        return Rule.create((String) arguments[0], (String) arguments[1], (String) arguments[1]);
      }
    }).getMock();
  }
}