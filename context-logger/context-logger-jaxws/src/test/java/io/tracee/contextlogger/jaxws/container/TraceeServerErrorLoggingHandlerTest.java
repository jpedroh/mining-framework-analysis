package io.tracee.contextlogger.jaxws.container;
import static io.tracee.contextlogger.jaxws.container.TraceeServerErrorLoggingHandler.THREAD_LOCAL_SOAP_MESSAGE_STR;
import io.tracee.NoopTraceeLoggerFactory;
import static org.hamcrest.Matchers.*;
import io.tracee.Tracee;
import static org.junit.Assert.assertThat;
import io.tracee.TraceeBackend;
import static org.mockito.Mockito.*;
import io.tracee.contextlogger.TraceeContextLogger;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import io.tracee.contextlogger.api.ImplicitContext;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import io.tracee.contextlogger.contextprovider.jaxws.JaxWsWrapper;
import java.io.OutputStream;
import org.hamcrest.Matchers;
import java.nio.charset.Charset;
import org.junit.Before;
import javax.xml.namespace.QName;
import org.junit.Test;
import javax.xml.soap.MessageFactory;
import org.junit.runner.RunWith;
import javax.xml.soap.SOAPException;
import org.mockito.Mockito;
import javax.xml.soap.SOAPMessage;
import org.powermock.core.classloader.annotations.PrepareForTest;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Test class for {@link io.tracee.contextlogger.jaxws.container.AbstractTraceeErrorLoggingHandler} and
 * {@link io.tracee.contextlogger.jaxws.container.TraceeServerErrorLoggingHandler}.
 */
@RunWith(value = PowerMockRunner.class) @PrepareForTest(value = { TraceeContextLogger.class, Tracee.class }) public class TraceeServerErrorLoggingHandlerTest {
  private final TraceeBackend mockedBackend = mock(TraceeBackend.class);

  private NoopTraceeLoggerFactory loggerFactory = spy(NoopTraceeLoggerFactory.INSTANCE);

  private TraceeServerErrorLoggingHandler unit;

  private TraceeContextLogger contextLogger;

  @Before public void setup() {
    when(mockedBackend.getLoggerFactory()).thenReturn(loggerFactory);
    unit = new TraceeServerErrorLoggingHandler(mockedBackend);
    THREAD_LOCAL_SOAP_MESSAGE_STR.remove();
    mockStatic(TraceeContextLogger.class);
    contextLogger = mock(TraceeContextLogger.class);
    when(TraceeContextLogger.createDefault()).thenReturn(contextLogger);
  }

  @Test public void defaultConstructorShouldUseTraceeBackend() {
    mockStatic(Tracee.class);
    when(Tracee.getBackend()).thenReturn(mockedBackend);
    new TraceeServerErrorLoggingHandler();
    verifyStatic();
    Tracee.getBackend();
  }

  @Test public void shouldConvertNullMessageToNullString() {
    assertThat(unit.convertSoapMessageAsString(null), equalTo("null"));
  }

  @Test public void shouldConvertSoapMessageToString() throws Exception {
    final SOAPMessage message = buildSpiedTestMessage("v\u00c4");
    assertThat(unit.convertSoapMessageAsString(message), Matchers.allOf(containsString("SOAP-ENV:Body A=\"v\u00c4\"/>"), containsString("SOAP-ENV:Envelope")));
  }

  @Test public void messageShouldBeWrittenToThreadLocal() throws Exception {
    SOAPMessageContext context = mock(SOAPMessageContext.class);
    when(context.getMessage()).thenReturn(buildSpiedTestMessage("v\u00c4"));
    unit.handleIncoming(context);
    assertThat(THREAD_LOCAL_SOAP_MESSAGE_STR.get(), Matchers.allOf(notNullValue(), containsString("SOAP-ENV:Body A=\"v\u00c4\"/>"), containsString("SOAP-ENV:Envelope")));
  }

