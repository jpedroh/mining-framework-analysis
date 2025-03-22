package com.roncoo.pay.permission.service.impl;
import com.roncoo.pay.common.core.page.PageBean;
import com.alibaba.druid.util.StringUtils;
import com.roncoo.pay.common.core.page.PageParam;
import com.roncoo.pay.permission.dao.PmsPermissionDao;
import com.roncoo.pay.permission.dao.PmsRolePermissionDao;
import com.roncoo.pay.permission.entity.PmsPermission;
import com.roncoo.pay.permission.entity.PmsRolePermission;
import com.roncoo.pay.permission.service.PmsRolePermissionService;
import com.roncoo.pay.permission.service.PmsOperatorRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * 角色权限service接口实现
 *
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
@Service(value = "pmsRolePermissionService") public class PmsRolePermissionServiceImpl implements PmsRolePermissionService {
  @Autowired private PmsRolePermissionDao pmsRolePermissionDao;

  @Autowired private PmsPermissionDao pmsPermissionDao;

  @Autowired private PmsOperatorRoleService pmsOperatorRoleService;

  /**
	 * 根据操作员ID，获取所有的功能权限集
	 * 
	 * @param operatorId
	 */
  public Set<String> getPermissionsByOperatorId(Long operatorId) {
    String roleIds = pmsOperatorRoleService.getRoleIdsByOperatorId(operatorId);
    String permissionIds = getActionIdsByRoleIds(roleIds);
    Set<String> permissionSet = new HashSet<String>();
    if (!StringUtils.isEmpty(permissionIds)) {
      List<PmsPermission> permissions = pmsPermissionDao.findByIds(permissionIds);
      for (PmsPermission permission : permissions) {
        permissionSet.add(permission.getPermission());
      }
    }
    return permissionSet;
  }

  /**
	 * 根据角色ID集得到所有权限ID集
	 * 
	 * @param roleIds
	 * @return actionIds
	 */
  private String getActionIdsByRoleIds(String roleIds) {
    List<PmsRolePermission> listRolePermission = pmsRolePermissionDao.listByRoleIds(roleIds);
    StringBuffer actionIdsBuf = new StringBuffer("");
    for (PmsRolePermission pmsRolePermission : listRolePermission) {
      actionIdsBuf.append(pmsRolePermission.getPermissionId()).append(",");
    }
    String actionIds = actionIdsBuf.toString();
    if (StringUtils.isEmpty(actionIds) && actionIds.length() > 0) {
      actionIds = actionIds.substring(0, actionIds.length() - 1);
    }
    return actionIds;
  }

  /**
	 * 创建pmsOperator
	 */
  public void saveData(PmsRolePermission pmsRolePermission) {
    pmsRolePermissionDao.insert(pmsRolePermission);
  }

  /**
	 * 修改pmsOperator
	 */
  public void updateData(PmsRolePermission pmsRolePermission) {
    pmsRolePermissionDao.update(pmsRolePermission);
  }

  /**
	 * 根据id获取数据pmsOperator
	 * 
	 * @param id
	 * @return
	 */
  public PmsRolePermission getDataById(Long id) {
    return pmsRolePermissionDao.getById(id);
  }

  /**
	 * 分页查询pmsOperator
	 * 
	 * @param pageParam
	 * @param ActivityVo
	 *            PmsOperator
	 * @return
	 */
  public PageBean listPage(PageParam pageParam, PmsRolePermission pmsRolePermission) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    return pmsRolePermissionDao.listPage(pageParam, paramMap);
  }

  /**
	 * 保存角色和权限之间的关联关系
	 */
  @Transactional(rollbackFor = Exception.class) public void saveRolePermission(Long roleId, String rolePermissionStr) {
    pmsRolePermissionDao.deleteByRoleId(roleId);
    if (!StringUtils.isEmpty(rolePermissionStr)) {
      String[] permissionIds = rolePermissionStr.split(",");
      for (int i = 0; i < permissionIds.length; i++) {
        Long permissionId = Long.valueOf(permissionIds[i]);
        PmsRolePermission item = new PmsRolePermission();
        item.setPermissionId(permissionId);
        item.setRoleId(roleId);
        pmsRolePermissionDao.insert(item);
      }
    }
  }
}