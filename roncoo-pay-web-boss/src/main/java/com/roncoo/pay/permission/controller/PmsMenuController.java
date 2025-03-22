package com.roncoo.pay.permission.controller;
import java.util.HashMap;
import com.roncoo.pay.common.core.dwz.DwzAjax;
import java.util.List;
import com.roncoo.pay.common.core.enums.PublicEnum;
import java.util.Map;
import com.roncoo.pay.common.core.enums.PublicStatusEnum;
import javax.servlet.http.HttpServletRequest;
import com.roncoo.pay.controller.common.BaseController;
import com.roncoo.pay.permission.biz.PmsMenuBiz;
import com.roncoo.pay.permission.entity.PmsMenu;
import com.roncoo.pay.permission.service.PmsMenuService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 权限-菜单控制器
 *
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
@Controller @RequestMapping(value = "/pms/menu") public class PmsMenuController extends BaseController {
  private static final Log log = LogFactory.getLog(PmsMenuController.class);

  @Autowired private PmsMenuService pmsMenuService;

  @Autowired private PmsMenuBiz pmsMenuBiz;

  /**
	 * 列出要管理的菜单.
	 * 
	 * @return PmsMenuList .
	 */
  @RequiresPermissions(value = "pms:menu:view") @RequestMapping(value = "/list") public String listPmsMenu(HttpServletRequest req, Model model) {
    String editMenuController = "pms/menu/editUI";
    String str = pmsMenuBiz.getTreeMenu(editMenuController);
    model.addAttribute("tree", str);
    return "pms/pmsMenuList";
  }

  /**
	 * 进入新菜单添加页面.
	 * 
	 * @return PmsMenuAdd .
	 */
  @RequiresPermissions(value = "pms:menu:add") @RequestMapping(value = "/addUI") public String addPmsMenuUI(HttpServletRequest req, PmsMenu pmsMenu, Model model, Long pid) {
    if (null != pid) {
      PmsMenu parentMenu = pmsMenuService.getById(pid);
      pmsMenu.setParent(parentMenu);
      model.addAttribute(pmsMenu);
    }
    return "pms/pmsMenuAdd";
  }

  /**
	 * 保存新增菜单.
	 * 
	 * @return operateSuccess or operateError .
	 */
  @RequiresPermissions(value = "pms:menu:add") @RequestMapping(value = "/add") public String addPmsMenu(HttpServletRequest req, PmsMenu pmsMenu, Model model, DwzAjax dwz) {
    try {
      String name = pmsMenu.getName();
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("isLeaf", "YES");
      map.put("name", name);
      List<PmsMenu> list = pmsMenuService.getMenuByNameAndIsLeaf(map);
      if (list.size() > 0) {
        return operateError("\u540c\u7ea7\u83dc\u5355\u540d\u79f0\u4e0d\u80fd\u91cd\u590d", model);
      }
      pmsMenu.setCreater(getPmsOperator().getLoginName());
      pmsMenu.setStatus(PublicStatusEnum.ACTIVE.name());
      pmsMenu.setIsLeaf("YES");
      if (null != pmsMenu.getParent().getId()) {
        pmsMenu.setLevel(pmsMenu.getParent().getLevel() + 1);
      } else {
        pmsMenu.setLevel(1L);
        PmsMenu parent = new PmsMenu();
        parent.setId(0l);
        pmsMenu.setParent(parent);
      }
      pmsMenuService.savaMenu(pmsMenu);
    } catch (Exception e) {
      log.error("== addPmsMenu exception:", e);
      return operateError("\u6dfb\u52a0\u83dc\u5355\u51fa\u9519", model);
    }
    return operateSuccess(model, dwz);
  }

  /**
	 * 进入菜单修改页面.
	 * 
	 * @return
	 */
  @RequiresPermissions(value = "pms:menu:edit") @RequestMapping(value = "/editUI") public String editPmsMenuUI(HttpServletRequest req, Long id, Model model) {
    if (null != id) {
      PmsMenu pmsMenu = pmsMenuService.getById(id);
      model.addAttribute(pmsMenu);
    }
    return "pms/pmsMenuEdit";
  }

  /**
	 * 保存要修改的菜单.
	 * 
	 * @return
	 */
  @RequiresPermissions(value = "pms:menu:edit") @RequestMapping(value = "/edit") public String editPmsMenu(HttpServletRequest req, PmsMenu menu, Model model, DwzAjax dwz) {
    try {
      PmsMenu parentMenu = menu.getParent();
      if (null == parentMenu) {
        parentMenu = new PmsMenu();
        parentMenu.setId(0L);
      }
      menu.setParent(parentMenu);
      pmsMenuService.update(menu);
      return operateSuccess(model, dwz);
    } catch (Exception e) {
      log.error("== editPmsMenu exception:", e);
      return operateError("\u4fdd\u5b58\u83dc\u5355\u51fa\u9519", model);
    }
  }

  /**
	 * 删除菜单.
	 * 
	 * @return
	 */
  @RequiresPermissions(value = "pms:menu:delete") @RequestMapping(value = "/delete") public String delPmsMenu(HttpServletRequest req, Long menuId, Model model, DwzAjax dwz) {
    try {
      if (menuId == null || menuId == 0) {
        return operateError("\u65e0\u6cd5\u83b7\u53d6\u8981\u5220\u9664\u7684\u6570\u636e", model);
      }
      PmsMenu menu = pmsMenuService.getById(menuId);
      if (menu == null) {
        return operateError("\u65e0\u6cd5\u83b7\u53d6\u8981\u5220\u9664\u7684\u6570\u636e", model);
      }
      Long parentId = menu.getParent().getId();
      List<PmsMenu> childMenuList = pmsMenuService.listByParentId(menuId);
      if (childMenuList != null && !childMenuList.isEmpty()) {
        return operateError("\u6b64\u83dc\u5355\u4e0b\u5173\u8054\u6709\u3010" + childMenuList.size() + "\u3011\u4e2a\u5b50\u83dc\u5355\uff0c\u4e0d\u80fd\u652f\u63a5\u5220\u9664!", model);
      }
      pmsMenuService.delete(menuId);
      List<PmsMenu> childList = pmsMenuService.listByParentId(parentId);
      if (childList == null || childList.isEmpty()) {
        PmsMenu parent = pmsMenuService.getById(parentId);
        parent.setIsLeaf(PublicEnum.YES.name());
        pmsMenuService.update(parent);
      }
      return operateSuccess(model, dwz);
    } catch (Exception e) {
      log.error("== delPmsMenu exception:", e);
      return operateError("\u5220\u9664\u83dc\u5355\u51fa\u9519", model);
    }
  }
}