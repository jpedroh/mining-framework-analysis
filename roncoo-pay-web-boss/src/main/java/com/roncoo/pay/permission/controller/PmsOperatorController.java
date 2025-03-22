package com.roncoo.pay.permission.controller;
import java.util.Date;
import com.roncoo.pay.common.core.dwz.DwzAjax;
import java.util.List;
import com.roncoo.pay.common.core.enums.PublicStatusEnum;
import java.util.regex.Pattern;
import com.roncoo.pay.common.core.page.PageBean;
import javax.servlet.http.HttpServletRequest;
import com.roncoo.pay.common.core.page.PageParam;
import com.roncoo.pay.controller.common.BaseController;
import com.roncoo.pay.permission.entity.PmsOperator;
import com.roncoo.pay.permission.entity.PmsOperatorRole;
import com.roncoo.pay.permission.enums.OperatorTypeEnum;
import com.roncoo.pay.permission.service.PmsOperatorService;
import com.roncoo.pay.permission.service.PmsOperatorRoleService;
import com.roncoo.pay.permission.service.PmsRoleService;
import com.roncoo.pay.permission.utils.PasswordHelper;
import org.apache.commons.lang.StringUtils;
import com.roncoo.pay.permission.utils.ValidateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 权限管理模块操作员管理
 *
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
@Controller @RequestMapping(value = "/pms/operator") public class PmsOperatorController extends BaseController {
  private static Log log = LogFactory.getLog(PmsOperatorController.class);

  @Autowired private PmsOperatorService pmsOperatorService;

  @Autowired private PmsRoleService pmsRoleService;

  @Autowired private PmsOperatorRoleService pmsOperatorRoleService;

  /**
	 * 分页列出操作员信息，并可按登录名获姓名进行查询.
	 * 
	 * @return listPmsOperator or operateError .
	 * 
	 */
  @RequiresPermissions(value = "pms:operator:view") @RequestMapping(value = "/list") public String listPmsOperator(HttpServletRequest req, PageParam pageParam, PmsOperator operator, Model model) {
    try {
      PageBean pageBean = pmsOperatorService.listPage(pageParam, operator);
      model.addAttribute(pageBean);
      model.addAttribute("OperatorStatusEnum", PublicStatusEnum.toMap());
      model.addAttribute("OperatorTypeEnum", OperatorTypeEnum.toMap());
      return "pms/pmsOperatorList";
    } catch (Exception e) {
      log.error("== listPmsOperator exception:", e);
      return operateError("\u83b7\u53d6\u6570\u636e\u5931\u8d25", model);
    }
  }

  /**
	 * 查看操作员详情.
	 * 
	 * @return .
	 */
  @RequiresPermissions(value = "pms:operator:view") @RequestMapping(value = "/viewUI") public String viewPmsOperatorUI(HttpServletRequest req, Long id, Model model) {
    try {
      PmsOperator pmsOperator = pmsOperatorService.getDataById(id);
      if (pmsOperator == null) {
        return operateError("\u65e0\u6cd5\u83b7\u53d6\u8981\u67e5\u770b\u7684\u6570\u636e", model);
      }
      if (OperatorTypeEnum.USER.name().equals(this.getPmsOperator().getType()) && OperatorTypeEnum.ADMIN.name().equals(pmsOperator.getType())) {
        return operateError("\u6743\u9650\u4e0d\u8db3", model);
      }
      model.addAttribute("rolesList", pmsRoleService.listAllRole());
      List<PmsOperatorRole> lisPmsOperatorRoles = pmsOperatorRoleService.listOperatorRoleByOperatorId(id);
      StringBuffer owenedRoleIdBuffer = new StringBuffer("");
      for (PmsOperatorRole pmsOperatorRole : lisPmsOperatorRoles) {
        owenedRoleIdBuffer.append(pmsOperatorRole.getRoleId());
        owenedRoleIdBuffer.append(",");
      }
      String owenedRoleIds = owenedRoleIdBuffer.toString();
      if (StringUtils.isNotBlank(owenedRoleIds) && owenedRoleIds.length() > 0) {
        owenedRoleIds = owenedRoleIds.substring(0, owenedRoleIds.length() - 1);
      }
      model.addAttribute("pmsOperator", pmsOperator);
      model.addAttribute("owenedRoleIds", owenedRoleIds);
      return "/pms/pmsOperatorView";
    } catch (Exception e) {
      log.error("== viewPmsOperatorUI exception:", e);
      return operateError("\u83b7\u53d6\u6570\u636e\u5931\u8d25", model);
    }
  }

