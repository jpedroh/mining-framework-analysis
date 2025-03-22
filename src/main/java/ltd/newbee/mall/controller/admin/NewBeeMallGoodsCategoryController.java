package ltd.newbee.mall.controller.admin;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Resource;
import ltd.newbee.mall.common.NewBeeMallCategoryLevelEnum;
import ltd.newbee.mall.common.NewBeeMallException;
import javax.servlet.http.HttpServletRequest;
import ltd.newbee.mall.common.ServiceResultEnum;
import org.springframework.stereotype.Controller;
import ltd.newbee.mall.entity.GoodsCategory;
import org.springframework.util.CollectionUtils;
import ltd.newbee.mall.service.NewBeeMallCategoryService;
import org.springframework.util.StringUtils;
import ltd.newbee.mall.util.PageQueryUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import ltd.newbee.mall.controller.vo.GoodsCampaignVO;
import ltd.newbee.mall.entity.Campaign;
import ltd.newbee.mall.entity.GoodsCampaign;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;

/**
 * @author 13
 * @qq交流群 796794009
 * @email 2449207463@qq.com
 * @link https://github.com/newbee-ltd
 */
@Controller @RequestMapping(value = "/admin") public class NewBeeMallGoodsCategoryController {
  @Resource private NewBeeMallCategoryService newBeeMallCategoryService;

  @GetMapping(value = "/categories") public String categoriesPage(HttpServletRequest request, @RequestParam(value = "categoryLevel") Byte categoryLevel, @RequestParam(value = "parentId") Long parentId, @RequestParam(value = "backParentId") Long backParentId) {
    if (categoryLevel == null || categoryLevel < 1 || categoryLevel > 3) {
      NewBeeMallException.fail("\u53c2\u6570\u5f02\u5e38");
    }
    request.setAttribute("path", "newbee_mall_category");
    request.setAttribute("parentId", parentId);
    request.setAttribute("backParentId", backParentId);
    request.setAttribute("categoryLevel", categoryLevel);
    return "admin/newbee_mall_category";
  }

