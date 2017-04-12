package org.jenkinsci.maven.plugins.hpi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Insert default test suite.
 *
 * @author Kohsuke Kawaguchi
 */
@Mojo(name = "insert-test", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES, requiresDependencyResolution = ResolutionScope.TEST)
public class TestInsertionMojo extends AbstractJenkinsMojo {

    /**
     * If true, the automatic test injection will be skipped.
     */
    @Parameter(property = "maven-hpi-plugin.disabledTestInjection", defaultValue = "false")
    private boolean disabledTestInjection;

    /**
     * Name of the injected test.
     *
     * You may change this to "InjectIT" to get the test running during phase integration-test.
     */
    @Parameter(property = "maven-hpi-plugin.injectedTestName", defaultValue = "InjectedTest")
    private String injectedTestName;

    /**
     * If true, verify that all the jelly scripts have the Jelly XSS PI in them.
     */
    @Parameter(property = "jelly.requirePI", defaultValue = "true")
    private boolean requirePI;

    /**
     * Optional string that represents "groupId:artifactId" of the Jenkins test harness.
     * If left unspecified, the default groupId/artifactId pair for the Jenkins test harness is looked for.
     *
     * @since 1.119
     */
    @Parameter(defaultValue = "org.jenkins-ci.main:jenkins-test-harness")
    protected String jenkinsTestHarnessId;

    private static String quote(String s) {
        return '"'+s.replace("\\", "\\\\")+'"';
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!project.getPackaging().equals("hpi")) {
            Artifact jenkinsTestHarness = null;
            if (jenkinsTestHarnessId != null) {
                for (Artifact b : (List<Artifact>) project.getTestArtifacts()) {
                    if (jenkinsTestHarnessId.equals(b.getGroupId() +":"+b.getArtifactId())) {
                        jenkinsTestHarness = b;
                        break;
                    }
                }
            }
            if (jenkinsTestHarness != null) {
                try {
                    ArtifactVersion version = jenkinsTestHarness.getSelectedVersion();
                    if (version == null || version.compareTo(new DefaultArtifactVersion("2.14")) == -1) {
                        getLog().info(
                                "Skipping " + project.getName()
                                        + " because it's not <packaging>hpi</packaging> and the " + jenkinsTestHarnessId
                                        + ", " + version + ", is less than 2.14");
                        return;
                    }
                } catch (OverConstrainedVersionException e) {
                    throw new MojoFailureException(
                            "Build should be failed before we get here if there is an over-constrained version",
                            e);
                }
            } else {
                getLog().info("Skipping " + project.getName()
                        + " because it's not <packaging>hpi</packaging> and we could not determine the version of "
                        + jenkinsTestHarnessId + " used by this project");
                return;
            }
        }
        
        if (disabledTestInjection) {
            getLog().info("Skipping auto-test generation");
            return;
        }

        String target = findJenkinsVersion();
        if (new VersionNumber(target).compareTo(new VersionNumber("1.327"))<0) {
            getLog().info("Skipping auto-test generation because we are targeting Jenkins "+target+" (at least 1.327 is required).");
            return;
        }

        try {
            File f = new File(project.getBasedir(), "target/generated-test-sources/injected");
            f.mkdirs();
            File javaFile = new File(f, injectedTestName + ".java");
            PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(javaFile)));
            w.println("import java.util.*;");
            w.println("/**");
            w.println(" * Entry point to auto-generated tests (generated by maven-hpi-plugin).");
            w.println(" * If this fails to compile, you are probably using Hudson &lt; 1.327. If so, disable");
            w.println(" * this code generation by configuring maven-hpi-plugin to &lt;disabledTestInjection&gt;true&lt;/disabledTestInjection&gt;.");
            w.println(" */");
            w.println("public class " + injectedTestName + " extends junit.framework.TestCase {");
            w.println("  public static junit.framework.Test suite() throws Exception {");
            w.println("    System.out.println(\"Running tests for \"+" + quote(project.getGroupId() + ":" + project.getArtifactId() + ":" + project.getVersion())+");");
            w.println("    Map parameters = new HashMap();");
            w.println("    parameters.put(\"basedir\","+quote(project.getBasedir().getAbsolutePath())+");");
            w.println("    parameters.put(\"artifactId\","+quote(project.getArtifactId())+");");
            w.println("    parameters.put(\"packaging\","+quote(project.getPackaging())+");");
            w.println("    parameters.put(\"outputDirectory\","+quote(project.getBuild().getOutputDirectory())+");");
            w.println("    parameters.put(\"testOutputDirectory\","+quote(project.getBuild().getTestOutputDirectory())+");");
            w.println("    parameters.put(\"requirePI\","+quote(String.valueOf(requirePI))+");");
            w.println("    return org.jvnet.hudson.test.PluginAutomaticTestBuilder.build(parameters);");
            w.println("  }");
            w.println("}");
            w.close();

            project.addTestCompileSourceRoot(f.getAbsolutePath());

            // always set the same time stamp on this file, so that Maven will not re-compile this
            // every time we run this mojo.
            javaFile.setLastModified(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
