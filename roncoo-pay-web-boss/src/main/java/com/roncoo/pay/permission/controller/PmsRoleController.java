package com.roncoo.pay.permission.controller;
import com.roncoo.pay.common.core.dwz.DwzAjax;
import com.roncoo.pay.common.core.page.PageBean;
import com.roncoo.pay.common.core.page.PageParam;
import com.roncoo.pay.controller.common.BaseController;
import com.roncoo.pay.permission.entity.PmsOperator;
import com.roncoo.pay.permission.entity.PmsPermission;
import com.roncoo.pay.permission.entity.PmsRole;
import com.roncoo.pay.permission.enums.OperatorTypeEnum;
import com.roncoo.pay.permission.service.*;
import com.roncoo.pay.permission.utils.ValidateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 权限管理模块角色管理、.<br/>
 * <p>
 * 龙果学院：www.roncoo.com
 *
 * @author：shenjialong
 */
@Controller @RequestMapping(value = "/pms/role") public class PmsRoleController extends BaseController {
  @Autowired private PmsRoleService pmsRoleService;

  @Autowired private PmsMenuService pmsMenuService;

  @Autowired private PmsMenuRoleService pmsMenuRoleService;

  @Autowired private PmsPermissionService pmsPermissionService;

  @Autowired private PmsRolePermissionService pmsRolePermissionService;

  @Autowired private PmsOperatorRoleService pmsOperatorRoleService;

  private static Log log = LogFactory.getLog(PmsRoleController.class);

  /**
     * 获取角色列表
     *
     * @return listPmsRole or operateError .
     */
  @RequiresPermissions(value = "pms:role:view") @RequestMapping(value = "/list") public String listPmsRole(HttpServletRequest req, PageParam pageParam, PmsRole pmsRole, Model model) {
    try {
      PageBean pageBean = pmsRoleService.listPage(pageParam, pmsRole);
      model.addAttribute(pageBean);
      model.addAttribute("pageParam", pageParam);
      model.addAttribute("pmsRole", pmsRole);
      return "pms/pmsRoleList";
    } catch (Exception e) {
      log.error("== listPmsRole exception:", e);
      return operateError("\u83b7\u53d6\u6570\u636e\u5931\u8d25", model);
    }
  }

  /**
     * 转到添加角色页面 .
     *
     * @return addPmsRoleUI or operateError .
     */
  @RequiresPermissions(value = "pms:role:add") @RequestMapping(value = "/addUI") public String addPmsRoleUI(HttpServletRequest req, Model model) {
    try {
      return "pms/pmsRoleAdd";
    } catch (Exception e) {
      log.error("== addPmsRoleUI get data exception:", e);
      return operateError("\u83b7\u53d6\u6570\u636e\u5931\u8d25", model);
    }
  }

  /**
     * 保存新添加的一个角色 .
     *
     * @return operateSuccess or operateError .
     */
  @RequiresPermissions(value = "pms:role:add") @RequestMapping(value = "/add") public String addPmsRole(HttpServletRequest req, Model model, @RequestParam(value = "roleCode") String roleCode, @RequestParam(value = "roleName") String roleName, @RequestParam(value = "remark") String remark, DwzAjax dwz) {
    try {
      PmsRole roleNameCheck = pmsRoleService.getByRoleNameOrRoleCode(roleName, null);
      if (roleNameCheck != null) {
        return operateError("\u89d2\u8272\u540d\u3010" + roleName + "\u3011\u5df2\u5b58\u5728", model);
      }
      PmsRole roleCodeCheck = pmsRoleService.getByRoleNameOrRoleCode(null, roleCode);
      if (roleCodeCheck != null) {
        return operateError("\u89d2\u8272\u7f16\u7801\u3010" + roleCode + "\u3011\u5df2\u5b58\u5728", model);
      }
      PmsRole pmsRole = new PmsRole();
      pmsRole.setRoleCode(roleCode);
      pmsRole.setRoleName(roleName);
      pmsRole.setRemark(remark);
      pmsRole.setCreateTime(new Date());
      String validateMsg = validatePmsRole(pmsRole);
      if (StringUtils.isNotBlank(validateMsg)) {
        return operateError(validateMsg, model);
      }
      pmsRoleService.saveData(pmsRole);
      return operateSuccess(model, dwz);
    } catch (Exception e) {
      log.error("== addPmsRole exception:", e);
      return operateError("\u4fdd\u5b58\u6570\u636e\u5931\u8d25", model);
    }
  }

