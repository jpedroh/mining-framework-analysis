package io.tracee.contextlogger.javaee;

import io.tracee.contextlogger.TraceeContextLogger;
import io.tracee.contextlogger.api.ImplicitContext;
import java.lang.reflect.Method;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.jms.Message;


/**
 * Message listener to detect exceptions that happened during javaee message processing.
 * In case of an exception contextual information will be written to the log.
 * Created by Tobias Gindler, holisticon AG on 13.03.14.
 */
public class TraceeJmsErrorMessageListener {
static final String JSON_PREFIXED_MESSAGE = "TRACEE JMS ERROR CONTEXT LOGGING LISTENER  : ";

  @SuppressWarnings("unused")
  public TraceeJmsErrorMessageListener() {
  }

  @AroundInvoke
  public Object intercept(InvocationContext ctx) throws Exception {
    try {
      return ctx.proceed();
    } catch (java.lang.Exception e) {
      final boolean isMdbInvocation = isMessageListenerOnMessageMethod(ctx.getMethod());
      if (isMdbInvocation) {
        TraceeContextLogger.createDefault().logJsonWithPrefixedMessage(JSON_PREFIXED_MESSAGE, ImplicitContext.COMMON, ImplicitContext.TRACEE, ctx, e);
      }
      throw e;
    }
  }

    boolean isMessageListenerOnMessageMethod(Method method) {
        return "onMessage".equals(method.getName())
                && method.getParameterTypes().length == 1
                && method.getParameterTypes()[0] == Message.class;
    }
}