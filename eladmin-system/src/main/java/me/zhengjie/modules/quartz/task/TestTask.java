package me.zhengjie.modules.quartz.task;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.TaskException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j @Async @SuppressWarnings(value = { "unused" }) public @Service class TestTask {
  @SuppressWarnings(value = { "unused" }) public void run() {
    log.info("run \u6267\u884c\u6210\u529f");
  }

  @SuppressWarnings(value = { "unused" }) public void run1(String str) {
    log.info("run1 \u6267\u884c\u6210\u529f\uff0c\u53c2\u6570\u4e3a\uff1a {}" + str);
  }

  @SuppressWarnings(value = { "unused" }) public void run2() {
    log.info("run2 \u6267\u884c\u6210\u529f");
  }

  @SuppressWarnings(value = { "unused" }) public void runWithException() {
    throw new TaskException("\u8fd4\u56de\u4e00\u4e2a\u6d4b\u8bd5\u9519\u8bef");
  }

  @SuppressWarnings(value = { "unused" }) public void runWithException(String param) {
    throw new TaskException("\u8fd4\u56de\u4e00\u4e2a\u5e26\u53c2\u6570\u7684\u6d4b\u8bd5\u9519\u8bef", param);
  }
}