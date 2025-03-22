package me.zhengjie.modules.system.rest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.system.domain.Dict;
import me.zhengjie.modules.system.service.DictService;
import me.zhengjie.modules.system.service.dto.DictDto;
import me.zhengjie.modules.system.service.dto.DictQueryCriteria;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.PageUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
@RestController @RequiredArgsConstructor @Api(tags = "\u7cfb\u7edf\uff1a\u5b57\u5178\u7ba1\u7406") @RequestMapping(value = "/api/dict") public class DictController {
  private final DictService dictService;

  private static final String ENTITY_NAME = "dict";

  @ApiOperation(value = "\u5bfc\u51fa\u5b57\u5178\u6570\u636e") @GetMapping(value = "/download") @PreAuthorize(value = "@el.check(\'dict:list\')") public void exportDict(HttpServletResponse response, DictQueryCriteria criteria) throws IOException {
    dictService.download(dictService.queryAll(criteria), response);
  }

  @ApiOperation(value = "\u67e5\u8be2\u5b57\u5178") @GetMapping(value = "/all") @PreAuthorize(value = "@el.check(\'dict:list\')") public ResponseEntity<List<DictDto>> queryAllDict() {
    return new ResponseEntity<>(dictService.queryAll(new DictQueryCriteria()), HttpStatus.OK);
  }

  @ApiOperation(value = "\u67e5\u8be2\u5b57\u5178") @GetMapping @PreAuthorize(value = "@el.check(\'dict:list\')") public ResponseEntity<PageResult<DictDto>> queryDict(DictQueryCriteria resources, Pageable pageable) {
    return new ResponseEntity<>(dictService.queryAll(resources, pageable), HttpStatus.OK);
  }

  @Log(value = "\u65b0\u589e\u5b57\u5178") @ApiOperation(value = "\u65b0\u589e\u5b57\u5178") @PostMapping @PreAuthorize(value = "@el.check(\'dict:add\')") public ResponseEntity<Object> createDict(@Validated @RequestBody Dict resources) {
    if (resources.getId() != null) {
      throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
    }
    dictService.create(resources);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Log(value = "\u4fee\u6539\u5b57\u5178") @ApiOperation(value = "\u4fee\u6539\u5b57\u5178") @PutMapping @PreAuthorize(value = "@el.check(\'dict:edit\')") public ResponseEntity<Object> updateDict(@Validated(value = Dict.Update.class) @RequestBody Dict resources) {
    if (resources.getId() <= 5) {
      throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
    }
    dictService.update(resources);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Log(value = "\u5220\u9664\u5b57\u5178") @ApiOperation(value = "\u5220\u9664\u5b57\u5178") @DeleteMapping @PreAuthorize(value = "@el.check(\'dict:del\')") public ResponseEntity<Object> deleteDict(@RequestBody Set<Long> ids) {
    for (Long id : ids) {
      if (id <= 5) {
        throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
      }
    }
    dictService.delete(ids);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}