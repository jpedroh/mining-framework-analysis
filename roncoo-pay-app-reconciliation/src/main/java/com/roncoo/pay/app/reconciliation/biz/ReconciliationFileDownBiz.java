package com.roncoo.pay.app.reconciliation.biz;
import java.io.File;
import com.roncoo.pay.common.core.utils.StringUtil;
import java.util.Date;
import com.roncoo.pay.reconciliation.fileDown.service.ReconciliationFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 对账文件下载业务逻辑.
 *
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
@Component(value = "reconciliationFileDownBiz") public class ReconciliationFileDownBiz {
  private static final Log LOG = LogFactory.getLog(ReconciliationFileDownBiz.class);

  private static final int DOWNLOAD_TRY_TIMES = 3;

  @Autowired private ReconciliationFactory reconciliationFactory;

  /**
	 * 请求下载对账文件 .
	 * 
	 * @param interfaceCode
	 *            支付渠道
	 * @param billDate
	 *            账单日
	 * @return
	 */
  public File downReconciliationFile(String interfaceCode, Date billDate) {
    if (StringUtil.isEmpty(interfaceCode)) {
      LOG.info("\u652f\u4ed8\u6e20\u9053\u7f16\u7801\u4e3a\u7a7a");
      return null;
    }
    return this.downFile(interfaceCode, billDate);
  }

  /**
	 * 下载文件
	 * 
	 * @param interfaceCode
	 *            接口编码
	 * @param tradeGainCheckFileTime
	 *            业务对账文件的获取时间
	 */
  private File downFile(String interfaceCode, Date billDate) {
    LOG.info("\u94f6\u884c\u6e20\u9053\u7f16\u53f7[" + interfaceCode + "],\u8fdb\u5165\u4e0b\u8f7d\u4e1a\u52a1\u5bf9\u8d26\u6587\u4ef6\u64cd\u4f5c>>>");
    try {
      File file = null;
      int downloadTrytimes = 0;
      while (file == null && downloadTrytimes < DOWNLOAD_TRY_TIMES) {
        try {
          downloadTrytimes++;
          file = reconciliationFactory.fileDown(interfaceCode, billDate);
        } catch (Exception e) {
          LOG.error("\u4e0b\u8f7d\u8d26\u5355\u6587\u4ef6\u5931\u8d25", e);
          Thread.sleep(10000);
        }
      }
      return file;
    } catch (Exception e) {
      LOG.error("\u4e0b\u8f7d\u5fae\u4fe1\u8d26\u5355\u6587\u4ef6\u5931\u8d25", e);
    }
    return null;
  }
}