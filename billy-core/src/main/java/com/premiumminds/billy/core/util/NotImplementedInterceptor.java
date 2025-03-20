package com.premiumminds.billy.core.util;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.premiumminds.billy.core.exceptions.NotImplementedException;

public class NotImplementedInterceptor implements MethodInterceptor {
  @Override public Object invoke(MethodInvocation arg0) throws Throwable {
    throw new NotImplementedException("The method is not implemented : " + arg0.getMethod().getName());
  }
}