package me.zhengjie.modules.monitor.rest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Limit;
import me.zhengjie.modules.security.annotation.AnonymousAccess;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 接口限流测试类
 */
@RestController @RequestMapping(value = "/api/limit") @Api(tags = "\u7cfb\u7edf\uff1a\u9650\u6d41\u6d4b\u8bd5\u7ba1\u7406") public class LimitController {
  private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();

  /**
     * 测试限流注解，下面配置说明该接口 60秒内最多只能访问 10次，保存到redis的键名为 limit_test，
     */
  @Limit(key = "test", period = 60, count = 10, name = "testLimit", prefix = "limit") @
<<<<<<< /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/monitor/rest/LimitController.java/left.java
  AnonymousAccess
=======
  GetMapping
>>>>>>> /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/monitor/rest/LimitController.java/right.java
   @PreAuthorize(value = "@el.check(\'anonymous\')") @ApiOperation(value = "\u6d4b\u8bd5") public int testLimit() {
    return ATOMIC_INTEGER.incrementAndGet();
  }
}