package com.roncoo.pay.account.service.impl;
import java.math.BigDecimal;
import com.roncoo.pay.account.dao.RpSettDailyCollectDao;
import java.util.Date;
import com.roncoo.pay.account.dao.RpSettRecordDao;
import java.util.List;
import com.roncoo.pay.account.entity.RpAccount;
import com.roncoo.pay.account.exception.AccountBizException;
import com.roncoo.pay.account.entity.RpSettDailyCollect;
import com.roncoo.pay.account.exception.SettBizException;
import com.roncoo.pay.account.entity.RpSettRecord;
import com.roncoo.pay.trade.enums.TrxTypeEnum;
import com.roncoo.pay.account.enums.SettDailyCollectStatusEnum;
import com.roncoo.pay.user.exception.UserBizException;
import com.roncoo.pay.account.enums.SettDailyCollectTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import com.roncoo.pay.account.enums.SettModeTypeEnum;
import org.springframework.stereotype.Service;
import com.roncoo.pay.account.enums.SettRecordStatusEnum;
import org.springframework.transaction.annotation.Transactional;
import com.roncoo.pay.account.service.RpAccountQueryService;
import com.roncoo.pay.account.service.RpAccountTransactionService;
import com.roncoo.pay.account.service.RpSettHandleService;
import com.roncoo.pay.account.utils.AccountConfigUtil;
import com.roncoo.pay.account.vo.DailyCollectAccountHistoryVo;
import com.roncoo.pay.common.core.exception.BizException;
import com.roncoo.pay.common.core.utils.DateUtils;
import com.roncoo.pay.user.entity.RpUserBankAccount;
import com.roncoo.pay.user.entity.RpUserInfo;
import com.roncoo.pay.user.enums.BankAccountTypeEnum;
import com.roncoo.pay.user.service.RpUserBankAccountService;
import com.roncoo.pay.user.service.RpUserInfoService;

/**
 * 结算核心业务处理实现类
 * 龙果学院：www.roncoo.com
 * @author：zenghao
 */
@Service(value = "rpSettHandleService") public class RpSettHandleServiceImpl implements RpSettHandleService {
  @Autowired private RpSettDailyCollectDao rpSettDailyCollectDao;

  @Autowired private RpSettRecordDao rpSettRecordDao;

  @Autowired private RpAccountTransactionService rpAccountTransactionService;

  @Autowired private RpAccountQueryService rpAccountQueryService;

  @Autowired private RpUserInfoService rpUserInfoService;

  @Autowired private RpUserBankAccountService rpUserBankAccountService;

  /**
	 * 按单个商户发起每日待结算数据统计汇总.<br/>
	 * 
	 * @param userNo
	 *            用户编号.
	 * @param endDate
	 *            汇总结束日期.
	 * @param riskDay
	 *            风险预存期.
	 * @param userName
	 *            用户名称
	 * @param codeNum
	 *            企业代号
	 */
  @Transactional(rollbackFor = Exception.class) public void dailySettlementCollect(String userNo, Date endDate, int riskDay, String userName) {
    RpAccount account = rpAccountQueryService.getAccountByUserNo(userNo);
    String endDateStr = DateUtils.formatDate(endDate, "yyyy-MM-dd");
    List<DailyCollectAccountHistoryVo> accountHistoryList = rpAccountQueryService.listDailyCollectAccountHistoryVo(account.getAccountNo(), endDateStr, riskDay, null);
    BigDecimal totalAmount = BigDecimal.ZERO;
    for (DailyCollectAccountHistoryVo collectVo : accountHistoryList) {
      totalAmount = totalAmount.add(collectVo.getTotalAmount());
      RpSettDailyCollect dailyCollect = new RpSettDailyCollect();
      dailyCollect.setAccountNo(collectVo.getAccountNo());
      dailyCollect.setUserName(userName);
      dailyCollect.setCollectDate(collectVo.getCollectDate());
      dailyCollect.setCollectType(SettDailyCollectTypeEnum.ALL.name());
      dailyCollect.setTotalAmount(collectVo.getTotalAmount());
      dailyCollect.setTotalCount(collectVo.getTotalNum());
      dailyCollect.setSettStatus(SettDailyCollectStatusEnum.SETTLLED.name());
      dailyCollect.setRiskDay(collectVo.getRiskDay());
      dailyCollect.setRemark("");
      dailyCollect.setEditTime(new Date());
      rpSettDailyCollectDao.insert(dailyCollect);
    }
    rpAccountTransactionService.settCollectSuccess(userNo, endDateStr, riskDay, totalAmount);
  }

