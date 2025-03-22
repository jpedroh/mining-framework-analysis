package me.zhengjie.modules.system.rest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.system.domain.DictDetail;
import me.zhengjie.modules.system.service.DictDetailService;
import me.zhengjie.modules.system.service.dto.DictDetailDto;
import me.zhengjie.modules.system.service.dto.DictDetailQueryCriteria;
import me.zhengjie.utils.PageResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
@RestController @RequiredArgsConstructor @Api(tags = "\u7cfb\u7edf\uff1a\u5b57\u5178\u8be6\u60c5\u7ba1\u7406") @RequestMapping(value = "/api/dictDetail") public class DictDetailController {
  private final DictDetailService dictDetailService;

  private static final String ENTITY_NAME = "dictDetail";

  @ApiOperation(value = "\u67e5\u8be2\u5b57\u5178\u8be6\u60c5") @GetMapping public ResponseEntity<PageResult<DictDetailDto>> queryDictDetail(DictDetailQueryCriteria criteria, @PageableDefault(sort = { "dictSort" }, direction = Sort.Direction.ASC) Pageable pageable) {
    return new ResponseEntity<>(dictDetailService.queryAll(criteria, pageable), HttpStatus.OK);
  }

  @ApiOperation(value = "\u67e5\u8be2\u591a\u4e2a\u5b57\u5178\u8be6\u60c5") @GetMapping(value = "/map") public ResponseEntity<Object> getDictDetailMaps(@RequestParam String dictName) {
    String[] names = dictName.split("[,\uff0c]");
    Map<String, List<DictDetailDto>> dictMap = new HashMap<>(16);
    for (String name : names) {
      dictMap.put(name, dictDetailService.getDictByName(name));
    }
    return new ResponseEntity<>(dictMap, HttpStatus.OK);
  }

  @Log(value = "\u65b0\u589e\u5b57\u5178\u8be6\u60c5") @ApiOperation(value = "\u65b0\u589e\u5b57\u5178\u8be6\u60c5") @PostMapping @PreAuthorize(value = "@el.check(\'dict:add\')") public ResponseEntity<Object> createDictDetail(@Validated @RequestBody DictDetail resources) {
    if (resources.getId() != null) {
      throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
    }
    dictDetailService.create(resources);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Log(value = "\u4fee\u6539\u5b57\u5178\u8be6\u60c5") @ApiOperation(value = "\u4fee\u6539\u5b57\u5178\u8be6\u60c5") @PutMapping @PreAuthorize(value = "@el.check(\'dict:edit\')") public ResponseEntity<Object> updateDictDetail(@Validated(value = DictDetail.Update.class) @RequestBody DictDetail resources) {
    if (resources.getId() <= 6) {
      throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
    }
    dictDetailService.update(resources);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Log(value = "\u5220\u9664\u5b57\u5178\u8be6\u60c5") @ApiOperation(value = "\u5220\u9664\u5b57\u5178\u8be6\u60c5") @DeleteMapping(value = "/{id}") @PreAuthorize(value = "@el.check(\'dict:del\')") public ResponseEntity<Object> deleteDictDetail(@PathVariable Long id) {
    if (id <= 6) {
      throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
    }
    dictDetailService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}