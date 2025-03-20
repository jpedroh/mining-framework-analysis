package xdi2.messaging.target.interceptor.impl;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.messaging.Message;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageInterceptor;

/**
 * This interceptor checks if the source peer root of a message matches the sender of the message.
 * 
 * @author markus
 */
public class FromInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageInterceptor, Prototype<FromInterceptor> {
  @Override public FromInterceptor instanceFor(PrototypingContext prototypingContext) {
    return this;
  }

  @Override public InterceptorResult before(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {
    throw new Xdi2RuntimeException("Not implemented.");
  }

  @Override public InterceptorResult after(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {
    return InterceptorResult.DEFAULT;
  }
}