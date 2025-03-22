package me.zhengjie.modules.mnt.rest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.mnt.domain.Deploy;
import me.zhengjie.modules.mnt.domain.DeployHistory;
import me.zhengjie.modules.mnt.service.DeployService;
import me.zhengjie.modules.mnt.service.dto.DeployDto;
import me.zhengjie.modules.mnt.service.dto.DeployQueryCriteria;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.PageResult;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
* @author zhanghouying
* @date 2019-08-24
*/
@RestController @Api(tags = "\u8fd0\u7ef4\uff1a\u90e8\u7f72\u7ba1\u7406") @RequiredArgsConstructor @RequestMapping(value = "/api/deploy") public class DeployController {
  private final String fileSavePath = FileUtil.getTmpDirPath() + "/";

  private final DeployService deployService;

  @ApiOperation(value = "\u5bfc\u51fa\u90e8\u7f72\u6570\u636e") @GetMapping(value = "/download") @PreAuthorize(value = "@el.check(\'database:list\')") public void exportDeployData(HttpServletResponse response, DeployQueryCriteria criteria) throws IOException {
    deployService.download(deployService.queryAll(criteria), response);
  }

  @ApiOperation(value = "\u67e5\u8be2\u90e8\u7f72") @GetMapping @PreAuthorize(value = "@el.check(\'deploy:list\')") public ResponseEntity<PageResult<DeployDto>> queryDeployData(DeployQueryCriteria criteria, Pageable pageable) {
    return new ResponseEntity<>(deployService.queryAll(criteria, pageable), HttpStatus.OK);
  }

  @Log(value = "\u65b0\u589e\u90e8\u7f72") @ApiOperation(value = "\u65b0\u589e\u90e8\u7f72") @PostMapping @PreAuthorize(value = "@el.check(\'deploy:add\')") public ResponseEntity<Object> createDeploy(@Validated @RequestBody Deploy resources) {
    throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
  }

  @Log(value = "\u4fee\u6539\u90e8\u7f72") @ApiOperation(value = "\u4fee\u6539\u90e8\u7f72") @PutMapping @PreAuthorize(value = "@el.check(\'deploy:edit\')") public ResponseEntity<Object> updateDeploy(@Validated @RequestBody Deploy resources) {
    deployService.update(resources);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Log(value = "\u5220\u9664\u90e8\u7f72") @ApiOperation(value = "\u5220\u9664\u90e8\u7f72") @DeleteMapping @PreAuthorize(value = "@el.check(\'deploy:del\')") public ResponseEntity<Object> deleteDeploy(@RequestBody Set<Long> ids) {
    deployService.delete(ids);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Log(value = "\u4e0a\u4f20\u6587\u4ef6\u90e8\u7f72") @ApiOperation(value = "\u4e0a\u4f20\u6587\u4ef6\u90e8\u7f72") @PostMapping(value = "/upload") @PreAuthorize(value = "@el.check(\'deploy:edit\')") public ResponseEntity<Object> uploadDeploy(@RequestBody MultipartFile file, HttpServletRequest request) throws Exception {
    Long id = Long.valueOf(request.getParameter("id"));
    String fileName = "";
    if (file != null) {
      fileName = file.getOriginalFilename();
      File deployFile = new File(fileSavePath + fileName);
      FileUtil.del(deployFile);
      file.transferTo(deployFile);
      deployService.deploy(fileSavePath + fileName, id);
    } else {
      System.out.println("\u6ca1\u6709\u627e\u5230\u76f8\u5bf9\u5e94\u7684\u6587\u4ef6");
    }
    System.out.println("\u6587\u4ef6\u4e0a\u4f20\u7684\u539f\u540d\u79f0\u4e3a:" + Objects.requireNonNull(file).getOriginalFilename());
    Map<String, Object> map = new HashMap<>(2);
    map.put("errno", 0);
    map.put("id", fileName);
    return new ResponseEntity<>(map, HttpStatus.OK);
  }

  @Log(value = "\u7cfb\u7edf\u8fd8\u539f") @ApiOperation(value = "\u7cfb\u7edf\u8fd8\u539f") @PostMapping(value = "/serverReduction") @PreAuthorize(value = "@el.check(\'deploy:edit\')") public ResponseEntity<Object> serverReduction(@Validated @RequestBody DeployHistory resources) {
    String result = deployService.serverReduction(resources);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Log(value = "\u670d\u52a1\u8fd0\u884c\u72b6\u6001") @ApiOperation(value = "\u670d\u52a1\u8fd0\u884c\u72b6\u6001") @PostMapping(value = "/serverStatus") @PreAuthorize(value = "@el.check(\'deploy:edit\')") public ResponseEntity<Object> serverStatus(@Validated @RequestBody Deploy resources) {
    String result = deployService.serverStatus(resources);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Log(value = "\u542f\u52a8\u670d\u52a1") @ApiOperation(value = "\u542f\u52a8\u670d\u52a1") @PostMapping(value = "/startServer") @PreAuthorize(value = "@el.check(\'deploy:edit\')") public ResponseEntity<Object> startServer(@Validated @RequestBody Deploy resources) {
    String result = deployService.startServer(resources);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Log(value = "\u505c\u6b62\u670d\u52a1") @ApiOperation(value = "\u505c\u6b62\u670d\u52a1") @PostMapping(value = "/stopServer") @PreAuthorize(value = "@el.check(\'deploy:edit\')") public ResponseEntity<Object> stopServer(@Validated @RequestBody Deploy resources) {
    String result = deployService.stopServer(resources);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}