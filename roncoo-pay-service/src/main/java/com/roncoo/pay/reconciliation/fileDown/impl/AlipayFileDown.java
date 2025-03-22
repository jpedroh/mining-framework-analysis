package com.roncoo.pay.reconciliation.fileDown.impl;
import java.io.File;
import com.roncoo.pay.common.core.utils.DateUtils;
import java.io.FileWriter;
import com.roncoo.pay.reconciliation.fileDown.service.FileDown;
import java.io.IOException;
import com.roncoo.pay.reconciliation.utils.alipay.AlipaySubmit;
import java.text.SimpleDateFormat;
import com.roncoo.pay.reconciliation.utils.alipay.httpClient.HttpProtocolHandler;
import java.util.Date;
import com.roncoo.pay.reconciliation.utils.alipay.httpClient.HttpRequest;
import java.util.HashMap;
import com.roncoo.pay.reconciliation.utils.alipay.httpClient.HttpResponse;
import java.util.Map;
import com.roncoo.pay.reconciliation.utils.alipay.httpClient.HttpResultType;
import com.roncoo.pay.trade.utils.AlipayConfigUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 支付宝账单下载.
 *
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
public class AlipayFileDown implements FileDown {
  private static final Log LOG = LogFactory.getLog(AlipayFileDown.class);

  SimpleDateFormat timestampSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  SimpleDateFormat billDateSDF = new SimpleDateFormat("yyyy-MM-dd");

  /*** 配置全部放入alipay_config.properties配置文件中/ ***/
  private String partner = AlipayConfigUtil.readConfig("partner");

  private String url = AlipayConfigUtil.readConfig("alipay_gateway_new");

  private String charset = AlipayConfigUtil.readConfig("input_charset");

  private String gmt_start_time = "";

  private String gmt_end_time = "";

  private String pageNo = "1";

  /**
	 * 文件下载类
	 *
	 * @param billDate
	 *            账单日
	 * @param dir
	 *            账单保存路径
	 * 
	 */
  public File fileDown(Date fileDate, String dir) throws Exception {
    LOG.info("======\u5f00\u59cb\u4e0b\u8f7d\u652f\u4ed8\u5b9d\u5bf9\u8d26\u5355");
    String bill_begin_date = billDateSDF.format(fileDate);
    String bill_end_date = billDateSDF.format(DateUtils.addDay(fileDate, 1));
    gmt_start_time = bill_begin_date + " 00:00:00";
    gmt_end_time = bill_end_date + " 00:00:00";
    HttpResponse response = null;
    Map<String, String> sParaTemp = new HashMap<String, String>();
    sParaTemp.put("service", "account.page.query");
    sParaTemp.put("partner", partner);
    sParaTemp.put("_input_charset", charset);
    sParaTemp.put("page_no", pageNo);
    sParaTemp.put("gmt_start_time", gmt_start_time);
    sParaTemp.put("gmt_end_time", gmt_end_time);
    response = this.buildRequest(sParaTemp);
    if (response == null) {
      return null;
    }
    String stringResult = response.getStringResult();
    File file = this.createFile(bill_begin_date, stringResult, dir);
    return file;
  }

  /**
	 * 建立请求，以模拟远程HTTP的POST请求方式构造并获取支付宝的处理结果
	 * 
	 * @param sParaTemp
	 *            请求参数
	 * @return 支付宝处理结果
	 * @throws Exception
	 */
  public HttpResponse buildRequest(Map<String, String> sParaTemp) throws Exception {
    Map<String, String> sPara = AlipaySubmit.buildRequestPara(sParaTemp);
    HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
    HttpRequest request = new HttpRequest(HttpResultType.BYTES);
    request.setCharset(charset);
    request.setParameters(AlipaySubmit.generatNameValuePair(sPara));
    request.setUrl(url + "_input_charset=" + charset);
    HttpResponse response = httpProtocolHandler.execute(request, "", "");
    if (response == null) {
      return null;
    }
    return response;
  }

  /**
	 * 创建账单文件
	 * 
	 * @param bill_date
	 *            账单日
	 * @param stringResult
	 *            文件内容
	 * @param dir
	 *            文件保存路径
	 * @return
	 * @throws IOException
	 */
  private File createFile(String bill_date, String stringResult, String dir) throws IOException {
    File file = new File(dir, bill_date + "_" + ".xml");
    int index = 1;
    while (file.exists()) {
      file = new File(dir, bill_date + "_" + index + ".xml");
      index++;
    }
    if (!file.getParentFile().exists()) {
      if (!file.getParentFile().mkdirs()) {
        throw new IOException("\u521b\u5efa\u6587\u4ef6(\u7236\u5c42\u6587\u4ef6\u5939)\u5931\u8d25, filepath: " + file.getAbsolutePath());
      }
    }
    if (!file.exists()) {
      if (!file.createNewFile()) {
        throw new IOException("\u521b\u5efa\u6587\u4ef6\u5931\u8d25, filepath: " + file.getAbsolutePath());
      }
    }
    try {
      FileWriter fileWriter = new FileWriter(file);
      fileWriter.write(stringResult);
      fileWriter.close();
    } catch (IOException e) {
      LOG.info("\u628a\u652f\u4ed8\u5b9d\u8fd4\u56de\u7684\u5bf9\u8d26\u6570\u636e\u5199\u5165\u6587\u4ef6\u5f02\u5e38:" + e);
    }
    return file;
  }
}