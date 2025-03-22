package com.roncoo.pay.app.settlement.scheduled.impl;
import java.util.Date;
import com.roncoo.pay.account.entity.RpAccount;
import java.util.List;
import com.roncoo.pay.account.service.RpAccountQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import com.roncoo.pay.app.settlement.biz.SettBiz;
import org.springframework.stereotype.Component;
import com.roncoo.pay.app.settlement.scheduled.SettScheduled;

/**
 * 结算定时任务实现.
 * 龙果学院：www.roncoo.com
 * @author zenghao
 */
@Component(value = "settScheduled") public class SettScheduledImpl implements SettScheduled {
  @Autowired private SettBiz settBiz;

  @Autowired private RpAccountQueryService rpAccountQueryService;

  /**
	 * 发起每日待结算数据汇总.
	 */
  public void launchDailySettCollect() {
    List<RpAccount> list = rpAccountQueryService.listAll();
    Date endDate = new Date();
    settBiz.launchDailySettCollect(list, endDate);
  }

  /**
	 * 发起定期自动结算.
	 */
  public void launchAutoSett() {
    List<RpAccount> list = rpAccountQueryService.listAll();
    settBiz.launchAutoSett(list);
  }
}