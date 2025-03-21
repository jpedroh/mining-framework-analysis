package hudson.plugins.build_timeout;
import jenkins.model.Jenkins;
import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Describable;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Defines an operation performed when timeout occurs.
 * They are called "Timeout Actions", but the class is BuildTimeOutOperation
 * not to be confused with {@link Action}
 */
public abstract class BuildTimeOutOperation implements ExtensionPoint, Describable<BuildTimeOutOperation> {
  /**
     * Perform operation.
     * 
     * @param build             build timed out
     * @param listener          build listener. can be used to print log.
     * @param effectiveTimeout  timeout (milliseconds)
     * @return false not to run subsequent operations. It also mark the build as failure.
     */
  public abstract boolean perform(@NonNull AbstractBuild<?, ?> build, @NonNull BuildListener listener, long effectiveTimeout);

  /**
     * @see hudson.model.Describable#getDescriptor()
     */
  public BuildTimeOutOperationDescriptor getDescriptor() {
    return (BuildTimeOutOperationDescriptor) Jenkins.getActiveInstance().getDescriptorOrDie(getClass());
  }

  public void addAction(@Nonnull AbstractBuild<?, ?> build, @Nonnull String reason) {
    BuildTimeOutAction buildTimeoutAction = build.getAction(BuildTimeOutAction.class);
    if (buildTimeoutAction == null) {
      buildTimeoutAction = new BuildTimeOutAction(reason);
    }
    build.addAction(buildTimeoutAction);
  }
}