package me.zhengjie.modules.system.rest;
import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.system.domain.Dept;
import me.zhengjie.modules.system.service.DeptService;
import me.zhengjie.modules.system.service.dto.DeptDto;
import me.zhengjie.modules.system.service.dto.DeptQueryCriteria;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.PageUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author Zheng Jie
* @date 2019-03-25
*/
@RestController @RequiredArgsConstructor @Api(tags = "\u7cfb\u7edf\uff1a\u90e8\u95e8\u7ba1\u7406") @RequestMapping(value = "/api/dept") public class DeptController {
  private final DeptService deptService;

  private static final String ENTITY_NAME = "dept";

  @ApiOperation(value = "\u5bfc\u51fa\u90e8\u95e8\u6570\u636e") @GetMapping(value = "/download") @PreAuthorize(value = "@el.check(\'dept:list\')") public void exportDept(HttpServletResponse response, DeptQueryCriteria criteria) throws Exception {
    deptService.download(deptService.queryAll(criteria, false), response);
  }

  @ApiOperation(value = "\u67e5\u8be2\u90e8\u95e8") @GetMapping @PreAuthorize(value = "@el.check(\'user:list\',\'dept:list\')") public ResponseEntity<PageResult<DeptDto>> queryDept(DeptQueryCriteria criteria) throws Exception {
    List<DeptDto> depts = deptService.queryAll(criteria, true);
    return new ResponseEntity<>(PageUtil.toPage(depts, depts.size()), HttpStatus.OK);
  }

  @ApiOperation(value = "\u67e5\u8be2\u90e8\u95e8:\u6839\u636eID\u83b7\u53d6\u540c\u7ea7\u4e0e\u4e0a\u7ea7\u6570\u636e") @PostMapping(value = "/superior") @PreAuthorize(value = "@el.check(\'user:list\',\'dept:list\')") public ResponseEntity<Object> getDeptSuperior(@RequestBody List<Long> ids) {
    Set<DeptDto> deptSet = new LinkedHashSet<>();
    for (Long id : ids) {
      DeptDto deptDto = deptService.findById(id);
      List<DeptDto> depts = deptService.getSuperior(deptDto, new ArrayList<>());
      for (DeptDto dept : depts) {
        if (dept.getId().equals(deptDto.getPid())) {
          dept.setSubCount(dept.getSubCount() - 1);
        }
      }
      deptSet.addAll(depts);
    }
    deptSet = deptSet.stream().filter((i) -> !ids.contains(i.getId())).collect(Collectors.toSet());
    return new ResponseEntity<>(deptService.buildTree(new ArrayList<>(deptSet)), HttpStatus.OK);
  }

  @Log(value = "\u65b0\u589e\u90e8\u95e8") @ApiOperation(value = "\u65b0\u589e\u90e8\u95e8") @PostMapping @PreAuthorize(value = "@el.check(\'dept:add\')") public ResponseEntity<Object> createDept(@Validated @RequestBody Dept resources) {
    if (resources.getId() != null) {
      throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
    }
    deptService.create(resources);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Log(value = "\u4fee\u6539\u90e8\u95e8") @ApiOperation(value = "\u4fee\u6539\u90e8\u95e8") @PutMapping @PreAuthorize(value = "@el.check(\'dept:edit\')") public ResponseEntity<Object> updateDept(@Validated(value = Dept.Update.class) @RequestBody Dept resources) {
    if (resources.getId() <= 11) {
      throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
    }
    deptService.update(resources);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Log(value = "\u5220\u9664\u90e8\u95e8") @ApiOperation(value = "\u5220\u9664\u90e8\u95e8") @DeleteMapping @PreAuthorize(value = "@el.check(\'dept:del\')") public ResponseEntity<Object> deleteDept(@RequestBody Set<Long> ids) {
    Set<DeptDto> deptDtos = new HashSet<>();
    for (Long id : ids) {
      if (id <= 11) {
        throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
      }
      List<Dept> deptList = deptService.findByPid(id);
      deptDtos.add(deptService.findById(id));
      if (CollectionUtil.isNotEmpty(deptList)) {
        deptDtos = deptService.getDeleteDepts(deptList, deptDtos);
      }
    }
    deptService.verification(deptDtos);
    deptService.delete(deptDtos);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}