  /**
	 * 发起结算--对应与接口
	 * 
	 * @param userNo
	 * @param accountNo
	 * @param settAmount
	 * @param bankAccount
	 */
  public void launchSett(String userNo, BigDecimal settAmount) {
    RpAccount account = rpAccountQueryService.getAccountByUserNo(userNo);
    RpUserInfo userInfo = rpUserInfoService.getDataByMerchentNo(userNo);
    RpUserBankAccount rpUserBankAccount = rpUserBankAccountService.getByUserNo(userNo);
    BigDecimal availableAmount = account.getAvailableSettAmount();
    if (settAmount.compareTo(availableAmount) > 0) {
      throw AccountBizException.ACCOUNT_SUB_AMOUNT_OUTLIMIT;
    }
    if (rpUserBankAccount == null) {
      throw UserBizException.USER_BANK_ACCOUNT_IS_NULL;
    }
    String settType = SettModeTypeEnum.SELFHELP_SETTLE.name();
    this.launchSett(userNo, userInfo.getUserName(), account.getAccountNo(), settAmount, rpUserBankAccount, settType);
  }

  /**
	 * 发起结算
	 * 
	 * @param userNo
	 * @param accountNo
	 * @param settAmount
	 * @param bankAccount
	 * @param settType 发起结算方式:手动、自动
	 */
  @Transactional(rollbackFor = Exception.class) private void launchSett(String userNo, String userName, String accountNo, BigDecimal settAmount, RpUserBankAccount bankAccount, String settType) {
    RpSettRecord settRecord = new RpSettRecord();
    settRecord.setAccountNo(accountNo);
    settRecord.setCountry("\u4e2d\u56fd");
    settRecord.setProvince(bankAccount.getProvince());
    settRecord.setCity(bankAccount.getCity());
    settRecord.setAreas(bankAccount.getAreas());
    settRecord.setBankAccountAddress(bankAccount.getStreet());
    settRecord.setBankAccountName(bankAccount.getBankAccountName());
    settRecord.setBankCode(bankAccount.getBankCode());
    settRecord.setBankName(bankAccount.getBankName());
    settRecord.setBankAccountNo(bankAccount.getBankAccountNo());
    settRecord.setBankAccountType(bankAccount.getBankAccountType());
    settRecord.setOperatorLoginname("");
    settRecord.setOperatorRealname("");
    settRecord.setRemitAmount(settAmount);
    settRecord.setRemitRequestTime(new Date());
    settRecord.setSettAmount(settAmount);
    settRecord.setSettFee(BigDecimal.ZERO);
    settRecord.setSettMode(settType);
    settRecord.setSettStatus(SettRecordStatusEnum.WAIT_CONFIRM.name());
    settRecord.setUserName(userName);
    settRecord.setUserNo(userNo);
    settRecord.setMobileNo(bankAccount.getMobileNo());
    settRecord.setEditTime(new Date());
    rpSettRecordDao.insert(settRecord);
    rpAccountTransactionService.freezeAmount(userNo, settAmount);
  }

