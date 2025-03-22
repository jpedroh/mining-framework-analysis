package com.roncoo.pay.permission.service.impl;
import java.util.List;
import com.alibaba.druid.util.StringUtils;
import com.roncoo.pay.permission.dao.PmsMenuRoleDao;
import com.roncoo.pay.permission.entity.PmsMenuRole;
import com.roncoo.pay.permission.service.PmsMenuRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 菜单角色service接口实现
 *
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
@Service(value = "pmsMenuRoleService") public class PmsMenuRoleServiceImpl implements PmsMenuRoleService {
  @Autowired private PmsMenuRoleDao pmsMenuRoleDao;

  /**
	 * 根据角色ID统计关联到此角色的菜单数.
	 * 
	 * @param roleId
	 *            角色ID.
	 * @return count.
	 */
  public int countMenuByRoleId(Long roleId) {
    List<PmsMenuRole> meunList = pmsMenuRoleDao.listByRoleId(roleId);
    if (meunList == null || meunList.isEmpty()) {
      return 0;
    } else {
      return meunList.size();
    }
  }

  /**
	 * 根据角色id，删除该角色关联的所有菜单权限
	 * 
	 * @param roleId
	 */
  public void deleteByRoleId(Long roleId) {
    pmsMenuRoleDao.deleteByRoleId(roleId);
  }

  @Transactional(rollbackFor = Exception.class) public void saveRoleMenu(Long roleId, String roleMenuStr) {
    pmsMenuRoleDao.deleteByRoleId(roleId);
    if (!StringUtils.isEmpty(roleMenuStr)) {
      String[] menuIds = roleMenuStr.split(",");
      for (int i = 0; i < menuIds.length; i++) {
        Long menuId = Long.valueOf(menuIds[i]);
        PmsMenuRole item = new PmsMenuRole();
        item.setMenuId(menuId);
        item.setRoleId(roleId);
        pmsMenuRoleDao.insert(item);
      }
    }
  }
}