  /**
	 * 转到添加操作员页面 .
	 * 
	 * @return addPmsOperatorUI or operateError .
	 */
  @RequiresPermissions(value = "pms:operator:add") @RequestMapping(value = "/addUI") public String addPmsOperatorUI(HttpServletRequest req, Model model) {
    try {
      model.addAttribute("rolesList", pmsRoleService.listAllRole());
      model.addAttribute("OperatorStatusEnumList", PublicStatusEnum.toList());
      return "/pms/pmsOperatorAdd";
    } catch (Exception e) {
      log.error("== addPmsOperatorUI exception:", e);
      return operateError("\u83b7\u53d6\u89d2\u8272\u5217\u8868\u6570\u636e\u5931\u8d25", model);
    }
  }

  /**
	 * 保存一个操作员
	 * 
	 */
  @RequiresPermissions(value = "pms:operator:add") @RequestMapping(value = "/add") public String addPmsOperator(HttpServletRequest req, PmsOperator pmsOperator, @RequestParam(value = "selectVal") String selectVal, Model model, DwzAjax dwz) {
    try {
      pmsOperator.setType(OperatorTypeEnum.USER.name());
      String roleOperatorStr = getRoleOperatorStr(selectVal);
      String validateMsg = validatePmsOperator(pmsOperator, roleOperatorStr);
      if (StringUtils.isNotBlank(validateMsg)) {
        return operateError(validateMsg, model);
      }
      PmsOperator loginNameCheck = pmsOperatorService.findOperatorByLoginName(pmsOperator.getLoginName());
      if (loginNameCheck != null) {
        return operateError("\u767b\u5f55\u540d\u3010" + pmsOperator.getLoginName() + "\u3011\u5df2\u5b58\u5728", model);
      }
      PasswordHelper.encryptPassword(pmsOperator);
      pmsOperator.setCreater(getPmsOperator().getLoginName());
      pmsOperator.setCreateTime(new Date());
      pmsOperatorService.saveOperator(pmsOperator, roleOperatorStr);
      return operateSuccess(model, dwz);
    } catch (Exception e) {
      log.error("== addPmsOperator exception:", e);
      return operateError("\u4fdd\u5b58\u64cd\u4f5c\u5458\u4fe1\u606f\u5931\u8d25", model);
    }
  }

  /**
	 * 验证输入的邮箱格式是否符合
	 * 
	 * @param email
	 * @return 是否合法
	 */
  public static boolean emailFormat(String email) {
    String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    boolean result = Pattern.matches(check, email);
    return result;
  }

  /**
	 * 验证输入的密码格式是否符合
	 * 
	 * @param loginPwd
	 * @return 是否合法
	 */
  public static boolean loginPwdFormat(String loginPwd) {
    return loginPwd.matches(".*?[^a-zA-Z\\d]+.*?") && loginPwd.matches(".*?[a-zA-Z]+.*?") && loginPwd.matches(".*?[\\d]+.*?");
  }

  /**
	 * 验证输入的操作员姓名格式是否符合
	 * 
	 * @param loginPwd
	 * @return 是否合法
	 */
  public static boolean realNameFormat(String realName) {
    return realName.matches("[^\\x00-\\xff]+");
  }

