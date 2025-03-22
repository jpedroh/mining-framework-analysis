package com.roncoo.pay.user.service.impl;
import java.util.Date;
import com.roncoo.pay.common.core.enums.PayTypeEnum;
import java.util.HashMap;
import com.roncoo.pay.common.core.enums.PayWayEnum;
import java.util.List;
import com.roncoo.pay.common.core.enums.PublicEnum;
import java.util.Map;
import com.roncoo.pay.common.core.enums.PublicStatusEnum;
import com.roncoo.pay.user.exception.PayBizException;
import com.roncoo.pay.common.core.page.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import com.roncoo.pay.common.core.page.PageParam;
import org.springframework.stereotype.Service;
import com.roncoo.pay.common.core.utils.StringUtil;
import com.roncoo.pay.user.dao.RpPayWayDao;
import com.roncoo.pay.user.entity.RpPayProduct;
import com.roncoo.pay.user.entity.RpPayWay;
import com.roncoo.pay.user.service.RpPayProductService;
import com.roncoo.pay.user.service.RpPayWayService;

/**
 * 支付方式service实现类
 * 龙果学院：www.roncoo.com
 * @author：zenghao
 */
@Service(value = "rpPayWayService") public class RpPayWayServiceImpl implements RpPayWayService {
  @Autowired private RpPayWayDao rpPayWayDao;

  @Autowired private RpPayProductService rpPayProductService;

  @Override public void saveData(RpPayWay rpPayWay) {
    rpPayWayDao.insert(rpPayWay);
  }

  @Override public void updateData(RpPayWay rpPayWay) {
    rpPayWayDao.update(rpPayWay);
  }

  @Override public RpPayWay getDataById(String id) {
    return rpPayWayDao.getById(id);
  }

  @Override public PageBean listPage(PageParam pageParam, RpPayWay rpPayWay) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("status", PublicStatusEnum.ACTIVE.name());
    paramMap.put("payProductCode", rpPayWay.getPayProductCode());
    paramMap.put("payWayName", rpPayWay.getPayWayName());
    paramMap.put("payTypeName", rpPayWay.getPayTypeName());
    return rpPayWayDao.listPage(pageParam, paramMap);
  }

  @Override public RpPayWay getByPayWayTypeCode(String payProductCode, String payWayCode, String payTypeCode) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("payProductCode", payProductCode);
    paramMap.put("payTypeCode", payTypeCode);
    paramMap.put("payWayCode", payWayCode);
    paramMap.put("status", PublicStatusEnum.ACTIVE.name());
    return rpPayWayDao.getBy(paramMap);
  }

  /**
	 * 绑定支付费率
	 * @param payWayCode
	 * @param payTypeCode
	 * @param payRate
	 */
  @Override public void createPayWay(String payProductCode, String payWayCode, String payTypeCode, Double payRate) throws PayBizException {
    RpPayWay payWay = getByPayWayTypeCode(payProductCode, payWayCode, payTypeCode);
    if (payWay != null) {
      throw new PayBizException(PayBizException.PAY_TYPE_IS_EXIST, "\u652f\u4ed8\u6e20\u9053\u5df2\u5b58\u5728");
    }
    RpPayProduct rpPayProduct = rpPayProductService.getByProductCode(payProductCode, null);
    if (rpPayProduct.getAuditStatus().equals(PublicEnum.YES.name())) {
      throw new PayBizException(PayBizException.PAY_PRODUCT_IS_EFFECTIVE, "\u652f\u4ed8\u4ea7\u54c1\u5df2\u751f\u6548\uff0c\u65e0\u6cd5\u7ed1\u5b9a\uff01");
    }
    RpPayWay rpPayWay = new RpPayWay();
    rpPayWay.setPayProductCode(payProductCode);
    rpPayWay.setPayRate(payRate);
    rpPayWay.setPayWayCode(payWayCode);
    rpPayWay.setPayWayName(PayWayEnum.getEnum(payWayCode).getDesc());
    rpPayWay.setPayTypeCode(payTypeCode);
    rpPayWay.setPayTypeName(PayTypeEnum.getEnum(payTypeCode).getDesc());
    rpPayWay.setStatus(PublicStatusEnum.ACTIVE.name());
    rpPayWay.setCreateTime(new Date());
    rpPayWay.setId(StringUtil.get32UUID());
    saveData(rpPayWay);
  }

  /**
	 * 根据支付产品获取支付方式
	 */
  @Override public List<RpPayWay> listByProductCode(String payProductCode) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("payProductCode", payProductCode);
    paramMap.put("status", PublicStatusEnum.ACTIVE.name());
    return rpPayWayDao.listBy(paramMap);
  }

  /**
	 * 获取所有支付方式
	 */
  @Override public List<RpPayWay> listAll() {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("status", PublicStatusEnum.ACTIVE.name());
    return rpPayWayDao.listBy(paramMap);
  }
}