package com.roncoo.pay.trade.utils;
import com.roncoo.pay.common.core.utils.StringUtil;
import com.roncoo.pay.trade.entity.weixinpay.WeiXinPrePay;
import com.roncoo.pay.trade.enums.weixinpay.WeiXinTradeTypeEnum;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * <b>功能说明:微信支付工具类 </b>
 *
 * @author Peter <a href="http://www.roncoo.com">龙果学院(www.roncoo.com)</a>
 */
public class WeiXinPayUtils {
  private static final Logger LOG = LoggerFactory.getLogger(WeiXinPayUtils.class);

  /**
     * 发送xml数据,获取返回结果
     *
     * @param requestUrl
     * @param requestMethod
     * @param xmlStr
     * @return
     */
  public static Map<String, Object> httpXmlRequest(String requestUrl, String requestMethod, String xmlStr) {
    Map<String, Object> map = new HashMap<String, Object>();
    try {
      HttpsURLConnection urlCon = (HttpsURLConnection) (new URL(requestUrl)).openConnection();
      urlCon.setDoInput(true);
      urlCon.setDoOutput(true);
      urlCon.setRequestMethod(requestMethod);
      if ("GET".equalsIgnoreCase(requestMethod)) {
        urlCon.connect();
      }
      urlCon.setRequestProperty("Content-Length", String.valueOf(xmlStr.getBytes().length));
      urlCon.setUseCaches(false);
      if (null != xmlStr) {
        OutputStream outputStream = urlCon.getOutputStream();
        outputStream.write(xmlStr.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
      }
      InputStream inputStream = urlCon.getInputStream();
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
      SAXReader reader = new SAXReader();
      Document document = reader.read(inputStreamReader);
      Element root = document.getRootElement();
      @SuppressWarnings(value = { "unchecked" }) List<Element> elementList = root.elements();
      for (Element e : elementList) {
        map.put(e.getName(), e.getText());
      }
      LOG.info("\u5fae\u4fe1\u67e5\u8be2\u8fd4\u56de\u7ed3\u679c:" + map.toString());
      inputStreamReader.close();
      inputStream.close();
      inputStream = null;
      urlCon.disconnect();
    } catch (MalformedURLException e) {
      LOG.error(e.getMessage());
    } catch (IOException e) {
      LOG.error(e.getMessage());
    } catch (Exception e) {
      LOG.error(e.getMessage());
    }
    return map;
  }

  /**
     * 生成预支付XML
     *
     * @param weiXinPrePay
     * @param partnerKey
     * @return
     */
  public static String getPrePayXml(WeiXinPrePay weiXinPrePay, String partnerKey) {
    getPrePaySign(weiXinPrePay, partnerKey);
    StringBuilder sb = new StringBuilder();
    sb.append("<xml><appid>").append(weiXinPrePay.getAppid()).append("</appid>");
    sb.append("<body>").append(weiXinPrePay.getBody()).append("</body>");
    sb.append("<device_info>").append(weiXinPrePay.getDeviceInfo()).append("</device_info>");
    sb.append("<mch_id>").append(weiXinPrePay.getMchId()).append("</mch_id>");
    sb.append("<nonce_str>").append(weiXinPrePay.getNonceStr()).append("</nonce_str>");
    sb.append("<notify_url>").append(weiXinPrePay.getNotifyUrl()).append("</notify_url>");
    if (WeiXinTradeTypeEnum.NATIVE.name().equals(weiXinPrePay.getTradeType())) {
      sb.append("<product_id>").append(weiXinPrePay.getProductId()).append("</product_id>");
    } else {
      if (WeiXinTradeTypeEnum.JSAPI.name().equals(weiXinPrePay.getTradeType())) {
        sb.append("<openid>").append(weiXinPrePay.getOpenid()).append("</openid>");
      }
    }
    sb.append("<out_trade_no>").append(weiXinPrePay.getOutTradeNo()).append("</out_trade_no>");
    sb.append("<spbill_create_ip>").append(weiXinPrePay.getSpbillCreateIp()).append("</spbill_create_ip>");
    sb.append("<time_start>").append(weiXinPrePay.getTimeStart()).append("</time_start>");
    sb.append("<time_expire>").append(weiXinPrePay.getTimeExpire()).append("</time_expire>");
    sb.append("<total_fee>").append(weiXinPrePay.getTotalFee()).append("</total_fee>");
    sb.append("<trade_type>").append(weiXinPrePay.getTradeType().name()).append("</trade_type>");
    sb.append("<sign>").append(weiXinPrePay.getSign()).append("</sign>");
    sb.append("</xml>");
    return sb.toString();
  }

  /**
     * 微信拼接签名参数
     *
     * @param appid       公众账号ID
     * @param mch_id      商户号
     * @param device_info 设备号
     * @param trade_type  交易类型
     * @param prePay      预支付返回Map
     * @param partnerKey  签名EY
     * @return
     */
  public static String geWeiXintPrePaySign(String appid, String mch_id, String device_info, String trade_type, Map<String, Object> prePay, String partnerKey) {
    Map<String, Object> preParams = new HashMap<String, Object>();
    if (!StringUtil.isEmpty(prePay.get("return_code"))) {
      preParams.put("return_code", prePay.get("return_code"));
    }
    if (!StringUtil.isEmpty(prePay.get("return_msg"))) {
      preParams.put("return_msg", prePay.get("return_msg"));
    }
    if (!StringUtil.isEmpty(prePay.get("appid"))) {
      preParams.put("appid", appid);
    }
    if (!StringUtil.isEmpty(prePay.get("mch_id"))) {
      preParams.put("mch_id", mch_id);
    }
    if (!StringUtil.isEmpty(prePay.get("device_info"))) {
      preParams.put("device_info", device_info);
    }
    if (!StringUtil.isEmpty(prePay.get("nonce_str"))) {
      preParams.put("nonce_str", prePay.get("nonce_str"));
    }
    if (!StringUtil.isEmpty(prePay.get("result_code"))) {
      preParams.put("result_code", prePay.get("result_code"));
    }
    if ("FAIL".equals(prePay.get("result_code"))) {
      if (!StringUtil.isEmpty(prePay.get("err_code"))) {
        preParams.put("err_code", prePay.get("err_code"));
      }
      if (!StringUtil.isEmpty(prePay.get("err_code_des"))) {
        preParams.put("err_code_des", prePay.get("err_code_des"));
      }
    }
    if (!StringUtil.isEmpty(prePay.get("trade_type"))) {
      preParams.put("trade_type", trade_type);
    }
    if (!StringUtil.isEmpty(prePay.get("prepay_id"))) {
      preParams.put("prepay_id", prePay.get("prepay_id"));
    }
    if (!StringUtil.isEmpty(prePay.get("code_url"))) {
      preParams.put("code_url", prePay.get("code_url"));
    }
    String argPreSign = getStringByMap(preParams) + "&key=" + partnerKey;
    String preSign = MD5Util.encode(argPreSign).toUpperCase();
    return preSign;
  }

  public static boolean notifySign(Map<String, String> result, String sign, String partnerKey) {
    String argNotifySign = getStringByStringMap(result) + "&key=" + partnerKey;
    String notifySign = MD5Util.encode(argNotifySign).toUpperCase();
    if (notifySign.equals(sign)) {
      return true;
    } else {
      return false;
    }
  }

  /**
     * 获取预支付请求签名
     *
     * @param weiXinPrePay
     * @param partnerKey
     * @return
     */
  private static void getPrePaySign(WeiXinPrePay weiXinPrePay, String partnerKey) {
    Map<String, Object> prePayMap = new HashMap<String, Object>();
    prePayMap.put("appid", weiXinPrePay.getAppid());
    prePayMap.put("mch_id", weiXinPrePay.getMchId());
    prePayMap.put("device_info", weiXinPrePay.getDeviceInfo());
    prePayMap.put("nonce_str", weiXinPrePay.getNonceStr());
    prePayMap.put("body", weiXinPrePay.getBody());
    prePayMap.put("out_trade_no", weiXinPrePay.getOutTradeNo());
    prePayMap.put("total_fee", weiXinPrePay.getTotalFee());
    prePayMap.put("spbill_create_ip", weiXinPrePay.getSpbillCreateIp());
    prePayMap.put("time_start", weiXinPrePay.getTimeStart());
    prePayMap.put("time_expire", weiXinPrePay.getTimeExpire());
    prePayMap.put("notify_url", weiXinPrePay.getNotifyUrl());
    prePayMap.put("trade_type", weiXinPrePay.getTradeType().name());
    if (WeiXinTradeTypeEnum.NATIVE.name().equals(weiXinPrePay.getTradeType())) {
      prePayMap.put("product_id", weiXinPrePay.getProductId());
    } else {
      if (WeiXinTradeTypeEnum.JSAPI.name().equals(weiXinPrePay.getTradeType())) {
        prePayMap.put("openid", weiXinPrePay.getOpenid());
      }
    }
    String argPreSign = getStringByMap(prePayMap) + "&key=" + partnerKey;
    String preSign = MD5Util.encode(argPreSign).toUpperCase();
    weiXinPrePay.setSign(preSign);
  }

  /**
     * 根据Map获取排序拼接后的字符串
     *
     * @param map
     * @return
     */
  public static String getStringByMap(Map<String, Object> map) {
    SortedMap<String, Object> smap = new TreeMap<String, Object>(map);
    StringBuffer sb = new StringBuffer();
    for (Map.Entry<String, Object> m : smap.entrySet()) {
      sb.append(m.getKey()).append("=").append(m.getValue()).append("&");
    }
    sb.delete(sb.length() - 1, sb.length());
    return sb.toString();
  }

  public static String getStringByStringMap(Map<String, String> map) {
    SortedMap<String, Object> smap = new TreeMap<String, Object>(map);
    StringBuffer sb = new StringBuffer();
    for (Map.Entry<String, Object> m : smap.entrySet()) {
      sb.append(m.getKey()).append("=").append(m.getValue()).append("&");
    }
    sb.delete(sb.length() - 1, sb.length());
    return sb.toString();
  }

  /**
     * 解析微信发来的请求（XML）
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
  @SuppressWarnings(value = { "unchecked" }) public static Map<String, String> parseXml(InputStream inputStream) throws Exception {
    if (inputStream == null) {
      return null;
    }
    Map<String, String> map = new HashMap<String, String>();
    SAXReader reader = new SAXReader();
    Document document = reader.read(inputStream);
    Element root = document.getRootElement();
    List<Element> elementList = root.elements();
    for (Element e : elementList) {
      map.put(e.getName(), e.getText());
    }
    inputStream.close();
    inputStream = null;
    return map;
  }

  /**
     * 订单查询
     *
     * @param outTradeNo
     * @return
     */
  public static Map<String, Object> orderQuery(String outTradeNo, String appId, String MchId, String partnerKey) {
    Random random = new Random();
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("appid", appId);
    paramMap.put("mch_id", MchId);
    String nonce_str = "";
    for (int i = 0; i < 31; i++) {
      nonce_str += random.nextInt(10);
    }
    paramMap.put("nonce_str", nonce_str);
    paramMap.put("out_trade_no", outTradeNo);
    String signStr = getStringByMap(paramMap) + "&key=" + partnerKey;
    paramMap.put("sign", MD5Util.encode(signStr).toUpperCase());
    Set<String> ks = paramMap.keySet();
    StringBuilder sb = new StringBuilder("<xml>");
    for (String key : ks) {
      sb.append("<" + key + ">" + paramMap.get(key) + "</" + key + ">");
    }
    sb.append("</xml>");
    Map<String, Object> resultMap = httpXmlRequest("https://api.mch.weixin.qq.com/pay/orderquery", "POST", sb.toString());
    return resultMap;
  }
}