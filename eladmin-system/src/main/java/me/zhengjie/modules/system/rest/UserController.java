package me.zhengjie.modules.system.rest;
import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.utils.PageResult;
import me.zhengjie.config.RsaProperties;
import me.zhengjie.modules.system.domain.Dept;
import me.zhengjie.modules.system.service.DataService;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.system.domain.vo.UserPassVo;
import me.zhengjie.modules.system.service.DeptService;
import me.zhengjie.modules.system.service.RoleService;
import me.zhengjie.modules.system.service.dto.RoleSmallDto;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.modules.system.service.dto.UserQueryCriteria;
import me.zhengjie.modules.system.service.VerifyService;
import me.zhengjie.utils.*;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.utils.enums.CodeEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Api(tags = "\u7cfb\u7edf\uff1a\u7528\u6237\u7ba1\u7406") @RestController @RequestMapping(value = "/api/users") @RequiredArgsConstructor public class UserController {
  private final PasswordEncoder passwordEncoder;

  private final UserService userService;

  private final DataService dataService;

  private final DeptService deptService;

  private final RoleService roleService;

  private final VerifyService verificationCodeService;

  @ApiOperation(value = "\u5bfc\u51fa\u7528\u6237\u6570\u636e") @GetMapping(value = "/download") @PreAuthorize(value = "@el.check(\'user:list\')") public void exportUser(HttpServletResponse response, UserQueryCriteria criteria) throws IOException {
    userService.download(userService.queryAll(criteria), response);
  }

  @ApiOperation(value = "\u67e5\u8be2\u7528\u6237") @GetMapping @PreAuthorize(value = "@el.check(\'user:list\')") public ResponseEntity<PageResult<UserDto>> queryUser(UserQueryCriteria criteria, Pageable pageable) {
    if (!ObjectUtils.isEmpty(criteria.getDeptId())) {
      criteria.getDeptIds().add(criteria.getDeptId());
      List<Dept> data = deptService.findByPid(criteria.getDeptId());
      criteria.getDeptIds().addAll(deptService.getDeptChildren(data));
    }
    List<Long> dataScopes = dataService.getDeptIds(userService.findByName(SecurityUtils.getCurrentUsername()));
    if (!CollectionUtils.isEmpty(criteria.getDeptIds()) && !CollectionUtils.isEmpty(dataScopes)) {
      criteria.getDeptIds().retainAll(dataScopes);
      if (!CollectionUtil.isEmpty(criteria.getDeptIds())) {
        return new ResponseEntity<>(userService.queryAll(criteria, pageable), HttpStatus.OK);
      }
    } else {
      criteria.getDeptIds().addAll(dataScopes);
      return new ResponseEntity<>(userService.queryAll(criteria, pageable), HttpStatus.OK);
    }
    return new ResponseEntity<>(PageUtil.noData(), HttpStatus.OK);
  }

  @Log(value = "\u65b0\u589e\u7528\u6237") @ApiOperation(value = "\u65b0\u589e\u7528\u6237") @PostMapping @PreAuthorize(value = "@el.check(\'user:add\')") public ResponseEntity<Object> createUser(@Validated @RequestBody User resources) {
    checkLevel(resources);
    resources.setPassword(passwordEncoder.encode("123456"));
    userService.create(resources);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Log(value = "\u4fee\u6539\u7528\u6237") @ApiOperation(value = "\u4fee\u6539\u7528\u6237") @PutMapping @PreAuthorize(value = "@el.check(\'user:edit\')") public ResponseEntity<Object> updateUser(@Validated(value = User.Update.class) @RequestBody User resources) throws Exception {
    if (resources.getId() <= 1) {
      throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
    }
    checkLevel(resources);
    userService.update(resources);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Log(value = "\u4fee\u6539\u7528\u6237\uff1a\u4e2a\u4eba\u4e2d\u5fc3") @ApiOperation(value = "\u4fee\u6539\u7528\u6237\uff1a\u4e2a\u4eba\u4e2d\u5fc3") @PutMapping(value = "center") public ResponseEntity<Object> centerUser(@Validated(value = User.Update.class) @RequestBody User resources) {
    if (!resources.getId().equals(SecurityUtils.getCurrentUserId())) {
      throw new BadRequestException("\u4e0d\u80fd\u4fee\u6539\u4ed6\u4eba\u8d44\u6599");
    }
    if (!resources.getId().equals(SecurityUtils.getCurrentUserId())) {
      throw new BadRequestException("\u4e0d\u80fd\u4fee\u6539\u4ed6\u4eba\u8d44\u6599");
    }
    userService.updateCenter(resources);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Log(value = "\u5220\u9664\u7528\u6237") @ApiOperation(value = "\u5220\u9664\u7528\u6237") @DeleteMapping @PreAuthorize(value = "@el.check(\'user:del\')") public ResponseEntity<Object> deleteUser(@RequestBody Set<Long> ids) {
    for (Long id : ids) {
      if (id <= 1) {
        throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
      }
      Integer currentLevel = Collections.min(roleService.findByUsersId(SecurityUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
      Integer optLevel = Collections.min(roleService.findByUsersId(id).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
      if (currentLevel > optLevel) {
        throw new BadRequestException("\u89d2\u8272\u6743\u9650\u4e0d\u8db3\uff0c\u4e0d\u80fd\u5220\u9664\uff1a" + userService.findById(id).getUsername());
      }
    }
    userService.delete(ids);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "\u4fee\u6539\u5bc6\u7801") @PostMapping(value = "/updatePass") public ResponseEntity<Object> updateUserPass(@RequestBody UserPassVo passVo) throws Exception {
    String oldPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, passVo.getOldPass());
    String newPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, passVo.getNewPass());
    UserDto user = userService.findByName(SecurityUtils.getCurrentUsername());
    if ("admin".equals(user.getUsername())) {
      throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
    }
    if (!passwordEncoder.matches(oldPass, user.getPassword())) {
      throw new BadRequestException("\u4fee\u6539\u5931\u8d25\uff0c\u65e7\u5bc6\u7801\u9519\u8bef");
    }
    if (passwordEncoder.matches(newPass, user.getPassword())) {
      throw new BadRequestException("\u65b0\u5bc6\u7801\u4e0d\u80fd\u4e0e\u65e7\u5bc6\u7801\u76f8\u540c");
    }
    userService.updatePass(user.getUsername(), passwordEncoder.encode(newPass));
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "\u4fee\u6539\u5934\u50cf") @PostMapping(value = "/updateAvatar") public ResponseEntity<Object> updateUserAvatar(@RequestParam MultipartFile avatar) {
    throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
  }

  @Log(value = "\u4fee\u6539\u90ae\u7bb1") @ApiOperation(value = "\u4fee\u6539\u90ae\u7bb1") @PostMapping(value = "/updateEmail/{code}") public ResponseEntity<Object> updateUserEmail(@PathVariable String code, @RequestBody User user) throws Exception {
    String password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, user.getPassword());
    UserDto userDto = userService.findByName(SecurityUtils.getCurrentUsername());
    if (!passwordEncoder.matches(password, userDto.getPassword())) {
      throw new BadRequestException("\u5bc6\u7801\u9519\u8bef");
    }
    verificationCodeService.validated(CodeEnum.EMAIL_RESET_EMAIL_CODE.getKey() + user.getEmail(), code);
    userService.updateEmail(userDto.getUsername(), user.getEmail());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
     * 如果当前用户的角色级别低于创建用户的角色级别，则抛出权限不足的错误
     * @param resources /
     */
  private void checkLevel(User resources) {
    Integer currentLevel = Collections.min(roleService.findByUsersId(SecurityUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
    Integer optLevel = roleService.findByRoles(resources.getRoles());
    if (currentLevel > optLevel) {
      throw new BadRequestException("\u89d2\u8272\u6743\u9650\u4e0d\u8db3");
    }
  }
}