  /**
     * 列表
     */
  @RequestMapping(value = "/categories/list", method = RequestMethod.GET) @ResponseBody public Result list(@RequestParam Map<String, Object> params) {
    if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit")) || StringUtils.isEmpty(params.get("categoryLevel")) || StringUtils.isEmpty(params.get("parentId"))) {
      return ResultGenerator.genFailResult("\u53c2\u6570\u5f02\u5e38\uff01");
    }
    PageQueryUtil pageUtil = new PageQueryUtil(params);
    return ResultGenerator.genSuccessResult(newBeeMallCategoryService.getCategorisPage(pageUtil));
  }

  /**
     * 列表
     */
  @RequestMapping(value = "/categories/listForSelect", method = RequestMethod.GET) @ResponseBody public Result listForSelect(@RequestParam(value = "categoryId") Long categoryId) {
    if (categoryId == null || categoryId < 1) {
      return ResultGenerator.genFailResult("\u7f3a\u5c11\u53c2\u6570\uff01");
    }
    GoodsCategory category = newBeeMallCategoryService.getGoodsCategoryById(categoryId);
    if (category == null || category.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
      return ResultGenerator.genFailResult("\u53c2\u6570\u5f02\u5e38\uff01");
    }
    Map categoryResult = new HashMap(4);
    if (category.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel()) {
      List<GoodsCategory> secondLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());
      if (!CollectionUtils.isEmpty(secondLevelCategories)) {
        List<GoodsCategory> thirdLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
        categoryResult.put("secondLevelCategories", secondLevelCategories);
        categoryResult.put("thirdLevelCategories", thirdLevelCategories);
      }
    }
    if (category.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel()) {
      List<GoodsCategory> thirdLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
      categoryResult.put("thirdLevelCategories", thirdLevelCategories);
    }
    return ResultGenerator.genSuccessResult(categoryResult);
  }

  /**
     * 添加
     */
  @RequestMapping(value = "/categories/save", method = RequestMethod.POST) @ResponseBody public Result save(@RequestBody GoodsCategory goodsCategory) {
    if (Objects.isNull(goodsCategory.getCategoryLevel()) || StringUtils.isEmpty(goodsCategory.getCategoryName()) || Objects.isNull(goodsCategory.getParentId()) || Objects.isNull(goodsCategory.getCategoryRank())) {
      return ResultGenerator.genFailResult("\u53c2\u6570\u5f02\u5e38\uff01");
    }
    String result = newBeeMallCategoryService.saveCategory(goodsCategory);
    if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
      return ResultGenerator.genSuccessResult();
    } else {
      return ResultGenerator.genFailResult(result);
    }
  }

  /**
     * 修改
     */
  @RequestMapping(value = "/categories/update", method = RequestMethod.POST) @ResponseBody public Result update(@RequestBody GoodsCategory goodsCategory) {
    if (Objects.isNull(goodsCategory.getCategoryId()) || Objects.isNull(goodsCategory.getCategoryLevel()) || StringUtils.isEmpty(goodsCategory.getCategoryName()) || Objects.isNull(goodsCategory.getParentId()) || Objects.isNull(goodsCategory.getCategoryRank())) {
      return ResultGenerator.genFailResult("\u53c2\u6570\u5f02\u5e38\uff01");
    }
    String result = newBeeMallCategoryService.updateGoodsCategory(goodsCategory);
    if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
      return ResultGenerator.genSuccessResult();
    } else {
      return ResultGenerator.genFailResult(result);
    }
  }

  /**
     * 详情
     */
  @GetMapping(value = "/categories/info/{id}") @ResponseBody public Result info(@PathVariable(value = "id") Long id) {
    GoodsCategory goodsCategory = newBeeMallCategoryService.getGoodsCategoryById(id);
    if (goodsCategory == null) {
      return ResultGenerator.genFailResult("\u672a\u67e5\u8be2\u5230\u6570\u636e");
    }
    return ResultGenerator.genSuccessResult(goodsCategory);
  }

  /**
     * 分类删除
     */
  @RequestMapping(value = "/categories/delete", method = RequestMethod.POST) @ResponseBody public Result delete(@RequestBody Integer[] ids) {
    if (ids.length < 1) {
      return ResultGenerator.genFailResult("\u53c2\u6570\u5f02\u5e38\uff01");
    }
    if (newBeeMallCategoryService.deleteBatch(ids)) {
      return ResultGenerator.genSuccessResult();
    } else {
      return ResultGenerator.genFailResult("\u5220\u9664\u5931\u8d25");
    }
  }

  @GetMapping(value = "/campaign") public String campaignPage(HttpServletRequest request) {
    request.setAttribute("path", "campaign");
    return "admin/campaign";
  }

  @RequestMapping(value = "/campaign/list", method = RequestMethod.GET) @ResponseBody public Result campaignList(@RequestParam Map<String, Object> params) {
    if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
      return ResultGenerator.genFailResult("\u53c2\u6570\u5f02\u5e38\uff01");
    }
    PageQueryUtil pageUtil = new PageQueryUtil(params);
    return ResultGenerator.genSuccessResult(newBeeMallCategoryService.getCampaignPage(pageUtil));
  }

  @RequestMapping(value = "/campaign/save", method = RequestMethod.POST) @ResponseBody public Result insertCampaign(@RequestBody Campaign campaign, HttpServletRequest request) {
    String loginUserId = request.getSession().getAttribute("loginUserId").toString();
    Long camId = newBeeMallCategoryService.getMaxCampaignId();
    Long newCamId = camId + 1;
    Date insertDate = new Date();
    String camName = campaign.getCamName();
    Campaign camInfo = newBeeMallCategoryService.getCampaignInfo(camName);
    campaign.setCreateUser(loginUserId);
    campaign.setCamId(newCamId);
    campaign.setTimeStamp(insertDate);
    campaign.setCamKind(camInfo.getCamKind());
    campaign.setPriority(camInfo.getPriority());
    int row = newBeeMallCategoryService.insertNewCampaign(campaign);
    if (row > 0) {
      return ResultGenerator.genSuccessResult("\u6dfb\u52a0\u6210\u529f");
    } else {
      return ResultGenerator.genErrorResult(404, "\u6dfb\u52a0\u5931\u8d25");
    }
  }

  @RequestMapping(value = "/campaign/edit", method = RequestMethod.POST) @ResponseBody public Result editCampaign(@RequestBody Map<String, Object> params, HttpServletRequest request) {
    String loginUserId = request.getSession().getAttribute("loginUserId").toString();
    Long camId = Long.parseLong(params.get("camId").toString());
    String camName = params.get("camName").toString();
    Campaign camInfo = newBeeMallCategoryService.getCampaignInfo(camName);
    String camKind = camInfo.getCamKind();
    int priority = camInfo.getPriority();
    Date editDate = new Date();
    Campaign newCam = newBeeMallCategoryService.getCampaignById(camId);
    newCam.setCamId(camId);
    newCam.setCal1(params.get("cal1").toString());
    newCam.setCamKind(camKind);
    newCam.setPriority(priority);
    newCam.setTimeStamp(editDate);
    newCam.setCamName(camName);
    newCam.setCreateUser(loginUserId);
    int row = newBeeMallCategoryService.updateByCamId(newCam);
    if (row > 0) {
      return ResultGenerator.genSuccessResult("\u4fee\u6539\u6210\u529f");
    } else {
      return ResultGenerator.genErrorResult(404, "\u4fee\u6539\u5931\u8d25");
    }
  }

  @RequestMapping(value = "/campaign/delete", method = RequestMethod.POST) @ResponseBody public Result deleteCampaign(@RequestBody Integer[] ids) {
    if (ids.length < 1) {
      return ResultGenerator.genFailResult("\u53c2\u6570\u5f02\u5e38\uff01");
    }
    if (newBeeMallCategoryService.deleteCampaign(ids)) {
      return ResultGenerator.genSuccessResult();
    } else {
      return ResultGenerator.genFailResult("\u5220\u9664\u5931\u8d25");
    }
  }

  @GetMapping(value = "/goodsCampaign") public String goodsCampaignPage(HttpServletRequest request) {
    List<GoodsCampaign> goodsCampaignList = newBeeMallCategoryService.getGoodsCampaignContent();
    List<GoodsCampaignVO> goodsCampaignVOList = BeanUtil.copyList(goodsCampaignList, GoodsCampaignVO.class);
    request.setAttribute("path", "goodsCampaign");
    request.setAttribute("goodsCampaign", goodsCampaignVOList);
    return "admin/goodsCampaign";
  }

  @RequestMapping(value = "/goodsCampaign/list", method = RequestMethod.GET) @ResponseBody public Result goodsCampaignList(@RequestParam Map<String, Object> params) {
    if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
      return ResultGenerator.genFailResult("\u53c2\u6570\u5f02\u5e38\uff01");
    }
    PageQueryUtil pageUtil = new PageQueryUtil(params);
    return ResultGenerator.genSuccessResult(newBeeMallCategoryService.getGoodsCampaignPage(pageUtil));
  }

  @RequestMapping(value = "/goodsCampaign/update", method = RequestMethod.POST) @ResponseBody public Result updateGoodsCam(@RequestBody List<GoodsCampaign> cams) {
    boolean errorflg = false;
    for (int i = 0; i < cams.size(); i++) {
      GoodsCampaign goodsCam = new GoodsCampaign();
      goodsCam = cams.get(i);
      Long newCamId = goodsCam.getCamId();
      int flag = goodsCam.getFlag();
      Long goodsId = goodsCam.getGoodsId();
      Date starDate = new Date();
      Date endDate = new Date();
      goodsCam.setGoodsId(goodsId);
      goodsCam.setCamId(newCamId);
      goodsCam.setStartDate(starDate);
      goodsCam.setEndDate(endDate);
      int row = 0;
      if (flag == 0) {
        row = newBeeMallCategoryService.setNewGoodsCam(goodsCam);
      }
      if (flag == 1) {
        row = newBeeMallCategoryService.insertNewGoodsCampaign(goodsCam);
      }
      if (flag == 2) {
        row = newBeeMallCategoryService.deleteGoodsCam(goodsCam);
      }
      if (row >= 0) {
        errorflg = false;
      } else {
        errorflg = true;
        break;
      }
    }
    if (!errorflg) {
      return ResultGenerator.genSuccessResult("\u66f4\u65b0\u6210\u529f");
    } else {
      return ResultGenerator.genFailResult("\u66f4\u65b0\u5931\u8d25");
    }
  }

  @RequestMapping(value = "/goodsCampaign/campaignList", method = RequestMethod.POST) @ResponseBody public Result camList() {
    List<GoodsCampaign> goodsCampaignList = newBeeMallCategoryService.getGoodsCampaignContent();
    List<GoodsCampaignVO> goodsCampaignVOList = BeanUtil.copyList(goodsCampaignList, GoodsCampaignVO.class);
    return ResultGenerator.genSuccessResult(goodsCampaignVOList);
  }
}