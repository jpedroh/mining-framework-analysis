package me.zhengjie.modules.system.service.impl;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.system.domain.Dept;
import me.zhengjie.modules.system.service.DataService;
import me.zhengjie.modules.system.service.DeptService;
import me.zhengjie.modules.system.service.RoleService;
import me.zhengjie.modules.system.service.dto.RoleSmallDto;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.utils.enums.DataScopeEnum;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @author Zheng Jie
 * @website https://el-admin.vip
 * @description 数据权限服务实现
 * @date 2020-05-07
 **/
@Service @RequiredArgsConstructor @CacheConfig(cacheNames = "data") public class DataServiceImpl implements DataService {
  private final RoleService roleService;

  private final DeptService deptService;

  /**
     * 用户角色和用户部门改变时需清理缓存
     * @param user /
     * @return /
     */
  @Override @Cacheable(key = "\'user:\' + #p0.id") public List<Long> getDeptIds(UserDto user) {
    Set<Long> deptIds = new HashSet<>();
    List<RoleSmallDto> roleSet = roleService.findByUsersId(user.getId());
    for (RoleSmallDto role : roleSet) {
      DataScopeEnum dataScopeEnum = DataScopeEnum.find(role.getDataScope());
      switch (Objects.requireNonNull(dataScopeEnum)) {
        case THIS_LEVEL:
        deptIds.add(user.getDept().getId());
        break;
        case THIS_LEVEL_AND_SUB:
        deptIds.add(user.getDept().getId());
        List<Dept> deptChildren = deptService.findByPid(user.getDept().getId());
        if (deptChildren != null && deptChildren.size() != 0) {
          deptIds.addAll(deptService.getDeptChildren(deptChildren));
        }
        break;
        case CUSTOMIZE:
        deptIds.addAll(getCustomize(deptIds, role));
        break;
        default:
        return new ArrayList<>(deptIds);
      }
    }
    return new ArrayList<>(deptIds);
  }

  /**
     * 获取自定义的数据权限
     * @param deptIds 部门ID
     * @param role 角色
     * @return 数据权限ID
     */
  public Set<Long> getCustomize(Set<Long> deptIds, RoleSmallDto role) {
    Set<Dept> depts = deptService.findByRoleId(role.getId());
    for (Dept dept : depts) {
      deptIds.add(dept.getId());
      List<Dept> deptChildren = deptService.findByPid(dept.getId());
      if (deptChildren != null && deptChildren.size() != 0) {
        deptIds.addAll(deptService.getDeptChildren(deptChildren));
      }
    }
    return deptIds;
  }
}