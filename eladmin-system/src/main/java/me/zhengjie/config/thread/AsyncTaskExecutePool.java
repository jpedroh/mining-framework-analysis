package me.zhengjie.config.thread;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.TaskException;
import me.zhengjie.modules.quartz.domain.QuartzLog;
import me.zhengjie.modules.quartz.service.QuartzLogService;
import me.zhengjie.utils.ThrowableUtil;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j @Configuration @RequiredArgsConstructor public class AsyncTaskExecutePool implements AsyncConfigurer {
  private final QuartzLogService quartzLogService;

  @Override public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(AsyncTaskProperties.corePoolSize);
    executor.setMaxPoolSize(AsyncTaskProperties.maxPoolSize);
    executor.setQueueCapacity(AsyncTaskProperties.queueCapacity);
    executor.setKeepAliveSeconds(AsyncTaskProperties.keepAliveSeconds);
    executor.setThreadFactory(new TheadFactoryName("el-async"));
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }

  @Override public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return (throwable, method, objects) -> {
      log.error("====" + throwable.getMessage() + "====", throwable);
      log.error("exception method:" + method.getName());
      if (throwable instanceof TaskException) {
        QuartzLog quartzLog = new QuartzLog();
        quartzLog.setBeanName(method.getDeclaringClass().getSimpleName());
        quartzLog.setMethodName(method.getName());
        quartzLog.setExceptionDetail(ThrowableUtil.getStackTrace(throwable));
        quartzLog.setParams(((TaskException) throwable).getParam());
        quartzLog.setIsSuccess(false);
        quartzLog.setTime(0L);
        quartzLogService.create(quartzLog);
      }
    };
  }
}