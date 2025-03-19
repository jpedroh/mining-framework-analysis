package io.tracee.contextlogger.servlet;
import static org.hamcrest.MatcherAssert.assertThat;
import io.tracee.TraceeException;
import static org.hamcrest.Matchers.sameInstance;
import io.tracee.contextlogger.TraceeContextLogger;
import static org.mockito.Mockito.*;
import io.tracee.contextlogger.api.ImplicitContext;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import org.junit.Before;
import java.io.IOException;
import org.junit.Test;
import javax.servlet.FilterChain;
import org.junit.runner.RunWith;
import javax.servlet.ServletException;
import org.powermock.core.classloader.annotations.PrepareForTest;
import javax.servlet.http.HttpServletRequest;
import org.powermock.modules.junit4.PowerMockRunner;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RunWith(value = PowerMockRunner.class) @PrepareForTest(value = TraceeContextLogger.class) public class TraceeErrorLoggingFilterTest {
  private final TraceeErrorLoggingFilter unit = new TraceeErrorLoggingFilter();

  private final HttpServletRequest request = mock(HttpServletRequest.class);

  private final HttpServletResponse response = mock(HttpServletResponse.class);

  private final HttpSession session = mock(HttpSession.class);

  private final FilterChain filterChain = mock(FilterChain.class);

  private TraceeContextLogger traceeContextLogger = mock(TraceeContextLogger.class);

  @Before public void setUpMocks() {
    mockStatic(TraceeContextLogger.class);
    when(TraceeContextLogger.createDefault()).thenReturn(traceeContextLogger);
    when(request.getSession(false)).thenReturn(session);
  }

  @Test public void isTransparentWhenEverythingIsFine() throws Exception {
    unit.doFilter(request, response, filterChain);
    verify(filterChain).doFilter(request, response);
  }

  @Test(expected = TraceeException.class) public void logWholeContextOnException() throws Exception {
    final TraceeException expectedException = new TraceeException("test");
    try {
      doThrow(expectedException).when(filterChain).doFilter(request, response);
      unit.doFilter(request, response, filterChain);
    } catch (Exception e) {
      verify(traceeContextLogger).logJsonWithPrefixedMessage(TraceeErrorLoggingFilter.LOGGING_PREFIX_MESSAGE, ImplicitContext.COMMON, ImplicitContext.TRACEE, request, response, session, expectedException);
      throw e;
    }
  }

  @Test(expected = RuntimeException.class) public void rethrowRuntimeException() throws Exception {
    final RuntimeException expectedException = new RuntimeException();
    try {
      doThrow(expectedException).when(filterChain).doFilter(request, response);
      unit.doFilter(request, response, filterChain);
    } catch (RuntimeException e) {
      assertThat(e, sameInstance(expectedException));
      throw e;
    }
  }

  @Test(expected = IOException.class) public void rethrowIOException() throws Exception {
    final IOException expectedException = new IOException();
    try {
      doThrow(expectedException).when(filterChain).doFilter(request, response);
      unit.doFilter(request, response, filterChain);
    } catch (IOException e) {
      assertThat(e, sameInstance(expectedException));
      throw e;
    }
  }

  @Test(expected = ServletException.class) public void rethrowServletException() throws Exception {
    final ServletException expectedException = new ServletException();
    try {
      doThrow(expectedException).when(filterChain).doFilter(request, response);
      unit.doFilter(request, response, filterChain);
    } catch (ServletException e) {
      assertThat(e, sameInstance(expectedException));
      throw e;
    }
  }
}