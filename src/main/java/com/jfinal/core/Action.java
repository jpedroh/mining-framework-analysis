package com.jfinal.core;
import java.lang.reflect.Method;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.paragetter.ParaProcessorBuilder;
import com.jfinal.core.paragetter.ParaProcessor;

/**
 * Action
 */
public class Action {
  private final Class<? extends Controller> controllerClass;

  private final String controllerKey;

  private final String actionKey;

  private final Method method;

  private final String methodName;

  private final Interceptor[] interceptors;

  private final String viewPath;

  private final ParaProcessor parameterGetter;

  public Action(String controllerKey, String actionKey, Class<? extends Controller> controllerClass, Method method, String methodName, Interceptor[] interceptors, String viewPath) {
    this.controllerKey = controllerKey;
    this.actionKey = actionKey;
    this.controllerClass = controllerClass;
    this.method = method;
    this.methodName = methodName;
    this.interceptors = interceptors;
    this.viewPath = viewPath;
    this.parameterGetter = ParaProcessorBuilder.me().build(controllerClass, method);
  }

  public Class<? extends Controller> getControllerClass() {
    return controllerClass;
  }

  public String getControllerKey() {
    return controllerKey;
  }

  public String getActionKey() {
    return actionKey;
  }

  public Method getMethod() {
    return method;
  }

  public Interceptor[] getInterceptors() {
    return interceptors;
  }

  public String getViewPath() {
    return viewPath;
  }

  public String getMethodName() {
    return methodName;
  }

  public ParaProcessor getParameterGetter() {
    return parameterGetter;
  }
}