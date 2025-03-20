package com.github.eirslett.maven.plugins.frontend.mojo;

import java.io.File;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystemSession;
import com.github.eirslett.maven.plugins.frontend.lib.FrontendException;
import com.github.eirslett.maven.plugins.frontend.lib.FrontendPluginFactory;
import com.github.eirslett.maven.plugins.frontend.lib.TaskRunnerException;

public abstract class AbstractFrontendMojo extends AbstractMojo {

    @Component
    protected MojoExecution execution;

    /**
     * Whether you should skip while running in the test phase (default is false)
     */
    @Parameter(property = "skipTests", required = false, defaultValue = "false")
    protected Boolean skipTests;
  
  /**
   * Specifies if the build will fail if there are errors during a frontend execution or not.
   *
   * @parameter property="maven.frontend.failOnError" default-value="true"
   * @since 1.4
   */
<<<<<<< /usr/src/app/output/eirslett/frontend-maven-plugin/16d0b92ccb95873464210ae627841b4891b78dbd/frontend-maven-plugin/src/main/java/com/github/eirslett/maven/plugins/frontend/mojo/AbstractFrontendMojo.java/left.java
  @Parameter(property = "maven.frontend.failOnError", required = false, defaultValue = "true")
  protected boolean failOnError;
||||||| /usr/src/app/output/eirslett/frontend-maven-plugin/16d0b92ccb95873464210ae627841b4891b78dbd/frontend-maven-plugin/src/main/java/com/github/eirslett/maven/plugins/frontend/mojo/AbstractFrontendMojo.java/base.java
=======
  @Parameter(property = "failOnError")
    protected Boolean failOnError;
>>>>>>> /usr/src/app/output/eirslett/frontend-maven-plugin/16d0b92ccb95873464210ae627841b4891b78dbd/frontend-maven-plugin/src/main/java/com/github/eirslett/maven/plugins/frontend/mojo/AbstractFrontendMojo.java/right.java
  /**
   * Whether you should continue build when some test will fail (default is false)
   */
<<<<<<< /usr/src/app/output/eirslett/frontend-maven-plugin/16d0b92ccb95873464210ae627841b4891b78dbd/frontend-maven-plugin/src/main/java/com/github/eirslett/maven/plugins/frontend/mojo/AbstractFrontendMojo.java/left.java
  @Parameter(property = "maven.test.failure.ignore", required = false, defaultValue = "false")
  protected boolean testFailureIgnore;
||||||| /usr/src/app/output/eirslett/frontend-maven-plugin/16d0b92ccb95873464210ae627841b4891b78dbd/frontend-maven-plugin/src/main/java/com/github/eirslett/maven/plugins/frontend/mojo/AbstractFrontendMojo.java/base.java
=======
  @Parameter(property = "maven.test.failure.ignore", defaultValue = "false")
    protected boolean testFailureIgnore;
>>>>>>> /usr/src/app/output/eirslett/frontend-maven-plugin/16d0b92ccb95873464210ae627841b4891b78dbd/frontend-maven-plugin/src/main/java/com/github/eirslett/maven/plugins/frontend/mojo/AbstractFrontendMojo.java/right.java
    /**
     * Set this to true to ignore a failure during testing. Its use is NOT RECOMMENDED, but quite convenient on
     * occasion.
     *
     * @since 1.4
     */

  /**
   * The base directory for running all Node commands. (Usually the directory that contains package.json)
   */
    @Parameter(defaultValue = "${basedir}", property = "workingDirectory", required = false)
    protected File workingDirectory;

    /**
     * The base directory for installing node and npm.
     */
    @Parameter(property = "installDirectory", required = false)
    protected File installDirectory;

    /**
     * Additional environment variables to pass to the build.
     */
    @Parameter
    protected Map<String, String> environmentVariables;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession repositorySystemSession;

    /**
     * Determines if this execution should be skipped.
     */
    private boolean skipTestPhase() {
        return skipTests && isTestingPhase();
    }

    /**
     * Determines if the current execution is during a testing phase (e.g., "test" or "integration-test").
     */
    private boolean isTestingPhase() {
        String phase = execution.getLifecyclePhase();
        return "test".equals(phase) || "integration-test".equals(phase);
    }

    protected abstract void execute(FrontendPluginFactory factory) throws FrontendException;

    /**
     * Implemented by children to determine if this execution should be skipped.
     */
    protected abstract boolean skipExecution();

    /**
     * Whether to raise an error or a failure if the execution fails. If unset, execution will fail if not in a testing
     * phase.
     *
     * @since 1.4
     * @see #isTestingPhase()
     */

    /**
     * @since 1.4
     * @see #failOnError
     */
    protected boolean isFailOnError() {
        if (failOnError == null) {
            failOnError = !isTestingPhase();
        }
        return failOnError;
    }

    @Override
    public void execute() throws MojoFailureException {
<<<<<<< /usr/src/app/output/eirslett/frontend-maven-plugin/16d0b92ccb95873464210ae627841b4891b78dbd/frontend-maven-plugin/src/main/java/com/github/eirslett/maven/plugins/frontend/mojo/AbstractFrontendMojo.java/left.java
	if (testFailureIgnore && !isTestingPhase()){
		LoggerFactory.getLogger(AbstractFrontendMojo.class).warn("testFailureIgnore property is ignored in non test phases");
	}
||||||| /usr/src/app/output/eirslett/frontend-maven-plugin/16d0b92ccb95873464210ae627841b4891b78dbd/frontend-maven-plugin/src/main/java/com/github/eirslett/maven/plugins/frontend/mojo/AbstractFrontendMojo.java/base.java
=======
	if (testFailureIgnore && !isTestingPhase()) {
            getLog().info("testFailureIgnore property is ignored in non test phases");
        }
>>>>>>> /usr/src/app/output/eirslett/frontend-maven-plugin/16d0b92ccb95873464210ae627841b4891b78dbd/frontend-maven-plugin/src/main/java/com/github/eirslett/maven/plugins/frontend/mojo/AbstractFrontendMojo.java/right.java
    if (!(skipTestPhase() || skipExecution())) {
        if (installDirectory == null) {
            installDirectory = workingDirectory;
        }
        try {
            execute(new FrontendPluginFactory(workingDirectory, installDirectory,
                    new RepositoryCacheResolver(repositorySystemSession)));
        } catch (TaskRunnerException e) {
            if (!isFailOnError() || testFailureIgnore && isTestingPhase()) {
                getLog().error("There are test failures.\nFailed to run task: " + e.getMessage(), e);
            } else {
              failOnError( "Failed to run task", e );
            }
        } catch (FrontendException e) {
            throw MojoUtils.toMojoFailureException(e);
        }
    } else {
        getLog().info("Skipping execution.");
    }
    }
  
	protected void failOnError(String prefix, Exception e) throws MojoFailureException {
		if (!failOnError || (testFailureIgnore && isTestingPhase()) ){
			if ((testFailureIgnore && isTestingPhase())){
	            LoggerFactory.getLogger(AbstractFrontendMojo.class)
	            .warn("There are ignored test failures/errors for: " + workingDirectory);			
			}
			LoggerFactory.getLogger(AbstractFrontendMojo.class).error(prefix + ": " + e.getMessage(), e);
		}else {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new MojoFailureException(prefix + ": " + e.getMessage(), e);
		}
	}
}
