package com.roncoo.pay.controller.trade;
import javax.servlet.http.HttpServletRequest;
import com.roncoo.pay.common.core.enums.PayTypeEnum;
import com.roncoo.pay.trade.enums.TradeStatusEnum;
import com.roncoo.pay.common.core.enums.PayWayEnum;
import com.roncoo.pay.trade.enums.TrxTypeEnum;
import com.roncoo.pay.common.core.page.PageBean;
import com.roncoo.pay.trade.vo.PaymentOrderQueryParam;
import com.roncoo.pay.common.core.page.PageParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.roncoo.pay.trade.service.RpTradePaymentQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import com.roncoo.pay.user.enums.FundInfoTypeEnum;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 交易管理
 * 龙果学院：www.roncoo.com
 * @author：Peter
 */
@Controller @RequestMapping(value = "/trade") public class TradeController {
  @Autowired private RpTradePaymentQueryService rpTradePaymentQueryService;

  @RequiresPermissions(value = "trade:order:view") @RequestMapping(value = "/listPaymentOrder", method = { RequestMethod.POST, RequestMethod.GET }) public String listPaymentOrder(HttpServletRequest request, PaymentOrderQueryParam paymentOrderQueryParam, PageParam pageParam, Model model) {
    PageBean pageBean = rpTradePaymentQueryService.listPaymentOrderPage(pageParam, paymentOrderQueryParam);
    model.addAttribute("pageBean", pageBean);
    model.addAttribute("pageParam", pageParam);
    model.addAttribute("paymentOrderQueryParam", paymentOrderQueryParam);
    model.addAttribute("statusEnums", TradeStatusEnum.toMap());
    model.addAttribute("payWayNameEnums", PayWayEnum.toMap());
    model.addAttribute("payTypeNameEnums", PayTypeEnum.toMap());
    model.addAttribute("fundIntoTypeEnums", FundInfoTypeEnum.toMap());
    return "trade/listPaymentOrder";
  }

  @RequiresPermissions(value = "trade:record:view") @RequestMapping(value = "/listPaymentRecord", method = { RequestMethod.POST, RequestMethod.GET }) public String listPaymentRecord(HttpServletRequest request, PaymentOrderQueryParam paymentOrderQueryParam, PageParam pageParam, Model model) {
    PageBean pageBean = rpTradePaymentQueryService.listPaymentRecordPage(pageParam, paymentOrderQueryParam);
    model.addAttribute("pageBean", pageBean);
    model.addAttribute("pageParam", pageParam);
    model.addAttribute("paymentOrderQueryParam", paymentOrderQueryParam);
    model.addAttribute("statusEnums", TradeStatusEnum.toMap());
    model.addAttribute("payWayNameEnums", PayWayEnum.toMap());
    model.addAttribute("payTypeNameEnums", PayTypeEnum.toMap());
    model.addAttribute("fundIntoTypeEnums", FundInfoTypeEnum.toMap());
    model.addAttribute("trxTypeEnums", TrxTypeEnum.toMap());
    return "trade/listPaymentRecord";
  }
}