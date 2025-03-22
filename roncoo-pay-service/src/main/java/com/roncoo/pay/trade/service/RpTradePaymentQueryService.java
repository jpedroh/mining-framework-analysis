package com.roncoo.pay.trade.service;
import java.util.List;
import com.roncoo.pay.common.core.page.PageBean;
import java.util.Map;
import com.roncoo.pay.common.core.page.PageParam;
import com.roncoo.pay.trade.vo.OrderPayResultVo;
import com.roncoo.pay.trade.entity.RpTradePaymentOrder;
import com.roncoo.pay.trade.vo.PaymentOrderQueryParam;
import com.roncoo.pay.trade.entity.RpTradePaymentRecord;

/**
 * <b>功能说明:交易模块查询接口</b>
 * @author  Peter
 * <a href="http://www.roncoo.com">龙果学院(www.roncoo.com)</a>
 */
public interface RpTradePaymentQueryService {
  /**
	 * 根据参数查询交易记录List
	 * 
	 * @param paremMap
	 * @return
	 */
  public List<RpTradePaymentRecord> listPaymentRecord(Map<String, Object> paremMap);

  /**
	 * 根据商户支付KEY 及商户订单号 查询支付结果
	 * 
	 * @param payKey
	 *            商户支付KEY
	 * @param orderNo
	 *            商户订单号
	 * @return
	 */
  public OrderPayResultVo getPayResult(String payKey, String orderNo);

  /**
	 * 根据银行订单号查询支付记录
	 * 
	 * @param bankOrderNo
	 * @return
	 */
  public RpTradePaymentRecord getRecordByBankOrderNo(String bankOrderNo);

  /**
	 * 根据支付流水号查询支付记录
	 * 
	 * @param trxNo
	 * @return
	 */
  public RpTradePaymentRecord getRecordByTrxNo(String trxNo);

  /**
	 * 分页查询支付订单
	 * @param pageParam
	 * @param paymentOrderQueryParam
	 * @return
	 */
  public PageBean<RpTradePaymentOrder> listPaymentOrderPage(PageParam pageParam, PaymentOrderQueryParam paymentOrderQueryParam);

  /**
	 * 分页查询支付记录
	 * @param pageParam
	 * @param paymentOrderQueryParam
	 * @return
	 */
  public PageBean<RpTradePaymentRecord> listPaymentRecordPage(PageParam pageParam, PaymentOrderQueryParam paymentOrderQueryParam);

  /**
	 * 获取交易流水报表
	 * 
	 * @param merchantNo
	 * @return
	 */
  public List<Map<String, String>> getPaymentReport(String merchantNo);

  /**
	 * 获取交易方式报表
	 * 
	 * @param merchantNo
	 * @return
	 */
  public List<Map<String, String>> getPayWayReport(String merchantNo);
}