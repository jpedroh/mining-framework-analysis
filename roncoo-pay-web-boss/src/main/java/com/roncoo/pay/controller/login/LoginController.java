package com.roncoo.pay.controller.login;
import java.util.ArrayList;
import com.roncoo.pay.common.core.dwz.DWZ;
import java.util.List;
import com.roncoo.pay.common.core.dwz.DwzAjax;
import java.util.Map;
import com.roncoo.pay.common.core.utils.StringUtil;
import javax.servlet.http.HttpServletRequest;
import com.roncoo.pay.controller.common.BaseController;
import javax.servlet.http.HttpSession;
import com.roncoo.pay.permission.entity.PmsOperator;
import com.roncoo.pay.permission.service.PmsMenuService;
import com.roncoo.pay.permission.exception.PermissionException;
import org.apache.commons.lang.StringUtils;
import com.roncoo.pay.permission.service.PmsOperatorRoleService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 龙果学院：www.roncoo.com
 * 
 * @author：Along
 */
@Controller public class LoginController extends BaseController {
  private static final Log LOG = LogFactory.getLog(LoginController.class);

  @Autowired private PmsOperatorRoleService pmsOperatorRoleService;

  @Autowired private PmsMenuService pmsMenuService;

  /**
	 * 函数功能说明 ： 进入后台登陆页面.
	 * 
	 * @参数： @return
	 * @return String
	 * @throws
	 */
  @RequestMapping(value = "/login") public String login(HttpServletRequest req, Model model) {
    String exceptionClassName = (String) req.getAttribute("shiroLoginFailure");
    String error = null;
    if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
      error = "\u7528\u6237\u540d/\u5bc6\u7801\u9519\u8bef";
    } else {
      if (IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
        error = "\u7528\u6237\u540d/\u5bc6\u7801\u9519\u8bef";
      } else {
        if (PermissionException.class.getName().equals(exceptionClassName)) {
          error = "\u7f51\u7edc\u5f02\u5e38,\u8bf7\u8054\u7cfb\u9f99\u679c\u7ba1\u7406\u5458";
        } else {
          if (exceptionClassName != null) {
            error = "\u9519\u8bef\u63d0\u793a\uff1a" + exceptionClassName;
          }
        }
      }
    }
    model.addAttribute("message", error);
    return "system/login";
  }

  /**
	 * 函数功能说明 ： 登陆后台管理系统. 修改者名字： 修改日期： 修改内容：
	 * 
	 * @参数： @param request
	 * @参数： @param model
	 * @参数： @return
	 * @return String
	 * @throws PermissionException
	 */
  @RequestMapping(value = "/") public String index(HttpServletRequest req, Model model) {
    PmsOperator pmsOperator = (PmsOperator) this.getSession().getAttribute("PmsOperator");
    try {
      String tree = this.buildOperatorPermissionMenu(pmsOperator);
      model.addAttribute("tree", tree);
    } catch (PermissionException e) {
      LOG.error("\u767b\u5f55\u5f02\u5e38:" + e.getMessage());
      model.addAttribute("message", e.getMessage());
      return "system/login";
    }
    return "system/index";
  }

  /**
	 * 函数功能说明 ：进入退出系统确认页面. 修改者名字： 修改日期： 修改内容：
	 * 
	 * @参数： @return
	 * @return String
	 * @throws
	 */
  @RequestMapping(value = "/admin/confirm", method = RequestMethod.GET) public String confirm() {
    return "system/confirm";
  }

  /**
	 * 函数功能说明 ： 退出系统. 修改者名字： 修改日期： 修改内容：
	 * 
	 * @参数： @return
	 * @return String
	 * @throws
	 */
  @RequestMapping(value = "/admin/logout", method = RequestMethod.POST) public String logout(HttpServletRequest request, Model model) {
    DwzAjax dwz = new DwzAjax();
    try {
      HttpSession session = request.getSession();
      session.removeAttribute("employee");
      LOG.info("***clean session success!***");
    } catch (Exception e) {
      LOG.error(e);
      dwz.setStatusCode(DWZ.ERROR);
      dwz.setMessage("\u9000\u51fa\u7cfb\u7edf\u65f6\u7cfb\u7edf\u51fa\u73b0\u5f02\u5e38\uff0c\u8bf7\u901a\u77e5\u7cfb\u7edf\u7ba1\u7406\u5458\uff01");
      model.addAttribute("dwz", dwz);
      return "admin.common.ajaxDone";
    }
    return "admin.login";
  }

  /**
	 * 获取用户的菜单权限
	 * 
	 * @param pmsOperator
	 * @return
	 * @throws PermissionException
	 * @throws Exception
	 */
  private String buildOperatorPermissionMenu(PmsOperator pmsOperator) throws PermissionException {
    String roleIds = pmsOperatorRoleService.getRoleIdsByOperatorId(pmsOperator.getId());
    if (StringUtils.isBlank(roleIds)) {
      LOG.error("==>\u7528\u6237[" + pmsOperator.getLoginName() + "]\u6ca1\u6709\u914d\u7f6e\u5bf9\u5e94\u7684\u6743\u9650\u89d2\u8272");
      throw new RuntimeException("\u8be5\u5e10\u53f7\u5df2\u88ab\u53d6\u6d88\u6240\u6709\u7cfb\u7edf\u6743\u9650");
    }
    return this.buildPermissionTree(roleIds);
  }

  /**
	 * 根据操作员拥有的角色ID,构建管理后台的树形权限功能菜单
	 * 
	 * @param roleIds
	 * @return
	 * @throws PermissionException
	 */
  @SuppressWarnings(value = { "rawtypes" }) public String buildPermissionTree(String roleIds) throws PermissionException {
    List treeData = null;
    try {
      treeData = pmsMenuService.listByRoleIds(roleIds);
      if (StringUtil.isEmpty(treeData)) {
        LOG.error("\u7528\u6237\u6ca1\u6709\u5206\u914d\u83dc\u5355\u6743\u9650");
        throw new PermissionException(PermissionException.PERMISSION_USER_NOT_MENU, "\u8be5\u7528\u6237\u6ca1\u6709\u5206\u914d\u83dc\u5355\u6743\u9650");
      }
    } catch (Exception e) {
      LOG.error("\u6839\u636e\u89d2\u8272\u67e5\u8be2\u83dc\u5355\u51fa\u73b0\u9519\u8bef", e);
      throw new PermissionException(PermissionException.PERMISSION_QUERY_MENU_BY_ROLE_ERROR, "\u6839\u636e\u89d2\u8272\u67e5\u8be2\u83dc\u5355\u51fa\u73b0\u9519\u8bef");
    }
    StringBuffer strJson = new StringBuffer();
    buildAdminPermissionTree("0", strJson, treeData);
    return strJson.toString();
  }

  /**
	 * 构建管理后台的树形权限功能菜单
	 * 
	 * @param pId
	 * @param treeBuf
	 * @param menuList
	 */
  @SuppressWarnings(value = { "rawtypes" }) private void buildAdminPermissionTree(String pId, StringBuffer treeBuf, List menuList) {
    List<Map> listMap = getSonMenuListByPid(pId.toString(), menuList);
    for (Map map : listMap) {
      String id = map.get("id").toString();
      String name = map.get("name").toString();
      String isLeaf = map.get("isLeaf").toString();
      String level = map.get("level").toString();
      String url = map.get("url").toString();
      String navTabId = "";
      if (!StringUtil.isEmpty(map.get("targetName"))) {
        navTabId = map.get("targetName").toString();
      }
      if ("1".equals(level)) {
        treeBuf.append("<div class=\'accordionHeader\'>");
        treeBuf.append("<h2> <span>Folder</span> " + name + "</h2>");
        treeBuf.append("</div>");
        treeBuf.append("<div class=\'accordionContent\'>");
      }
      if ("YES".equals(isLeaf)) {
        treeBuf.append("<li><a href=\'" + url + "\' target=\'navTab\' rel=\'" + navTabId + "\'>" + name + "</a></li>");
      } else {
        if ("1".equals(level)) {
          treeBuf.append("<ul class=\'tree treeFolder\'>");
        } else {
          treeBuf.append("<li><a>" + name + "</a>");
          treeBuf.append("<ul>");
        }
        buildAdminPermissionTree(id, treeBuf, menuList);
        if ("1".equals(level)) {
          treeBuf.append("</ul>");
        } else {
          treeBuf.append("</ul></li>");
        }
      }
      if ("1".equals(level)) {
        treeBuf.append("</div>");
      }
    }
  }

  /**
	 * 根据(pId)获取(menuList)中的所有子菜单集合.
	 * 
	 * @param pId
	 *            父菜单ID.
	 * @param menuList
	 *            菜单集合.
	 * @return sonMenuList.
	 */
  @SuppressWarnings(value = { "rawtypes", "unchecked" }) private List<Map> getSonMenuListByPid(String pId, List menuList) {
    List sonMenuList = new ArrayList<Object>();
    for (Object menu : menuList) {
      Map map = (Map) menu;
      if (map != null) {
        String parentId = map.get("pId").toString();
        if (parentId.equals(pId)) {
          sonMenuList.add(map);
        }
      }
    }
    return sonMenuList;
  }
}