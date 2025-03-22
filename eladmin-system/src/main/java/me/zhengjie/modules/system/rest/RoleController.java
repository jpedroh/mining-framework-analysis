package me.zhengjie.modules.system.rest;
import cn.hutool.core.lang.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.system.domain.Role;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.system.service.RoleService;
import me.zhengjie.modules.system.service.dto.RoleDto;
import me.zhengjie.modules.system.service.dto.RoleQueryCriteria;
import me.zhengjie.modules.system.service.dto.RoleSmallDto;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Zheng Jie
 * @date 2018-12-03
 */
@RestController @RequiredArgsConstructor @Api(tags = "\u7cfb\u7edf\uff1a\u89d2\u8272\u7ba1\u7406") @RequestMapping(value = "/api/roles") public class RoleController {
  private final RoleService roleService;

  private static final String ENTITY_NAME = "role";

  @ApiOperation(value = "\u83b7\u53d6\u5355\u4e2arole") @GetMapping(value = "/{id}") @PreAuthorize(value = "@el.check(\'roles:list\')") public ResponseEntity<RoleDto> findRoleById(@PathVariable Long id) {
    return new ResponseEntity<>(roleService.findById(id), HttpStatus.OK);
  }

  @ApiOperation(value = "\u5bfc\u51fa\u89d2\u8272\u6570\u636e") @GetMapping(value = "/download") @PreAuthorize(value = "@el.check(\'role:list\')") public void exportRole(HttpServletResponse response, RoleQueryCriteria criteria) throws IOException {
    roleService.download(roleService.queryAll(criteria), response);
  }

  @ApiOperation(value = "\u8fd4\u56de\u5168\u90e8\u7684\u89d2\u8272") @GetMapping(value = "/all") @PreAuthorize(value = "@el.check(\'roles:list\',\'user:add\',\'user:edit\')") public ResponseEntity<List<RoleDto>> queryAllRole() {
    return new ResponseEntity<>(roleService.queryAll(), HttpStatus.OK);
  }

  @ApiOperation(value = "\u67e5\u8be2\u89d2\u8272") @GetMapping @PreAuthorize(value = "@el.check(\'roles:list\')") public ResponseEntity<PageResult<RoleDto>> queryRole(RoleQueryCriteria criteria, Pageable pageable) {
    return new ResponseEntity<>(roleService.queryAll(criteria, pageable), HttpStatus.OK);
  }

  @ApiOperation(value = "\u83b7\u53d6\u7528\u6237\u7ea7\u522b") @GetMapping(value = "/level") public ResponseEntity<Object> getRoleLevel() {
    return new ResponseEntity<>(Dict.create().set("level", getLevels(null)), HttpStatus.OK);
  }

  @Log(value = "\u65b0\u589e\u89d2\u8272") @ApiOperation(value = "\u65b0\u589e\u89d2\u8272") @PostMapping @PreAuthorize(value = "@el.check(\'roles:add\')") public ResponseEntity<Object> createRole(@Validated @RequestBody Role resources) {
    if (resources.getId() != null) {
      throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
    }
    getLevels(resources.getLevel());
    roleService.create(resources);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Log(value = "\u4fee\u6539\u89d2\u8272") @ApiOperation(value = "\u4fee\u6539\u89d2\u8272") @PutMapping @PreAuthorize(value = "@el.check(\'roles:edit\')") public ResponseEntity<Object> updateRole(@Validated(value = Role.Update.class) @RequestBody Role resources) {
    if (resources.getId() <= 1) {
      throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
    }
    getLevels(resources.getLevel());
    roleService.update(resources);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Log(value = "\u4fee\u6539\u89d2\u8272\u83dc\u5355") @ApiOperation(value = "\u4fee\u6539\u89d2\u8272\u83dc\u5355") @PutMapping(value = "/menu") @PreAuthorize(value = "@el.check(\'roles:edit\')") public ResponseEntity<Object> updateRoleMenu(@RequestBody Role resources) {
    if (resources.getId() <= 1) {
      throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
    }
    RoleDto role = roleService.findById(resources.getId());
    getLevels(role.getLevel());
    roleService.updateMenu(resources, role);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Log(value = "\u5220\u9664\u89d2\u8272") @ApiOperation(value = "\u5220\u9664\u89d2\u8272") @DeleteMapping @PreAuthorize(value = "@el.check(\'roles:del\')") public ResponseEntity<Object> deleteRole(@RequestBody Set<Long> ids) {
    for (Long id : ids) {
      if (id <= 1) {
        throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
      }
      RoleDto role = roleService.findById(id);
      getLevels(role.getLevel());
    }
    roleService.verification(ids);
    roleService.delete(ids);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
     * 获取用户的角色级别
     * @return /
     */
  private int getLevels(Integer level) {
    List<Integer> levels = roleService.findByUsersId(SecurityUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList());
    int min = Collections.min(levels);
    if (level != null) {
      if (level < min) {
        throw new BadRequestException("\u6743\u9650\u4e0d\u8db3\uff0c\u4f60\u7684\u89d2\u8272\u7ea7\u522b\uff1a" + min + "\uff0c\u4f4e\u4e8e\u64cd\u4f5c\u7684\u89d2\u8272\u7ea7\u522b\uff1a" + level);
      }
    }
    return min;
  }
}