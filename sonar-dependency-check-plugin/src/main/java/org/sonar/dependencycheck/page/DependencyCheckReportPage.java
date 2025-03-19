package org.sonar.dependencycheck.page;
import org.sonar.api.web.page.Context;
import org.sonar.api.web.page.Page;
import org.sonar.api.web.page.Page.Scope;
import org.sonar.api.web.page.PageDefinition;

public class DependencyCheckReportPage implements PageDefinition {
  @Override public void define(Context context) {
    context.addPage(Page.builder("dependencycheck/report").setScope(Scope.COMPONENT).setComponentQualifiers(Page.Qualifier.PROJECT, Page.Qualifier.MODULE).setName("Dependency-Check").setAdmin(false).build());
  }
}