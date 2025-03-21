package org.jenkinsci.maven.plugins.hpi;
import hudson.util.VersionNumber;
import io.jenkins.lib.versionnumber.JavaSpecificationVersion;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

/**
 * Make sure that we are running in the right environment.
 *
 * @author Kohsuke Kawaguchi
 */
@Mojo(name = "validate", defaultPhase = LifecyclePhase.VALIDATE) public class ValidateMojo extends AbstractJenkinsMojo {
  @Override public void execute() throws MojoExecutionException {

<<<<<<< /usr/src/app/output/jenkinsci/maven-hpi-plugin/47546f63c65605f85296c517613a8ef93b04d7f5/src/main/java/org/jenkinsci/maven/plugins/hpi/ValidateMojo.java/left.java
    MavenProject
=======
    JavaSpecificationVersion
>>>>>>> /usr/src/app/output/jenkinsci/maven-hpi-plugin/47546f63c65605f85296c517613a8ef93b04d7f5/src/main/java/org/jenkinsci/maven/plugins/hpi/ValidateMojo.java/right.java
     
<<<<<<< /usr/src/app/output/jenkinsci/maven-hpi-plugin/47546f63c65605f85296c517613a8ef93b04d7f5/src/main/java/org/jenkinsci/maven/plugins/hpi/ValidateMojo.java/left.java
    parent = project.getParent()
=======
    javaVersion = getMinimumJavaVersion()
>>>>>>> /usr/src/app/output/jenkinsci/maven-hpi-plugin/47546f63c65605f85296c517613a8ef93b04d7f5/src/main/java/org/jenkinsci/maven/plugins/hpi/ValidateMojo.java/right.java
    ;

<<<<<<< Unknown file: This is a bug in JDime.
=======
    if (JavaSpecificationVersion.forCurrentJVM().isOlderThan(javaVersion)) {
      throw new MojoExecutionException("Java " + javaVersion + " or later is necessary to build this plugin.");
    }
>>>>>>> /usr/src/app/output/jenkinsci/maven-hpi-plugin/47546f63c65605f85296c517613a8ef93b04d7f5/src/main/java/org/jenkinsci/maven/plugins/hpi/ValidateMojo.java/right.java


<<<<<<< /usr/src/app/output/jenkinsci/maven-hpi-plugin/47546f63c65605f85296c517613a8ef93b04d7f5/src/main/java/org/jenkinsci/maven/plugins/hpi/ValidateMojo.java/left.java
    if (parent != null && parent.getGroupId().equals("org.jenkins-ci.plugins") && parent.getArtifactId().equals("plugin") && !parent.getProperties().containsKey("java.level") && project.getProperties().containsKey("java.level")) {
      getLog().warn("Ignoring deprecated java.level property." + " This property should be removed from your plugin\'s POM." + " In the future this warning will be changed to an error and will break the build.");
    }
=======
    if (new VersionNumber(findJenkinsVersion()).compareTo(new VersionNumber("2.204")) < 0) {
      throw new MojoExecutionException("This version of maven-hpi-plugin requires Jenkins 2.204 or later");
    }
>>>>>>> /usr/src/app/output/jenkinsci/maven-hpi-plugin/47546f63c65605f85296c517613a8ef93b04d7f5/src/main/java/org/jenkinsci/maven/plugins/hpi/ValidateMojo.java/right.java
  }
}