package com.roncoo.pay.app.settlement.biz;
import java.util.Date;
import com.roncoo.pay.account.entity.RpAccount;
import java.util.List;
import com.roncoo.pay.account.service.RpSettHandleService;
import com.roncoo.pay.common.core.enums.PublicEnum;
import com.roncoo.pay.user.entity.RpUserPayConfig;
import com.roncoo.pay.user.service.RpUserPayConfigService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 结算业务逻辑类.
 * 龙果学院：www.roncoo.com
 * @author zenghao
 */
@Component(value = "settBiz") public class SettBiz {
  private static final Log LOG = LogFactory.getLog(SettBiz.class);

  @Autowired private DailySettCollectBiz dailySettCollectBiz;

  @Autowired private RpUserPayConfigService rpUserPayConfigService;

  @Autowired private RpSettHandleService rpSettHandleService;

  /**
	 * 发起每日待结算数据统计汇总.<br/>
	 * 
	 * @param userEnterpriseList
	 *            结算商户.<br/>
	 * @param collectDate
	 *            统计截止日期(一般为昨天的日期)
	 */
  public void launchDailySettCollect(List<RpAccount> accountList, Date endDate) {
    if (accountList == null || accountList.isEmpty()) {
      return;
    }
    for (RpAccount rpAccount : accountList) {
      try {
        LOG.debug(rpAccount.getUserNo() + ":\u5f00\u59cb\u6c47\u603b");
        dailySettCollectBiz.dailySettCollect(rpAccount, endDate);
        LOG.debug(rpAccount.getUserNo() + ":\u6c47\u603b\u7ed3\u675f");
      } catch (Exception e) {
        LOG.error(rpAccount.getUserNo() + ":\u6c47\u603b\u5f02\u5e38", e);
      }
    }
  }

  /**
	 * 发起定期自动结算.<br/>
	 * 
	 * @param userEnterpriseList
	 *            结算商户.<br/>
	 */
  public void launchAutoSett(List<RpAccount> accountList) {
    if (accountList == null || accountList.isEmpty()) {
      return;
    }
    for (RpAccount rpAccount : accountList) {
      try {
        RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByUserNo(rpAccount.getUserNo());
        if (rpUserPayConfig == null) {
          LOG.info(rpAccount.getUserNo() + "\u6ca1\u6709\u5546\u5bb6\u8bbe\u7f6e\u4fe1\u606f\uff0c\u4e0d\u8fdb\u884c\u7ed3\u7b97");
          continue;
        }
        if (rpUserPayConfig.getIsAutoSett().equals(PublicEnum.YES.name())) {
          LOG.debug(rpAccount.getUserNo() + ":\u5f00\u59cb\u81ea\u52a8\u7ed3\u7b97");
          rpSettHandleService.launchAutoSett(rpAccount.getUserNo());
          LOG.debug(rpAccount.getUserNo() + ":\u81ea\u52a8\u7ed3\u7b97\u7ed3\u675f");
        } else {
          LOG.info(rpAccount.getUserNo() + ":\u975e\u81ea\u52a8\u7ed3\u7b97\u5546\u5bb6");
        }
      } catch (Exception e) {
        LOG.error("\u81ea\u52a8\u7ed3\u7b97\u5f02\u5e38\uff1a" + rpAccount.getUserNo(), e);
      }
    }
  }
}