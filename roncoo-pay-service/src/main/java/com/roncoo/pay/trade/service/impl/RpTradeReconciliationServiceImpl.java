package com.roncoo.pay.trade.service.impl;
import java.math.BigDecimal;
import com.roncoo.pay.account.service.RpAccountTransactionService;
import com.roncoo.pay.notify.service.RpNotifyService;
import com.roncoo.pay.reconciliation.entity.RpAccountCheckMistake;
import com.roncoo.pay.trade.dao.RpTradePaymentOrderDao;
import com.roncoo.pay.trade.dao.RpTradePaymentRecordDao;
import com.roncoo.pay.trade.enums.TradeStatusEnum;
import com.roncoo.pay.trade.entity.RpTradePaymentOrder;
import com.roncoo.pay.trade.enums.TrxTypeEnum;
import com.roncoo.pay.trade.entity.RpTradePaymentRecord;
import com.roncoo.pay.trade.exception.TradeBizException;
import com.roncoo.pay.trade.service.RpTradeReconciliationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <b>功能说明:交易模块对账差错实现</b>
 * @author  Peter
 * <a href="http://www.roncoo.com">龙果学院(www.roncoo.com)</a>
 */
@Service(value = "rpTradeReconciliationService") public class RpTradeReconciliationServiceImpl implements RpTradeReconciliationService {
  private static final Logger LOG = LoggerFactory.getLogger(RpTradeReconciliationServiceImpl.class);

  @Autowired private RpTradePaymentOrderDao rpTradePaymentOrderDao;

  @Autowired private RpTradePaymentRecordDao rpTradePaymentRecordDao;

  @Autowired private RpNotifyService rpNotifyService;

  @Autowired private RpAccountTransactionService rpAccountTransactionService;

  /**
	 * 平台成功，银行记录不存在，或者银行失败，以银行为准
	 * 
	 * @param trxNo
	 *            平台交易流水
	 */
  public void bankMissOrBankFailBaseBank(String trxNo) {
    LOG.info("===== \u628a\u8ba2\u5355\u6539\u4e3a\u5931\u8d25\uff0c\u5e76\u51cf\u6b3e\u5f00\u59cb========");
    RpTradePaymentRecord record = rpTradePaymentRecordDao.getByTrxNo(trxNo);
    if (record == null) {
      throw new TradeBizException(TradeBizException.TRADE_ORDER_ERROR, "trxNo[" + trxNo + "]\u7684\u652f\u4ed8\u8bb0\u5f55\u4e0d\u5b58\u5728");
    }
    if (!record.getStatus().equals(TradeStatusEnum.SUCCESS.name())) {
      throw new TradeBizException(TradeBizException.TRADE_ORDER_STATUS_NOT_SUCCESS, "trxNo[" + trxNo + "]\u7684\u652f\u4ed8\u8bb0\u5f55\u72b6\u6001\u4e0d\u662fsuccess");
    }
    record.setStatus(TradeStatusEnum.FAILED.name());
    record.setRemark("\u5bf9\u8d26\u5dee\u9519\u5904\u7406,\u8ba2\u5355\u6539\u4e3a\u5931\u8d25\uff0c\u5e76\u51cf\u6b3e.");
    rpTradePaymentRecordDao.update(record);
    RpTradePaymentOrder order = rpTradePaymentOrderDao.selectByMerchantNoAndMerchantOrderNo(record.getMerchantNo(), record.getMerchantOrderNo());
    order.setStatus(TradeStatusEnum.FAILED.name());
    order.setRemark("\u5bf9\u8d26\u5dee\u9519\u5904\u7406,\u8ba2\u5355\u6539\u4e3a\u5931\u8d25\uff0c\u5e76\u51cf\u6b3e.");
    rpTradePaymentOrderDao.update(order);
    rpAccountTransactionService.debitToAccount(record.getMerchantNo(), record.getOrderAmount().subtract(record.getPlatIncome()), record.getBankOrderNo(), TrxTypeEnum.ERRORHANKLE.name(), "\u5bf9\u8d26\u5dee\u9519\u5904\u7406,\u8ba2\u5355\u6539\u4e3a\u5931\u8d25\uff0c\u5e76\u51cf\u6b3e.");
    LOG.info("===== \u628a\u8ba2\u5355\u6539\u4e3a\u5931\u8d25\uff0c\u5e76\u51cf\u6b3e\u6210\u529f========");
  }

