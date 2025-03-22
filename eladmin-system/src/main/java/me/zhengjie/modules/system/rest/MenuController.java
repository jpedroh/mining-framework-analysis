package me.zhengjie.modules.system.rest;
import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.system.domain.Menu;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.system.domain.vo.MenuVo;
import me.zhengjie.modules.system.service.MenuService;
import me.zhengjie.modules.system.service.dto.DeptDto;
import me.zhengjie.modules.system.service.dto.MenuDto;
import me.zhengjie.modules.system.service.dto.MenuQueryCriteria;
import me.zhengjie.modules.system.service.mapstruct.MenuMapper;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zheng Jie
 * @date 2018-12-03
 */
@RestController @RequiredArgsConstructor @Api(tags = "\u7cfb\u7edf\uff1a\u83dc\u5355\u7ba1\u7406") @RequestMapping(value = "/api/menus") public class MenuController {
  private final MenuService menuService;

  private final MenuMapper menuMapper;

  private static final String ENTITY_NAME = "menu";

  @ApiOperation(value = "\u5bfc\u51fa\u83dc\u5355\u6570\u636e") @GetMapping(value = "/download") @PreAuthorize(value = "@el.check(\'menu:list\')") public void exportMenu(HttpServletResponse response, MenuQueryCriteria criteria) throws Exception {
    menuService.download(menuService.queryAll(criteria, false), response);
  }

  @GetMapping(value = "/build") @ApiOperation(value = "\u83b7\u53d6\u524d\u7aef\u6240\u9700\u83dc\u5355") public ResponseEntity<List<MenuVo>> buildMenus() {
    List<MenuDto> menuDtoList = menuService.findByUser(SecurityUtils.getCurrentUserId());
    List<MenuDto> menus = menuService.buildTree(menuDtoList);
    return new ResponseEntity<>(menuService.buildMenus(menus), HttpStatus.OK);
  }

  @ApiOperation(value = "\u8fd4\u56de\u5168\u90e8\u7684\u83dc\u5355") @GetMapping(value = "/lazy") @PreAuthorize(value = "@el.check(\'menu:list\',\'roles:list\')") public ResponseEntity<List<MenuDto>> queryAllMenu(@RequestParam Long pid) {
    return new ResponseEntity<>(menuService.getMenus(pid), HttpStatus.OK);
  }

  @ApiOperation(value = "\u6839\u636e\u83dc\u5355ID\u8fd4\u56de\u6240\u6709\u5b50\u8282\u70b9ID\uff0c\u5305\u542b\u81ea\u8eabID") @GetMapping(value = "/child") @PreAuthorize(value = "@el.check(\'menu:list\',\'roles:list\')") public ResponseEntity<Object> childMenu(@RequestParam Long id) {
    Set<Menu> menuSet = new HashSet<>();
    List<MenuDto> menuList = menuService.getMenus(id);
    menuSet.add(menuService.findOne(id));
    menuSet = menuService.getChildMenus(menuMapper.toEntity(menuList), menuSet);
    Set<Long> ids = menuSet.stream().map(Menu::getId).collect(Collectors.toSet());
    return new ResponseEntity<>(ids, HttpStatus.OK);
  }

  @GetMapping @ApiOperation(value = "\u67e5\u8be2\u83dc\u5355") @PreAuthorize(value = "@el.check(\'menu:list\')") public ResponseEntity<PageResult<MenuDto>> queryMenu(MenuQueryCriteria criteria) throws Exception {
    List<MenuDto> menuDtoList = menuService.queryAll(criteria, true);
    return new ResponseEntity<>(PageUtil.toPage(menuDtoList, menuDtoList.size()), HttpStatus.OK);
  }

  @ApiOperation(value = "\u67e5\u8be2\u83dc\u5355:\u6839\u636eID\u83b7\u53d6\u540c\u7ea7\u4e0e\u4e0a\u7ea7\u6570\u636e") @PostMapping(value = "/superior") @PreAuthorize(value = "@el.check(\'menu:list\')") public ResponseEntity<List<MenuDto>> getMenuSuperior(@RequestBody List<Long> ids) {
    Set<MenuDto> menuDtos = new LinkedHashSet<>();
    if (CollectionUtil.isNotEmpty(ids)) {
      for (Long id : ids) {
        MenuDto menuDto = menuService.findById(id);
        List<MenuDto> menuDtoList = menuService.getSuperior(menuDto, new ArrayList<>());
        for (MenuDto menu : menuDtoList) {
          if (menu.getId().equals(menuDto.getPid())) {
            menu.setSubCount(menu.getSubCount() - 1);
          }
        }
        menuDtos.addAll(menuDtoList);
      }
      menuDtos = menuDtos.stream().filter((i) -> !ids.contains(i.getId())).collect(Collectors.toSet());
      return new ResponseEntity<>(menuService.buildTree(new ArrayList<>(menuDtos)), HttpStatus.OK);
    }
    return new ResponseEntity<>(menuService.getMenus(null), HttpStatus.OK);
  }

  @Log(value = "\u65b0\u589e\u83dc\u5355") @ApiOperation(value = "\u65b0\u589e\u83dc\u5355") @PostMapping @PreAuthorize(value = "@el.check(\'menu:add\')") public ResponseEntity<Object> createMenu(@Validated @RequestBody Menu resources) {
    if (resources.getId() != null) {
      throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
    }
    menuService.create(resources);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Log(value = "\u4fee\u6539\u83dc\u5355") @ApiOperation(value = "\u4fee\u6539\u83dc\u5355") @PutMapping @PreAuthorize(value = "@el.check(\'menu:edit\')") public ResponseEntity<Object> updateMenu(@Validated(value = Menu.Update.class) @RequestBody Menu resources) {
    if (resources.getId() <= 126) {
      throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
    }
    menuService.update(resources);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Log(value = "\u5220\u9664\u83dc\u5355") @ApiOperation(value = "\u5220\u9664\u83dc\u5355") @DeleteMapping @PreAuthorize(value = "@el.check(\'menu:del\')") public ResponseEntity<Object> deleteMenu(@RequestBody Set<Long> ids) {
    Set<Menu> menuSet = new HashSet<>();
    for (Long id : ids) {
      if (id <= 126) {
        throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
      }
      List<MenuDto> menuList = menuService.getMenus(id);
      menuSet.add(menuService.findOne(id));
      menuSet = menuService.getChildMenus(menuMapper.toEntity(menuList), menuSet);
    }
    menuService.delete(menuSet);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}