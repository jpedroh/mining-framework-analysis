package me.zhengjie;
import io.swagger.annotations.Api;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import me.zhengjie.utils.SpringContextHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

/**
 * 开启审计功能 -> @EnableJpaAuditing
 *
 * @author Zheng Jie
 * @date 2018/11/15 9:20:19
 */
@EnableAsync @RestController @Api(hidden = true) @SpringBootApplication @EnableTransactionManagement @EnableJpaAuditing(auditorAwareRef = "auditorAware") public class AppRun {
  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(AppRun.class);
    springApplication.addListeners(new ApplicationPidFileWriter());
    springApplication.run(args);
  }

  @Bean public SpringContextHolder springContextHolder() {
    return new SpringContextHolder();
  }

  /**
     * 访问首页提示
     *
     * @return /
     */
  @AnonymousGetMapping(value = "/") public String index() {
    return "Backend service started successfully";
  }

  /**
     * 监控服务运行状态
     *
     * @return /generate_204
     */
  @AnonymousGetMapping(value = "/generate_204") public ResponseEntity<Object> monitor() {
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}