package org.sonar.plugins.scmactivity;
import org.apache.maven.scm.provider.ScmProvider;
import org.apache.maven.scm.provider.accurev.AccuRevScmProvider;
import org.apache.maven.scm.provider.bazaar.BazaarScmProvider;
import org.apache.maven.scm.provider.clearcase.ClearCaseScmProvider;
import org.apache.maven.scm.provider.cvslib.cvsexe.CvsExeScmProvider;
import org.apache.maven.scm.provider.hg.HgScmProvider;
import org.apache.maven.scm.provider.jazz.JazzScmProvider;
import org.apache.maven.scm.provider.perforce.PerforceScmProvider;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;
import org.sonar.plugins.scmactivity.maven.SonarGitExeScmProvider;
import org.sonar.plugins.scmactivity.maven.integrity.SonarIntegrityScmProvider;
import org.sonar.plugins.scmactivity.maven.SonarTfsScmProvider;

public enum SupportedScm {
  SVN(new SvnExeScmProvider(), "scm:svn:svn://"),
  CVS(new CvsExeScmProvider(), null),
  GIT(new SonarGitExeScmProvider(), "scm:git:"),
  HG(new HgScmProvider(), "scm:hg:"),
  BAZAAR(new BazaarScmProvider(), "scm:bazaar:"),
  CLEAR_CASE(new ClearCaseScmProvider(), null),
  ACCU_REV(new AccuRevScmProvider(), null),
  PERFORCE(new PerforceScmProvider(), null),
  TFS(new SonarTfsScmProvider(), "scm:tfs:"),
  JAZZ(new JazzScmProvider(), null),
  INTEGRITY(new SonarIntegrityScmProvider(), null)
  ;

  private final ScmProvider provider;

  private final String guessedUrl;

  private SupportedScm(ScmProvider provider, String guessedUrl) {
    this.provider = provider;
    this.guessedUrl = guessedUrl;
  }

  public String getGuessedUrl() {
    return guessedUrl;
  }

  public String getType() {
    return provider.getScmType();
  }

  public String getScmSpecificFilename() {
    return provider.getScmSpecificFilename();
  }

  public ScmProvider getProvider() {
    return provider;
  }
}