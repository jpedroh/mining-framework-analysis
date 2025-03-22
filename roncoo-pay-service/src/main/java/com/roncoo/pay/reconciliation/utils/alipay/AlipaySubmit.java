package com.roncoo.pay.reconciliation.utils.alipay;
import java.io.IOException;
import com.roncoo.pay.trade.utils.AlipayConfigUtil;
import java.net.MalformedURLException;
import org.apache.commons.httpclient.NameValuePair;
import java.net.URL;
import org.dom4j.Document;
import java.util.ArrayList;
import org.dom4j.DocumentException;
import java.util.List;
import org.dom4j.Node;
import java.util.Map;
import org.dom4j.io.SAXReader;

public class AlipaySubmit {
  /**
	 * 合作身份者ID，签约账号
	 */
  private static final String PARTNER = AlipayConfigUtil.readConfig("partner");

  /**
	 * MD5密钥，安全检验码
	 */
  private static final String KEY = AlipayConfigUtil.readConfig("key");

  /**
	 * 支付宝提供给商户的服务接入网关URL(新)
	 */
  private static final String ALIPAY_GATEWAY_NEW = AlipayConfigUtil.readConfig("alipay_gateway_new");

  /**
	 * 签名方式
	 */
  private static final String SIGN_TYPE = AlipayConfigUtil.readConfig("sign_type");

  /**
	 * 字符编码格式 目前支持
	 */
  private static final String INPUT_CHARSET = AlipayConfigUtil.readConfig("input_charset");

  /**
	 * 生成签名结果
	 * 
	 * @param sPara
	 *            要签名的数组
	 * @return 签名结果字符串
	 */
  public static String buildRequestMysign(Map<String, String> sPara) {
    String prestr = AlipayCore.createLinkString(sPara);
    String mysign = "";
    if (SIGN_TYPE.equals("MD5")) {
      mysign = MD5.sign(prestr, KEY, INPUT_CHARSET);
    }
    return mysign;
  }

  /**
	 * 生成要请求给支付宝的参数数组
	 * 
	 * @param sParaTemp
	 *            请求前的参数数组
	 * @return 要请求的参数数组
	 */
  public static Map<String, String> buildRequestPara(Map<String, String> sParaTemp) {
    Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
    String mysign = buildRequestMysign(sPara);
    sPara.put("sign", mysign);
    sPara.put("sign_type", SIGN_TYPE);
    return sPara;
  }

  /**
	 * 建立请求，以表单HTML形式构造（默认）
	 * 
	 * @param sParaTemp
	 *            请求参数数组
	 * @param strMethod
	 *            提交方式。两个值可选：post、get
	 * @param strButtonName
	 *            确认按钮显示文字
	 * @return 提交表单HTML文本
	 */
  public static String buildRequest(Map<String, String> sParaTemp, String strMethod, String strButtonName) {
    Map<String, String> sPara = buildRequestPara(sParaTemp);
    List<String> keys = new ArrayList<String>(sPara.keySet());
    StringBuffer sbHtml = new StringBuffer();
    sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + ALIPAY_GATEWAY_NEW + "_input_charset=" + INPUT_CHARSET + "\" method=\"" + strMethod + "\">");
    for (int i = 0; i < keys.size(); i++) {
      String name = (String) keys.get(i);
      String value = (String) sPara.get(name);
      sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
    }
    sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
    sbHtml.append("<script>document.forms[\'alipaysubmit\'].submit();</script>");
    return sbHtml.toString();
  }

  /**
	 * MAP类型数组转换成NameValuePair类型
	 * 
	 * @param properties
	 *            MAP类型数组
	 * @return NameValuePair类型数组
	 */
  public static NameValuePair[] generatNameValuePair(Map<String, String> properties) {
    NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
    int i = 0;
    for (Map.Entry<String, String> entry : properties.entrySet()) {
      nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
    }
    return nameValuePair;
  }

  /**
	 * 用于防钓鱼，调用接口query_timestamp来获取时间戳的处理函数 注意：远程解析XML出错，与服务器是否支持SSL等配置有关
	 * 
	 * @return 时间戳字符串
	 * @throws IOException
	 * @throws DocumentException
	 * @throws MalformedURLException
	 */
  public static String query_timestamp() throws MalformedURLException, DocumentException, IOException {
    String strUrl = ALIPAY_GATEWAY_NEW + "service=query_timestamp&partner=" + PARTNER + "&_input_charset" + INPUT_CHARSET;
    StringBuffer result = new StringBuffer();
    SAXReader reader = new SAXReader();
    Document doc = reader.read(new URL(strUrl).openStream());
    List<Node> nodeList = doc.selectNodes("//alipay/*");
    for (Node node : nodeList) {
      if (node.getName().equals("is_success") && node.getText().equals("T")) {
        List<Node> nodeList1 = doc.selectNodes("//response/timestamp/*");
        for (Node node1 : nodeList1) {
          result.append(node1.getText());
        }
      }
    }
    return result.toString();
  }
}