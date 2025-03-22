package com.ijpay.demo.controller.wxpay;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ijpay.core.enums.SignType;
import com.ijpay.core.enums.TradeType;
import com.ijpay.core.kit.*;
import com.ijpay.demo.entity.H5SceneInfo;
import com.ijpay.demo.entity.WxPayBean;
import com.ijpay.demo.vo.AjaxResult;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.WxPayApiConfig;
import com.ijpay.wxpay.WxPayApiConfigKit;
import com.ijpay.wxpay.model.*;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>IJPay 让支付触手可及，封装了微信支付、支付宝支付、银联支付常用的支付方式以及各种常用的接口。</p>
 *
 * <p>不依赖任何第三方 mvc 框架，仅仅作为工具使用简单快速完成支付模块的开发，可轻松嵌入到任何系统里。 </p>
 *
 * <p>IJPay 交流群: 723992875</p>
 *
 * <p>Node.js 版: https://gitee.com/javen205/TNWX</p>
 *
 * <p>微信支付 Demo</p>
 *
 * @author Javen
 */
@Controller @RequestMapping(value = "/wxPay") public class WxPayController extends AbstractWxPayApiController {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired WxPayBean wxPayBean;

  private String notifyUrl;

  private String refundNotifyUrl;

  private static final String USER_PAYING = "USERPAYING";

  @Override public WxPayApiConfig getApiConfig() {
    WxPayApiConfig apiConfig;
    try {
      apiConfig = WxPayApiConfigKit.getApiConfig(wxPayBean.getAppId());
    } catch (Exception e) {
      apiConfig = WxPayApiConfig.builder().appId(wxPayBean.getAppId()).mchId(wxPayBean.getMchId()).partnerKey(wxPayBean.getPartnerKey()).certPath(wxPayBean.getCertPath()).domain(wxPayBean.getDomain()).build();
    }
    notifyUrl = apiConfig.getDomain().concat("/wxPay/payNotify");
    refundNotifyUrl = apiConfig.getDomain().concat("/wxPay/refundNotify");
    return apiConfig;
  }

  @RequestMapping(value = "") @ResponseBody public String index() {
    log.info("\u6b22\u8fce\u4f7f\u7528 IJPay \u4e2d\u7684\u5fae\u4fe1\u652f\u4ed8 -By Javen  <br/><br>  \u4ea4\u6d41\u7fa4\uff1a723992875");
    log.info(wxPayBean.toString());
    return ("\u6b22\u8fce\u4f7f\u7528 IJPay \u4e2d\u7684\u5fae\u4fe1\u652f\u4ed8 -By Javen  <br/><br>  \u4ea4\u6d41\u7fa4\uff1a723992875");
  }

  @GetMapping(value = "/test") @ResponseBody public WxPayBean test() {
    return wxPayBean;
  }

  @GetMapping(value = "/getKey") @ResponseBody public String getKey() {
    return WxPayApi.getSignKey(wxPayBean.getMchId(), wxPayBean.getPartnerKey(), SignType.MD5);
  }

