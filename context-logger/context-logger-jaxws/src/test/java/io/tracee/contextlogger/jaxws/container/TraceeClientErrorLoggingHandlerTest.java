package io.tracee.contextlogger.jaxws.container;
import static io.tracee.contextlogger.jaxws.container.AbstractTraceeErrorLoggingHandler.THREAD_LOCAL_SOAP_MESSAGE_STR;
import io.tracee.NoopTraceeLoggerFactory;
import static org.mockito.Mockito.*;
import io.tracee.Tracee;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import io.tracee.TraceeBackend;
import org.junit.Before;
import io.tracee.contextlogger.TraceeContextLogger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Test class for {@link io.tracee.contextlogger.jaxws.container.TraceeClientErrorLoggingHandler}.
 */
@RunWith(value = PowerMockRunner.class) @PrepareForTest(value = { TraceeContextLogger.class, Tracee.class }) public class TraceeClientErrorLoggingHandlerTest {
  private final TraceeBackend mockedBackend = mock(TraceeBackend.class);

  private NoopTraceeLoggerFactory loggerFactory = spy(NoopTraceeLoggerFactory.INSTANCE);

  private TraceeClientErrorLoggingHandler unit;

  private SOAPMessageContext contextMock;

  private TraceeClientErrorLoggingHandler handlerSpy;

  @Before public void setup() {
    when(mockedBackend.getLoggerFactory()).thenReturn(loggerFactory);
    unit = new TraceeClientErrorLoggingHandler(mockedBackend);
    THREAD_LOCAL_SOAP_MESSAGE_STR.remove();
    contextMock = Mockito.mock(SOAPMessageContext.class);
    handlerSpy = Mockito.spy(unit);
  }

  @Test public void shouldCallStoreMessageInThreadLocalForOutgoing() {
    handlerSpy.handleOutgoing(contextMock);
    Mockito.verify(handlerSpy).storeMessageInThreadLocal(contextMock);
  }

  @Test public void shouldDoNothingForIncoming() {
    handlerSpy.handleIncoming(contextMock);
    Mockito.verify(handlerSpy, never()).storeMessageInThreadLocal(contextMock);
  }
}