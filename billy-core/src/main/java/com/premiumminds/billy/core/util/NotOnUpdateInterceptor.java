package com.premiumminds.billy.core.util;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.premiumminds.billy.core.exceptions.BillyUpdateException;
import com.premiumminds.billy.core.persistence.entities.BaseEntity;
import com.premiumminds.billy.core.services.builders.impl.AbstractBuilder;

/**
 * Intercepts methods calls to annotated with {@link NotOnUpdate}.
 *
 * Only runs if it building an entity marked as new. Otherwise @throws
 * {@link BillyUpdateException}
 *
 * @author Hugo Correia
 *
 */
public class NotOnUpdateInterceptor implements MethodInterceptor {
  public static final String METHOD_NAME = "getTypeInstance";

  @Override public Object invoke(MethodInvocation invocation) throws Throwable {
    String exceptionMessage = invocation.getMethod().getAnnotation(NotOnUpdate.class).message();
    Method method = this.getMethod(invocation.getThis().getClass());
    BaseEntity entity = (BaseEntity) method.invoke(invocation.getThis(), new Object[] {  });
    if (entity.isNew()) {
      return invocation.proceed();
    } else {
      throw new BillyUpdateException(exceptionMessage);
    }
  }

  private Method getMethod(Class<? extends Object> clazz) throws NoSuchMethodException {
    if (clazz.getCanonicalName().equals(Object.class.getCanonicalName())) {
      throw new NoSuchMethodException(NotOnUpdateInterceptor.METHOD_NAME);
    }
    if (clazz.getCanonicalName().equals(AbstractBuilder.class.getCanonicalName())) {
      Method foundMethod = clazz.getDeclaredMethod(NotOnUpdateInterceptor.METHOD_NAME, new Class[] {  });
      foundMethod.setAccessible(true);
      return foundMethod;
    } else {
      return this.getMethod(clazz.getSuperclass());
    }
  }
}