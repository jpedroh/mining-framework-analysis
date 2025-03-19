package io.tracee.contextlogger.watchdog;
import io.tracee.Tracee;
import io.tracee.TraceeBackend;
import io.tracee.contextlogger.TraceeContextLogger;
import io.tracee.contextlogger.api.ImplicitContext;
import io.tracee.contextlogger.contextprovider.aspectj.WatchdogDataWrapper;
import io.tracee.contextlogger.watchdog.util.WatchdogUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Watchdog Assert class.
 * This aspect logs method calls of Watchdog annotated classes and methods in case of an exception is thrown during the execution of the method.
 * <p/>
 * Created by Tobias Gindler, holisticon AG on 16.02.14.
 */
@Aspect public class WatchdogAspect {
  private final boolean active;

  public WatchdogAspect() {
    this(Boolean.valueOf(System.getProperty(Constants.SYSTEM_PROPERTY_IS_ACTIVE, "true")));
  }

  WatchdogAspect(boolean active) {
    this.active = active;
  }

  @SuppressWarnings(value = { "unused" }) @Pointcut(value = "(execution(* *(..)) && @annotation(io.tracee.contextlogger.watchdog.Watchdog))") void withinWatchdogAnnotatedMethods() {
  }

  @SuppressWarnings(value = { "unused" }) @Pointcut(value = "within(@io.tracee.contextlogger.watchdog.Watchdog *)") void withinClassWithWatchdogAnnotation() {
  }

  @SuppressWarnings(value = { "unused" }) @Pointcut(value = "execution(public * *(..))") void publicMethods() {
  }

  @SuppressWarnings(value = { "unused" }) @Around(value = "withinWatchdogAnnotatedMethods() || (publicMethods() && withinClassWithWatchdogAnnotation()) ") public Object guard(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    try {
      return proceedingJoinPoint.proceed();
    } catch (final Throwable e) {
      if (active) {
        final TraceeBackend traceeBackend = Tracee.getBackend();
        try {
          Watchdog watchdog = WatchdogUtils.getWatchdogAnnotation(proceedingJoinPoint);
          if (WatchdogUtils.checkProcessWatchdog(watchdog, proceedingJoinPoint, e)) {
            String annotatedId = watchdog.id().isEmpty() ? null : watchdog.id();
            sendErrorReportToConnectors(traceeBackend, proceedingJoinPoint, annotatedId, e);
            writeMethodCallToMdc(traceeBackend, proceedingJoinPoint, annotatedId);
          }
        } catch (Throwable error) {
          traceeBackend.getLoggerFactory().getLogger(WatchdogAspect.class).error("error", error);
        }
      }
      throw e;
    }
  }

  /**
     * Adds or enhances an mdc variable by call information.
     *
     * @param traceeBackend       the tracee backend
     * @param proceedingJoinPoint the aspectj calling context
     * @param annotatedId         the id defined in the watchdog annotation
     */
  void writeMethodCallToMdc(TraceeBackend traceeBackend, ProceedingJoinPoint proceedingJoinPoint, String annotatedId) {
    String json = TraceeContextLogger.createDefault().createJson(WatchdogDataWrapper.wrap(annotatedId, proceedingJoinPoint));
    String existingContent = traceeBackend.get(Constants.TRACEE_ATTRIBUTE_NAME);
    traceeBackend.put(Constants.TRACEE_ATTRIBUTE_NAME, existingContent != null ? existingContent + Constants.SEPARATOR + json : json);
  }

  /**
     * Sends the error reports to all connectors.
     *
     * @param traceeBackend       the tracee backend
     * @param proceedingJoinPoint the aspectj calling context
     * @param annotatedId         the id defined in the watchdog annotation
     */
  void sendErrorReportToConnectors(TraceeBackend traceeBackend, ProceedingJoinPoint proceedingJoinPoint, String annotatedId, Throwable e) {
    TraceeContextLogger.createDefault().logJsonWithPrefixedMessage("TRACEE WATCHDOG ERROR CONTEXT LISTENER :", ImplicitContext.COMMON, ImplicitContext.TRACEE, WatchdogDataWrapper.wrap(annotatedId, proceedingJoinPoint), e);
  }
}