package org.sonar.plugins.objectivec;
import org.sonar.api.profiles.AnnotationProfileParser;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.objectivec.checks.CheckList;
import org.sonar.plugins.objectivec.core.ObjectiveC;

public class ObjectiveCProfile extends ProfileDefinition {
  private final AnnotationProfileParser annotationProfileParser;

  public ObjectiveCProfile(AnnotationProfileParser annotationProfileParser) {
    this.annotationProfileParser = annotationProfileParser;
  }

  @Override public RulesProfile createProfile(ValidationMessages validation) {
    return annotationProfileParser.parse(CheckList.REPOSITORY_KEY, CheckList.SONAR_WAY_PROFILE, ObjectiveC.KEY, CheckList.getChecks(), validation);
  }
}