  /**
	 * 发起自动结算
	 * 
	 * @param userNo
	 */
  public void launchAutoSett(String userNo) {
    RpUserInfo userInfo = rpUserInfoService.getDataByMerchentNo(userNo);
    RpAccount account = rpAccountQueryService.getAccountByUserNo(userNo);
    BigDecimal settAmount = account.getAvailableSettAmount();
    String settMinAmount = AccountConfigUtil.readConfig("sett_min_amount");
    if (settAmount.compareTo(new BigDecimal(settMinAmount)) == -1) {
      throw new BizException("\u6bcf\u6b21\u53d1\u8d77\u7ed3\u7b97\u7684\u91d1\u989d\u5fc5\u987b\u5927\u4e8e:" + settMinAmount);
    }
    RpUserBankAccount rpUserBankAccount = rpUserBankAccountService.getByUserNo(userNo);
    if (rpUserBankAccount == null) {
      throw new BizException("\u6ca1\u6709\u7ed3\u7b97\u94f6\u884c\u5361\u4fe1\u606f\uff0c\u8bf7\u5148\u7ed1\u5b9a\u7ed3\u7b97\u94f6\u884c\u5361");
    }
    String bankType = rpUserBankAccount.getBankAccountType();
    if (bankType.equals(BankAccountTypeEnum.PRIVATE_DEBIT_ACCOUNT.name())) {
      String settMaxAmount = AccountConfigUtil.readConfig("sett_max_amount");
      if (settAmount.compareTo(new BigDecimal(settMaxAmount)) == 1) {
        throw new BizException("\u6bcf\u6b21\u53d1\u8d77\u7ed3\u7b97\u7684\u91d1\u989d\u5fc5\u987b\u5c0f\u4e8e:" + settMaxAmount);
      }
    }
    String userName = userInfo.getUserName();
    String accountNo = account.getAccountNo();
    String settType = SettModeTypeEnum.REGULAR_SETTLE.name();
    this.launchSett(userNo, userName, accountNo, settAmount, rpUserBankAccount, settType);
  }

  /**
	 * 结算审核
	 */
  public void audit(String settId, String settStatus, String remark) {
    RpSettRecord settRecord = rpSettRecordDao.getById(settId);
    if (!settRecord.getSettStatus().equals(SettRecordStatusEnum.WAIT_CONFIRM.name())) {
      throw SettBizException.SETT_STATUS_ERROR;
    }
    settRecord.setSettStatus(settStatus);
    settRecord.setEditTime(new Date());
    settRecord.setRemark(remark);
    rpSettRecordDao.update(settRecord);
    if (settStatus.equals(SettRecordStatusEnum.CANCEL.name())) {
      rpAccountTransactionService.unFreezeSettAmount(settRecord.getUserNo(), settRecord.getSettAmount());
    }
  }

  /**
	 * 打款
	 */
  @Transactional(rollbackFor = Exception.class) public void remit(String settId, String settStatus, String remark) {
    RpSettRecord settRecord = rpSettRecordDao.getById(settId);
    if (!settRecord.getSettStatus().equals(SettRecordStatusEnum.CONFIRMED.name())) {
      throw SettBizException.SETT_STATUS_ERROR;
    }
    settRecord.setSettStatus(settStatus);
    settRecord.setEditTime(new Date());
    settRecord.setRemitRemark(remark);
    settRecord.setRemitAmount(settRecord.getSettAmount());
    settRecord.setRemitConfirmTime(new Date());
    settRecord.setRemitRequestTime(new Date());
    rpSettRecordDao.update(settRecord);
    if (settStatus.equals(SettRecordStatusEnum.REMIT_FAIL.name())) {
      rpAccountTransactionService.unFreezeSettAmount(settRecord.getUserNo(), settRecord.getSettAmount());
    } else {
      if (settStatus.equals(SettRecordStatusEnum.REMIT_SUCCESS.name())) {
        rpAccountTransactionService.unFreezeAmount(settRecord.getUserNo(), settRecord.getSettAmount(), settRecord.getId(), TrxTypeEnum.REMIT.name(), remark);
      }
    }
  }
}