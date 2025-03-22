package com.roncoo.pay.permission.controller;
import java.util.Date;
import com.roncoo.pay.common.core.dwz.DwzAjax;
import java.util.List;
import com.roncoo.pay.common.core.enums.PublicStatusEnum;
import javax.servlet.http.HttpServletRequest;
import com.roncoo.pay.common.core.page.PageBean;
import com.roncoo.pay.common.core.page.PageParam;
import com.roncoo.pay.controller.common.BaseController;
import com.roncoo.pay.permission.entity.PmsPermission;
import com.roncoo.pay.permission.entity.PmsRole;
import com.roncoo.pay.permission.service.PmsPermissionService;
import com.roncoo.pay.permission.service.PmsRoleService;
import org.apache.commons.lang.StringUtils;
import com.roncoo.pay.permission.utils.ValidateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 权限管理模块的Permission类，包括权限点管理、角色管理、操作员管理.<br/>
 *
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
@Controller @RequestMapping(value = "/pms/permission") public class PmsPermissionController extends BaseController {
  @Autowired private PmsPermissionService pmsPermissionService;

  @Autowired private PmsRoleService pmsRoleService;

  private static Log log = LogFactory.getLog(PmsPermissionController.class);

  /**
	 * 分页列出pms权限，也可根据权限获权限名称进行查询.
	 * 
	 * @return PmsPermissionList or operateError.
	 */
  @RequiresPermissions(value = "pms:permission:view") @RequestMapping(value = "/list") public String listPmsPermission(HttpServletRequest req, PageParam pageParam, PmsPermission pmsPermission, Model model) {
    try {
      PageBean pageBean = pmsPermissionService.listPage(pageParam, pmsPermission);
      model.addAttribute(pageBean);
      model.addAttribute("pageParam", pageParam);
      return "pms/pmsPermissionList";
    } catch (Exception e) {
      log.error("== listPmsPermission exception:", e);
      return operateError("\u83b7\u53d6\u6570\u636e\u5931\u8d25", model);
    }
  }

  /**
	 * 进入添加Pms权限页面 .
	 * 
	 * @return addPmsPermissionUI .
	 */
  @RequiresPermissions(value = "pms:permission:add") @RequestMapping(value = "/addUI") public String addPmsPermissionUI() {
    return "pms/pmsPermissionAdd";
  }

  /**
	 * 将权限信息保存到数据库中
	 * 
	 * @return operateSuccess or operateError.
	 */
  @RequiresPermissions(value = "pms:permission:add") @RequestMapping(value = "/add") public String addPmsPermission(HttpServletRequest req, PmsPermission pmsPermission, Model model, DwzAjax dwz) {
    try {
      String validateMsg = validatePmsPermission(pmsPermission);
      if (StringUtils.isNotBlank(validateMsg)) {
        return operateError(validateMsg, model);
      }
      String permissionName = pmsPermission.getPermissionName().trim();
      String permission = pmsPermission.getPermission();
      PmsPermission checkName = pmsPermissionService.getByPermissionName(permissionName);
      if (checkName != null) {
        return operateError("\u6743\u9650\u540d\u79f0\u3010" + permissionName + "\u3011\u5df2\u5b58\u5728", model);
      }
      PmsPermission checkPermission = pmsPermissionService.getByPermission(permission);
      if (checkPermission != null) {
        return operateError("\u6743\u9650\u3010" + permission + "\u3011\u5df2\u5b58\u5728", model);
      }
      pmsPermission.setStatus(PublicStatusEnum.ACTIVE.name());
      pmsPermission.setCreater(getPmsOperator().getLoginName());
      pmsPermission.setCreateTime(new Date());
      pmsPermissionService.saveData(pmsPermission);
      return operateSuccess(model, dwz);
    } catch (Exception e) {
      log.error("== addPmsPermission exception:", e);
      return operateError("\u4fdd\u5b58\u5931\u8d25", model);
    }
  }