  /**
	 * 校验Pms操作员表单数据.
	 * 
	 * @param PmsOperator
	 *            操作员信息.
	 * @param roleOperatorStr
	 *            关联的角色ID串.
	 * @return
	 */
  private String validatePmsOperator(PmsOperator operator, String roleOperatorStr) {
    String msg = "";
    msg += ValidateUtils.lengthValidate("\u771f\u5b9e\u59d3\u540d", operator.getRealName(), true, 2, 15);
    msg += ValidateUtils.lengthValidate("\u767b\u5f55\u540d", operator.getLoginName(), true, 3, 50);
    String mobileNo = operator.getMobileNo();
    String mobileNoMsg = ValidateUtils.lengthValidate("\u624b\u673a\u53f7", mobileNo, true, 0, 12);
    if (StringUtils.isBlank(mobileNoMsg) && !ValidateUtils.isMobile(mobileNo)) {
      mobileNoMsg += "\u624b\u673a\u53f7\u683c\u5f0f\u4e0d\u6b63\u786e\uff0c";
    }
    msg += mobileNoMsg;
    String status = operator.getStatus();
    if (status == null) {
      msg += "\u8bf7\u9009\u62e9\u72b6\u6001\uff0c";
    } else {
      if (!PublicStatusEnum.ACTIVE.name().equals(status) || PublicStatusEnum.UNACTIVE.name().equals(status)) {
        msg += "\u72b6\u6001\u503c\u4e0d\u6b63\u786e\uff0c";
      }
    }
    msg += ValidateUtils.lengthValidate("\u63cf\u8ff0", operator.getRemark(), true, 3, 100);
    if (StringUtils.isBlank(roleOperatorStr) && operator.getId() == null) {
      msg += "\u64cd\u4f5c\u5458\u5173\u8054\u7684\u89d2\u8272\u4e0d\u80fd\u4e3a\u7a7a";
    }
    return msg;
  }

  /**
	 * 删除操作员
	 * 
	 * @return
	 * */
  @RequiresPermissions(value = "pms:operator:delete") @RequestMapping(value = "/delete") public String deleteOperatorStatus(HttpServletRequest req, Long id, Model model, DwzAjax dwz) {
    pmsOperatorService.deleteOperatorById(id);
    return this.operateSuccess(model, dwz);
  }

  /**
	 * 转到修改操作员界面
	 * 
	 * @return PmsOperatorEdit or operateError .
	 */
  @RequiresPermissions(value = "pms:operator:edit") @RequestMapping(value = "/editUI") public String editPmsOperatorUI(HttpServletRequest req, Long id, Model model) {
    try {
      PmsOperator pmsOperator = pmsOperatorService.getDataById(id);
      if (pmsOperator == null) {
        return operateError("\u65e0\u6cd5\u83b7\u53d6\u8981\u4fee\u6539\u7684\u6570\u636e", model);
      }
      if (OperatorTypeEnum.USER.name().equals(this.getPmsOperator().getType()) && OperatorTypeEnum.ADMIN.name().equals(pmsOperator.getType())) {
        return operateError("\u6743\u9650\u4e0d\u8db3", model);
      }
      model.addAttribute("rolesList", pmsRoleService.listAllRole());
      List<PmsOperatorRole> lisPmsOperatorRoles = pmsOperatorRoleService.listOperatorRoleByOperatorId(id);
      StringBuffer owenedRoleIdBuffer = new StringBuffer("");
      for (PmsOperatorRole pmsOperatorRole : lisPmsOperatorRoles) {
        owenedRoleIdBuffer.append(pmsOperatorRole.getRoleId());
        owenedRoleIdBuffer.append(",");
      }
      String owenedRoleIds = owenedRoleIdBuffer.toString();
      if (StringUtils.isNotBlank(owenedRoleIds) && owenedRoleIds.length() > 0) {
        owenedRoleIds = owenedRoleIds.substring(0, owenedRoleIds.length() - 1);
      }
      model.addAttribute("owenedRoleIds", owenedRoleIds);
      model.addAttribute("OperatorStatusEnum", PublicStatusEnum.toMap());
      model.addAttribute("OperatorTypeEnum", OperatorTypeEnum.toMap());
      model.addAttribute("pmsOperator", pmsOperator);
      return "pms/pmsOperatorEdit";
    } catch (Exception e) {
      log.error("== editPmsOperatorUI exception:", e);
      return operateError("\u83b7\u53d6\u4fee\u6539\u6570\u636e\u5931\u8d25", model);
    }
  }

