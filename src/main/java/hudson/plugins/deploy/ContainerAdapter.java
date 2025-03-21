package hudson.plugins.deploy;

import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStep;
import jenkins.model.Jenkins;

import java.io.IOException;

/**
 * Encapsulates container-specific deployment operation.
 *
 * <h2>Persistence</h2>
 * <p>
 * Instances of these objects are persisted in projects' configuration XML via XStream.
 * 
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class ContainerAdapter implements Describable<ContainerAdapter>, ExtensionPoint {
    /**
     * Perform redeployment.
     *
<<<<<<< /usr/src/app/output/jenkinsci/deploy-plugin/429e7a00021ce25331dcda7ad7a127ea6a37aab8/src/main/java/hudson/plugins/deploy/ContainerAdapter.java/left.java
     * If failed, return false.
     * Retained as abstract for back-compatibility in cases where a plugin extends deploy plugin and implements this only.
     * Implementations that are pipeline compatible should implement {@link #redeploy(FilePath, String, Run, Launcher, BuildListener)}
     *   and have this simply delegate to the now-compatible implementation.
     * @deprecated Prefer to invoke {@link #redeploy(FilePath, String, Run, Launcher, BuildListener)} where possible.
||||||| /usr/src/app/output/jenkinsci/deploy-plugin/429e7a00021ce25331dcda7ad7a127ea6a37aab8/src/main/java/hudson/plugins/deploy/ContainerAdapter.java/base.java
     * If failed, return false.
=======
     * @param war the file path of the war to deploy
     * @param aContextPath the context path for the war to be deployed
     * @param build the build that is being deployed
     * @param launcher the launcher of the build
     * @param listener the BuildListener of the build to deploy
     * @return true if deployed successfully, false if failed
     * @throws IOException if there is an error locating the war file
     * @throws InterruptedException if there is an error deploying to the server
>>>>>>> /usr/src/app/output/jenkinsci/deploy-plugin/429e7a00021ce25331dcda7ad7a127ea6a37aab8/src/main/java/hudson/plugins/deploy/ContainerAdapter.java/right.java
     */
    @Deprecated
    public abstract boolean redeploy(FilePath war, String aContextPath, AbstractBuild<?,?> build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException;

    /**
     * Perform redeployment.
     *
     * If failed, return false.
     *
     * Implementations should override me and make {@link #redeploy(FilePath, String, AbstractBuild, Launcher, BuildListener)}
     *  delegate to that implementation to be usable within Pipeline projects
     */
    public boolean redeploy(FilePath war, String aContextPath, Run<?,?> build, Launcher launcher, final TaskListener listener) throws IOException, InterruptedException {
        if (build instanceof AbstractBuild) {
            return redeploy(war, aContextPath, (AbstractBuild<?, ?>) build, launcher, (BuildListener)listener);
        }
        return false;
    }

    public ContainerAdapterDescriptor getDescriptor() {
        return (ContainerAdapterDescriptor)Jenkins.getActiveInstance().getDescriptor(getClass());
    }

    public static DescriptorExtensionList<ContainerAdapter,ContainerAdapterDescriptor> all() {
        return Jenkins.getActiveInstance().<ContainerAdapter,ContainerAdapterDescriptor>getDescriptorList(ContainerAdapter.class);
    }
}
