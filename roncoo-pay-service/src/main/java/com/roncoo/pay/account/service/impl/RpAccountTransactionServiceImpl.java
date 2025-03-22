package com.roncoo.pay.account.service.impl;
import java.math.BigDecimal;
import com.roncoo.pay.account.dao.RpAccountDao;
import java.util.Date;
import com.roncoo.pay.account.dao.RpAccountHistoryDao;
import java.util.HashMap;
import com.roncoo.pay.account.entity.RpAccount;
import java.util.Map;
import com.roncoo.pay.account.entity.RpAccountHistory;
import com.roncoo.pay.account.exception.AccountBizException;
import com.roncoo.pay.account.enums.AccountFundDirectionEnum;
import com.roncoo.pay.trade.enums.TrxTypeEnum;
import com.roncoo.pay.account.service.RpAccountTransactionService;
import org.apache.commons.logging.Log;
import com.roncoo.pay.common.core.enums.PublicEnum;
import org.apache.commons.logging.LogFactory;
import com.roncoo.pay.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.roncoo.pay.common.core.utils.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账户操作service实现类
 * 龙果学院：www.roncoo.com
 * @author：zenghao
 */
@Service(value = "rpAccountTransactionService") public class RpAccountTransactionServiceImpl implements RpAccountTransactionService {
  private static final Log LOG = LogFactory.getLog(RpAccountTransactionServiceImpl.class);

  @Autowired private RpAccountDao rpAccountDao;

  @Autowired private RpAccountHistoryDao rpAccountHistoryDao;

  /**
	 * 根据用户编号编号获取账户信息
	 * 
	 * @param userNO
	 *            用户编号
	 * @param isPessimist
	 *            是否加行锁
	 * @return
	 */
  private RpAccount getByUserNo_IsPessimist(String userNo, boolean isPessimist) {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("userNo", userNo);
    map.put("isPessimist", isPessimist);
    return rpAccountDao.getByUserNo(map);
  }

  /**
	 * 加款
	 * 
	 * @param userNo
	 *            用户编号
	 * @param amount
	 *            加款金额
	 * @param requestNo
	 *            请求号
	 * @param trxType
	 *            业务类型
	 * @param remark
	 *            备注
	 */
  @Transactional(rollbackFor = Exception.class) public RpAccount creditToAccount(String userNo, BigDecimal amount, String requestNo, String trxType, String remark) {
    return this.creditToAccount(userNo, amount, requestNo, null, trxType, remark);
  }

  /**
	 * 加款:有银行流水
	 * 
	 * @param userNo
	 *            用户编号
	 * @param amount
	 *            加款金额
	 * @param requestNo
	 *            请求号
	 * @param trxType
	 *            业务类型
	 * @param remark
	 *            备注
	 */
  @Transactional(rollbackFor = Exception.class) public RpAccount creditToAccount(String userNo, BigDecimal amount, String requestNo, String bankTrxNo, String trxType, String remark) {
    RpAccount account = this.getByUserNo_IsPessimist(userNo, true);
    if (account == null) {
      throw AccountBizException.ACCOUNT_NOT_EXIT;
    }
    Date lastModifyDate = account.getEditTime();
    if (!DateUtils.isSameDayWithToday(lastModifyDate)) {
      account.setTodayExpend(BigDecimal.ZERO);
      account.setTodayIncome(BigDecimal.ZERO);
    }
    if (TrxTypeEnum.EXPENSE.name().equals(trxType)) {
      account.setTotalIncome(account.getTotalIncome().add(amount));
      if (DateUtils.isSameDayWithToday(lastModifyDate)) {
        account.setTodayIncome(account.getTodayIncome().add(amount));
      } else {
        account.setTodayIncome(amount);
      }
    }
    String completeSett = PublicEnum.NO.name();
    String isAllowSett = PublicEnum.YES.name();
    account.setBalance(account.getBalance().add(amount));
    account.setEditTime(new Date());
    RpAccountHistory accountHistoryEntity = new RpAccountHistory();
    accountHistoryEntity.setCreateTime(new Date());
    accountHistoryEntity.setEditTime(new Date());
    accountHistoryEntity.setIsAllowSett(isAllowSett);
    accountHistoryEntity.setAmount(amount);
    accountHistoryEntity.setBalance(account.getBalance());
    accountHistoryEntity.setRequestNo(requestNo);
    accountHistoryEntity.setBankTrxNo(bankTrxNo);
    accountHistoryEntity.setIsCompleteSett(completeSett);
    accountHistoryEntity.setRemark(remark);
    accountHistoryEntity.setFundDirection(AccountFundDirectionEnum.ADD.name());
    accountHistoryEntity.setAccountNo(account.getAccountNo());
    accountHistoryEntity.setTrxType(trxType);
    accountHistoryEntity.setId(StringUtil.get32UUID());
    accountHistoryEntity.setUserNo(userNo);
    this.rpAccountHistoryDao.insert(accountHistoryEntity);
    this.rpAccountDao.update(account);
    LOG.info("\u8d26\u6237\u52a0\u6b3e\u6210\u529f\uff0c\u5e76\u8bb0\u5f55\u4e86\u8d26\u6237\u5386\u53f2");
    return account;
  }

