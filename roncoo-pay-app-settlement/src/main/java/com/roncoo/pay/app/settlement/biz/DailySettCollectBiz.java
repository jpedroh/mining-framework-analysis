package com.roncoo.pay.app.settlement.biz;
import java.util.Date;
import com.roncoo.pay.account.entity.RpAccount;
import com.roncoo.pay.account.service.RpSettHandleService;
import com.roncoo.pay.user.entity.RpUserPayConfig;
import com.roncoo.pay.user.service.RpUserPayConfigService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 每日待结算数据汇总.
 * 龙果学院：www.roncoo.com
 * @author zenghao
 */
@Component(value = "dailySettCollectBiz") public class DailySettCollectBiz {
  private static final Log LOG = LogFactory.getLog(DailySettCollectBiz.class);

  @Autowired private RpSettHandleService rpSettHandleService;

  @Autowired private RpUserPayConfigService rpUserPayConfigService;

  /**
	 * 按单个商户发起每日待结算数据统计汇总.<br/>
	 * 
	 * @param userEnterprise
	 *            单个商户的结算规则.<br/>
	 * @param endDate
	 *            统计日期 ==定时器执行的日期<br/>
	 */
  public void dailySettCollect(RpAccount rpAccount, Date endDate) {
    LOG.info("\u6309\u5355\u4e2a\u5546\u6237\u53d1\u8d77\u6bcf\u65e5\u5f85\u7ed3\u7b97\u6570\u636e\u7edf\u8ba1\u6c47\u603b");
    RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByUserNo(rpAccount.getUserNo());
    if (rpUserPayConfig == null) {
      LOG.info("userNo:" + rpAccount.getUserNo() + ":\u6ca1\u6709\u5546\u5bb6\u8bbe\u7f6e\u4fe1\u606f\uff0c\u4e0d\u8fdb\u884c\u6c47\u603b");
      return;
    }
    int riskDay = rpUserPayConfig.getRiskDay();
    rpSettHandleService.dailySettlementCollect(rpUserPayConfig.getUserNo(), endDate, riskDay, rpUserPayConfig.getUserName());
  }
}