package com.roncoo.pay.permission.service.impl;
import com.roncoo.pay.common.core.enums.PublicStatusEnum;
import com.roncoo.pay.common.core.page.PageBean;
import com.roncoo.pay.common.core.page.PageParam;
import com.roncoo.pay.permission.dao.PmsOperatorDao;
import com.roncoo.pay.permission.dao.PmsOperatorRoleDao;
import com.roncoo.pay.permission.entity.PmsOperator;
import com.roncoo.pay.permission.entity.PmsOperatorRole;
import com.roncoo.pay.permission.service.PmsOperatorService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * 操作员service接口实现
 *
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
@Service(value = "pmsOperatorService") public class PmsOperatorServiceImpl implements PmsOperatorService {
  @Autowired private PmsOperatorDao pmsOperatorDao;

  @Autowired private PmsOperatorRoleDao pmsOperatorRoleDao;

  /**
	 * 创建pmsOperator
	 */
  public void saveData(PmsOperator pmsOperator) {
    pmsOperatorDao.insert(pmsOperator);
  }

  /**
	 * 修改pmsOperator
	 */
  public void updateData(PmsOperator pmsOperator) {
    pmsOperatorDao.update(pmsOperator);
  }

  /**
	 * 根据id获取数据pmsOperator
	 * 
	 * @param id
	 * @return
	 */
  public PmsOperator getDataById(Long id) {
    return pmsOperatorDao.getById(id);
  }

  /**
	 * 分页查询pmsOperator
	 * 
	 * @param pageParam
	 * @param ActivityVo
	 *            PmsOperator
	 * @return
	 */
  public PageBean listPage(PageParam pageParam, PmsOperator pmsOperator) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("loginName", pmsOperator.getLoginName());
    paramMap.put("realName", pmsOperator.getRealName());
    paramMap.put("status", pmsOperator.getStatus());
    return pmsOperatorDao.listPage(pageParam, paramMap);
  }

  /**
	 * 根据ID删除一个操作员，同时删除与该操作员关联的角色关联信息. type="1"的超级管理员不能删除.
	 * 
	 * @param id
	 *            操作员ID.
	 */
  public void deleteOperatorById(Long operatorId) {
    PmsOperator pmsOperator = pmsOperatorDao.getById(operatorId);
    if (pmsOperator != null) {
      if ("admin".equals(pmsOperator.getType())) {
        throw new RuntimeException("\u3010" + pmsOperator.getLoginName() + "\u3011\u4e3a\u8d85\u7ea7\u7ba1\u7406\u5458\uff0c\u4e0d\u80fd\u5220\u9664\uff01");
      }
      pmsOperatorDao.delete(operatorId);
      pmsOperatorRoleDao.deleteByOperatorId(operatorId);
    }
  }

  /**
	 * 更新操作员信息.
	 * 
	 * @param operator
	 */
  public void update(PmsOperator operator) {
    pmsOperatorDao.update(operator);
  }

  /**
	 * 根据操作员ID更新操作员密码.
	 * 
	 * @param operatorId
	 * @param newPwd
	 *            (已进行SHA1加密)
	 */
  public void updateOperatorPwd(Long operatorId, String newPwd) {
    PmsOperator pmsOperator = pmsOperatorDao.getById(operatorId);
    pmsOperator.setLoginPwd(newPwd);
    pmsOperatorDao.update(pmsOperator);
  }

  /**
	 * 根据登录名取得操作员对象
	 */
  public PmsOperator findOperatorByLoginName(String loginName) {
    return pmsOperatorDao.findByLoginName(loginName);
  }

  /**
	 * 保存操作員信息及其关联的角色.
	 * 
	 * @param pmsOperator
	 *            .
	 * @param roleOperatorStr
	 *            .
	 */
  @Transactional public void saveOperator(PmsOperator pmsOperator, String roleOperatorStr) {
    pmsOperatorDao.insert(pmsOperator);
    if (StringUtils.isNotBlank(roleOperatorStr) && roleOperatorStr.length() > 0) {
      saveOrUpdateOperatorRole(pmsOperator, roleOperatorStr);
    }
  }

  /**
	 * 保存用户和角色之间的关联关系
	 */
  private void saveOrUpdateOperatorRole(PmsOperator pmsOperator, String roleIdsStr) {
    List<PmsOperatorRole> listPmsOperatorRoles = pmsOperatorRoleDao.listByOperatorId(pmsOperator.getId());
    Map<Long, PmsOperatorRole> delMap = new HashMap<Long, PmsOperatorRole>();
    for (PmsOperatorRole pmsOperatorRole : listPmsOperatorRoles) {
      delMap.put(pmsOperatorRole.getRoleId(), pmsOperatorRole);
    }
    if (StringUtils.isNotBlank(roleIdsStr)) {
      String[] roleIds = roleIdsStr.split(",");
      for (int i = 0; i < roleIds.length; i++) {
        long roleId = Long.parseLong(roleIds[i]);
        if (delMap.get(roleId) == null) {
          PmsOperatorRole pmsOperatorRole = new PmsOperatorRole();
          pmsOperatorRole.setOperatorId(pmsOperator.getId());
          pmsOperatorRole.setRoleId(roleId);
          pmsOperatorRole.setCreater(pmsOperator.getCreater());
          pmsOperatorRole.setCreateTime(new Date());
          pmsOperatorRole.setStatus(PublicStatusEnum.ACTIVE.name());
          pmsOperatorRoleDao.insert(pmsOperatorRole);
        } else {
          delMap.remove(roleId);
        }
      }
    }
    Iterator<Long> iterator = delMap.keySet().iterator();
    while (iterator.hasNext()) {
      long roleId = iterator.next();
      pmsOperatorRoleDao.deleteByRoleIdAndOperatorId(roleId, pmsOperator.getId());
    }
  }

  /**
	 * 修改操作員信息及其关联的角色.
	 * 
	 * @param pmsOperator
	 *            .
	 * @param roleOperatorStr
	 *            .
	 */
  public void updateOperator(PmsOperator pmsOperator, String roleOperatorStr) {
    pmsOperatorDao.update(pmsOperator);
    this.saveOrUpdateOperatorRole(pmsOperator, roleOperatorStr);
  }
}