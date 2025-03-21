package com.jfinal.aop;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.jfinal.core.Action;
import com.jfinal.core.Controller;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Invocation is used to invoke the interceptors and the target method
 */
@SuppressWarnings(value = { "unchecked" }) public class Invocation {
  private Action action;

  boolean useInjectTarget;

  private Object target;

  private Method method;

  private Object[] args;

  private MethodProxy methodProxy;

  private Interceptor[] inters;

  private Object returnValue;

  private int index = 0;

  protected Invocation() {
    this.action = null;
  }

  public Invocation(Action action, Controller controller) {
    this.action = action;
    this.inters = action.getInterceptors();
    this.target = controller;
    this.args = action.getParameterGetter().get(controller);
  }

  public Invocation(Object target, Method method, Object[] args, MethodProxy methodProxy, Interceptor[] inters) {
    this.action = null;
    this.target = target;
    this.method = method;
    this.args = args;
    this.methodProxy = methodProxy;
    this.inters = inters;
  }

  public void invoke() {
    if (index < inters.length) {
      inters[index++].intercept(this);
    } else {
      if (index++ == inters.length) {
        try {
          if (action != null) {
            returnValue = action.getMethod().invoke(target, args);
          } else {
            if (useInjectTarget) {
              returnValue = methodProxy.invoke(target, args);
            } else {
              returnValue = methodProxy.invokeSuper(target, args);
            }
          }
        } catch (InvocationTargetException e) {
          Throwable t = e.getTargetException();
          throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(e);
        } catch (RuntimeException e) {
          throw e;
        } catch (Throwable t) {
          throw new RuntimeException(t);
        }
      }
    }
  }

  public Object getArg(int index) {
    if (index >= args.length) {
      throw new ArrayIndexOutOfBoundsException();
    }
    return args[index];
  }

  public void setArg(int index, Object value) {
    if (index >= args.length) {
      throw new ArrayIndexOutOfBoundsException();
    }
    args[index] = value;
  }

  public Object[] getArgs() {
    return args;
  }

  /**
	 * Get the target object which be intercepted
	 * <pre>
	 * Example:
	 * OrderService os = getTarget();
	 * </pre>
	 */
  public <T extends java.lang.Object> T getTarget() {
    return (T) target;
  }

  /**
	 * Return the method of this action.
	 * <p>
	 * You can getMethod.getAnnotations() to get annotation on action method to do more things
	 */
  public Method getMethod() {
    if (action != null) {
      return action.getMethod();
    }
    return method;
  }

  /**
	 * Return the method name of this action's method.
	 */
  public String getMethodName() {
    if (action != null) {
      return action.getMethodName();
    }
    return method.getName();
  }

  /**
	 * Get the return value of the target method
	 */
  public <T extends java.lang.Object> T getReturnValue() {
    return (T) returnValue;
  }

  /**
	 * Set the return value of the target method
	 */
  public void setReturnValue(Object returnValue) {
    this.returnValue = returnValue;
  }

  /**
	 * Return the controller of this action.
	 */
  public Controller getController() {
    if (action == null) {
      throw new RuntimeException("This method can only be used for action interception");
    }
    return (Controller) target;
  }

  /**
	 * Return the action key.
	 * actionKey = controllerKey + methodName
	 */
  public String getActionKey() {
    if (action == null) {
      throw new RuntimeException("This method can only be used for action interception");
    }
    return action.getActionKey();
  }

  /**
	 * Return the controller key.
	 */
  public String getControllerKey() {
    if (action == null) {
      throw new RuntimeException("This method can only be used for action interception");
    }
    return action.getControllerKey();
  }

  /**
	 * Return view path of this controller.
	 */
  public String getViewPath() {
    if (action == null) {
      throw new RuntimeException("This method can only be used for action interception");
    }
    return action.getViewPath();
  }

  /**
	 * return true if it is action invocation.
	 */
  public boolean isActionInvocation() {
    return action != null;
  }
}