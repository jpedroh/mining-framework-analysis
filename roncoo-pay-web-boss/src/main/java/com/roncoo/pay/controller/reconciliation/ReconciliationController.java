package com.roncoo.pay.controller.reconciliation;
import java.util.HashMap;
import com.roncoo.pay.common.core.dwz.DWZ;
import java.util.Map;
import com.roncoo.pay.common.core.dwz.DwzAjax;
import javax.servlet.http.HttpServletRequest;
import com.roncoo.pay.common.core.enums.PayWayEnum;
import com.roncoo.pay.trade.enums.TradeStatusEnum;
import com.roncoo.pay.common.core.exception.BizException;
import org.apache.commons.logging.Log;
import com.roncoo.pay.common.core.page.PageBean;
import org.apache.commons.logging.LogFactory;
import com.roncoo.pay.common.core.page.PageParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.roncoo.pay.reconciliation.entity.RpAccountCheckBatch;
import org.springframework.beans.factory.annotation.Autowired;
import com.roncoo.pay.reconciliation.entity.RpAccountCheckMistake;
import org.springframework.stereotype.Controller;
import com.roncoo.pay.reconciliation.entity.RpAccountCheckMistakeScratchPool;
import org.springframework.ui.Model;
import com.roncoo.pay.reconciliation.enums.MistakeHandleStatusEnum;
import org.springframework.web.bind.annotation.RequestMapping;
import com.roncoo.pay.reconciliation.enums.ReconciliationMistakeTypeEnum;
import org.springframework.web.bind.annotation.RequestParam;
import com.roncoo.pay.reconciliation.service.RpAccountCheckBatchService;
import com.roncoo.pay.reconciliation.service.RpAccountCheckMistakeScratchPoolService;
import com.roncoo.pay.reconciliation.service.RpAccountCheckMistakeService;
import com.roncoo.pay.reconciliation.service.RpAccountCheckTransactionService;

/**
 * 
 * 对账控制器.
 * 
 * @company：广州领课网络科技有限公司（龙果学院:www.roncoo.com）
 * @author：Along.shen
 *
 */
@Controller @RequestMapping(value = "/reconciliation") public class ReconciliationController {
  private static final Log log = LogFactory.getLog(ReconciliationController.class);

  @Autowired private RpAccountCheckBatchService rpAccountCheckBatchService;

  @Autowired private RpAccountCheckTransactionService rpAccountCheckTransactionService;

  @Autowired private RpAccountCheckMistakeService rpAccountCheckMistakeService;

  @Autowired private RpAccountCheckMistakeScratchPoolService rpAccountCheckMistakeScratchPoolService;

  /**
	 * 展示对账批次信息
	 * 
	 * @param pageParam
	 * @param checkbatch
	 * @param model
	 * @return
	 */
  @RequiresPermissions(value = "recon:batch:view") @RequestMapping(value = "/list/checkbatch") public String listCheckbatch(PageParam pageParam, RpAccountCheckBatch checkbatch, Model model, String billDay) {
    try {
      Map<String, Object> paramMap = new HashMap<String, Object>();
      paramMap.put("billDate", billDay);
      PageBean pageBean = rpAccountCheckBatchService.listPage(pageParam, paramMap);
      model.addAttribute("pageBean", pageBean);
      model.addAttribute("pageParam", pageParam);
      model.addAttribute("billDay", billDay);
    } catch (Exception e) {
      log.error(e);
      DwzAjax dwz = new DwzAjax();
      dwz.setStatusCode(DWZ.ERROR);
      dwz.setMessage("\u5bf9\u8d26\u6279\u6b21\u4fe1\u606f\u5206\u9875\u5217\u8868\u5f02\u5e38\uff0c\u8bf7\u901a\u77e5\u7cfb\u7edf\u7ba1\u7406\u5458\uff01");
      model.addAttribute("dwz", dwz);
      return "common/ajaxDone";
    }
    return "reconciliation/batch/list";
  }

