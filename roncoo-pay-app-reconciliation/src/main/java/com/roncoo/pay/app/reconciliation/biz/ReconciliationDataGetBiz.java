package com.roncoo.pay.app.reconciliation.biz;
import java.text.SimpleDateFormat;
import com.roncoo.pay.trade.entity.RpTradePaymentRecord;
import com.roncoo.pay.trade.enums.TradeStatusEnum;
import org.apache.commons.logging.Log;
import com.roncoo.pay.trade.service.RpTradePaymentQueryService;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 平台数据获取biz业务类.
 * 
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
@Component(value = "reconciliationDataGetBiz") public class ReconciliationDataGetBiz {
  private static final Log LOG = LogFactory.getLog(ReconciliationDataGetBiz.class);

  @Autowired private RpTradePaymentQueryService rpTradePaymentQueryService;

  /**
	 * 获取平台指定支付渠道、指定订单日下[所有成功]的数据
	 * 
	 * @param billDate
	 *            账单日
	 * @param interfaceCode
	 *            支付渠道
	 * @return
	 */
  public List<RpTradePaymentRecord> getSuccessPlatformDateByBillDate(Date billDate, String interfaceCode) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String billDateStr = sdf.format(billDate);
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("billDate", billDateStr);
    paramMap.put("interfaceCode", interfaceCode);
    paramMap.put("status", TradeStatusEnum.SUCCESS.name());
    LOG.info("\u5f00\u59cb\u67e5\u8be2\u5e73\u53f0\u652f\u4ed8\u6210\u529f\u7684\u6570\u636e\uff1abillDate[" + billDateStr + "],\u652f\u4ed8\u65b9\u5f0f\u4e3a[" + interfaceCode + "]");
    List<RpTradePaymentRecord> recordList = rpTradePaymentQueryService.listPaymentRecord(paramMap);
    if (recordList == null) {
      recordList = new ArrayList<RpTradePaymentRecord>();
    }
    LOG.info("\u67e5\u8be2\u5f97\u5230\u7684\u6570\u636ecount[" + recordList.size() + "]");
    return recordList;
  }

  /**
	 * 获取平台指定支付渠道、指定订单日下[所有]的数据
	 * 
	 * @param billDate
	 *            账单日
	 * @param interfaceCode
	 *            支付渠道
	 * @return
	 */
  public List<RpTradePaymentRecord> getAllPlatformDateByBillDate(Date billDate, String interfaceCode) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String billDateStr = sdf.format(billDate);
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("billDate", billDateStr);
    paramMap.put("interfaceCode", interfaceCode);
    LOG.info("\u5f00\u59cb\u67e5\u8be2\u5e73\u53f0\u652f\u4ed8\u6240\u6709\u7684\u6570\u636e\uff1abillDate[" + billDateStr + "],\u652f\u4ed8\u65b9\u5f0f\u4e3a[" + interfaceCode + "]");
    List<RpTradePaymentRecord> recordList = rpTradePaymentQueryService.listPaymentRecord(paramMap);
    if (recordList == null) {
      recordList = new ArrayList<RpTradePaymentRecord>();
    }
    LOG.info("\u67e5\u8be2\u5f97\u5230\u7684\u6570\u636ecount[" + recordList.size() + "]");
    return recordList;
  }
}