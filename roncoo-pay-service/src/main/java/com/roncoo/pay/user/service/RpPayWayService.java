package com.roncoo.pay.user.service;
import java.util.List;
import com.roncoo.pay.common.core.page.PageBean;
import com.roncoo.pay.common.core.page.PageParam;
import com.roncoo.pay.user.exception.PayBizException;
import com.roncoo.pay.user.entity.RpPayWay;

/**
 * 支付方式service接口
 * 龙果学院：www.roncoo.com
 * @author：zenghao
 */
public interface RpPayWayService {
  /**
	 * 保存
	 */
  void saveData(RpPayWay rpPayWay);

  /**
	 * 更新
	 */
  void updateData(RpPayWay rpPayWay);

  /**
	 * 根据id获取数据
	 * 
	 * @param id
	 * @return
	 */
  RpPayWay getDataById(String id);

  /**
	 * 根据支付方式、渠道编码获取数据
	 * @param rpTypeCode
	 * @return
	 */
  RpPayWay getByPayWayTypeCode(String payProductCode, String payWayCode, String rpTypeCode);

  /**
	 * 获取分页数据
	 * 
	 * @param pageParam
	 * @return
	 */
  PageBean listPage(PageParam pageParam, RpPayWay rpPayWay);

  /**
	 * 绑定支付费率
	 * @param payWayCode
	 * @param payTypeCode
	 * @param payRate
	 */
  void createPayWay(String payProductCode, String payWayCode, String payTypeCode, Double payRate) throws PayBizException;

  /**
	 * 根据支付产品获取支付方式
	 * @param payProductCode
	 */
  List<RpPayWay> listByProductCode(String payProductCode);

  /**
	 * 获取所有支付方式
	 */
  List<RpPayWay> listAll();
}