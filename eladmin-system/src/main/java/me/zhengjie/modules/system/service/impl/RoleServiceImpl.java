package me.zhengjie.modules.system.service.impl;
import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.security.service.UserCacheClean;
import me.zhengjie.modules.system.domain.Menu;
import me.zhengjie.modules.system.domain.Role;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.repository.RoleRepository;
import me.zhengjie.modules.system.repository.UserRepository;
import me.zhengjie.modules.system.service.RoleService;
import me.zhengjie.modules.system.service.dto.RoleDto;
import me.zhengjie.modules.system.service.dto.RoleQueryCriteria;
import me.zhengjie.modules.system.service.dto.RoleSmallDto;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.modules.system.service.mapstruct.RoleMapper;
import me.zhengjie.modules.system.service.mapstruct.RoleSmallMapper;
import me.zhengjie.utils.*;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zheng Jie
 * @date 2018-12-03
 */
@Service @RequiredArgsConstructor @CacheConfig(cacheNames = "role") public class RoleServiceImpl implements RoleService {
  private final RoleRepository roleRepository;

  private final RoleMapper roleMapper;

  private final RoleSmallMapper roleSmallMapper;

  private final RedisUtils redisUtils;

  private final UserRepository userRepository;

  private final UserCacheClean userCacheClean;

  @Override public List<RoleDto> queryAll() {
    Sort sort = new Sort(Sort.Direction.ASC, "level");
    return roleMapper.toDto(roleRepository.findAll(sort));
  }

  @Override public List<RoleDto> queryAll(RoleQueryCriteria criteria) {
    return roleMapper.toDto(roleRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
  }

  @Override public Object queryAll(RoleQueryCriteria criteria, Pageable pageable) {
    Page<Role> page = roleRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
    return PageUtil.toPage(page.map(roleMapper::toDto));
  }

  @Override @Cacheable(key = "\'id:\' + #p0") @Transactional(rollbackFor = Exception.class) public RoleDto findById(long id) {
    Role role = roleRepository.findById(id).orElseGet(Role::new);
    ValidationUtil.isNull(role.getId(), "Role", "id", id);
    return roleMapper.toDto(role);
  }

  @Override @Transactional(rollbackFor = Exception.class) public void create(Role resources) {
    if (roleRepository.findByName(resources.getName()) != null) {
      throw new EntityExistException(Role.class, "username", resources.getName());
    }
    roleRepository.save(resources);
  }

  @Override @Transactional(rollbackFor = Exception.class) public void update(Role resources) {
    Role role = roleRepository.findById(resources.getId()).orElseGet(Role::new);
    ValidationUtil.isNull(role.getId(), "Role", "id", resources.getId());
    Role role1 = roleRepository.findByName(resources.getName());
    if (role1 != null && !role1.getId().equals(role.getId())) {
      throw new EntityExistException(Role.class, "username", resources.getName());
    }
    role.setName(resources.getName());
    role.setDescription(resources.getDescription());
    role.setDataScope(resources.getDataScope());
    role.setDepts(resources.getDepts());
    role.setLevel(resources.getLevel());
    roleRepository.save(role);
    delCaches(role.getId());
  }

  @Override public void updateMenu(Role resources, RoleDto roleDTO) {
    Role role = roleMapper.toEntity(roleDTO);
    List<User> users = userRepository.findByRoleId(role.getId());
    role.setMenus(resources.getMenus());
    redisUtils.delByKeys("role::auth:", userIds);
    cleanCache(resources, users);
    roleRepository.save(role);
  }

  @Override @Transactional(rollbackFor = Exception.class) public void untiedMenu(Long menuId) {
    roleRepository.untiedMenu(menuId);
  }

  @Override @Transactional(rollbackFor = Exception.class) public void delete(Set<Long> ids) {
    for (Long id : ids) {
      delCaches(id);
    }
    roleRepository.deleteAllByIdIn(ids);
  }

  @Override public List<RoleSmallDto> findByUsersId(Long id) {
    return roleSmallMapper.toDto(new ArrayList<>(roleRepository.findByUserId(id)));
  }

  @Override public Integer findByRoles(Set<Role> roles) {
    Set<RoleDto> roleDtos = new HashSet<>();
    for (Role role : roles) {
      roleDtos.add(findById(role.getId()));
    }
    return Collections.min(roleDtos.stream().map(RoleDto::getLevel).collect(Collectors.toList()));
  }

  @Override @Cacheable(key = "\'auth:\' + #p0.id") public List<GrantedAuthority> mapToGrantedAuthorities(UserDto user) {
    Set<String> permissions = new HashSet<>();
    if (user.getIsAdmin()) {
      permissions.add("admin");
      return permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
    Set<Role> roles = roleRepository.findByUserId(user.getId());
    permissions = roles.stream().flatMap((role) -> role.getMenus().stream()).filter((menu) -> StringUtils.isNotBlank(menu.getPermission())).map(Menu::getPermission).collect(Collectors.toSet());
    return permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
  }

  @Override public void download(List<RoleDto> roles, HttpServletResponse response) throws IOException {
    List<Map<String, Object>> list = new ArrayList<>();
    for (RoleDto role : roles) {
      Map<String, Object> map = new LinkedHashMap<>();
      map.put("\u89d2\u8272\u540d\u79f0", role.getName());
      map.put("\u89d2\u8272\u7ea7\u522b", role.getLevel());
      map.put("\u63cf\u8ff0", role.getDescription());
      map.put("\u521b\u5efa\u65e5\u671f", role.getCreateTime());
      list.add(map);
    }
    FileUtil.downloadExcel(list, response);
  }

  @Override public void verification(Set<Long> ids) {
    if (userRepository.countByRoles(ids) > 0) {
      throw new BadRequestException("\u6240\u9009\u89d2\u8272\u5b58\u5728\u7528\u6237\u5173\u8054\uff0c\u8bf7\u89e3\u9664\u5173\u8054\u518d\u8bd5\uff01");
    }
  }

  /**
     * 清理缓存
     * @param id /
     */
  public void delCaches(Long id) {
    List<User> users = userRepository.findByRoleId(id);
    if (CollectionUtil.isNotEmpty(users)) {
      users.stream().forEach((item) -> {
        userCacheClean.cleanUserCache(item.getUsername());
      });
      Set<Long> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
      redisUtils.delByKeys(CacheKey.DATE_USER, userIds);
      redisUtils.delByKeys(CacheKey.MENU_USER, userIds);
      redisUtils.delByKeys(CacheKey.ROLE_AUTH, userIds);
    }
  }

  @Override public List<Role> findInMenuId(List<Long> menuIds) {
    return roleRepository.findInMenuId(menuIds);
  }

  /**
     * 清理缓存
     *
     * @param resources
     * @param users
     */
  private void cleanCache(Role resources, List<User> users) {
    if (CollectionUtil.isNotEmpty(users)) {
      users.stream().forEach((item) -> {
        userCacheClean.cleanUserCache(item.getUsername());
      });
      Set<Long> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
      redisUtils.delByKeys(CacheKey.MENU_USER, userIds);
      redisUtils.del(CacheKey.ROLE_ID + resources.getId());
    }
  }
}