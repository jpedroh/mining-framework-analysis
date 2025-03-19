package io.tracee.contextlogger.javaee;
import static org.hamcrest.MatcherAssert.assertThat;
import io.tracee.contextlogger.TraceeContextLogger;
import static org.hamcrest.Matchers.*;
import io.tracee.contextlogger.api.ContextLogger;
import static org.mockito.Matchers.any;
import io.tracee.contextlogger.api.ImplicitContext;
import static org.mockito.Matchers.anyString;
import org.junit.Before;
import static org.mockito.Mockito.*;
import org.junit.Test;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import org.junit.runner.RunWith;
import javax.interceptor.InvocationContext;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Test class for {@link io.tracee.contextlogger.javaee.TraceeEjbErrorContextLoggingInterceptor}.
 * Created by Tobias Gindler on 19.06.14.
 */
@RunWith(value = PowerMockRunner.class) @PrepareForTest(value = TraceeContextLogger.class) public class TraceeEjbErrorContextLoggingInterceptorTest {
  private final TraceeEjbErrorContextLoggingInterceptor unit = mock(TraceeEjbErrorContextLoggingInterceptor.class);

  private final ContextLogger contextLogger = mock(ContextLogger.class);

  @Before public void setupMocks() throws Exception {
    when(unit.intercept(Mockito.any(InvocationContext.class))).thenCallRealMethod();
    mockStatic(TraceeContextLogger.class);
    when(TraceeContextLogger.createDefault()).thenReturn(contextLogger);
  }

  @Test public void shouldBeInitializable() {
    assertThat(new TraceeEjbErrorContextLoggingInterceptor(), is(not(nullValue())));
  }

  @Test public void noInteractionWhenNoExceptionOccurs() throws Exception {
    final InvocationContext invocationContext = mock(InvocationContext.class);
    unit.intercept(invocationContext);
    verify(invocationContext).proceed();
    verify(contextLogger, never()).logJsonWithPrefixedMessage(anyString(), any(), any(), any(), any());
  }

  @Test(expected = RuntimeException.class) public void logJsonWithPrefixedMessageIfAnExceptionOccurs() throws Exception {
    final InvocationContext invocationContext = mock(InvocationContext.class);
    final RuntimeException exception = new RuntimeException();
    when(invocationContext.proceed()).thenThrow(exception);
    try {
      unit.intercept(invocationContext);
    } catch (Exception e) {
      verify(invocationContext).proceed();
      verify(contextLogger).logJsonWithPrefixedMessage(TraceeEjbErrorContextLoggingInterceptor.JSON_PREFIXED_MESSAGE, ImplicitContext.COMMON, ImplicitContext.TRACEE, invocationContext, exception);
      throw e;
    }
  }
}