  /**
	 * 减款
	 * 
	 * @param userNo
	 *            用户编号
	 * @param amount
	 *            减款金额
	 * @param requestNo
	 *            请求号
	 * @param trxType
	 *            业务类型
	 * @param remark
	 *            备注
	 */
  @Transactional(rollbackFor = Exception.class) public RpAccount debitToAccount(String userNo, BigDecimal amount, String requestNo, String trxType, String remark) {
    return this.debitToAccount(userNo, amount, requestNo, null, trxType, remark);
  }

  /**
	 * 减款:有银行流水
	 * 
	 * @param userNo
	 *            用户编号
	 * @param amount
	 *            减款金额
	 * @param requestNo
	 *            请求号
	 * @param trxType
	 *            业务类型
	 * @param remark
	 *            备注
	 */
  @Transactional(rollbackFor = Exception.class) public RpAccount debitToAccount(String userNo, BigDecimal amount, String requestNo, String bankTrxNo, String trxType, String remark) {
    RpAccount account = this.getByUserNo_IsPessimist(userNo, true);
    if (account == null) {
      throw AccountBizException.ACCOUNT_NOT_EXIT;
    }
    BigDecimal availableBalance = account.getAvailableBalance();
    String isAllowSett = PublicEnum.YES.name();
    String completeSett = PublicEnum.NO.name();
    if (availableBalance.compareTo(amount) == -1) {
      throw AccountBizException.ACCOUNT_SUB_AMOUNT_OUTLIMIT;
    }
    account.setBalance(account.getBalance().subtract(amount));
    Date lastModifyDate = account.getEditTime();
    if (!DateUtils.isSameDayWithToday(lastModifyDate)) {
      account.setTodayExpend(BigDecimal.ZERO);
      account.setTodayIncome(BigDecimal.ZERO);
      account.setTodayExpend(amount);
    } else {
      account.setTodayExpend(account.getTodayExpend().add(amount));
    }
    account.setTotalExpend(account.getTodayExpend().add(amount));
    account.setEditTime(new Date());
    RpAccountHistory accountHistoryEntity = new RpAccountHistory();
    accountHistoryEntity.setCreateTime(new Date());
    accountHistoryEntity.setEditTime(new Date());
    accountHistoryEntity.setIsAllowSett(isAllowSett);
    accountHistoryEntity.setAmount(amount);
    accountHistoryEntity.setBalance(account.getBalance());
    accountHistoryEntity.setRequestNo(requestNo);
    accountHistoryEntity.setBankTrxNo(bankTrxNo);
    accountHistoryEntity.setIsCompleteSett(completeSett);
    accountHistoryEntity.setRemark(remark);
    accountHistoryEntity.setFundDirection(AccountFundDirectionEnum.SUB.name());
    accountHistoryEntity.setAccountNo(account.getAccountNo());
    accountHistoryEntity.setTrxType(trxType);
    accountHistoryEntity.setId(StringUtil.get32UUID());
    accountHistoryEntity.setUserNo(userNo);
    this.rpAccountHistoryDao.insert(accountHistoryEntity);
    this.rpAccountDao.update(account);
    return account;
  }

  /**
	 * 冻结账户资金
	 * 
	 * @param userNo
	 *            用户编号
	 * @param freezeAmount
	 *            冻结金额
	 **/
  @Transactional(rollbackFor = Exception.class) public RpAccount freezeAmount(String userNo, BigDecimal freezeAmount) {
    RpAccount account = this.getByUserNo_IsPessimist(userNo, true);
    if (account == null) {
      throw AccountBizException.ACCOUNT_NOT_EXIT;
    }
    account.setEditTime(new Date());
    if (!account.availableBalanceIsEnough(freezeAmount)) {
      throw AccountBizException.ACCOUNT_FROZEN_AMOUNT_OUTLIMIT;
    }
    account.setUnbalance(account.getUnbalance().add(freezeAmount));
    this.rpAccountDao.update(account);
    return account;
  }

