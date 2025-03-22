package com.roncoo.pay.controller.pay;
import javax.servlet.http.HttpServletRequest;
import com.roncoo.pay.common.core.dwz.DWZ;
import com.roncoo.pay.common.core.enums.SecurityRatingEnum;
import com.roncoo.pay.common.core.dwz.DwzAjax;
import com.roncoo.pay.common.core.utils.StringUtil;
import com.roncoo.pay.common.core.enums.PayWayEnum;
import com.roncoo.pay.user.service.RpUserBankAccountService;
import com.roncoo.pay.common.core.page.PageBean;
import com.roncoo.pay.user.service.RpUserInfoService;
import com.roncoo.pay.common.core.page.PageParam;
import com.roncoo.pay.user.service.RpUserPayConfigService;
import com.roncoo.pay.user.entity.RpUserBankAccount;
import com.roncoo.pay.user.service.RpUserPayInfoService;
import com.roncoo.pay.user.entity.RpUserInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.roncoo.pay.user.entity.RpUserPayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import com.roncoo.pay.user.entity.RpUserPayInfo;
import org.springframework.stereotype.Controller;
import com.roncoo.pay.user.enums.BankAccountTypeEnum;
import org.springframework.ui.Model;
import com.roncoo.pay.user.enums.BankCodeEnum;
import org.springframework.web.bind.annotation.RequestMapping;
import com.roncoo.pay.user.enums.CardTypeEnum;
import org.springframework.web.bind.annotation.RequestMethod;
import com.roncoo.pay.user.enums.FundInfoTypeEnum;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户支付设置管理
 * 龙果学院：www.roncoo.com
 * @author：zenghao
 */
@Controller @RequestMapping(value = "/pay/config") public class UserPayConfigController {
  @Autowired private RpUserPayConfigService rpUserPayConfigService;

  @Autowired private RpUserInfoService rpUserInfoService;

  @Autowired private RpUserPayInfoService rpUserPayInfoService;

  @Autowired private RpUserBankAccountService rpUserBankAccountService;

  /**
	 * 函数功能说明 ： 查询分页数据
	 * 
	 * @参数： @return
	 * @return String
	 * @throws
	 */
  @RequestMapping(value = "/list", method = { RequestMethod.POST, RequestMethod.GET }) public String list(RpUserPayConfig rpUserPayConfig, PageParam pageParam, Model model) {
    PageBean pageBean = rpUserPayConfigService.listPage(pageParam, rpUserPayConfig);
    model.addAttribute("pageBean", pageBean);
    model.addAttribute("pageParam", pageParam);
    model.addAttribute("rpUserPayConfig", rpUserPayConfig);
    return "pay/config/list";
  }

  /**
	 * 函数功能说明 ：跳转添加
	 * 
	 * @参数： @return
	 * @return String
	 * @throws
	 */
  @RequiresPermissions(value = "pay:config:add") @RequestMapping(value = "/addUI", method = RequestMethod.GET) public String addUI(Model model) {
    model.addAttribute("FundInfoTypeEnums", FundInfoTypeEnum.toList());
    model.addAttribute("SecurityRatingEnum", SecurityRatingEnum.toList());
    return "pay/config/add";
  }