  /**
     * 校验角色表单数据.
     *
     * @param pmsRole 角色信息.
     * @return msg .
     */
  private String validatePmsRole(PmsRole pmsRole) {
    String msg = "";
    String roleName = pmsRole.getRoleName();
    String desc = pmsRole.getRemark();
    msg += ValidateUtils.lengthValidate("\u89d2\u8272\u540d\u79f0", roleName, true, 3, 90);
    msg += ValidateUtils.lengthValidate("\u63cf\u8ff0", desc, true, 3, 300);
    return msg;
  }

  /**
     * 转到角色修改页面 .
     *
     * @return editPmsRoleUI or operateError .
     */
  @RequiresPermissions(value = "pms:role:edit") @RequestMapping(value = "/editUI") public String editPmsRoleUI(HttpServletRequest req, Model model, Long roleId) {
    try {
      PmsRole pmsRole = pmsRoleService.getDataById(roleId);
      if (pmsRole == null) {
        return operateError("\u83b7\u53d6\u6570\u636e\u5931\u8d25", model);
      }
      model.addAttribute(pmsRole);
      return "/pms/pmsRoleEdit";
    } catch (Exception e) {
      log.error("== editPmsRoleUI exception:", e);
      return operateError("\u83b7\u53d6\u6570\u636e\u5931\u8d25", model);
    }
  }

  /**
     * 保存修改后的角色信息 .
     *
     * @return operateSuccess or operateError .
     */
  @RequiresPermissions(value = "pms:role:edit") @RequestMapping(value = "/edit") public String editPmsRole(HttpServletRequest req, Model model, PmsRole role, DwzAjax dwz) {
    try {
      Long id = role.getId();
      PmsRole pmsRole = pmsRoleService.getDataById(id);
      if (pmsRole == null) {
        return operateError("\u65e0\u6cd5\u83b7\u53d6\u8981\u4fee\u6539\u7684\u6570\u636e", model);
      }
      PmsRole roleNameCheck = pmsRoleService.getByRoleNameOrRoleCode(role.getRoleName(), null);
      if (roleNameCheck != null && !roleNameCheck.getId().equals(id)) {
        return operateError("\u89d2\u8272\u540d\u3010" + role.getRoleName() + "\u3011\u5df2\u5b58\u5728", model);
      }
      PmsRole roleCodeCheck = pmsRoleService.getByRoleNameOrRoleCode(null, role.getRoleCode());
      if (roleCodeCheck != null && !roleCodeCheck.getId().equals(id)) {
        return operateError("\u89d2\u8272\u7f16\u7801\u3010" + role.getRoleCode() + "\u3011\u5df2\u5b58\u5728", model);
      }
      pmsRole.setRoleName(role.getRoleName());
      pmsRole.setRoleCode(role.getRoleCode());
      pmsRole.setRemark(role.getRemark());
      String validateMsg = validatePmsRole(pmsRole);
      if (StringUtils.isNotBlank(validateMsg)) {
        return operateError(validateMsg, model);
      }
      pmsRoleService.updateData(pmsRole);
      return operateSuccess(model, dwz);
    } catch (Exception e) {
      log.error("== editPmsRole exception:", e);
      return operateError("\u4fdd\u5b58\u5931\u8d25", model);
    }
  }

  /**
     * 删除一个角色
     *
     * @return operateSuccess or operateError .
     */
  @RequiresPermissions(value = "pms:role:delete") @RequestMapping(value = "/delete") public String deletePmsRole(HttpServletRequest req, Model model, Long roleId, DwzAjax dwz) {
    try {
      PmsRole role = pmsRoleService.getDataById(roleId);
      if (role == null) {
        return operateError("\u65e0\u6cd5\u83b7\u53d6\u8981\u5220\u9664\u7684\u89d2\u8272", model);
      }
      String msg = "";
      int operatorCount = pmsOperatorRoleService.countOperatorByRoleId(roleId);
      if (operatorCount > 0) {
        msg += "\u6709\u3010" + operatorCount + "\u3011\u4e2a\u64cd\u4f5c\u5458\u5173\u8054\u5230\u6b64\u89d2\u8272\uff0c\u8981\u5148\u89e3\u9664\u6240\u6709\u5173\u8054\u540e\u624d\u80fd\u5220\u9664!";
        return operateError(msg, model);
      }
      pmsRoleService.delete(roleId);
      return operateSuccess(model, dwz);
    } catch (Exception e) {
      log.error("== deletePmsRole exception:", e);
      return operateError("\u5220\u9664\u5931\u8d25", model);
    }
  }