  /**
	 * 保存修改后的操作员信息
	 * 
	 * @return operateSuccess or operateError .
	 */
  @RequiresPermissions(value = "pms:operator:edit") @RequestMapping(value = "/edit") public String editPmsOperator(HttpServletRequest req, PmsOperator operator, String selectVal, Model model, DwzAjax dwz) {
    try {
      Long id = operator.getId();
      PmsOperator pmsOperator = pmsOperatorService.getDataById(id);
      if (pmsOperator == null) {
        return operateError("\u65e0\u6cd5\u83b7\u53d6\u8981\u4fee\u6539\u7684\u64cd\u4f5c\u5458\u4fe1\u606f", model);
      }
      if ("USER".equals(this.getPmsOperator().getType()) && "ADMIN".equals(pmsOperator.getType())) {
        return operateError("\u6743\u9650\u4e0d\u8db3", model);
      }
      pmsOperator.setRemark(operator.getRemark());
      pmsOperator.setMobileNo(operator.getMobileNo());
      pmsOperator.setRealName(operator.getRealName());
      String roleOperatorStr = getRoleOperatorStr(selectVal);
      String validateMsg = validatePmsOperator(pmsOperator, roleOperatorStr);
      if (StringUtils.isNotBlank(validateMsg)) {
        return operateError(validateMsg, model);
      }
      pmsOperatorService.updateOperator(pmsOperator, roleOperatorStr);
      return operateSuccess(model, dwz);
    } catch (Exception e) {
      log.error("== editPmsOperator exception:", e);
      return operateError("\u66f4\u65b0\u64cd\u4f5c\u5458\u4fe1\u606f\u5931\u8d25", model);
    }
  }

  /**
	 * 根据ID冻结或激活操作员.
	 * 
	 * @return operateSuccess or operateError .
	 */
  @RequiresPermissions(value = "pms:operator:changestatus") @RequestMapping(value = "/changeStatus") public String changeOperatorStatus(HttpServletRequest req, PmsOperator operator, Model model, DwzAjax dwz) {
    try {
      Long operatorId = operator.getId();
      PmsOperator pmsOperator = pmsOperatorService.getDataById(operatorId);
      if (pmsOperator == null) {
        return operateError("\u65e0\u6cd5\u83b7\u53d6\u8981\u64cd\u4f5c\u7684\u6570\u636e", model);
      }
      if (this.getPmsOperator().getId() == operatorId) {
        return operateError("\u4e0d\u80fd\u4fee\u6539\u81ea\u5df1\u8d26\u6237\u7684\u72b6\u6001", model);
      }
      if ("USER".equals(this.getPmsOperator().getType()) && "ADMIN".equals(pmsOperator.getType())) {
        return operateError("\u6743\u9650\u4e0d\u8db3", model);
      }
      if (pmsOperator.getStatus().equals(PublicStatusEnum.ACTIVE.name())) {
        if ("ADMIN".equals(pmsOperator.getType())) {
          return operateError("\u3010" + pmsOperator.getLoginName() + "\u3011\u4e3a\u8d85\u7ea7\u7ba1\u7406\u5458\uff0c\u4e0d\u80fd\u51bb\u7ed3", model);
        }
        pmsOperator.setStatus(PublicStatusEnum.UNACTIVE.name());
        pmsOperatorService.updateData(pmsOperator);
      } else {
        pmsOperator.setStatus(PublicStatusEnum.ACTIVE.name());
        pmsOperatorService.updateData(pmsOperator);
      }
      return operateSuccess(model, dwz);
    } catch (Exception e) {
      log.error("== changeOperatorStatus exception:", e);
      return operateError("\u5220\u9664\u64cd\u4f5c\u5458\u5931\u8d25:" + e.getMessage(), model);
    }
  }

