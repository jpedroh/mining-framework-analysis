package com.xxl.job.core.executor.impl;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.handler.impl.MethodJobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * xxl-job executor (for spring)
 *
 * @author xuxueli 2018-11-01 09:24:52
 */
public class XxlJobSpringExecutor extends XxlJobExecutor implements ApplicationContextAware, InitializingBean, DisposableBean {
  private static final Logger logger = LoggerFactory.getLogger(XxlJobSpringExecutor.class);

  @Override public void afterPropertiesSet() throws Exception {
    initJobHandlerMethodRepository(applicationContext);
    GlueFactory.refreshInstance(1);
    super.start();
  }

  @Override public void destroy() {
    super.destroy();
  }

  private void initJobHandlerMethodRepository(ApplicationContext applicationContext) {
    if (applicationContext == null) {
      return;
    }
    String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
    for (String beanDefinitionName : beanDefinitionNames) {
      Object bean = applicationContext.getBean(beanDefinitionName);
      Map<Method, XxlJob> annotatedMethods = new HashMap<>();
      try {
        annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(), new MethodIntrospector.MetadataLookup<XxlJob>() {
          @Override public XxlJob inspect(Method method) {
            return AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class);
          }
        });
      } catch (Throwable ex) {
        if (logger.isDebugEnabled()) {
          logger.debug("Could not resolve methods for bean with name \'" + beanDefinitionName + "\'", ex);
        }
      }
      for (Map.Entry<Method, XxlJob> methodXxlJobEntry : annotatedMethods.entrySet()) {
        Method method = methodXxlJobEntry.getKey();
        XxlJob xxlJob = methodXxlJobEntry.getValue();
        if (xxlJob == null) {
          continue;
        }
        String name = xxlJob.value();
        if (name.trim().length() == 0) {
          throw new RuntimeException("xxl-job method-jobhandler name invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
        }
        if (loadJobHandler(name) != null) {
          throw new RuntimeException("xxl-job jobhandler[" + name + "] naming conflicts.");
        }
        if (!(method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isAssignableFrom(String.class))) {
          throw new RuntimeException("xxl-job method-jobhandler param-classtype invalid, for[" + bean.getClass() + "#" + method.getName() + "] , " + "The correct method format like \" public ReturnT<String> execute(String param) \" .");
        }
        if (!method.getReturnType().isAssignableFrom(ReturnT.class)) {
          throw new RuntimeException("xxl-job method-jobhandler return-classtype invalid, for[" + bean.getClass() + "#" + method.getName() + "] , " + "The correct method format like \" public ReturnT<String> execute(String param) \" .");
        }
        method.setAccessible(true);
        Method initMethod = null;
        Method destroyMethod = null;
        if (xxlJob.init().trim().length() > 0) {
          try {
            initMethod = bean.getClass().getDeclaredMethod(xxlJob.init());
            initMethod.setAccessible(true);
          } catch (NoSuchMethodException e) {
            throw new RuntimeException("xxl-job method-jobhandler initMethod invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
          }
        }
        if (xxlJob.destroy().trim().length() > 0) {
          try {
            destroyMethod = bean.getClass().getDeclaredMethod(xxlJob.destroy());
            destroyMethod.setAccessible(true);
          } catch (NoSuchMethodException e) {
            throw new RuntimeException("xxl-job method-jobhandler destroyMethod invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
          }
        }
        registJobHandler(name, new MethodJobHandler(bean, method, initMethod, destroyMethod));
      }
    }
  }

  private static ApplicationContext applicationContext;

  @Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  public static ApplicationContext getApplicationContext() {
    return applicationContext;
  }
}