  /**
     * 分配权限UI
     *
     * @return
     */
  @SuppressWarnings(value = { "unchecked" }) @RequiresPermissions(value = "pms:role:assignpermission") @RequestMapping(value = "/assignPermissionUI") public String assignPermissionUI(HttpServletRequest req, Model model, Long roleId) {
    PmsRole role = pmsRoleService.getDataById(roleId);
    if (role == null) {
      return operateError("\u65e0\u6cd5\u83b7\u53d6\u89d2\u8272\u4fe1\u606f", model);
    }
    if (OperatorTypeEnum.USER.name().equals(this.getPmsOperator().getType()) && "admin".equals(role.getRoleName())) {
      return operateError("\u6743\u9650\u4e0d\u8db3", model);
    }
    String permissionIds = pmsPermissionService.getPermissionIdsByRoleId(roleId);
    List<PmsPermission> permissionList = pmsPermissionService.listAll();
    List<PmsOperator> operatorList = pmsOperatorRoleService.listOperatorByRoleId(roleId);
    model.addAttribute("permissionIds", permissionIds);
    model.addAttribute("permissionList", permissionList);
    model.addAttribute("operatorList", operatorList);
    model.addAttribute("role", role);
    return "/pms/assignPermissionUI";
  }

  /**
     * 分配角色权限
     */
  @RequiresPermissions(value = "pms:role:assignpermission") @RequestMapping(value = "/assignPermission") public String assignPermission(HttpServletRequest req, Model model, @RequestParam(value = "roleId") Long roleId, DwzAjax dwz, @RequestParam(value = "selectVal") String selectVal) {
    try {
      String rolePermissionStr = getRolePermissionStr(selectVal);
      pmsRolePermissionService.saveRolePermission(roleId, rolePermissionStr);
      return operateSuccess(model, dwz);
    } catch (Exception e) {
      log.error("== assignPermission exception:", e);
      return operateError("\u4fdd\u5b58\u5931\u8d25", model);
    }
  }

  /**
     * 分配菜单UI
     *
     * @return
     */
  @SuppressWarnings(value = { "unchecked" }) @RequestMapping(value = "/assignMenuUI") public String assignMenuUI(HttpServletRequest req, Model model, Long roleId) {
    PmsRole role = pmsRoleService.getDataById(roleId);
    if (role == null) {
      return operateError("\u65e0\u6cd5\u83b7\u53d6\u89d2\u8272\u4fe1\u606f", model);
    }
    if (OperatorTypeEnum.USER.name().equals(this.getPmsOperator().getType()) && "admin".equals(role.getRoleName())) {
      return operateError("\u6743\u9650\u4e0d\u8db3", model);
    }
    String menuIds = pmsMenuService.getMenuIdsByRoleId(roleId);
    List menuList = pmsMenuService.getListByParent(null);
    List<PmsOperator> operatorList = pmsOperatorRoleService.listOperatorByRoleId(roleId);
    model.addAttribute("menuIds", menuIds);
    model.addAttribute("menuList", menuList);
    model.addAttribute("operatorList", operatorList);
    model.addAttribute("role", role);
    return "/pms/assignMenuUI";
  }

  /**
     * 分配角色菜单
     */
  @RequestMapping(value = "/assignMenu") public String assignMenu(HttpServletRequest req, Model model, @RequestParam(value = "roleId") Long roleId, DwzAjax dwz, @RequestParam(value = "selectVal") String selectVal) {
    try {
      String roleMenuStr = getRolePermissionStr(selectVal);
      pmsMenuRoleService.saveRoleMenu(roleId, roleMenuStr);
      return operateSuccess(model, dwz);
    } catch (Exception e) {
      log.error("== assignPermission exception:", e);
      return operateError("\u4fdd\u5b58\u5931\u8d25", model);
    }
  }

  /**
     * 得到角色和权限关联的ID字符串
     *
     * @return
     */
  private String getRolePermissionStr(String selectVal) throws Exception {
    String roleStr = selectVal;
    if (StringUtils.isNotBlank(roleStr) && roleStr.length() > 0) {
      roleStr = roleStr.substring(0, roleStr.length() - 1);
    }
    return roleStr;
  }
}