  /***
	 * 重置操作员的密码（注意：不是修改当前登录操作员自己的密码） .
	 * 
	 * @return
	 */
  @RequiresPermissions(value = "pms:operator:resetpwd") @RequestMapping(value = "/resetPwdUI") public String resetOperatorPwdUI(HttpServletRequest req, Long id, Model model) {
    PmsOperator operator = pmsOperatorService.getDataById(id);
    if (operator == null) {
      return operateError("\u65e0\u6cd5\u83b7\u53d6\u8981\u91cd\u7f6e\u7684\u4fe1\u606f", model);
    }
    if ("USER".equals(this.getPmsOperator().getType()) && "ADMIN".equals(operator.getType())) {
      return operateError("\u6743\u9650\u4e0d\u8db3", model);
    }
    model.addAttribute("operator", operator);
    return "pms/pmsOperatorResetPwd";
  }

  /**
	 * 重置操作员密码.
	 * 
	 * @return
	 */
  @RequiresPermissions(value = "pms:operator:resetpwd") @RequestMapping(value = "/resetPwd") public String resetOperatorPwd(HttpServletRequest req, Long id, String newPwd, String newPwd2, Model model, DwzAjax dwz) {
    try {
      PmsOperator operator = pmsOperatorService.getDataById(id);
      if (operator == null) {
        return operateError("\u65e0\u6cd5\u83b7\u53d6\u8981\u91cd\u7f6e\u5bc6\u7801\u7684\u64cd\u4f5c\u5458\u4fe1\u606f", model);
      }
      if ("USER".equals(this.getPmsOperator().getType()) && "ADMIN".equals(operator.getType())) {
        return operateError("\u6743\u9650\u4e0d\u8db3", model);
      }
      String validateMsg = validatePassword(newPwd, newPwd2);
      if (StringUtils.isNotBlank(validateMsg)) {
        return operateError(validateMsg, model);
      }
      operator.setLoginPwd(newPwd);
      PasswordHelper.encryptPassword(operator);
      pmsOperatorService.updateData(operator);
      return operateSuccess(model, dwz);
    } catch (Exception e) {
      log.error("== resetOperatorPwd exception:", e);
      return operateError("\u5bc6\u7801\u91cd\u7f6e\u51fa\u9519:" + e.getMessage(), model);
    }
  }

  /**
	 * 得到角色和操作员关联的ID字符串
	 * 
	 * @return
	 */
  private String getRoleOperatorStr(String selectVal) throws Exception {
    String roleStr = selectVal;
    if (StringUtils.isNotBlank(roleStr) && roleStr.length() > 0) {
      roleStr = roleStr.substring(0, roleStr.length() - 1);
    }
    return roleStr;
  }

  /***
	 * 验证重置密码
	 * 
	 * @param newPwd
	 * @param newPwd2
	 * @return
	 */
  private String validatePassword(String newPwd, String newPwd2) {
    String msg = "";
    if (StringUtils.isBlank(newPwd)) {
      msg += "\u65b0\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a\uff0c";
    } else {
      if (newPwd.length() < 6) {
        msg += "\u65b0\u5bc6\u7801\u4e0d\u80fd\u5c11\u4e8e6\u4f4d\u957f\u5ea6\uff0c";
      }
    }
    if (!newPwd.equals(newPwd2)) {
      msg += "\u4e24\u6b21\u8f93\u5165\u7684\u5bc6\u7801\u4e0d\u4e00\u81f4";
    }
    return msg;
  }
}