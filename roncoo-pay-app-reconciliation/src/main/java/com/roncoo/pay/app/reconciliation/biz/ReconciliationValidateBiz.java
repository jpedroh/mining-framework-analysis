package com.roncoo.pay.app.reconciliation.biz;
import java.text.SimpleDateFormat;
import com.roncoo.pay.app.reconciliation.utils.DateUtil;
import org.apache.commons.logging.Log;
import com.roncoo.pay.reconciliation.entity.RpAccountCheckBatch;
import org.apache.commons.logging.LogFactory;
import com.roncoo.pay.reconciliation.entity.RpAccountCheckMistake;
import org.springframework.beans.factory.annotation.Autowired;
import com.roncoo.pay.reconciliation.entity.RpAccountCheckMistakeScratchPool;
import org.springframework.stereotype.Component;
import com.roncoo.pay.reconciliation.enums.BatchStatusEnum;
import com.roncoo.pay.reconciliation.enums.MistakeHandleStatusEnum;
import com.roncoo.pay.reconciliation.enums.ReconciliationMistakeTypeEnum;
import com.roncoo.pay.reconciliation.service.RpAccountCheckBatchService;
import com.roncoo.pay.reconciliation.service.RpAccountCheckMistakeScratchPoolService;
import com.roncoo.pay.reconciliation.service.RpAccountCheckTransactionService;
import java.util.*;

/**
 * 对账验证biz，(检查是否已经对过账).
 *
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
@Component(value = "reconciliationValidateBiz") public class ReconciliationValidateBiz {
  private static final Log LOG = LogFactory.getLog(ReconciliationValidateBiz.class);

  @Autowired private RpAccountCheckBatchService rpAccountCheckBatchService;

  @Autowired private RpAccountCheckTransactionService rpAccountCheckTransactionService;

  @Autowired private RpAccountCheckMistakeScratchPoolService rpAccountCheckMistakeScratchPoolService;

  /**
	 * 判断某支付方式某天是否对过账，避免重复对账
	 * 
	 * @param interfaceCode
	 *            支付方式
	 * @param billDate
	 *            账单日
	 * @return
	 */
  public Boolean isChecked(String interfaceCode, Date billDate) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String billDateStr = sdf.format(billDate);
    LOG.info("\u68c0\u67e5,\u652f\u4ed8\u65b9\u5f0f[" + interfaceCode + "],\u8ba2\u5355\u65e5\u671f[" + billDateStr + "]");
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("billDate", billDateStr);
    paramMap.put("interfaceCode", interfaceCode);
    paramMap.put("status", BatchStatusEnum.ERROR.name() + "," + BatchStatusEnum.FAIL.name());
    List<RpAccountCheckBatch> list = rpAccountCheckBatchService.listBy(paramMap);
    if (list.isEmpty()) {
      return false;
    }
    return true;
  }

  /**
	 * 如果缓冲池中有三天前的数据就清理掉并记录差错
	 */
  public void validateScratchPool() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String dateStr = sdf.format(DateUtil.addDay(new Date(), -3));
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("maxDate", dateStr);
    List<RpAccountCheckMistakeScratchPool> list = rpAccountCheckMistakeScratchPoolService.listScratchPoolRecord(paramMap);
    List<RpAccountCheckMistake> mistakeList = null;
    if (!list.isEmpty()) {
      mistakeList = new ArrayList<RpAccountCheckMistake>();
      for (RpAccountCheckMistakeScratchPool scratchRecord : list) {
        RpAccountCheckMistake mistake = new RpAccountCheckMistake();
        mistake.setAccountCheckBatchNo(scratchRecord.getBatchNo());
        mistake.setBillDate(scratchRecord.getBillDate());
        mistake.setErrType(ReconciliationMistakeTypeEnum.BANK_MISS.name());
        mistake.setHandleStatus(MistakeHandleStatusEnum.NOHANDLE.name());
        mistake.setBankType(scratchRecord.getPayWayCode());
        mistake.setOrderNo(scratchRecord.getMerchantOrderNo());
        mistake.setTradeTime(scratchRecord.getPaySuccessTime());
        mistake.setTrxNo(scratchRecord.getTrxNo());
        mistake.setOrderAmount(scratchRecord.getOrderAmount());
        mistake.setRefundAmount(scratchRecord.getSuccessRefundAmount());
        mistake.setTradeStatus(scratchRecord.getStatus());
        mistake.setFee(scratchRecord.getPlatCost());
        mistakeList.add(mistake);
      }
      rpAccountCheckTransactionService.removeDateFromPool(list, mistakeList);
    }
  }
}