  /**
	 * 银行支付成功，平台失败.
	 * 
	 * @param trxNo
	 *            平台交易流水
	 * @param bankTrxNo
	 *            银行返回流水
	 */
  @Transactional(rollbackFor = Exception.class) public void platFailBankSuccess(String trxNo, String bankTrxNo) {
    LOG.info("===== \u94f6\u884c\u652f\u4ed8\u6210\u529f\uff0c\u5e73\u53f0\u5931\u8d25.========");
    RpTradePaymentRecord record = rpTradePaymentRecordDao.getByTrxNo(trxNo);
    if (record == null) {
      throw new TradeBizException(TradeBizException.TRADE_ORDER_ERROR, "trxNo[" + trxNo + "]\u7684\u652f\u4ed8\u8bb0\u5f55\u4e0d\u5b58\u5728");
    }
    record.setBankTrxNo(bankTrxNo);
    record.setBankReturnMsg("SUCCESS");
    record.setStatus(TradeStatusEnum.SUCCESS.name());
    rpTradePaymentRecordDao.update(record);
    RpTradePaymentOrder rpTradePaymentOrder = rpTradePaymentOrderDao.selectByMerchantNoAndMerchantOrderNo(record.getMerchantNo(), record.getMerchantOrderNo());
    rpTradePaymentOrder.setStatus(TradeStatusEnum.SUCCESS.name());
    rpTradePaymentOrderDao.update(rpTradePaymentOrder);
    rpAccountTransactionService.creditToAccount(record.getMerchantNo(), record.getOrderAmount().subtract(record.getPlatIncome()), record.getBankOrderNo(), record.getBankTrxNo(), record.getTrxType(), record.getRemark());
    rpNotifyService.notifySend(record.getNotifyUrl(), record.getMerchantOrderNo(), record.getMerchantNo());
  }