  /**
	 * 结算成功 解冻金额+减款
	 * 
	 * @param userNo
	 *            用户编号
	 * @param amount
	 *            解冻和减款金额
	 * @param requestNo
	 *            流水号
	 * @param trxType
	 *            业务类型
	 * @param remark
	 *            备注
	 */
  @Transactional(rollbackFor = Exception.class) public RpAccount unFreezeAmount(String userNo, BigDecimal amount, String requestNo, String trxType, String remark) {
    RpAccount account = this.getByUserNo_IsPessimist(userNo, true);
    if (account == null) {
      throw AccountBizException.ACCOUNT_NOT_EXIT;
    }
    Date lastModifyDate = account.getEditTime();
    if (!DateUtils.isSameDayWithToday(lastModifyDate)) {
      account.setTodayExpend(BigDecimal.ZERO);
      account.setTodayIncome(BigDecimal.ZERO);
      account.setTodayExpend(amount);
    } else {
      account.setTodayExpend(account.getTodayExpend().add(amount));
    }
    account.setTotalExpend(account.getTodayExpend().add(amount));
    if (account.getUnbalance().subtract(amount).compareTo(BigDecimal.ZERO) == -1) {
      throw AccountBizException.ACCOUNT_UN_FROZEN_AMOUNT_OUTLIMIT;
    }
    account.setEditTime(new Date());
    account.setBalance(account.getBalance().subtract(amount));
    account.setUnbalance(account.getUnbalance().subtract(amount));
    account.setSettAmount(account.getSettAmount().subtract(amount));
    String isAllowSett = PublicEnum.NO.name();
    String completeSett = PublicEnum.NO.name();
    RpAccountHistory accountHistoryEntity = new RpAccountHistory();
    accountHistoryEntity.setCreateTime(new Date());
    accountHistoryEntity.setEditTime(new Date());
    accountHistoryEntity.setIsAllowSett(isAllowSett);
    accountHistoryEntity.setAmount(amount);
    accountHistoryEntity.setBalance(account.getBalance());
    accountHistoryEntity.setRequestNo(requestNo);
    accountHistoryEntity.setIsCompleteSett(completeSett);
    accountHistoryEntity.setRemark(remark);
    accountHistoryEntity.setFundDirection(AccountFundDirectionEnum.SUB.name());
    accountHistoryEntity.setAccountNo(account.getAccountNo());
    accountHistoryEntity.setTrxType(trxType);
    accountHistoryEntity.setUserNo(userNo);
    this.rpAccountHistoryDao.insert(accountHistoryEntity);
    this.rpAccountDao.update(account);
    return account;
  }

  /**
	 * 结算失败 解冻金额
	 * 
	 * @param userNo
	 *            用户编号
	 * @param amount
	 *            解冻和减款金额
	 * @param requestNo
	 *            流水号
	 * @param trxType
	 *            业务类型
	 * @param remark
	 *            备注
	 */
  @Transactional(rollbackFor = Exception.class) public RpAccount unFreezeSettAmount(String userNo, BigDecimal amount) {
    RpAccount account = this.getByUserNo_IsPessimist(userNo, true);
    if (account == null) {
      throw AccountBizException.ACCOUNT_NOT_EXIT;
    }
    Date lastModifyDate = account.getEditTime();
    if (!DateUtils.isSameDayWithToday(lastModifyDate)) {
      account.setTodayExpend(BigDecimal.ZERO);
      account.setTodayIncome(BigDecimal.ZERO);
    }
    if (account.getUnbalance().subtract(amount).compareTo(BigDecimal.ZERO) == -1) {
      throw AccountBizException.ACCOUNT_UN_FROZEN_AMOUNT_OUTLIMIT;
    }
    account.setEditTime(new Date());
    account.setUnbalance(account.getUnbalance().subtract(amount));
    this.rpAccountDao.update(account);
    return account;
  }

  /**
	 * 更新账户历史中的结算状态，并且累加可结算金额
	 * 
	 * @param userNo
	 *            用户编号
	 * @param collectDate
	 *            汇总日期
	 * @param riskDay
	 *            风险预存期
	 * @param totalAmount
	 *            可结算金额累计
	 * 
	 */
  @Transactional(rollbackFor = Exception.class) public void settCollectSuccess(String userNo, String collectDate, int riskDay, BigDecimal totalAmount) {
    LOG.info("==>settCollectSuccess");
    LOG.info(String.format("==>userNo:%s, collectDate:%s, riskDay:%s", userNo, collectDate, riskDay));
    RpAccount account = this.getByUserNo_IsPessimist(userNo, true);
    if (account == null) {
      throw AccountBizException.ACCOUNT_NOT_EXIT.newInstance("\u8d26\u6237\u4e0d\u5b58\u5728,\u7528\u6237\u7f16\u53f7{%s}", userNo).print();
    }
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("accountNo", account.getAccountNo());
    params.put("statDate", collectDate);
    params.put("riskDay", riskDay);
    rpAccountHistoryDao.updateCompleteSettTo100(params);
    account.setSettAmount(account.getSettAmount().add(totalAmount));
    rpAccountDao.update(account);
    LOG.info("==>settCollectSuccess<==");
  }
}