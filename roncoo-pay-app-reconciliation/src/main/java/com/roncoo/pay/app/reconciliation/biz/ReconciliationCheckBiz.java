package com.roncoo.pay.app.reconciliation.biz;
import java.math.BigDecimal;
import com.roncoo.pay.reconciliation.entity.RpAccountCheckBatch;
import java.util.ArrayList;
import com.roncoo.pay.reconciliation.entity.RpAccountCheckMistake;
import java.util.List;
import com.roncoo.pay.reconciliation.entity.RpAccountCheckMistakeScratchPool;
import com.roncoo.pay.trade.enums.TradeStatusEnum;
import com.roncoo.pay.reconciliation.enums.MistakeHandleStatusEnum;
import org.apache.commons.logging.Log;
import com.roncoo.pay.reconciliation.enums.ReconciliationMistakeTypeEnum;
import org.apache.commons.logging.LogFactory;
import com.roncoo.pay.reconciliation.service.RpAccountCheckMistakeScratchPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import com.roncoo.pay.reconciliation.service.RpAccountCheckTransactionService;
import org.springframework.stereotype.Component;
import com.roncoo.pay.reconciliation.vo.ReconciliationEntityVo;
import com.roncoo.pay.trade.entity.RpTradePaymentRecord;

/**
 * 对账的核心业务biz.
 *
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
@Component(value = "reconciliationCheckBiz") public class ReconciliationCheckBiz {
  private static final Log LOG = LogFactory.getLog(ReconciliationCheckBiz.class);

  @Autowired private RpAccountCheckMistakeScratchPoolService rpAccountCheckMistakeScratchPoolService;

  @Autowired private RpAccountCheckTransactionService rpAccountCheckTransactionService;

  @Autowired private ReconciliationDataGetBiz reconciliationDataGetBiz;

  /**
	 * 对账核心方法
	 * 
	 * @param bankList
	 *            对账文件解析出来的数据
	 * @param interfaceCode
	 *            支付渠道
	 * @param batch
	 *            对账批次记录
	 */
  public void check(List<ReconciliationEntityVo> bankList, String interfaceCode, RpAccountCheckBatch batch) {
    if (bankList == null) {
      bankList = new ArrayList<ReconciliationEntityVo>();
    }
    List<RpTradePaymentRecord> platSucessDateList = reconciliationDataGetBiz.getSuccessPlatformDateByBillDate(batch.getBillDate(), interfaceCode);
    List<RpTradePaymentRecord> platAllDateList = reconciliationDataGetBiz.getAllPlatformDateByBillDate(batch.getBillDate(), interfaceCode);
    List<RpAccountCheckMistakeScratchPool> platScreatchRecordList = rpAccountCheckMistakeScratchPoolService.listScratchPoolRecord(null);
    List<RpAccountCheckMistake> mistakeList = new ArrayList<RpAccountCheckMistake>();
    List<RpAccountCheckMistakeScratchPool> insertScreatchRecordList = new ArrayList<RpAccountCheckMistakeScratchPool>();
    List<RpAccountCheckMistakeScratchPool> removeScreatchRecordList = new ArrayList<RpAccountCheckMistakeScratchPool>();
    LOG.info("  \u5f00\u59cb\u4ee5\u5e73\u53f0\u7684\u6570\u636e\u4e3a\u51c6\u5bf9\u8d26,\u5e73\u53f0\u957f\u6b3e\u8bb0\u5165\u7f13\u51b2\u6c60");
    baseOnPaltForm(platSucessDateList, bankList, mistakeList, insertScreatchRecordList, batch);
    LOG.info("\u7ed3\u675f\u4ee5\u5e73\u53f0\u7684\u6570\u636e\u4e3a\u51c6\u5bf9\u8d26");
    LOG.info("  \u5f00\u59cb\u4ee5\u94f6\u884c\u901a\u9053\u7684\u6570\u636e\u4e3a\u51c6\u5bf9\u8d26");
    baseOnBank(platAllDateList, bankList, platScreatchRecordList, mistakeList, batch, removeScreatchRecordList);
    LOG.info(" \u7ed3\u675f\u4ee5\u94f6\u884c\u901a\u9053\u7684\u6570\u636e\u4e3a\u51c6\u5bf9\u8d26");
    rpAccountCheckTransactionService.saveDatasaveDate(batch, mistakeList, insertScreatchRecordList, removeScreatchRecordList);
  }

  /**
	 * 以平台的数据为准对账
	 * 
	 * @param platformDateList
	 *            平台dilldate的成功数据
	 * @param bankList
	 *            银行成功对账单数据
	 * 
	 * @param misTakeList
	 *            差错list
	 * @param screatchRecordList
	 *            需要放入缓冲池中平台长款list
	 * 
	 * @param batch
	 *            对账批次
	 */
  private void baseOnPaltForm(List<RpTradePaymentRecord> platformDateList, List<ReconciliationEntityVo> bankList, List<RpAccountCheckMistake> misTakeList, List<RpAccountCheckMistakeScratchPool> screatchRecordList, RpAccountCheckBatch batch) {
    BigDecimal platTradeAmount = BigDecimal.ZERO;
    BigDecimal platFee = BigDecimal.ZERO;
    Integer tradeCount = 0;
    Integer mistakeCount = 0;
    for (RpTradePaymentRecord record : platformDateList) {
      Boolean flag = false;
      platTradeAmount = platTradeAmount.add(record.getOrderAmount());
      platFee = platFee.add(record.getPlatCost() == null ? BigDecimal.ZERO : record.getPlatCost());
      tradeCount++;
      for (ReconciliationEntityVo bankRecord : bankList) {
        if (record.getBankOrderNo().equalsIgnoreCase(bankRecord.getBankOrderNo())) {
          flag = true;
          if (record.getOrderAmount().compareTo(bankRecord.getBankAmount()) == 1) {
            RpAccountCheckMistake misktake = createMisktake(null, record, bankRecord, ReconciliationMistakeTypeEnum.PLATFORM_OVER_CASH_MISMATCH, batch);
            misTakeList.add(misktake);
            mistakeCount++;
            break;
          } else {
            if (record.getOrderAmount().compareTo(bankRecord.getBankAmount()) == -1) {
              RpAccountCheckMistake misktake = createMisktake(null, record, bankRecord, ReconciliationMistakeTypeEnum.PLATFORM_SHORT_CASH_MISMATCH, batch);
              misTakeList.add(misktake);
              mistakeCount++;
              break;
            }
          }
          if (record.getPlatCost().compareTo(bankRecord.getBankFee()) != 0) {
            RpAccountCheckMistake misktake = createMisktake(null, record, bankRecord, ReconciliationMistakeTypeEnum.FEE_MISMATCH, batch);
            misTakeList.add(misktake);
            mistakeCount++;
            break;
          }
        }
      }
      if (!flag) {
        RpAccountCheckMistakeScratchPool screatchRecord = getScratchRecord(record, batch);
        screatchRecordList.add(screatchRecord);
      }
    }
    batch.setTradeAmount(platTradeAmount);
    batch.setTradeCount(tradeCount);
    batch.setFee(platFee);
    batch.setMistakeCount(mistakeCount);
  }

  /**
	 * 以银行的数据为准对账
	 * 
	 * @param bankList
	 *            银行对账单数据
	 * 
	 * @param misTakeList
	 *            差错list
	 * 
	 * @param platScreatchRecordList
	 *            平台缓冲池中的数据
	 * 
	 * @param batch
	 *            对账批次
	 */
  private void baseOnBank(List<RpTradePaymentRecord> platAllDateList, List<ReconciliationEntityVo> bankList, List<RpAccountCheckMistakeScratchPool> platScreatchRecordList, List<RpAccountCheckMistake> misTakeList, RpAccountCheckBatch batch, List<RpAccountCheckMistakeScratchPool> removeScreatchRecordList) {
    BigDecimal platTradeAmount = BigDecimal.ZERO;
    BigDecimal platFee = BigDecimal.ZERO;
    Integer tradeCount = 0;
    Integer mistakeCount = 0;
    for (ReconciliationEntityVo bankRecord : bankList) {
      boolean flag = false;
      for (RpTradePaymentRecord record : platAllDateList) {
        if (bankRecord.getBankOrderNo().equals(record.getBankOrderNo())) {
          flag = true;
          if (!TradeStatusEnum.SUCCESS.name().equals(record.getStatus())) {
            RpAccountCheckMistake misktake1 = createMisktake(null, record, bankRecord, ReconciliationMistakeTypeEnum.PLATFORM_SHORT_STATUS_MISMATCH, batch);
            misTakeList.add(misktake1);
            mistakeCount++;
            if (record.getOrderAmount().compareTo(bankRecord.getBankAmount()) == 1) {
              RpAccountCheckMistake misktake = createMisktake(null, record, bankRecord, ReconciliationMistakeTypeEnum.PLATFORM_OVER_CASH_MISMATCH, batch);
              misTakeList.add(misktake);
              mistakeCount++;
              break;
            } else {
              if (record.getOrderAmount().compareTo(bankRecord.getBankAmount()) == -1) {
                RpAccountCheckMistake misktake = createMisktake(null, record, bankRecord, ReconciliationMistakeTypeEnum.PLATFORM_SHORT_CASH_MISMATCH, batch);
                misTakeList.add(misktake);
                mistakeCount++;
                break;
              }
            }
            if (record.getPlatCost().compareTo(bankRecord.getBankFee()) != 0) {
              RpAccountCheckMistake misktake = createMisktake(null, record, bankRecord, ReconciliationMistakeTypeEnum.FEE_MISMATCH, batch);
              misTakeList.add(misktake);
              mistakeCount++;
              break;
            }
          }
        }
      }
      if (!flag) {
        if (platScreatchRecordList != null) {
          for (RpAccountCheckMistakeScratchPool scratchRecord : platScreatchRecordList) {
            if (scratchRecord.getBankOrderNo().equals(bankRecord.getBankOrderNo())) {
              platTradeAmount = platTradeAmount.add(scratchRecord.getOrderAmount());
              platFee = platFee.add(scratchRecord.getPlatCost() == null ? BigDecimal.ZERO : scratchRecord.getPlatCost());
              tradeCount++;
              flag = true;
              if (scratchRecord.getOrderAmount().compareTo(bankRecord.getBankAmount()) == 1) {
                RpAccountCheckMistake misktake = createMisktake(scratchRecord, null, bankRecord, ReconciliationMistakeTypeEnum.PLATFORM_OVER_CASH_MISMATCH, batch);
                misTakeList.add(misktake);
                mistakeCount++;
                break;
              } else {
                if (scratchRecord.getOrderAmount().compareTo(bankRecord.getBankAmount()) == -1) {
                  RpAccountCheckMistake misktake = createMisktake(scratchRecord, null, bankRecord, ReconciliationMistakeTypeEnum.PLATFORM_SHORT_CASH_MISMATCH, batch);
                  misTakeList.add(misktake);
                  mistakeCount++;
                  break;
                }
              }
              if (scratchRecord.getPlatCost().compareTo(bankRecord.getBankFee()) != 0) {
                RpAccountCheckMistake misktake = createMisktake(scratchRecord, null, bankRecord, ReconciliationMistakeTypeEnum.FEE_MISMATCH, batch);
                misTakeList.add(misktake);
                mistakeCount++;
                break;
              }
              removeScreatchRecordList.add(scratchRecord);
            }
          }
        }
      }
      if (!flag) {
        RpAccountCheckMistake misktake = createMisktake(null, null, bankRecord, ReconciliationMistakeTypeEnum.PLATFORM_MISS, batch);
        misTakeList.add(misktake);
        mistakeCount++;
      }
    }
    batch.setTradeAmount(batch.getTradeAmount().add(platTradeAmount));
    batch.setTradeCount(batch.getTradeCount() + tradeCount);
    batch.setFee(batch.getFee().add(platFee));
    batch.setMistakeCount(batch.getMistakeCount() + mistakeCount);
  }

  /**
	 * 创建差错记录
	 * 
	 * @param scratchRecord
	 *            平台缓冲池中的订单记录
	 * @param record
	 *            平台订单记录
	 * @param bankRecord
	 *            银行账单记录
	 * @param mistakeType
	 *            差错类型
	 * @return 注意：scratchRecord和record 至少有一个为空
	 */
  private RpAccountCheckMistake createMisktake(RpAccountCheckMistakeScratchPool scratchRecord, RpTradePaymentRecord record, ReconciliationEntityVo bankRecord, ReconciliationMistakeTypeEnum mistakeType, RpAccountCheckBatch batch) {
    RpAccountCheckMistake mistake = new RpAccountCheckMistake();
    mistake.setAccountCheckBatchNo(batch.getBatchNo());
    mistake.setBillDate(batch.getBillDate());
    mistake.setErrType(mistakeType.name());
    mistake.setHandleStatus(MistakeHandleStatusEnum.NOHANDLE.name());
    mistake.setBankType(batch.getBankType());
    if (record != null) {
      mistake.setMerchantName(record.getMerchantName());
      mistake.setMerchantNo(record.getMerchantNo());
      mistake.setOrderNo(record.getMerchantOrderNo());
      mistake.setTradeTime(record.getPaySuccessTime());
      mistake.setTrxNo(record.getTrxNo());
      mistake.setOrderAmount(record.getOrderAmount());
      mistake.setRefundAmount(record.getSuccessRefundAmount());
      mistake.setTradeStatus(record.getStatus());
      mistake.setFee(record.getPlatCost());
    }
    if (scratchRecord != null) {
      mistake.setOrderNo(scratchRecord.getMerchantOrderNo());
      mistake.setTradeTime(scratchRecord.getPaySuccessTime());
      mistake.setTrxNo(scratchRecord.getTrxNo());
      mistake.setOrderAmount(scratchRecord.getOrderAmount());
      mistake.setRefundAmount(scratchRecord.getSuccessRefundAmount());
      mistake.setTradeStatus(scratchRecord.getStatus());
      mistake.setFee(scratchRecord.getPlatCost());
    }
    if (bankRecord != null) {
      mistake.setBankAmount(bankRecord.getBankAmount());
      mistake.setBankFee(bankRecord.getBankFee());
      mistake.setBankOrderNo(bankRecord.getBankOrderNo());
      mistake.setBankRefundAmount(bankRecord.getBankRefundAmount());
      mistake.setBankTradeStatus(bankRecord.getBankTradeStatus());
      mistake.setBankTradeTime(bankRecord.getBankTradeTime());
      mistake.setBankTrxNo(bankRecord.getBankTrxNo());
    }
    return mistake;
  }

  /**
	 * 得到缓存记录：用于放入缓冲池
	 * 
	 * @param record
	 *            支付记录
	 * @param batch
	 *            对账批次记录
	 * @return
	 */
  private RpAccountCheckMistakeScratchPool getScratchRecord(RpTradePaymentRecord record, RpAccountCheckBatch batch) {
    RpAccountCheckMistakeScratchPool scratchRecord = new RpAccountCheckMistakeScratchPool();
    scratchRecord.setBankOrderNo(record.getBankOrderNo());
    scratchRecord.setBankTrxNo(record.getBankTrxNo());
    scratchRecord.setCompleteTime(record.getCompleteTime());
    scratchRecord.setPaySuccessTime(record.getPaySuccessTime());
    scratchRecord.setMerchantOrderNo(record.getMerchantOrderNo());
    scratchRecord.setOrderAmount(record.getOrderAmount());
    scratchRecord.setPlatCost(record.getPlatCost());
    scratchRecord.setPayWayCode(record.getPayWayCode());
    scratchRecord.setTrxNo(record.getTrxNo());
    scratchRecord.setStatus(TradeStatusEnum.SUCCESS.name());
    scratchRecord.setBatchNo(batch.getBatchNo());
    scratchRecord.setBillDate(batch.getBillDate());
    return scratchRecord;
  }
}