  /**
	 * 处理金额不匹配异常
	 * 
	 * @param mistake
	 *            差错记录
	 * @param isBankMore
	 *            是否是银行金额多
	 * @param baseOnBank
	 *            是否以银行为准
	 */
  @Transactional(rollbackFor = Exception.class) public void handleAmountMistake(RpAccountCheckMistake mistake, boolean isBankMore) {
    LOG.info("=====\u5f00\u59cb\u5904\u7406\u91d1\u989d\u5dee\u9519,\u662f\u5426\u662f\u94f6\u884c\u91d1\u989d\u591a[" + isBankMore + "],\u4e14\u90fd\u662f\u4ee5\u94f6\u884c\u6570\u636e\u4e3a\u51c6========");
    String trxNo = mistake.getTrxNo();
    RpTradePaymentRecord record = rpTradePaymentRecordDao.getByTrxNo(trxNo);
    if (record == null) {
      throw new TradeBizException(TradeBizException.TRADE_ORDER_ERROR, "trxNo[" + trxNo + "]\u7684\u652f\u4ed8\u8bb0\u5f55\u4e0d\u5b58\u5728");
    }
    if (!record.getStatus().equals(TradeStatusEnum.SUCCESS.name())) {
      throw new TradeBizException(TradeBizException.TRADE_ORDER_STATUS_NOT_SUCCESS, "\u8bf7\u5148\u5904\u7406\u8be5\u8ba2\u5355\u72b6\u6001\u4e0d\u7b26\u7684\u5dee\u9519");
    }
    BigDecimal bankAmount = mistake.getBankAmount();
    BigDecimal bankFee = mistake.getBankFee();
    BigDecimal orderAmount = record.getOrderAmount();
    BigDecimal fee = record.getPlatIncome();
    BigDecimal needFee = bankAmount.multiply(record.getFeeRate()).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    BigDecimal subOrderAmount = bankAmount.subtract(orderAmount).abs();
    BigDecimal subFee = needFee.subtract(fee).abs();
    if (isBankMore) {
      record.setOrderAmount(bankAmount);
      record.setPlatCost(bankFee);
      record.setPlatIncome(needFee);
      record.setRemark("\u5dee\u9519\u8c03\u6574\uff1a\u8ba2\u5355\u91d1\u989d\u52a0[" + subOrderAmount + "],\u624b\u7eed\u8d39\u52a0[" + subFee + "],\u6210\u672c\u53d8\u6210[" + bankFee + "]");
      rpTradePaymentRecordDao.update(record);
      RpTradePaymentOrder rpTradePaymentOrder = rpTradePaymentOrderDao.selectByMerchantNoAndMerchantOrderNo(record.getMerchantNo(), record.getMerchantOrderNo());
      rpTradePaymentOrder.setOrderAmount(bankAmount);
      rpTradePaymentOrder.setRemark("\u5dee\u9519\u5904\u7406:\u8ba2\u5355\u91d1\u989d\u7531[" + orderAmount + "]\u6539\u4e3a[" + bankAmount + "]");
      rpTradePaymentOrderDao.update(rpTradePaymentOrder);
      rpAccountTransactionService.creditToAccount(record.getMerchantNo(), subOrderAmount.subtract(subFee), record.getBankOrderNo(), record.getBankTrxNo(), TrxTypeEnum.ERRORHANKLE.name(), "\u5dee\u9519\u5904\u7406\u52a0\u6b3e\u3002");
    } else {
      record.setOrderAmount(bankAmount);
      record.setPlatCost(bankFee);
      record.setPlatIncome(needFee);
      record.setRemark("\u5dee\u9519\u8c03\u6574\uff1a\u8ba2\u5355\u91d1\u989d\u51cf[" + subOrderAmount + "],\u624b\u7eed\u8d39\u51cf[" + subFee + "],\u6210\u672c\u53d8\u6210[" + bankFee + "]");
      rpTradePaymentRecordDao.update(record);
      RpTradePaymentOrder rpTradePaymentOrder = rpTradePaymentOrderDao.selectByMerchantNoAndMerchantOrderNo(record.getMerchantNo(), record.getMerchantOrderNo());
      rpTradePaymentOrder.setOrderAmount(bankAmount);
      rpTradePaymentOrder.setRemark("\u5dee\u9519\u5904\u7406:\u8ba2\u5355\u91d1\u989d\u7531[" + orderAmount + "]\u6539\u4e3a[" + bankAmount + "]");
      rpTradePaymentOrderDao.update(rpTradePaymentOrder);
      rpAccountTransactionService.debitToAccount(record.getMerchantNo(), subOrderAmount.subtract(subFee), record.getBankOrderNo(), record.getBankTrxNo(), TrxTypeEnum.ERRORHANKLE.name(), "\u5dee\u9519\u5904\u7406\u51cf\u6b3e\u3002");
    }
  }

  /**
	 * 处理手续费不匹配差错（默认以银行为准）
	 * 
	 * @param mistake
	 */
  @Transactional(rollbackFor = Exception.class) public void handleFeeMistake(RpAccountCheckMistake mistake) {
    String trxNo = mistake.getTrxNo();
    RpTradePaymentRecord record = rpTradePaymentRecordDao.getByTrxNo(trxNo);
    if (record == null) {
      throw new TradeBizException(TradeBizException.TRADE_ORDER_ERROR, "trxNo[" + trxNo + "]\u7684\u652f\u4ed8\u8bb0\u5f55\u4e0d\u5b58\u5728");
    }
    if (!record.getStatus().equals(TradeStatusEnum.SUCCESS.name())) {
      throw new TradeBizException(TradeBizException.TRADE_ORDER_STATUS_NOT_SUCCESS, "\u8bf7\u5148\u5904\u7406\u8be5\u8ba2\u5355\u72b6\u6001\u4e0d\u7b26\u7684\u5dee\u9519");
    }
    BigDecimal oldBankFee = record.getPlatCost();
    BigDecimal bankFee = mistake.getBankFee();
    record.setPlatCost(bankFee);
    record.setRemark("\u5dee\u9519\u5904\u7406:\u94f6\u884c\u6210\u672c\u7531[" + oldBankFee + "]\u6539\u4e3a[" + bankFee + "]");
    rpTradePaymentRecordDao.update(record);
  }
}