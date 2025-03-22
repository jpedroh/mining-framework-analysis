package com.roncoo.pay.trade.service.impl;
import com.roncoo.pay.common.core.enums.PublicEnum;
import com.roncoo.pay.common.core.page.PageBean;
import com.roncoo.pay.common.core.page.PageParam;
import com.roncoo.pay.common.core.utils.DateUtils;
import com.roncoo.pay.trade.dao.RpTradePaymentOrderDao;
import com.roncoo.pay.trade.dao.RpTradePaymentRecordDao;
import com.roncoo.pay.trade.enums.TradeStatusEnum;
import com.roncoo.pay.trade.entity.RpTradePaymentOrder;
import com.roncoo.pay.trade.service.RpTradePaymentQueryService;
import com.roncoo.pay.trade.entity.RpTradePaymentRecord;
import com.roncoo.pay.trade.utils.MerchantApiUtil;
import com.roncoo.pay.trade.vo.OrderPayResultVo;
import com.roncoo.pay.trade.vo.PaymentOrderQueryParam;
import com.roncoo.pay.user.entity.RpUserPayConfig;
import com.roncoo.pay.user.exception.UserBizException;
import com.roncoo.pay.user.service.RpUserPayConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>功能说明:交易模块查询类实现</b>
 * @author  Peter
 * <a href="http://www.roncoo.com">龙果学院(www.roncoo.com)</a>
 */
@Service(value = "rpTradePaymentQueryService") public class RpTradePaymentQueryServiceImpl implements RpTradePaymentQueryService {
  @Autowired private RpTradePaymentRecordDao rpTradePaymentRecordDao;

  @Autowired private RpTradePaymentOrderDao rpTradePaymentOrderDao;

  @Autowired private RpUserPayConfigService rpUserPayConfigService;

  /**
	 * 根据参数查询交易记录List
	 *
	 * @param paramMap
	 * @return
	 */
  public List<RpTradePaymentRecord> listPaymentRecord(Map<String, Object> paramMap) {
    return rpTradePaymentRecordDao.listByColumn(paramMap);
  }

  /**
	 * 根据商户支付KEY 及商户订单号 查询支付结果
	 *
	 * @param payKey
	 *            商户支付KEY
	 * @param orderNo
	 *            商户订单号
	 * @return
	 */
  @Override public OrderPayResultVo getPayResult(String payKey, String orderNo) {
    RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByPayKey(payKey);
    if (rpUserPayConfig == null) {
      throw new UserBizException(UserBizException.USER_PAY_CONFIG_ERRPR, "\u7528\u6237\u652f\u4ed8\u914d\u7f6e\u6709\u8bef");
    }
    String merchantNo = rpUserPayConfig.getUserNo();
    RpTradePaymentOrder rpTradePaymentOrder = rpTradePaymentOrderDao.selectByMerchantNoAndMerchantOrderNo(merchantNo, orderNo);
    RpTradePaymentRecord rpTradePaymentRecord = rpTradePaymentRecordDao.getSuccessRecordByMerchantNoAndMerchantOrderNo(rpTradePaymentOrder.getMerchantNo(), rpTradePaymentOrder.getMerchantOrderNo());
    OrderPayResultVo orderPayResultVo = new OrderPayResultVo();
    if (rpTradePaymentOrder != null && TradeStatusEnum.SUCCESS.name().equals(rpTradePaymentOrder.getStatus())) {
      orderPayResultVo.setStatus(PublicEnum.YES.name());
      orderPayResultVo.setOrderPrice(rpTradePaymentOrder.getOrderAmount());
      orderPayResultVo.setProductName(rpTradePaymentOrder.getProductName());
      String url = getMerchantNotifyUrl(rpTradePaymentRecord, rpTradePaymentOrder, rpTradePaymentRecord.getReturnUrl(), TradeStatusEnum.SUCCESS);
      orderPayResultVo.setReturnUrl(url);
    }
    return orderPayResultVo;
  }

  private String getMerchantNotifyUrl(RpTradePaymentRecord rpTradePaymentRecord, RpTradePaymentOrder rpTradePaymentOrder, String sourceUrl, TradeStatusEnum tradeStatusEnum) {
    RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByUserNo(rpTradePaymentRecord.getMerchantNo());
    if (rpUserPayConfig == null) {
      throw new UserBizException(UserBizException.USER_PAY_CONFIG_ERRPR, "\u7528\u6237\u652f\u4ed8\u914d\u7f6e\u6709\u8bef");
    }
    Map<String, Object> paramMap = new HashMap<>();
    String payKey = rpUserPayConfig.getPayKey();
    paramMap.put("payKey", payKey);
    String productName = rpTradePaymentRecord.getProductName();
    paramMap.put("productName", productName);
    String orderNo = rpTradePaymentRecord.getMerchantOrderNo();
    paramMap.put("orderNo", orderNo);
    BigDecimal orderPrice = rpTradePaymentRecord.getOrderAmount();
    paramMap.put("orderPrice", orderPrice);
    String payWayCode = rpTradePaymentRecord.getPayWayCode();
    paramMap.put("payWayCode", payWayCode);
    paramMap.put("tradeStatus", tradeStatusEnum);
    String orderDateStr = DateUtils.formatDate(rpTradePaymentOrder.getOrderDate(), "yyyyMMdd");
    paramMap.put("orderDate", orderDateStr);
    String orderTimeStr = DateUtils.formatDate(rpTradePaymentOrder.getOrderTime(), "yyyyMMddHHmmss");
    paramMap.put("orderTime", orderTimeStr);
    String remark = rpTradePaymentRecord.getRemark();
    paramMap.put("remark", remark);
    String trxNo = rpTradePaymentRecord.getTrxNo();
    paramMap.put("trxNo", trxNo);
    String field1 = rpTradePaymentOrder.getField1();
    paramMap.put("field1", field1);
    String field2 = rpTradePaymentOrder.getField2();
    paramMap.put("field2", field2);
    String field3 = rpTradePaymentOrder.getField3();
    paramMap.put("field3", field3);
    String field4 = rpTradePaymentOrder.getField4();
    paramMap.put("field4", field4);
    String field5 = rpTradePaymentOrder.getField5();
    paramMap.put("field5", field5);
    String paramStr = MerchantApiUtil.getParamStr(paramMap);
    String sign = MerchantApiUtil.getSign(paramMap, rpUserPayConfig.getPaySecret());
    String notifyUrl = sourceUrl + "?" + paramStr + "&sign=" + sign;
    return notifyUrl;
  }

