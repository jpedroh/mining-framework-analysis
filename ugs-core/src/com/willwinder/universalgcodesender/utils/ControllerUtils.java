package com.willwinder.universalgcodesender.utils;
import com.willwinder.universalgcodesender.IController;
import com.willwinder.universalgcodesender.types.CommandListener;
import com.willwinder.universalgcodesender.types.GcodeCommand;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Joacim Breiler
 */
public class ControllerUtils {
  private static final int MAX_EXECUTION_TIME = 2000;

  /**
     * Sends a command and blocks the thread until the command is done - either with an ok or error.
     *
     * @param controller       the controller to send the command through
     * @param command          a command to send
     * @param maxExecutionTime the max number of milliseconds to wait before throwing a timeout error
     * @throws Exception if the command could not be sent or a timeout occurred
     */
  public static <T extends GcodeCommand> T sendAndWaitForCompletion(IController controller, T command, long maxExecutionTime) throws Exception {
    final AtomicBoolean isDone = new AtomicBoolean(false);
    CommandListener listener = (c) -> isDone.set(c.isDone());
    command.addListener(listener);
    controller.sendCommandImmediately(command);
    long startTime = System.currentTimeMillis();
    while (!isDone.get()) {
      if (System.currentTimeMillis() > startTime + maxExecutionTime) {
        throw new RuntimeException("The command \"" + command.getCommandString() + "\" has timed out as it wasn\'t finished within " + maxExecutionTime + "ms");
      }
      Thread.sleep(10);
    }
    return command;
  }

  /**
     * Sends a command and blocks the thread until the command is done - either with an ok or error.
     *
     * @param controller the controller to send the command through
     * @param command    a command
     * @param <T>        a class extending from {@link GcodeCommand}
     * @return the executed command with the response
     * @throws Exception if the command could not be sent or a timeout occurred
     */
  public static <T extends GcodeCommand> T sendAndWaitForCompletion(IController controller, T command) throws Exception {
    return sendAndWaitForCompletion(controller, command, MAX_EXECUTION_TIME);
  }

  /**
     * Waits for all commands to complete before continuing
     *
     * @param controller the controller to check for active commands
     */
  public static void waitOnActiveCommands(IController controller) throws InterruptedException {
    long maxExecutionTime = MAX_EXECUTION_TIME;
    long startTime = System.currentTimeMillis();
    while (controller.getActiveCommand().isPresent()) {
      if (startTime + maxExecutionTime < System.currentTimeMillis()) {
        throw new RuntimeException("The command \"" + controller.getActiveCommand().get().getCommandString() + "\" has timed out as it wasn\'t finished within " + maxExecutionTime + "ms");
      }
      Thread.sleep(10);
    }
  }

  /**
     * Sends a command and blocks the thread until the command is done and was ok. It will retry to send the command in case of a timeout or an error.
     *
     * @param commandSupplier  a supplier for creating gcode commands to send
     * @param controller       the controller to send through
     * @param maxExecutionTime the maximum execution time in milliseconds
     * @param retryCount       the number of times to retry and send
     * @param onExecute        an action that should be executed before sending
     * @param <T>              a class extending from {@link GcodeCommand}
     * @return the last executed command with the response
     * @throws InterruptedException
     */
  public static <T extends GcodeCommand> T sendAndWaitForCompletionWithRetry(Supplier<T> commandSupplier, IController controller, long maxExecutionTime, int retryCount, Consumer<Integer> onExecute) throws InterruptedException {
    int times = 0;
    T command = null;
    while (times < retryCount && controller.isCommOpen()) {
      long startTime = System.currentTimeMillis();
      try {
        onExecute.accept(times + 1);
        command = commandSupplier.get();
        if (command == null) {
          throw new IllegalArgumentException("The command generated was null");
        }
        command = sendAndWaitForCompletion(controller, command, maxExecutionTime);
      } catch (Exception e) {
      }
      if (!command.isDone() || command.isError()) {
        if (startTime + maxExecutionTime > System.currentTimeMillis()) {
          long sleepTime = startTime + maxExecutionTime - System.currentTimeMillis();
          Thread.sleep(sleepTime);
        }
      } else {
        if (command.isDone() && !command.isError()) {
          break;
        }
      }
      times++;
    }
    return command;
  }
}