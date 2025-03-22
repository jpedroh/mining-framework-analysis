package me.zhengjie.modules.quartz.rest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.quartz.domain.QuartzJob;
import me.zhengjie.modules.quartz.domain.QuartzLog;
import me.zhengjie.modules.quartz.service.QuartzJobService;
import me.zhengjie.modules.quartz.service.dto.JobQueryCriteria;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.SpringContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2019-01-07
 */
@Slf4j @RestController @RequiredArgsConstructor @RequestMapping(value = "/api/jobs") @Api(tags = "\u7cfb\u7edf:\u5b9a\u65f6\u4efb\u52a1\u7ba1\u7406") public class QuartzJobController {
  private static final String ENTITY_NAME = "quartzJob";

  private final QuartzJobService quartzJobService;

  @ApiOperation(value = "\u67e5\u8be2\u5b9a\u65f6\u4efb\u52a1") @GetMapping @PreAuthorize(value = "@el.check(\'timing:list\')") public ResponseEntity<PageResult<QuartzJob>> queryQuartzJob(JobQueryCriteria criteria, Pageable pageable) {
    return new ResponseEntity<>(quartzJobService.queryAll(criteria, pageable), HttpStatus.OK);
  }

  @ApiOperation(value = "\u5bfc\u51fa\u4efb\u52a1\u6570\u636e") @GetMapping(value = "/download") @PreAuthorize(value = "@el.check(\'timing:list\')") public void exportQuartzJob(HttpServletResponse response, JobQueryCriteria criteria) throws IOException {
    quartzJobService.download(quartzJobService.queryAll(criteria), response);
  }

  @ApiOperation(value = "\u5bfc\u51fa\u65e5\u5fd7\u6570\u636e") @GetMapping(value = "/logs/download") @PreAuthorize(value = "@el.check(\'timing:list\')") public void exportQuartzJobLog(HttpServletResponse response, JobQueryCriteria criteria) throws IOException {
    quartzJobService.downloadLog(quartzJobService.queryAllLog(criteria), response);
  }

  @ApiOperation(value = "\u67e5\u8be2\u4efb\u52a1\u6267\u884c\u65e5\u5fd7") @GetMapping(value = "/logs") @PreAuthorize(value = "@el.check(\'timing:list\')") public ResponseEntity<PageResult<QuartzLog>> queryQuartzJobLog(JobQueryCriteria criteria, Pageable pageable) {
    return new ResponseEntity<>(quartzJobService.queryAllLog(criteria, pageable), HttpStatus.OK);
  }

  @Log(value = "\u65b0\u589e\u5b9a\u65f6\u4efb\u52a1") @ApiOperation(value = "\u65b0\u589e\u5b9a\u65f6\u4efb\u52a1") @PostMapping @PreAuthorize(value = "@el.check(\'timing:add\')") public ResponseEntity<Object> createQuartzJob(@Validated @RequestBody QuartzJob resources) {
    throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u652f\u6301\u65b0\u589e\u4efb\u52a1\uff01");
  }

  @Log(value = "\u4fee\u6539\u5b9a\u65f6\u4efb\u52a1") @ApiOperation(value = "\u4fee\u6539\u5b9a\u65f6\u4efb\u52a1") @PutMapping @PreAuthorize(value = "@el.check(\'timing:edit\')") public ResponseEntity<Object> updateQuartzJob(@Validated(value = QuartzJob.Update.class) @RequestBody QuartzJob resources) {
    checkBean(resources.getBeanName());
    quartzJobService.update(resources);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Log(value = "\u66f4\u6539\u5b9a\u65f6\u4efb\u52a1\u72b6\u6001") @ApiOperation(value = "\u66f4\u6539\u5b9a\u65f6\u4efb\u52a1\u72b6\u6001") @PutMapping(value = "/{id}") @PreAuthorize(value = "@el.check(\'timing:edit\')") public ResponseEntity<Object> updateQuartzJobStatus(@PathVariable Long id) {
    throw new BadRequestException("\u6f14\u793a\u73af\u5883\u8bf7\u4f7f\u7528\u6267\u884c\u6309\u94ae\u8fd0\u884c\u4efb\u52a1\uff01");
  }

  @Log(value = "\u6267\u884c\u5b9a\u65f6\u4efb\u52a1") @ApiOperation(value = "\u6267\u884c\u5b9a\u65f6\u4efb\u52a1") @PutMapping(value = "/exec/{id}") @PreAuthorize(value = "@el.check(\'timing:edit\')") public ResponseEntity<Object> executionQuartzJob(@PathVariable Long id) {
    quartzJobService.execution(quartzJobService.findById(id));
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Log(value = "\u5220\u9664\u5b9a\u65f6\u4efb\u52a1") @ApiOperation(value = "\u5220\u9664\u5b9a\u65f6\u4efb\u52a1") @DeleteMapping @PreAuthorize(value = "@el.check(\'timing:del\')") public ResponseEntity<Object> deleteQuartzJob(@RequestBody Set<Long> ids) {
    throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u652f\u6301\u5220\u9664\u5b9a\u65f6\u4efb\u52a1\uff01");
  }

  private void checkBean(String beanName) {
    if (!SpringContextHolder.getAllServiceBeanName().contains(beanName)) {
      throw new BadRequestException("\u975e\u6cd5\u7684 Bean\uff0c\u8bf7\u91cd\u65b0\u8f93\u5165\uff01");
    }
  }
}