  /**
	 * 校验Pms权限信息.
	 * 
	 * @param pmsPermission
	 *            .
	 * @return msg .
	 */
  private String validatePmsPermission(PmsPermission pmsPermission) {
    String msg = "";
    String permissionName = pmsPermission.getPermissionName();
    String permission = pmsPermission.getPermission();
    String desc = pmsPermission.getRemark();
    msg += ValidateUtils.lengthValidate("\u6743\u9650\u540d\u79f0", permissionName, true, 3, 90);
    msg += ValidateUtils.lengthValidate("\u6743\u9650\u6807\u8bc6", permission, true, 3, 100);
    msg += ValidateUtils.lengthValidate("\u63cf\u8ff0", desc, true, 3, 60);
    return msg;
  }

  /**
	 * 转到权限修改页面 .
	 * 
	 * @return editPmsPermissionUI or operateError .
	 */
  @RequiresPermissions(value = "pms:permission:edit") @RequestMapping(value = "/editUI") public String editPmsPermissionUI(HttpServletRequest req, Long id, Model model) {
    try {
      PmsPermission pmsPermission = pmsPermissionService.getDataById(id);
      model.addAttribute("pmsPermission", pmsPermission);
      return "pms/pmsPermissionEdit";
    } catch (Exception e) {
      log.error("== editPmsPermissionUI exception:", e);
      return operateError("\u83b7\u53d6\u6570\u636e\u5931\u8d25", model);
    }
  }

  /**
	 * 保存修改后的权限信息
	 * 
	 * @return operateSuccess or operateError .
	 */
  @RequiresPermissions(value = "pms:permission:edit") @RequestMapping(value = "/edit") public String editPmsPermission(HttpServletRequest req, PmsPermission permission, Model model, DwzAjax dwz) {
    try {
      Long id = permission.getId();
      PmsPermission pmsPermission = pmsPermissionService.getDataById(id);
      if (pmsPermission == null) {
        return operateError("\u65e0\u6cd5\u83b7\u53d6\u8981\u4fee\u6539\u7684\u6570\u636e", model);
      } else {
        String permissionName = permission.getPermissionName();
        String remark = permission.getRemark();
        pmsPermission.setPermissionName(permissionName);
        pmsPermission.setRemark(remark);
        String validateMsg = validatePmsPermission(pmsPermission);
        if (StringUtils.isNotBlank(validateMsg)) {
          return operateError(validateMsg, model);
        }
        PmsPermission checkName = pmsPermissionService.getByPermissionNameNotEqId(permissionName, id);
        if (checkName != null) {
          return operateError("\u6743\u9650\u540d\u79f0\u3010" + permissionName + "\u3011\u5df2\u5b58\u5728", model);
        }
        pmsPermissionService.updateData(pmsPermission);
        return operateSuccess(model, dwz);
      }
    } catch (Exception e) {
      log.error("== editPmsPermission exception:", e);
      return operateError("\u4fee\u6539\u5931\u8d25", model);
    }
  }

  /**
	 * 删除一条权限记录
	 * 
	 * @return operateSuccess or operateError .
	 */
  @RequiresPermissions(value = "pms:permission:delete") @RequestMapping(value = "/delete") public String deletePmsPermission(HttpServletRequest req, Long permissionId, Model model, DwzAjax dwz) {
    try {
      PmsPermission permission = pmsPermissionService.getDataById(permissionId);
      if (permission == null) {
        return operateError("\u65e0\u6cd5\u83b7\u53d6\u8981\u5220\u9664\u7684\u6570\u636e", model);
      }
      List<PmsRole> roleList = pmsRoleService.listByPermissionId(permissionId);
      if (roleList != null && !roleList.isEmpty()) {
        return operateError("\u6743\u9650\u3010" + permission.getPermission() + "\u3011\u5173\u8054\u4e86\u3010" + roleList.size() + "\u3011\u4e2a\u89d2\u8272\uff0c\u8981\u89e3\u9664\u6240\u6709\u5173\u8054\u540e\u624d\u80fd\u5220\u9664\u3002\u5176\u4e2d\u4e00\u4e2a\u89d2\u8272\u540d\u4e3a:" + roleList.get(0).getRoleName(), model);
      }
      pmsPermissionService.delete(permissionId);
      return operateSuccess(model, dwz);
    } catch (Exception e) {
      log.error("== deletePmsPermission exception:", e);
      return operateError("\u5220\u9664\u9650\u6743\u5f02\u5e38", model);
    }
  }
}