  /**
	 * 函数功能说明 ： 保存
	 * 
	 * @参数： @return
	 * @return String
	 * @throws
	 */
  @RequiresPermissions(value = "pay:config:add") @RequestMapping(value = "/add", method = RequestMethod.POST) public String add(HttpServletRequest request, Model model, RpUserPayConfig rpUserPayConfig, DwzAjax dwz) {
    String userNo = request.getParameter("user.userNo");
    String userName = request.getParameter("user.userName");
    String productCode = request.getParameter("product.productCode");
    String productName = request.getParameter("product.productName");
    String securityRating = request.getParameter("securityRating");
    String merchantServerIp = request.getParameter("merchantServerIp");
    String we_appId = request.getParameter("we_appId");
    String we_merchantId = request.getParameter("we_merchantId");
    String we_partnerKey = request.getParameter("we_partnerKey");
    String ali_partner = request.getParameter("ali_partner");
    String ali_key = request.getParameter("ali_key");
    String ali_sellerId = request.getParameter("ali_sellerId");
    String ali_appid = request.getParameter("ali_appid");
    String ali_rsaPrivateKey = request.getParameter("ali_rsaPrivateKey");
    String ali_rsaPublicKey = request.getParameter("ali_rsaPublicKey");
    if (SecurityRatingEnum.MD5_IP.name().equals(securityRating)) {
      if (StringUtil.isEmpty(merchantServerIp)) {
        dwz.setStatusCode(DWZ.ERROR);
        dwz.setMessage("\u5546\u6237IP\u767d\u540d\u5355\u4e0d\u80fd\u4e3a\u7a7a");
        model.addAttribute("dwz", dwz);
        return DWZ.AJAX_DONE;
      }
    }
    rpUserPayConfigService.createUserPayConfig(userNo, userName, productCode, productName, rpUserPayConfig.getRiskDay(), rpUserPayConfig.getFundIntoType(), rpUserPayConfig.getIsAutoSett(), we_appId, we_merchantId, we_partnerKey, ali_partner, ali_sellerId, ali_key, ali_appid, ali_rsaPrivateKey, ali_rsaPublicKey, securityRating, merchantServerIp);
    dwz.setStatusCode(DWZ.SUCCESS);
    dwz.setMessage(DWZ.SUCCESS_MSG);
    model.addAttribute("dwz", dwz);
    return DWZ.AJAX_DONE;
  }

  /**
	 * 函数功能说明 ：跳转编辑
	 * 
	 * @参数： @return
	 * @return String
	 * @throws
	 */
  @RequiresPermissions(value = "pay:config:edit") @RequestMapping(value = "/editUI", method = RequestMethod.GET) public String editUI(Model model, @RequestParam(value = "userNo") String userNo) {
    RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByUserNo(userNo, null);
    RpUserPayInfo wxUserPayInfo = rpUserPayInfoService.getByUserNo(userNo, PayWayEnum.WEIXIN.name());
    RpUserPayInfo aliUserPayInfo = rpUserPayInfoService.getByUserNo(userNo, PayWayEnum.ALIPAY.name());
    model.addAttribute("FundInfoTypeEnums", FundInfoTypeEnum.toList());
    model.addAttribute("rpUserPayConfig", rpUserPayConfig);
    model.addAttribute("wxUserPayInfo", wxUserPayInfo);
    model.addAttribute("aliUserPayInfo", aliUserPayInfo);
    model.addAttribute("SecurityRatingEnum", SecurityRatingEnum.toList());
    return "pay/config/edit";
  }

  /**
	 * 函数功能说明 ： 更新
	 * 
	 * @参数： @return
	 * @return String
	 * @throws
	 */
  @RequiresPermissions(value = "pay:config:edit") @RequestMapping(value = "/edit", method = RequestMethod.POST) public String edit(HttpServletRequest request, Model model, RpUserPayConfig rpUserPayConfig, DwzAjax dwz) {
    String productCode = request.getParameter("product.productCode");
    String productName = request.getParameter("product.productName");
    String securityRating = request.getParameter("securityRating");
    String merchantServerIp = request.getParameter("merchantServerIp");
    String we_appId = request.getParameter("we_appId");
    String we_merchantId = request.getParameter("we_merchantId");
    String we_partnerKey = request.getParameter("we_partnerKey");
    String ali_partner = request.getParameter("ali_partner");
    String ali_key = request.getParameter("ali_key");
    String ali_sellerId = request.getParameter("ali_sellerId");
    String ali_appid = request.getParameter("ali_appid");
    String ali_rsaPrivateKey = request.getParameter("ali_rsaPrivateKey");
    String ali_rsaPublicKey = request.getParameter("ali_rsaPublicKey");
    if (SecurityRatingEnum.MD5_IP.name().equals(securityRating)) {
      if (StringUtil.isEmpty(merchantServerIp)) {
        dwz.setStatusCode(DWZ.ERROR);
        dwz.setMessage("\u5546\u6237IP\u767d\u540d\u5355\u4e0d\u80fd\u4e3a\u7a7a");
        model.addAttribute("dwz", dwz);
        return DWZ.AJAX_DONE;
      }
    }
    rpUserPayConfigService.updateUserPayConfig(rpUserPayConfig.getUserNo(), productCode, productName, rpUserPayConfig.getRiskDay(), rpUserPayConfig.getFundIntoType(), rpUserPayConfig.getIsAutoSett(), we_appId, we_merchantId, we_partnerKey, ali_partner, ali_sellerId, ali_key, ali_appid, ali_rsaPrivateKey, ali_rsaPublicKey, securityRating, merchantServerIp);
    dwz.setStatusCode(DWZ.SUCCESS);
    dwz.setMessage(DWZ.SUCCESS_MSG);
    model.addAttribute("dwz", dwz);
    return DWZ.AJAX_DONE;
  }

