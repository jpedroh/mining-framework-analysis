package org.assertj.core.api;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/** Collects error messages of all AssertionErrors thrown by the proxied method. */
public class ErrorCollector implements MethodInterceptor {
  private static final String INTERCEPT_METHOD_NAME = "intercept";

  private static final String CLASS_NAME = ErrorCollector.class.getName();

  private final List<Throwable> errors = new ArrayList<>();

  private final LastResult lastResult = new LastResult();

  @Override public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    Object result = obj;
    try {
      result = proxy.invokeSuper(obj, args);
      lastResult.setSuccess(true);
    } catch (AssertionError e) {
      if (isNestedErrorCollectorProxyCall()) {
        throw e;
      }
      lastResult.setSuccess(false);
      errors.add(e);
    }
    return result;
  }

  public List<Throwable> errors() {
    return Collections.unmodifiableList(errors);
  }

  public boolean wasSuccess() {
    return lastResult.wasSuccess();
  }

  private boolean isNestedErrorCollectorProxyCall() {
    return countErrorCollectorProxyCalls() > 1;
  }

  private static int countErrorCollectorProxyCalls() {
    int nbCalls = 0;
    for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
      if (CLASS_NAME.equals(stackTraceElement.getClassName()) && INTERCEPT_METHOD_NAME.equals(stackTraceElement.getMethodName())) {
        nbCalls++;
      }
    }
    return nbCalls;
  }

  private static class LastResult {
    private boolean wasSuccess = true;

    private boolean errorFound = false;

    private boolean wasSuccess() {
      return wasSuccess;
    }

    private void setSuccess(boolean success) {
      errorFound |= !success;
      if (resolvingOutermostErrorCollectorProxyNestedCall()) {
        wasSuccess = !errorFound;
        errorFound = false;
      }
    }

    private boolean resolvingOutermostErrorCollectorProxyNestedCall() {
      return countErrorCollectorProxyCalls() == 1;
    }

    @Override public String toString() {
      return String.format("LastResult [wasSuccess=%s, errorFound=%s]", wasSuccess, errorFound);
    }
  }
}