  /**
     * 微信H5 支付
     * 注意：必须再web页面中发起支付且域名已添加到开发配置中
     */
  @RequestMapping(value = "/wapPay", method = { RequestMethod.POST, RequestMethod.GET }) public void wapPay(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String ip = IpKit.getRealIp(request);
    if (StrKit.isBlank(ip)) {
      ip = "127.0.0.1";
    }
    H5SceneInfo sceneInfo = new H5SceneInfo();
    H5SceneInfo.H5 h5_info = new H5SceneInfo.H5();
    h5_info.setType("Wap");
    h5_info.setWap_url("https://gitee.com/javen205/IJPay");
    h5_info.setWap_name("IJPay VIP \u5145\u503c");
    sceneInfo.setH5Info(h5_info);
    WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
    Map<String, String> params = UnifiedOrderModel.builder().appid(wxPayApiConfig.getAppId()).mch_id(wxPayApiConfig.getMchId()).nonce_str(WxPayKit.generateStr()).body("IJPay \u8ba9\u652f\u4ed8\u89e6\u624b\u53ef\u53ca-H5\u652f\u4ed8").attach("Node.js \u7248:https://gitee.com/javen205/TNWX").out_trade_no(WxPayKit.generateStr()).total_fee("1000").spbill_create_ip(ip).notify_url(notifyUrl).trade_type(TradeType.MWEB.getTradeType()).scene_info(JSON.toJSONString(sceneInfo)).build().createSign(wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
    String xmlResult = WxPayApi.pushOrder(false, params);
    log.info(xmlResult);
    Map<String, String> result = WxPayKit.xmlToMap(xmlResult);
    String return_code = result.get("return_code");
    String return_msg = result.get("return_msg");
    if (!WxPayKit.codeIsOk(return_code)) {
      throw new RuntimeException(return_msg);
    }
    String result_code = result.get("result_code");
    if (!WxPayKit.codeIsOk(result_code)) {
      throw new RuntimeException(return_msg);
    }
    String prepayId = result.get("prepay_id");
    String webUrl = result.get("mweb_url");
    log.info("prepay_id:" + prepayId + " mweb_url:" + webUrl);
    response.sendRedirect(webUrl);
  }

  /**
     * 公众号支付
     */
  @RequestMapping(value = "/webPay", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public AjaxResult webPay(HttpServletRequest request, @RequestParam(value = "total_fee") String totalFee) {
    String openId = (String) request.getSession().getAttribute("openId");
    if (openId == null) {
      openId = "11111111";
    }
    if (StrUtil.isEmpty(openId)) {
      return new AjaxResult().addError("openId is null");
    }
    if (StrUtil.isEmpty(totalFee)) {
      return new AjaxResult().addError("\u8bf7\u8f93\u5165\u6570\u5b57\u91d1\u989d");
    }
    String ip = IpKit.getRealIp(request);
    if (StrUtil.isEmpty(ip)) {
      ip = "127.0.0.1";
    }
    WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
    Map<String, String> params = UnifiedOrderModel.builder().appid(wxPayApiConfig.getAppId()).mch_id(wxPayApiConfig.getMchId()).nonce_str(WxPayKit.generateStr()).body("IJPay \u8ba9\u652f\u4ed8\u89e6\u624b\u53ef\u53ca-\u516c\u4f17\u53f7\u652f\u4ed8").attach("Node.js \u7248:https://gitee.com/javen205/TNWX").out_trade_no(WxPayKit.generateStr()).total_fee("1000").spbill_create_ip(ip).notify_url(notifyUrl).trade_type(TradeType.JSAPI.getTradeType()).openid(openId).build().createSign(wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
    String xmlResult = WxPayApi.pushOrder(false, params);
    log.info(xmlResult);
    Map<String, String> resultMap = WxPayKit.xmlToMap(xmlResult);
    String returnCode = resultMap.get("return_code");
    String returnMsg = resultMap.get("return_msg");
    if (!WxPayKit.codeIsOk(returnCode)) {
      return new AjaxResult().addError(returnMsg);
    }
    String resultCode = resultMap.get("result_code");
    if (!WxPayKit.codeIsOk(resultCode)) {
      return new AjaxResult().addError(returnMsg);
    }
    String prepayId = resultMap.get("prepay_id");
    Map<String, String> packageParams = WxPayKit.prepayIdCreateSign(prepayId, wxPayApiConfig.getAppId(), wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
    String jsonStr = JSON.toJSONString(packageParams);
    return new AjaxResult().success(jsonStr);
  }

  /**
     * 扫码模式一
     */
  @RequestMapping(value = "/scanCode1", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public AjaxResult scanCode1(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "productId") String productId) {
    try {
      if (StrKit.isBlank(productId)) {
        return new AjaxResult().addError("productId is null");
      }
      WxPayApiConfig config = WxPayApiConfigKit.getWxPayApiConfig();
      String qrCodeUrl = WxPayKit.bizPayUrl(config.getPartnerKey(), config.getAppId(), config.getMchId(), productId);
      log.info(qrCodeUrl);
      String name = "payQRCode1.png";
      log.info(ResourceUtils.getURL("classpath:").getPath());
      boolean encode = QrCodeKit.encode(qrCodeUrl, BarcodeFormat.QR_CODE, 3, ErrorCorrectionLevel.H, "png", 200, 200, ResourceUtils.getURL("classpath:").getPath().concat("static").concat(File.separator).concat(name));
      if (encode) {
        return new AjaxResult().success(name);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return new AjaxResult().addError("\u7cfb\u7edf\u5f02\u5e38\uff1a" + e.getMessage());
    }
    return null;
  }

  /**
     * 扫码支付模式一回调
     */
  @RequestMapping(value = "/scanCodeNotify", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public String scanCodeNotify(HttpServletRequest request, HttpServletResponse response) {
    try {
      String result = HttpKit.readData(request);
      log.info("scanCodeNotify>>>" + result);
      Map<String, String> map = WxPayKit.xmlToMap(result);
      for (String key : map.keySet()) {
        log.info("key= " + key + " and value= " + map.get(key));
      }
      String appId = map.get("appid");
      String openId = map.get("openid");
      String mchId = map.get("mch_id");
      String isSubscribe = map.get("is_subscribe");
      String nonceStr = map.get("nonce_str");
      String productId = map.get("product_id");
      String sign = map.get("sign");
      Map<String, String> packageParams = new HashMap<String, String>(6);
      packageParams.put("appid", appId);
      packageParams.put("openid", openId);
      packageParams.put("mch_id", mchId);
      packageParams.put("is_subscribe", isSubscribe);
      packageParams.put("nonce_str", nonceStr);
      packageParams.put("product_id", productId);
      WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
      String packageSign = WxPayKit.createSign(packageParams, wxPayApiConfig.getPartnerKey(), SignType.MD5);
      String ip = IpKit.getRealIp(request);
      if (StrKit.isBlank(ip)) {
        ip = "127.0.0.1";
      }
      Map<String, String> params = UnifiedOrderModel.builder().appid(wxPayApiConfig.getAppId()).mch_id(wxPayApiConfig.getMchId()).nonce_str(WxPayKit.generateStr()).body("IJPay \u8ba9\u652f\u4ed8\u89e6\u624b\u53ef\u53ca-\u626b\u7801\u652f\u4ed8\u6a21\u5f0f\u4e00").attach("Node.js \u7248:https://gitee.com/javen205/TNWX").out_trade_no(WxPayKit.generateStr()).total_fee("1").spbill_create_ip(ip).notify_url(notifyUrl).trade_type(TradeType.NATIVE.getTradeType()).openid(openId).build().createSign(wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
      String xmlResult = WxPayApi.pushOrder(false, params);
      log.info("\u7edf\u4e00\u4e0b\u5355:" + xmlResult);
      Map<String, String> payResult = WxPayKit.xmlToMap(xmlResult);
      String returnCode = payResult.get("return_code");
      String resultCode = payResult.get("result_code");
      if (WxPayKit.codeIsOk(returnCode) && WxPayKit.codeIsOk(resultCode)) {
        String prepayId = payResult.get("prepay_id");
        Map<String, String> prepayParams = new HashMap<String, String>(10);
        prepayParams.put("return_code", "SUCCESS");
        prepayParams.put("appid", appId);
        prepayParams.put("mch_id", mchId);
        prepayParams.put("nonce_str", System.currentTimeMillis() + "");
        prepayParams.put("prepay_id", prepayId);
        String prepaySign;
        if (sign.equals(packageSign)) {
          prepayParams.put("result_code", "SUCCESS");
        } else {
          prepayParams.put("result_code", "FAIL");
          prepayParams.put("err_code_des", "\u8ba2\u5355\u5931\u6548");
        }
        prepaySign = WxPayKit.createSign(prepayParams, wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
        prepayParams.put("sign", prepaySign);
        String xml = WxPayKit.toXml(prepayParams);
        log.error(xml);
        return xml;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
     * 扫码支付模式二
     */
  @RequestMapping(value = "/scanCode2", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public AjaxResult scanCode2(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "total_fee") String totalFee) {
    if (StrKit.isBlank(totalFee)) {
      return new AjaxResult().addError("\u652f\u4ed8\u91d1\u989d\u4e0d\u80fd\u4e3a\u7a7a");
    }
    String ip = IpKit.getRealIp(request);
    if (StrKit.isBlank(ip)) {
      ip = "127.0.0.1";
    }
    WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
    Map<String, String> params = UnifiedOrderModel.builder().appid(wxPayApiConfig.getAppId()).mch_id(wxPayApiConfig.getMchId()).nonce_str(WxPayKit.generateStr()).body("IJPay \u8ba9\u652f\u4ed8\u89e6\u624b\u53ef\u53ca-\u626b\u7801\u652f\u4ed8\u6a21\u5f0f\u4e8c").attach("Node.js \u7248:https://gitee.com/javen205/TNWXX").out_trade_no(WxPayKit.generateStr()).total_fee("1").spbill_create_ip(ip).notify_url(notifyUrl).trade_type(TradeType.NATIVE.getTradeType()).build().createSign(wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
    String xmlResult = WxPayApi.pushOrder(false, params);
    log.info("\u7edf\u4e00\u4e0b\u5355:" + xmlResult);
    Map<String, String> result = WxPayKit.xmlToMap(xmlResult);
    String returnCode = result.get("return_code");
    String returnMsg = result.get("return_msg");
    System.out.println(returnMsg);
    if (!WxPayKit.codeIsOk(returnCode)) {
      return new AjaxResult().addError("error:" + returnMsg);
    }
    String resultCode = result.get("result_code");
    if (!WxPayKit.codeIsOk(resultCode)) {
      return new AjaxResult().addError("error:" + returnMsg);
    }
    String qrCodeUrl = result.get("code_url");
    String name = "payQRCode2.png";
    boolean encode = QrCodeKit.encode(qrCodeUrl, BarcodeFormat.QR_CODE, 3, ErrorCorrectionLevel.H, "png", 200, 200, request.getSession().getServletContext().getRealPath("/") + File.separator + name);
    if (encode) {
      return new AjaxResult().success(name);
    }
    return null;
  }

  /**
     * 刷卡支付
     */
  @RequestMapping(value = "/micropay", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public AjaxResult microPay(HttpServletRequest request, HttpServletResponse response) {
    String authCode = request.getParameter("auth_code");
    String totalFee = request.getParameter("total_fee");
    if (StrKit.isBlank(totalFee)) {
      return new AjaxResult().addError("\u652f\u4ed8\u91d1\u989d\u4e0d\u80fd\u4e3a\u7a7a");
    }
    if (StrKit.isBlank(authCode)) {
      return new AjaxResult().addError("auth_code\u53c2\u6570\u9519\u8bef");
    }
    String ip = IpKit.getRealIp(request);
    if (StrKit.isBlank(ip)) {
      ip = "127.0.0.1";
    }
    WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
    Map<String, String> params = MicroPayModel.builder().appid(wxPayApiConfig.getAppId()).mch_id(wxPayApiConfig.getMchId()).nonce_str(WxPayKit.generateStr()).body("IJPay \u8ba9\u652f\u4ed8\u89e6\u624b\u53ef\u53ca-\u5237\u5361\u652f\u4ed8").attach("Node.js \u7248:https://gitee.com/javen205/TNWXX").out_trade_no(WxPayKit.generateStr()).total_fee("1").spbill_create_ip(ip).auth_code(authCode).build().createSign(wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
    String xmlResult = WxPayApi.microPay(false, params);
    log.info("xmlResult:" + xmlResult);
    Map<String, String> result = WxPayKit.xmlToMap(xmlResult);
    String returnCode = result.get("return_code");
    String returnMsg = result.get("return_msg");
    if (!WxPayKit.codeIsOk(returnCode)) {
      String errCode = result.get("err_code");
      if (StrKit.notBlank(errCode)) {
        if (USER_PAYING.equals(errCode)) {
        }
      }
      log.info("\u63d0\u4ea4\u5237\u5361\u652f\u4ed8\u5931\u8d25>>" + xmlResult);
      return new AjaxResult().addError(returnMsg);
    }
    String resultCode = result.get("result_code");
    if (!WxPayKit.codeIsOk(resultCode)) {
      log.info("\u652f\u4ed8\u5931\u8d25>>" + xmlResult);
      String errCodeDes = result.get("err_code_des");
      return new AjaxResult().addError(errCodeDes);
    }
    return new AjaxResult().success(xmlResult);
  }

  /**
     * 微信APP支付
     */
  @RequestMapping(value = "/appPay", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public AjaxResult appPay(HttpServletRequest request) {
    String ip = IpKit.getRealIp(request);
    if (StrKit.isBlank(ip)) {
      ip = "127.0.0.1";
    }
    WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
    Map<String, String> params = UnifiedOrderModel.builder().appid(wxPayApiConfig.getAppId()).mch_id(wxPayApiConfig.getMchId()).nonce_str(WxPayKit.generateStr()).body("IJPay \u8ba9\u652f\u4ed8\u89e6\u624b\u53ef\u53ca-App\u652f\u4ed8").attach("Node.js \u7248:https://gitee.com/javen205/TNWXX").out_trade_no(WxPayKit.generateStr()).total_fee("1000").spbill_create_ip(ip).notify_url(notifyUrl).trade_type(TradeType.APP.getTradeType()).build().createSign(wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
    String xmlResult = WxPayApi.pushOrder(false, params);
    log.info(xmlResult);
    Map<String, String> result = WxPayKit.xmlToMap(xmlResult);
    String returnCode = result.get("return_code");
    String returnMsg = result.get("return_msg");
    if (!WxPayKit.codeIsOk(returnCode)) {
      return new AjaxResult().addError(returnMsg);
    }
    String prepayId = result.get("prepay_id");
    Map<String, String> packageParams = WxPayKit.appPrepayIdCreateSign(wxPayApiConfig.getAppId(), wxPayApiConfig.getMchId(), prepayId, wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
    String jsonStr = JSON.toJSONString(packageParams);
    log.info("\u8fd4\u56deapk\u7684\u53c2\u6570:" + jsonStr);
    return new AjaxResult().success(jsonStr);
  }

  /**
     * 微信小程序支付
     */
  @RequestMapping(value = "/miniAppPay", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public AjaxResult miniAppPay(HttpServletRequest request) {
    String openId = (String) request.getSession().getAttribute("openId");
    String ip = IpKit.getRealIp(request);
    if (StrKit.isBlank(ip)) {
      ip = "127.0.0.1";
    }
    WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
    Map<String, String> params = UnifiedOrderModel.builder().appid(wxPayApiConfig.getAppId()).mch_id(wxPayApiConfig.getMchId()).nonce_str(WxPayKit.generateStr()).body("IJPay \u8ba9\u652f\u4ed8\u89e6\u624b\u53ef\u53ca-\u5c0f\u7a0b\u5e8f\u652f\u4ed8").attach("Node.js \u7248:https://gitee.com/javen205/TNWXX").out_trade_no(WxPayKit.generateStr()).total_fee("1000").spbill_create_ip(ip).notify_url(notifyUrl).trade_type(TradeType.JSAPI.getTradeType()).openid(openId).build().createSign(wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
    String xmlResult = WxPayApi.pushOrder(false, params);
    log.info(xmlResult);
    Map<String, String> result = WxPayKit.xmlToMap(xmlResult);
    String returnCode = result.get("return_code");
    String returnMsg = result.get("return_msg");
    if (!WxPayKit.codeIsOk(returnCode)) {
      return new AjaxResult().addError(returnMsg);
    }
    String resultCode = result.get("result_code");
    if (!WxPayKit.codeIsOk(resultCode)) {
      return new AjaxResult().addError(returnMsg);
    }
    String prepayId = result.get("prepay_id");
    Map<String, String> packageParams = WxPayKit.miniAppPrepayIdCreateSign(wxPayApiConfig.getAppId(), prepayId, wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
    String jsonStr = JSON.toJSONString(packageParams);
    log.info("\u5c0f\u7a0b\u5e8f\u652f\u4ed8\u7684\u53c2\u6570:" + jsonStr);
    return new AjaxResult().success(jsonStr);
  }

  /**
     * 企业付款到零钱
     */
  @RequestMapping(value = "/transfer", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public String transfer(HttpServletRequest request, @RequestParam(value = "openId") String openId) {
    String ip = IpKit.getRealIp(request);
    if (StrKit.isBlank(ip)) {
      ip = "127.0.0.1";
    }
    WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
    Map<String, String> params = TransferModel.builder().mch_appid(wxPayApiConfig.getAppId()).mchid(wxPayApiConfig.getMchId()).nonce_str(WxPayKit.generateStr()).partner_trade_no(WxPayKit.generateStr()).openid(openId).check_name("NO_CHECK").amount("100").desc("IJPay \u8ba9\u652f\u4ed8\u89e6\u624b\u53ef\u53ca-\u4f01\u4e1a\u4ed8\u6b3e").spbill_create_ip(ip).build().createSign(wxPayApiConfig.getPartnerKey(), SignType.MD5, false);
    String transfers = WxPayApi.transfers(params, wxPayApiConfig.getCertPath(), wxPayApiConfig.getMchId());
    log.info("\u63d0\u73b0\u7ed3\u679c:" + transfers);
    Map<String, String> map = WxPayKit.xmlToMap(transfers);
    String returnCode = map.get("return_code");
    String resultCode = map.get("result_code");
    if (WxPayKit.codeIsOk(returnCode) && WxPayKit.codeIsOk(resultCode)) {
    } else {
    }
    return transfers;
  }

  /**
     * 查询企业付款到零钱
     */
  @RequestMapping(value = "/transferInfo", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public String transferInfo(@RequestParam(value = "partner_trade_no") String partnerTradeNo) {
    try {
      WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
      Map<String, String> params = GetTransferInfoModel.builder().nonce_str(WxPayKit.generateStr()).partner_trade_no(partnerTradeNo).mch_id(wxPayApiConfig.getMchId()).appid(wxPayApiConfig.getAppId()).build().createSign(wxPayApiConfig.getPartnerKey(), SignType.MD5, false);
      return WxPayApi.getTransferInfo(params, wxPayApiConfig.getCertPath(), wxPayApiConfig.getMchId());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
     * 获取RSA加密公钥
     */
  @RequestMapping(value = "/getPublicKey", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public String getPublicKey() {
    try {
      WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
      Map<String, String> params = new HashMap<String, String>(4);
      params.put("mch_id", wxPayApiConfig.getMchId());
      params.put("nonce_str", String.valueOf(System.currentTimeMillis()));
      params.put("sign_type", "MD5");
      String createSign = WxPayKit.createSign(params, wxPayApiConfig.getPartnerKey(), SignType.MD5);
      params.put("sign", createSign);
      return WxPayApi.getPublicKey(params, wxPayApiConfig.getCertPath(), wxPayApiConfig.getMchId());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
     * 企业付款到银行卡
     */
  @RequestMapping(value = "/payBank", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public String payBank() {
    try {
      WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
      final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6Bl76IwSvBTiibZ+CNRUA6BfahMshZ0WJpHD1GpmvcQjeN6Yrv6c9eIl6gB4nU3isN7bn+LmoVTpH1gHViaV2YyG/zXj4z4h7r+V+ezesMqqorEg38BCNUHNmhnw4/C0I4gBAQ4x0SJOGnfKGZKR9yzvbkJtvEn732JcEZCbdTZmaxkwlenXvM+mStcJaxBCB/h5xJ5VOF5nDbTPzLphIpzddr3zx/Jxjna9QB1v/YSKYXn+iuwruNUXGCvvxBWaBGKrjOdRTRy9adWOgNmtuYDQJ2YOfG8PtPe06ELKjmr2CfaAGrKKUroyaGvy3qxAV0PlT+UQ4ADSXWt/zl0o5wIDAQAB";
      Map<String, String> params = new HashMap<String, String>(10);
      params.put("mch_id", wxPayApiConfig.getMchId());
      params.put("partner_trade_no", System.currentTimeMillis() + "");
      params.put("nonce_str", System.currentTimeMillis() + "");
      params.put("enc_bank_no", RsaKit.encryptByPublicKeyByWx("\u94f6\u884c\u5361\u53f7", PUBLIC_KEY));
      params.put("enc_true_name", RsaKit.encryptByPublicKeyByWx("\u94f6\u884c\u5361\u6301\u6709\u4eba\u59d3\u540d", PUBLIC_KEY));
      params.put("bank_code", "1001");
      params.put("amount", "1");
      params.put("desc", "IJPay \u8ba9\u652f\u4ed8\u89e6\u624b\u53ef\u53ca-\u4ed8\u6b3e\u5230\u94f6\u884c\u5361");
      params.put("sign", WxPayKit.createSign(params, wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256));
      return WxPayApi.payBank(params, wxPayApiConfig.getCertPath(), wxPayApiConfig.getMchId());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
     * 查询企业付款到银行
     */
  @RequestMapping(value = "/queryBank", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public String queryBank(@RequestParam(value = "partner_trade_no") String partnerTradeNo) {
    try {
      WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
      Map<String, String> params = new HashMap<String, String>(4);
      params.put("mch_id", wxPayApiConfig.getMchId());
      params.put("partner_trade_no", partnerTradeNo);
      params.put("nonce_str", System.currentTimeMillis() + "");
      params.put("sign", WxPayKit.createSign(params, wxPayApiConfig.getPartnerKey(), SignType.MD5));
      return WxPayApi.queryBank(params, wxPayApiConfig.getCertPath(), wxPayApiConfig.getMchId());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
     * 微信退款
     */
  @RequestMapping(value = "/refund", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public String refund(@RequestParam(value = "transactionId") String transactionId, @RequestParam(value = "out_trade_no") String outTradeNo) {
    if (StrKit.isBlank(outTradeNo) && StrKit.isBlank(transactionId)) {
      return "transactionId\u3001out_trade_no\u4e8c\u9009\u4e00";
    }
    WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
    Map<String, String> params = RefundModel.builder().appid(wxPayApiConfig.getAppId()).mch_id(wxPayApiConfig.getMchId()).nonce_str(WxPayKit.generateStr()).transaction_id(transactionId).out_trade_no(outTradeNo).out_refund_no(WxPayKit.generateStr()).total_fee("1").refund_fee("1").notify_url(refundNotifyUrl).build().createSign(wxPayApiConfig.getPartnerKey(), SignType.MD5);
    return WxPayApi.orderRefund(false, params, wxPayApiConfig.getCertPath(), wxPayApiConfig.getMchId());
  }

  /**
     * 微信退款查询
     */
  @RequestMapping(value = "/refundQuery", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public String refundQuery(@RequestParam(value = "transactionId") String transactionId, @RequestParam(value = "out_trade_no") String outTradeNo, @RequestParam(value = "out_refund_no") String outRefundNo, @RequestParam(value = "refund_id") String refundId) {
    WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
    Map<String, String> params = RefundQueryModel.builder().appid(wxPayApiConfig.getAppId()).mch_id(wxPayApiConfig.getMchId()).nonce_str(WxPayKit.generateStr()).transaction_id(transactionId).out_trade_no(outTradeNo).out_refund_no(outRefundNo).refund_id(refundId).build().createSign(wxPayApiConfig.getPartnerKey(), SignType.MD5);
    return WxPayApi.orderRefundQuery(false, params);
  }

  /**
     * 退款通知
     */
  @RequestMapping(value = "/refundNotify", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public String refundNotify(HttpServletRequest request) {
    String xmlMsg = HttpKit.readData(request);
    log.info("\u9000\u6b3e\u901a\u77e5=" + xmlMsg);
    Map<String, String> params = WxPayKit.xmlToMap(xmlMsg);
    String returnCode = params.get("return_code");
    if (WxPayKit.codeIsOk(returnCode)) {
      String reqInfo = params.get("req_info");
      String decryptData = WxPayKit.decryptData(reqInfo, WxPayApiConfigKit.getWxPayApiConfig().getPartnerKey());
      log.info("\u9000\u6b3e\u901a\u77e5\u89e3\u5bc6\u540e\u7684\u6570\u636e=" + decryptData);
      Map<String, String> xml = new HashMap<String, String>(2);
      xml.put("return_code", "SUCCESS");
      xml.put("return_msg", "OK");
      return WxPayKit.toXml(xml);
    }
    return null;
  }

  @RequestMapping(value = "/sendRedPack", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public String sendRedPack(HttpServletRequest request, @RequestParam(value = "openId") String openId) {
    try {
      String ip = IpKit.getRealIp(request);
      if (StrKit.isBlank(ip)) {
        ip = "127.0.0.1";
      }
      WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
      Map<String, String> params = SendRedPackModel.builder().nonce_str(WxPayKit.generateStr()).mch_billno(WxPayKit.generateStr()).mch_id(wxPayApiConfig.getMchId()).wxappid(wxPayApiConfig.getAppId()).send_name("IJPay \u7ea2\u5305\u6d4b\u8bd5").re_openid(openId).total_amount("1000").total_num("1").wishing("\u611f\u8c22\u60a8\u4f7f\u7528 IJPay").client_ip(ip).act_name("\u611f\u6069\u56de\u9988\u6d3b\u52a8").remark("\u70b9 start \u9001\u7ea2\u5305\uff0c\u5feb\u6765\u62a2!").build().createSign(wxPayApiConfig.getPartnerKey(), SignType.MD5);
      String result = WxPayApi.sendRedPack(params, wxPayApiConfig.getCertPath(), wxPayApiConfig.getMchId());
      System.out.println("\u53d1\u9001\u7ea2\u5305\u7ed3\u679c:" + result);
      Map<String, String> map = WxPayKit.xmlToMap(result);
      return JSON.toJSONString(map);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
     * 异步通知
     */
  @RequestMapping(value = "/payNotify", method = { RequestMethod.POST, RequestMethod.GET }) @ResponseBody public String payNotify(HttpServletRequest request) {
    String xmlMsg = HttpKit.readData(request);
    log.info("\u652f\u4ed8\u901a\u77e5=" + xmlMsg);
    Map<String, String> params = WxPayKit.xmlToMap(xmlMsg);
    String returnCode = params.get("return_code");
    if (WxPayKit.verifyNotify(params, WxPayApiConfigKit.getWxPayApiConfig().getPartnerKey(), SignType.HMACSHA256)) {
      if (WxPayKit.codeIsOk(returnCode)) {
        Map<String, String> xml = new HashMap<String, String>(2);
        xml.put("return_code", "SUCCESS");
        xml.put("return_msg", "OK");
        return WxPayKit.toXml(xml);
      }
    }
    return null;
  }
}