  /**
	 * 函数功能说明 ： 删除
	 * 
	 * @参数： @return
	 * @return String
	 * @throws
	 */
  @RequiresPermissions(value = "pay:config:delete") @RequestMapping(value = "/delete", method = { RequestMethod.POST, RequestMethod.GET }) public String delete(Model model, DwzAjax dwz, @RequestParam(value = "userNo") String userNo) {
    rpUserPayConfigService.deleteUserPayConfig(userNo);
    dwz.setStatusCode(DWZ.SUCCESS);
    dwz.setMessage(DWZ.SUCCESS_MSG);
    model.addAttribute("dwz", dwz);
    return DWZ.AJAX_DONE;
  }

  /**
	 * 函数功能说明 ： 审核
	 * 
	 * @参数： @return
	 * @return String
	 * @throws
	 */
  @RequiresPermissions(value = "pay:config:add") @RequestMapping(value = "/audit", method = { RequestMethod.POST, RequestMethod.GET }) public String audit(Model model, DwzAjax dwz, @RequestParam(value = "userNo") String userNo, @RequestParam(value = "auditStatus") String auditStatus) {
    rpUserPayConfigService.audit(userNo, auditStatus);
    dwz.setStatusCode(DWZ.SUCCESS);
    dwz.setMessage(DWZ.SUCCESS_MSG);
    model.addAttribute("dwz", dwz);
    return DWZ.AJAX_DONE;
  }

  /**
	 * 函数功能说明 ：跳转编辑银行卡信息
	 * 
	 * @参数： @return
	 * @return String
	 * @throws
	 */
  @RequiresPermissions(value = "pay:config:edit") @RequestMapping(value = "/editBankUI", method = RequestMethod.GET) public String editBankUI(Model model, @RequestParam(value = "userNo") String userNo) {
    RpUserBankAccount rpUserBankAccount = rpUserBankAccountService.getByUserNo(userNo);
    RpUserInfo rpUserInfo = rpUserInfoService.getDataByMerchentNo(userNo);
    model.addAttribute("BankCodeEnums", BankCodeEnum.toList());
    model.addAttribute("BankAccountTypeEnums", BankAccountTypeEnum.toList());
    model.addAttribute("CardTypeEnums", CardTypeEnum.toList());
    model.addAttribute("rpUserBankAccount", rpUserBankAccount);
    model.addAttribute("rpUserInfo", rpUserInfo);
    return "pay/config/editBank";
  }

  /**
	 * 函数功能说明 ： 更新银行卡信息
	 * 
	 * @参数： @return
	 * @return String
	 * @throws
	 */
  @RequiresPermissions(value = "pay:config:edit") @RequestMapping(value = "/editBank", method = RequestMethod.POST) public String editBank(Model model, RpUserBankAccount rpUserBankAccount, DwzAjax dwz) {
    rpUserBankAccountService.createOrUpdate(rpUserBankAccount);
    dwz.setStatusCode(DWZ.SUCCESS);
    dwz.setMessage(DWZ.SUCCESS_MSG);
    model.addAttribute("dwz", dwz);
    return DWZ.AJAX_DONE;
  }
}