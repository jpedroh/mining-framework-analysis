package com.roncoo.pay.account.service.impl;
import java.math.BigDecimal;
import com.roncoo.pay.account.dao.RpAccountDao;
import java.util.Date;
import com.roncoo.pay.account.dao.RpAccountHistoryDao;
import java.util.HashMap;
import com.roncoo.pay.account.entity.RpAccount;
import java.util.List;
import com.roncoo.pay.account.entity.RpAccountHistory;
import java.util.Map;
import com.roncoo.pay.account.exception.AccountBizException;
import org.apache.commons.lang.StringUtils;
import com.roncoo.pay.account.service.RpAccountQueryService;
import org.slf4j.Logger;
import com.roncoo.pay.account.vo.DailyCollectAccountHistoryVo;
import org.slf4j.LoggerFactory;
import com.roncoo.pay.common.core.enums.PublicStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import com.roncoo.pay.common.core.exception.BizException;
import org.springframework.stereotype.Service;
import com.roncoo.pay.common.core.page.PageBean;
import com.roncoo.pay.common.core.page.PageParam;
import com.roncoo.pay.common.core.utils.DateUtils;

/**
 * 账户查询service实现类
 * 龙果学院：www.roncoo.com
 * @author：zenghao
 */
@Service(value = "rpAccountQueryService") public class RpAccountQueryServiceImpl implements RpAccountQueryService {
  @Autowired private RpAccountDao rpAccountDao;

  @Autowired private RpAccountHistoryDao rpAccountHistoryDao;

  private static final Logger LOG = LoggerFactory.getLogger(RpAccountQueryServiceImpl.class);

  /**
	 * 根据账户编号获取账户信息
	 * 
	 * @param accountNo
	 *            账户编号
	 * @return
	 */
  public RpAccount getAccountByAccountNo(String accountNo) {
    LOG.info("\u6839\u636e\u8d26\u6237\u7f16\u53f7\u67e5\u8be2\u8d26\u6237\u4fe1\u606f");
    RpAccount account = this.rpAccountDao.getByAccountNo(accountNo);
    if (!DateUtils.isSameDayWithToday(account.getEditTime())) {
      account.setTodayExpend(BigDecimal.ZERO);
      account.setTodayIncome(BigDecimal.ZERO);
      account.setEditTime(new Date());
      rpAccountDao.update(account);
    }
    return account;
  }

  /**
	 * 根据用户编号编号获取账户信息
	 * 
	 * @param userNO
	 *            用户编号
	 * @return
	 */
  public RpAccount getAccountByUserNo(String userNo) {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("userNo", userNo);
    LOG.info("\u6839\u636e\u7528\u6237\u7f16\u53f7\u67e5\u8be2\u8d26\u6237\u4fe1\u606f");
    RpAccount account = this.rpAccountDao.getBy(map);
    if (account == null) {
      throw AccountBizException.ACCOUNT_NOT_EXIT;
    }
    if (!DateUtils.isSameDayWithToday(account.getEditTime())) {
      account.setTodayExpend(BigDecimal.ZERO);
      account.setTodayIncome(BigDecimal.ZERO);
      account.setEditTime(new Date());
      rpAccountDao.update(account);
    }
    return account;
  }

  /**
	 * 分页查询账户历史单用户
	 */
  public PageBean queryAccountHistoryListPage(PageParam pageParam, String accountNo) {
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("accountNo", accountNo);
    return rpAccountDao.listPage(pageParam, params);
  }

  /**
	 * 分页查询账户历史单角色
	 */
  public PageBean queryAccountHistoryListPageByRole(PageParam pageParam, Map<String, Object> params) {
    String accountType = (String) params.get("accountType");
    if (StringUtils.isBlank(accountType)) {
      throw AccountBizException.ACCOUNT_TYPE_IS_NULL;
    }
    return rpAccountDao.listPage(pageParam, params);
  }

  /**
	 * 获取账户历史单角色
	 * 
	 * @param accountNo
	 *            账户编号
	 * @param requestNo
	 *            请求号
	 * @param trxType
	 *            业务类型
	 * @return AccountHistory
	 */
  public RpAccountHistory getAccountHistoryByAccountNo_requestNo_trxType(String accountNo, String requestNo, Integer trxType) {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("accountNo", accountNo);
    map.put("requestNo", requestNo);
    map.put("trxType", trxType);
    return rpAccountHistoryDao.getBy(map);
  }

  /**
	 * 日汇总账户待结算金额 .
	 * 
	 * @param accountNo
	 *            账户编号
	 * @param statDate
	 *            统计日期
	 * @param riskDay
	 *            风险预测期
	 * @param fundDirection
	 *            资金流向
	 * @return
	 */
  public List<DailyCollectAccountHistoryVo> listDailyCollectAccountHistoryVo(String accountNo, String statDate, Integer riskDay, Integer fundDirection) {
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("accountNo", accountNo);
    params.put("statDate", statDate);
    params.put("riskDay", riskDay);
    params.put("fundDirection", fundDirection);
    return rpAccountHistoryDao.listDailyCollectAccountHistoryVo(params);
  }

  /**
	 * 根据参数分页查询账户.
	 * 
	 * @param pageParam
	 *            分页参数.
	 * @param params
	 *            查询参数，可以为null.
	 * @return AccountList.
	 * @throws BizException
	 */
  public PageBean queryAccountListPage(PageParam pageParam, Map<String, Object> params) {
    return rpAccountDao.listPage(pageParam, params);
  }

  /**
	 * 根据参数分页查询账户历史.
	 * 
	 * @param pageParam
	 *            分页参数.
	 * @param params
	 *            查询参数，可以为null.
	 * @return AccountHistoryList.
	 * @throws BizException
	 */
  public PageBean queryAccountHistoryListPage(PageParam pageParam, Map<String, Object> params) {
    return rpAccountHistoryDao.listPage(pageParam, params);
  }

  /**
	 * 获取所有账户
	 * @return
	 */
  @Override public List<RpAccount> listAll() {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("status", PublicStatusEnum.ACTIVE.name());
    return rpAccountDao.listBy(paramMap);
  }
}