package com.jcabi.dynamodb.maven.plugin;
import com.jcabi.aspects.Loggable;
import com.jcabi.log.VerboseProcess;
import com.jcabi.log.VerboseRunnable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Running instances of DynamoDB Local.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 * @see <a href="http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Tools.html">DynamoDB Local</a>
 */
@ToString @EqualsAndHashCode(of = "processes") @Loggable(value = Loggable.INFO) @SuppressWarnings(value = { "PMD.DoNotUseThreads" }) final class Instances {
  /**
     * Running processes.
     */
  private final transient ConcurrentMap<Integer, Process> processes = new ConcurrentHashMap<Integer, Process>(0);

  /**
     * Public ctor.
     */
  Instances() {
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override public void run() {
        Instances.this.shutdown();
      }
    }));
  }

  /**
     * Start a new one at this port.
     * @param dist Path to DynamoDBLocal distribution
     * @param port The port to start at
     * @param home Java home directory
     * @param args Command line arguments
     * @throws IOException If fails to start
     * @checkstyle ParameterNumber (5 lines)
     */
  public void start(@NotNull final File dist, final int port, final File home, @NotNull final List<String> args) throws IOException {
    final Process process = Instances.process(dist, port, home, args);
    final Thread thread = new Thread(new VerboseRunnable(new InstanceProcess(process)));
    thread.setDaemon(true);
    thread.start();
    this.processes.put(port, process);
  }

  /**
     * Launch a new one at this port. This goal will block maven.
     * @param dist Path to DynamoDBLocal distribution
     * @param port The port to start at
     * @param home Java home directory
     * @param args Command line arguments
     * @throws IOException If fails to start
     * @checkstyle ParameterNumber (5 lines)
     */
  public void run(@NotNull final File dist, final int port, final File home, @NotNull final List<String> args) throws IOException {
    final Process process = Instances.process(dist, port, home, args);
    this.processes.put(port, process);
    new VerboseRunnable(new InstanceProcess(process)).run();
  }

  /**
     * Stop a running one at this port.
     * @param port The port to stop at
     */
  public void stop(final int port) {
    synchronized (this.processes) {
      final Process process = this.processes.get(port);
      if (process == null) {
        throw new IllegalArgumentException(String.format("No DynamoDB Local instances running on port %d", port));
      }
      process.destroy();
      this.processes.remove(port);
    }
  }

  /**
     * Shutdown everything that is still running.
     */
  private void shutdown() {
    for (final int port : this.processes.keySet()) {
      this.stop(port);
    }
  }

  /**
     * Create new process.
     * @param dist Path to DynamoDBLocal distribution
     * @param port The port to start at
     * @param home Java home directory
     * @param args Extra command line args
     * @return Process ready to be started
     * @throws IOException If fails to start
     * @checkstyle ParameterNumber (5 lines)
     */
  private static Process process(final File dist, final int port, final File home, final List<String> args) throws IOException {
    final List<String> command = new ArrayList<String>(args.size());
    command.add(new File(home, "bin/java").getAbsolutePath());
    command.add(new StringBuilder("-Djava.library.path=").append(dist).append(System.getProperty("path.separator")).append(new File(dist, "DynamoDBLocal_lib")).toString());
    command.add("-jar");
    command.add("DynamoDBLocal.jar");
    command.add("--port");
    command.add(Integer.toString(port));
    command.addAll(args);
    return new ProcessBuilder().command(command).directory(dist).redirectErrorStream(true).start();
  }

  private static final class InstanceProcess implements Callable<Void> {
    /**
         * Process.
         */
    private final transient Process prc;

    /**
         * Constructor.
         * @param process The process to work with.
         */
    InstanceProcess(final Process process) {
      this.prc = process;
    }

    @Override public Void call() throws Exception {
      new VerboseProcess(this.prc).stdoutQuietly();
      return null;
    }
  }
}