  /**
	 * 展示对账差错信息
	 * 
	 * @param pageParam
	 * @param checkbatch
	 * @param model
	 * @return
	 */
  @RequiresPermissions(value = "recon:mistake:view") @RequestMapping(value = "/list/mistake") public String listMistake(PageParam pageParam, RpAccountCheckMistake mistake, String billBeginDate, String billEndDate, Model model) {
    try {
      Map<String, Object> paramMap = new HashMap<String, Object>();
      paramMap.put("beginDate", billBeginDate);
      paramMap.put("endDate", billEndDate);
      PageBean pageBean = rpAccountCheckMistakeService.listPage(pageParam, paramMap);
      model.addAttribute("billBeginDate", billBeginDate);
      model.addAttribute("billEndDate", billEndDate);
      model.addAttribute("pageBean", pageBean);
      model.addAttribute("pageParam", pageParam);
      model.addAttribute("tradeStatusEnums", TradeStatusEnum.toList());
      model.addAttribute("payWayEnums", PayWayEnum.toList());
      model.addAttribute("mistakeHandleStatusEnums", MistakeHandleStatusEnum.toList());
      model.addAttribute("reconciliationMistakeTypeEnums", ReconciliationMistakeTypeEnum.toList());
    } catch (Exception e) {
      log.error(e);
      DwzAjax dwz = new DwzAjax();
      dwz.setStatusCode(DWZ.ERROR);
      dwz.setMessage("\u5bf9\u8d26\u5dee\u9519\u4fe1\u606f\u5206\u9875\u5217\u8868\u5f02\u5e38\uff0c\u8bf7\u901a\u77e5\u7cfb\u7edf\u7ba1\u7406\u5458\uff01");
      model.addAttribute("dwz", dwz);
      return "common/ajaxDone";
    }
    return "reconciliation/mistake/list";
  }

  /**
	 * 展示对账缓冲池信息
	 * 
	 * @param pageParam
	 * @param checkbatch
	 * @param model
	 * @return
	 */
  @RequiresPermissions(value = "recon:scratchPool:view") @RequestMapping(value = "/list/scratchPool") public String listScratchPool(PageParam pageParam, RpAccountCheckMistakeScratchPool scratchRecord, Model model) {
    try {
      PageBean pageBean = rpAccountCheckMistakeScratchPoolService.listPage(pageParam, scratchRecord);
      model.addAttribute("pageBean", pageBean);
      model.addAttribute("pageParam", pageParam);
    } catch (Exception e) {
      log.error(e);
      DwzAjax dwz = new DwzAjax();
      dwz.setStatusCode(DWZ.ERROR);
      dwz.setMessage("\u5bf9\u8d26\u5dee\u9519\u4fe1\u606f\u5206\u9875\u5217\u8868\u5f02\u5e38\uff0c\u8bf7\u901a\u77e5\u7cfb\u7edf\u7ba1\u7406\u5458\uff01");
      model.addAttribute("dwz", dwz);
      return "common/ajaxDone";
    }
    return "reconciliation/scratchPool/list";
  }

  /**
	 * 展示对账差错信息
	 * 
	 * @param pageParam
	 * @param checkbatch
	 * @param model
	 * @return
	 */
  @RequiresPermissions(value = "recon:mistake:view") @RequestMapping(value = "/mistake/tohandlePage") public String toHandlePage(Model model, HttpServletRequest request, @RequestParam(value = "id") String id) {
    RpAccountCheckMistake mistake = rpAccountCheckMistakeService.getDataById(id);
    model.addAttribute("mistake", mistake);
    model.addAttribute("reconciliationMistakeTypeEnums", ReconciliationMistakeTypeEnum.toList());
    model.addAttribute("tradeStatusEnums", TradeStatusEnum.toList());
    return "reconciliation/mistake/handlePage";
  }

  /**
	 * 差错处理方法
	 * 
	 * @param dwz
	 * @param model
	 * @param request
	 * @param id
	 *            差错id
	 * @param handleType
	 *            处理类型(平台认账、银行认账)
	 * @param handleRemark
	 *            处理备注
	 * @return
	 */
  @RequiresPermissions(value = "recon:mistake:edit") @RequestMapping(value = "/mistake/handle") public String handleMistake(DwzAjax dwz, Model model, HttpServletRequest request, @RequestParam(value = "id") String id, @RequestParam(value = "handleType") String handleType, @RequestParam(value = "handleRemark") String handleRemark) {
    try {
      rpAccountCheckTransactionService.handle(id, handleType, handleRemark);
    } catch (BizException e) {
      log.error(e);
      dwz.setStatusCode(DWZ.ERROR);
      dwz.setMessage(e.getMsg());
      model.addAttribute("dwz", dwz);
      return "common/ajaxDone";
    } catch (Exception e) {
      log.error(e);
      dwz.setStatusCode(DWZ.ERROR);
      dwz.setMessage("\u5bf9\u8d26\u5dee\u9519\u5904\u7406\u5f02\u5e38\uff0c\u8bf7\u901a\u77e5\u7cfb\u7edf\u7ba1\u7406\u5458\uff01");
      model.addAttribute("dwz", dwz);
      return "common/ajaxDone";
    }
    dwz.setStatusCode(DWZ.SUCCESS);
    dwz.setMessage("\u64cd\u4f5c\u6210\u529f\uff01");
    model.addAttribute("dwz", dwz);
    return "common/ajaxDone";
  }
}