  @Test public void messageShouldBeWrittenToThreadLocalAndRespectEncoding() throws Exception {
    SOAPMessageContext context = mock(SOAPMessageContext.class);
    SOAPMessage soapMessage = buildSpiedTestMessage("v\u0159");
    soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "ISO-8859-2");
    when(context.getMessage()).thenReturn(soapMessage);
    unit.handleIncoming(context);
    assertThat(THREAD_LOCAL_SOAP_MESSAGE_STR.get(), Matchers.allOf(notNullValue(), containsString("SOAP-ENV:Body A=\"v\u0159\"/>")));
  }

  @Test public void useUtf8IfNoEncodingIsSpecified() throws Exception {
    SOAPMessage soapMessage = buildSpiedTestMessage("v\u0159");
    assertThat(unit.determineMessageEncoding(soapMessage), is(Charset.forName("UTF-8")));
  }

  @Test public void useGivenEncodingFromSoapMessage() throws Exception {
    SOAPMessage soapMessage = buildSpiedTestMessage("v\u0159");
    soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "ISO-8859-2");
    assertThat(unit.determineMessageEncoding(soapMessage), is(Charset.forName("ISO-8859-2")));
  }

  @Test public void fallbackToUtf8IfEncodingisNotSupported() throws Exception {
    SOAPMessage soapMessage = buildSpiedTestMessage("v\u0159");
    soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "DonaldDuck");
    assertThat(unit.determineMessageEncoding(soapMessage), is(Charset.forName("UTF-8")));
  }

  @Test public void failedMessageTranslationShouldTranslateToErrorString() throws Exception {
    SOAPMessage soapMessage = buildSpiedTestMessage("v");
    doThrow(new RuntimeException("ohoh")).when(soapMessage).writeTo(Mockito.any(OutputStream.class));
    assertThat(unit.convertSoapMessageAsString(soapMessage), is("ERROR"));
  }

  @Test public void closeShouldDeleteThreadLocalStore() throws Exception {
    THREAD_LOCAL_SOAP_MESSAGE_STR.set("My Value");
    unit.close(null);
    assertThat(THREAD_LOCAL_SOAP_MESSAGE_STR.get(), is(nullValue()));
  }

  @Test public void faultsShouldLogTheSoapMessage() throws Exception {
    final SOAPMessageContext messageContext = mock(SOAPMessageContext.class);
    when(messageContext.getMessage()).thenReturn(buildSpiedTestMessage("vA"));
    unit.handleFault(messageContext);
    verify(contextLogger).logJsonWithPrefixedMessage(eq("TRACEE JAXWS ERROR CONTEXT LISTENER"), eq(ImplicitContext.COMMON), eq(ImplicitContext.TRACEE), Mockito.any(JaxWsWrapper.class));
  }

  @Test public void handleFouldShouldReturnTrueToProcessWithHandlerChain() throws Exception {
    final SOAPMessageContext messageContext = mock(SOAPMessageContext.class);
    when(messageContext.getMessage()).thenReturn(buildSpiedTestMessage("vA"));
    unit.handleFault(messageContext);
    assertThat(unit.handleFault(messageContext), is(true));
  }

  @Test public void outgoingMessageShouldNotBeProcessed() {
    final SOAPMessageContext messageContext = mock(SOAPMessageContext.class);
    verifyNoMoreInteractions(messageContext, loggerFactory);
    unit.handleOutgoing(messageContext);
    assertThat(THREAD_LOCAL_SOAP_MESSAGE_STR.get(), is(nullValue()));
  }

  @Test public void registerForNoHeaders() {
    assertThat(unit.getHeaders(), is(nullValue()));
  }

  private SOAPMessage buildSpiedTestMessage(String value) throws SOAPException {
    final SOAPMessage message = MessageFactory.newInstance().createMessage();
    message.getSOAPBody().addAttribute(new QName("A"), value);
    return spy(message);
  }
}