  /**
	 * 根据银行订单号查询支付记录
	 *
	 * @param bankOrderNo
	 * @return
	 */
  public RpTradePaymentRecord getRecordByBankOrderNo(String bankOrderNo) {
    return rpTradePaymentRecordDao.getByBankOrderNo(bankOrderNo);
  }

  /**
	 * 根据支付流水号查询支付记录
	 *
	 * @param trxNo
	 * @return
	 */
  public RpTradePaymentRecord getRecordByTrxNo(String trxNo) {
    return rpTradePaymentRecordDao.getByTrxNo(trxNo);
  }

  /**
	 * 分页查询支付订单
	 *
	 * @param pageParam
	 * @param paymentOrderQueryParam
	 * @return
	 */
  @Override public PageBean<RpTradePaymentOrder> listPaymentOrderPage(PageParam pageParam, PaymentOrderQueryParam paymentOrderQueryParam) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("merchantNo", paymentOrderQueryParam.getMerchantNo());
    paramMap.put("merchantName", paymentOrderQueryParam.getMerchantName());
    paramMap.put("merchantOrderNo", paymentOrderQueryParam.getMerchantOrderNo());
    paramMap.put("fundIntoType", paymentOrderQueryParam.getFundIntoType());
    paramMap.put("merchantOrderNo", paymentOrderQueryParam.getOrderDateBegin());
    paramMap.put("merchantOrderNo", paymentOrderQueryParam.getOrderDateEnd());
    paramMap.put("payTypeName", paymentOrderQueryParam.getPayTypeName());
    paramMap.put("payWayName", paymentOrderQueryParam.getPayWayName());
    paramMap.put("status", paymentOrderQueryParam.getStatus());
    if (paymentOrderQueryParam.getOrderDateBegin() != null) {
      paramMap.put("orderDateBegin", paymentOrderQueryParam.getOrderDateBegin());
    }
    if (paymentOrderQueryParam.getOrderDateEnd() != null) {
      paramMap.put("orderDateEnd", paymentOrderQueryParam.getOrderDateEnd());
    }
    return rpTradePaymentOrderDao.listPage(pageParam, paramMap);
  }

  /**
	 * 分页查询支付记录
	 *
	 * @param pageParam
	 * @param paymentOrderQueryParam
	 * @return
	 */
  @Override public PageBean<RpTradePaymentRecord> listPaymentRecordPage(PageParam pageParam, PaymentOrderQueryParam paymentOrderQueryParam) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("merchantNo", paymentOrderQueryParam.getMerchantNo());
    paramMap.put("merchantName", paymentOrderQueryParam.getMerchantName());
    paramMap.put("merchantOrderNo", paymentOrderQueryParam.getMerchantOrderNo());
    paramMap.put("fundIntoType", paymentOrderQueryParam.getFundIntoType());
    paramMap.put("merchantOrderNo", paymentOrderQueryParam.getOrderDateBegin());
    paramMap.put("merchantOrderNo", paymentOrderQueryParam.getOrderDateEnd());
    paramMap.put("payTypeName", paymentOrderQueryParam.getPayTypeName());
    paramMap.put("payWayName", paymentOrderQueryParam.getPayWayName());
    paramMap.put("status", paymentOrderQueryParam.getStatus());
    if (paymentOrderQueryParam.getOrderDateBegin() != null) {
      paramMap.put("orderDateBegin", paymentOrderQueryParam.getOrderDateBegin());
    }
    if (paymentOrderQueryParam.getOrderDateEnd() != null) {
      paramMap.put("orderDateEnd", paymentOrderQueryParam.getOrderDateEnd());
    }
    return rpTradePaymentRecordDao.listPage(pageParam, paramMap);
  }

  /**
	 * 获取交易流水报表
	 *
	 * @param merchantNo
	 * @return
	 */
  public List<Map<String, String>> getPaymentReport(String merchantNo) {
    return rpTradePaymentRecordDao.getPaymentReport(merchantNo);
  }

  /**
	 * 获取交易方式报表
	 *
	 * @param merchantNo
	 * @return
	 */
  public List<Map<String, String>> getPayWayReport(String merchantNo) {
    return rpTradePaymentRecordDao.getPayWayReport(merchantNo);
  }
}