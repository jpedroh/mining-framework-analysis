package com.roncoo.pay.account.dao.impl;
import com.roncoo.pay.account.entity.RpAccountHistory;
import com.roncoo.pay.account.dao.RpAccountHistoryDao;
import com.roncoo.pay.account.vo.DailyCollectAccountHistoryVo;
import com.roncoo.pay.common.core.dao.impl.BaseDaoImpl;
import java.util.List;
import org.springframework.stereotype.Repository;
import java.util.Map;

/**
 * 账户历史dao实现类
 * 龙果学院：www.roncoo.com
 * @author：zenghao
 */
@Repository public class RpAccountHistoryDaoImpl extends BaseDaoImpl<RpAccountHistory> implements RpAccountHistoryDao {
  public List<RpAccountHistory> listPageByParams(Map<String, Object> params) {
    return this.listBy(params);
  }

  public List<DailyCollectAccountHistoryVo> listDailyCollectAccountHistoryVo(Map<String, Object> params) {
    return this.getSessionTemplate().selectList(getStatement("listDailyCollectAccountHistoryVo"), params);
  }

  /** 更新账户风险预存期外的账户历史记录记为结算完成 **/
  public void updateCompleteSettTo100(Map<String, Object> params) {
    this.getSessionTemplate().update(getStatement("updateCompleteSettTo100"), params);
  }
}