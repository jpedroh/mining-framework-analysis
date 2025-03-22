package com.roncoo.pay.app.reconciliation;
import com.roncoo.pay.app.reconciliation.biz.ReconciliationCheckBiz;
import com.roncoo.pay.app.reconciliation.biz.ReconciliationFileDownBiz;
import com.roncoo.pay.app.reconciliation.biz.ReconciliationFileParserBiz;
import com.roncoo.pay.app.reconciliation.biz.ReconciliationValidateBiz;
import com.roncoo.pay.app.reconciliation.utils.DateUtil;
import com.roncoo.pay.app.reconciliation.vo.ReconciliationInterface;
import com.roncoo.pay.reconciliation.entity.RpAccountCheckBatch;
import com.roncoo.pay.reconciliation.enums.BatchStatusEnum;
import com.roncoo.pay.reconciliation.service.RpAccountCheckBatchService;
import com.roncoo.pay.reconciliation.vo.ReconciliationEntityVo;
import com.roncoo.pay.user.service.BuildNoService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 对账处理(包括下载对账文件、转换对账文件、对账) .
 *
 * 龙果学院：www.roncoo.com
 *
 * @author：shenjialong
 */
@Component public class ReconciliationTask {
  private static final Log LOG = LogFactory.getLog(ReconciliationTask.class);

  @Autowired private ReconciliationFileDownBiz fileDownBiz;

  @Autowired private ReconciliationFileParserBiz parserBiz;

  @Autowired private ReconciliationCheckBiz checkBiz;

  @Autowired private ReconciliationValidateBiz validateBiz;

  @Autowired private RpAccountCheckBatchService batchService;

  @Autowired private BuildNoService buildNoService;

  @Scheduled(cron = "0 15 10 * * ?") public void taskRun() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    try {
      @SuppressWarnings(value = { "rawtypes" }) List reconciliationInterList = ReconciliationInterface.getInterface();
      for (int num = 0; num < reconciliationInterList.size(); num++) {
        ReconciliationInterface reconciliationInter = (ReconciliationInterface) reconciliationInterList.get(num);
        if (reconciliationInter == null) {
          LOG.info("\u5bf9\u8d26\u63a5\u53e3\u4fe1\u606f" + reconciliationInter + "\u4e3a\u7a7a");
          continue;
        }
        Date billDate = DateUtil.addDay(new Date(), -reconciliationInter.getBillDay());
        String interfaceCode = reconciliationInter.getInterfaceCode();
        RpAccountCheckBatch batch = new RpAccountCheckBatch();
        Boolean checked = validateBiz.isChecked(interfaceCode, billDate);
        if (checked) {
          LOG.info("\u8d26\u5355\u65e5[" + sdf.format(billDate) + "],\u652f\u4ed8\u65b9\u5f0f[" + interfaceCode + "],\u5df2\u7ecf\u5bf9\u8fc7\u8d26\uff0c\u4e0d\u80fd\u518d\u6b21\u53d1\u8d77\u81ea\u52a8\u5bf9\u8d26\u3002");
          continue;
        }
        batch.setCreater("reconciliationSystem");
        batch.setCreateTime(new Date());
        batch.setBillDate(billDate);
        batch.setBatchNo(buildNoService.buildReconciliationNo());
        batch.setBankType(interfaceCode);
        File file = null;
        try {
          LOG.info("ReconciliationFileDownBiz,\u5bf9\u8d26\u6587\u4ef6\u4e0b\u8f7d\u5f00\u59cb");
          file = fileDownBiz.downReconciliationFile(interfaceCode, billDate);
          if (file == null) {
            continue;
          }
          LOG.info("\u5bf9\u8d26\u6587\u4ef6\u4e0b\u8f7d\u7ed3\u675f");
        } catch (Exception e) {
          LOG.error("\u5bf9\u8d26\u6587\u4ef6\u4e0b\u8f7d\u5f02\u5e38:", e);
          batch.setStatus(BatchStatusEnum.FAIL.name());
          batch.setRemark("\u5bf9\u8d26\u6587\u4ef6\u4e0b\u8f7d\u5f02\u5e38");
          batchService.saveData(batch);
          continue;
        }
        List<ReconciliationEntityVo> bankList = null;
        try {
          LOG.info("=ReconciliationFileParserBiz=>\u5bf9\u8d26\u6587\u4ef6\u89e3\u6790\u5f00\u59cb>>>");
          bankList = parserBiz.parser(batch, file, billDate, interfaceCode);
          if (BatchStatusEnum.ERROR.name().equals(batch.getStatus())) {
            continue;
          }
          LOG.info("\u5bf9\u8d26\u6587\u4ef6\u89e3\u6790\u7ed3\u675f");
        } catch (Exception e) {
          LOG.error("\u5bf9\u8d26\u6587\u4ef6\u89e3\u6790\u5f02\u5e38:", e);
          batch.setStatus(BatchStatusEnum.FAIL.name());
          batch.setRemark("\u5bf9\u8d26\u6587\u4ef6\u89e3\u6790\u5f02\u5e38");
          batchService.saveData(batch);
          continue;
        }
        try {
          checkBiz.check(bankList, interfaceCode, batch);
        } catch (Exception e) {
          LOG.error("\u5bf9\u8d26\u5f02\u5e38:", e);
          batch.setStatus(BatchStatusEnum.FAIL.name());
          batch.setRemark("\u5bf9\u8d26\u5f02\u5e38");
          batchService.saveData(batch);
          continue;
        }
      }
      validateBiz.validateScratchPool();
    } catch (Exception e) {
      LOG.error("roncoo-app-reconciliation error:", e);
    }
  }
}