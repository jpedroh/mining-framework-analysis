package org.jboss.msc.service;
import java.util.concurrent.Executor;

/**
 * A context object for lifecycle events.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 * @author <a href="mailto:ropalka@redhat.com">Richard Opalka</a>
 */
public interface LifecycleContext extends Executor {
  /**
     * Call within the service lifecycle method to trigger an <em>asynchronous</em> lifecycle action.  This action
     * will not be considered complete until indicated so by calling a {@link #complete()} method on this interface.
     *
     * @throws IllegalStateException if called twice in a row
     */
  void asynchronous() throws IllegalStateException;

  /**
     * Call when either <em>synchronous</em> or <em>asynchronous</em> lifecycle action is complete.
     *
     * @throws IllegalStateException if called twice in a row
     */
  void complete() throws IllegalStateException;

  /**
     * Get the amount of time elapsed since the start or stop was initiated, in nanoseconds.
     *
     * @return the elapsed time
     */
  long getElapsedTime();

  /**
     * Get the associated service controller.
     *
     * @return the service controller
     */
  ServiceController<?> getController();

  /**
     * Execute a task asynchronously using the MSC task executor.
     * <p>
     * <strong>Note:</strong> This method should not be used for executing tasks that may block,
     * particularly from within a service's {@link Service#start(StartContext)} or {@link Service#stop(StopContext)}
     * methods. See {@link Service the Service class javadoc} for further details.
     *
     * @param command the command to execute
     * @throws IllegalStateException if this method is called outside of service lifecycle methods.
     */
  @Override void execute(Runnable command);
}