package com.roncoo.pay.reconciliation.fileDown.impl;
import java.io.File;
import com.alibaba.druid.util.StringUtils;
import java.io.IOException;
import com.roncoo.pay.reconciliation.fileDown.service.FileDown;
import java.text.SimpleDateFormat;
import com.roncoo.pay.reconciliation.utils.FileUtils;
import java.util.Date;
import com.roncoo.pay.reconciliation.utils.SignHelper;
import java.util.HashMap;
import com.roncoo.pay.reconciliation.utils.WeiXinBaseUtils;
import java.util.Iterator;
import com.roncoo.pay.reconciliation.utils.https.HttpClientUtil;
import java.util.Map.Entry;
import com.roncoo.pay.reconciliation.utils.https.HttpResponse;
import org.apache.commons.logging.Log;
import com.roncoo.pay.trade.utils.WeixinConfigUtil;
import org.apache.commons.logging.LogFactory;

/**
 * 微信文件下载类
 *
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
public class WinXinFileDown implements FileDown {
  private static final Log LOG = LogFactory.getLog(WinXinFileDown.class);

  /*** 配置全部放入weixinpay_config.properties配置文件中/ ***/
  private String url = WeixinConfigUtil.readConfig("download_bill_url");

  private String appid = WeixinConfigUtil.readConfig("appId");



  private String mch_id = WeixinConfigUtil.readConfig("mch_id");

  private String bill_date;

  private String appSecret = WeixinConfigUtil.readConfig("partnerKey");

  private String bill_type = WeixinConfigUtil.readConfig("bill_type");

  /**
	 * 文件下载类
	 *
	 * @param billDate
	 *            账单日
	 * @param dir
	 *            账单保存路径
	 * 
	 */
  public File fileDown(Date billDate, String dir) throws IOException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    bill_date = sdf.format(billDate);
    HttpResponse response = null;
    try {
      String xml = this.generateXml();
      LOG.info(xml);
      response = HttpClientUtil.httpsRequest(url, "POST", xml);
      File file = new File(dir, bill_date + "_" + bill_type.toLowerCase() + ".txt");
      int index = 1;
      while (file.exists()) {
        file = new File(dir, bill_date + "_" + bill_type.toLowerCase() + index + ".txt");
        index++;
      }
      return FileUtils.saveFile(response, file);
    } catch (IOException e) {
      throw new IOException("\u4e0b\u8f7d\u5fae\u4fe1\u8d26\u5355\u5931\u8d25", e);
    } finally {
      try {
        if (response != null) {
          response.close();
        }
      } catch (IOException e) {
        LOG.error("\u5173\u95ed\u4e0b\u8f7d\u8d26\u5355\u7684\u6d41/\u8fde\u63a5\u5931\u8d25", e);
      }
    }
  }

  /**
	 * 根据微信接口要求，生成xml文件
	 * 
	 * @param appId
	 *            必填
	 * @param mchId
	 *            必填
	 * @param billDate
	 *            必填, 下载对账单的日期(最小单位天)
	 * @param billType
	 *            下载单类型
	 * @param appSecret
	 *            必填, 供签名使用
	 * @return
	 */
  public String generateXml() {
    HashMap<String, String> params = new HashMap<String, String>();
    params.put("appid", appid);
    params.put("mch_id", mch_id);
    params.put("bill_date", bill_date);
    params.put("bill_type", bill_type);
    params.put("nonce_str", WeiXinBaseUtils.createNoncestr());
    for (Iterator<Entry<String, String>> it = params.entrySet().iterator(); it.hasNext(); ) {
      Entry<String, String> entry = it.next();
      if (StringUtils.isEmpty(entry.getValue())) {
        it.remove();
      }
    }
    String sign = SignHelper.getSign(params, appSecret);
    params.put("sign", sign.toUpperCase());
    return WeiXinBaseUtils.arrayToXml(params);
  }
}