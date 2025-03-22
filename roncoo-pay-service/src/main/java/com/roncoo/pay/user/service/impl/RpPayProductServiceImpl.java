package com.roncoo.pay.user.service.impl;
import java.util.Date;
import com.roncoo.pay.common.core.enums.PublicEnum;
import java.util.HashMap;
import com.roncoo.pay.common.core.enums.PublicStatusEnum;
import java.util.List;
import com.roncoo.pay.common.core.page.PageBean;
import java.util.Map;
import com.roncoo.pay.common.core.page.PageParam;
import com.roncoo.pay.user.exception.PayBizException;
import com.roncoo.pay.common.core.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.roncoo.pay.user.dao.RpPayProductDao;
import org.springframework.stereotype.Service;
import com.roncoo.pay.user.entity.RpPayProduct;
import com.roncoo.pay.user.entity.RpPayWay;
import com.roncoo.pay.user.entity.RpUserPayConfig;
import com.roncoo.pay.user.service.RpPayProductService;
import com.roncoo.pay.user.service.RpPayWayService;
import com.roncoo.pay.user.service.RpUserPayConfigService;

/**
 * 支付产品service实现类
 * 龙果学院：www.roncoo.com
 * @author：zenghao
 */
@Service(value = "rpPayProductService") public class RpPayProductServiceImpl implements RpPayProductService {
  @Autowired private RpPayProductDao rpPayProductDao;

  @Autowired private RpPayWayService rpPayWayService;

  @Autowired private RpUserPayConfigService rpUserPayConfigService;

  @Override public void saveData(RpPayProduct rpPayProduct) {
    rpPayProductDao.insert(rpPayProduct);
  }

  @Override public void updateData(RpPayProduct rpPayProduct) {
    rpPayProductDao.update(rpPayProduct);
  }

  @Override public RpPayProduct getDataById(String id) {
    return rpPayProductDao.getById(id);
  }

  @Override public PageBean listPage(PageParam pageParam, RpPayProduct rpPayProduct) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("status", PublicStatusEnum.ACTIVE.name());
    paramMap.put("auditStatus", rpPayProduct.getAuditStatus());
    paramMap.put("productName", rpPayProduct.getProductName());
    return rpPayProductDao.listPage(pageParam, paramMap);
  }

  /**
	 * 根据产品编号获取支付产品
	 */
  @Override public RpPayProduct getByProductCode(String productCode, String auditStatus) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("productCode", productCode);
    paramMap.put("status", PublicStatusEnum.ACTIVE.name());
    paramMap.put("auditStatus", auditStatus);
    return rpPayProductDao.getBy(paramMap);
  }

  /**
	 * 创建支付产品
	 * @param productCode
	 * @param productName
	 */
  @Override public void createPayProduct(String productCode, String productName) throws PayBizException {
    RpPayProduct rpPayProduct = getByProductCode(productCode, null);
    if (rpPayProduct != null) {
      throw new PayBizException(PayBizException.PAY_PRODUCT_IS_EXIST, "\u652f\u4ed8\u4ea7\u54c1\u5df2\u5b58\u5728");
    }
    rpPayProduct = new RpPayProduct();
    rpPayProduct.setStatus(PublicStatusEnum.ACTIVE.name());
    rpPayProduct.setCreateTime(new Date());
    rpPayProduct.setId(StringUtil.get32UUID());
    rpPayProduct.setProductCode(productCode);
    rpPayProduct.setProductName(productName);
    rpPayProduct.setAuditStatus(PublicEnum.NO.name());
    saveData(rpPayProduct);
  }

  /**
	 * 删除支付产品
	 * @param productCode
	 */
  @Override public void deletePayProduct(String productCode) throws PayBizException {
    List<RpPayWay> payWayList = rpPayWayService.listByProductCode(productCode);
    if (!payWayList.isEmpty()) {
      throw new PayBizException(PayBizException.PAY_PRODUCT_HAS_DATA, "\u652f\u4ed8\u4ea7\u54c1\u5df2\u5173\u8054\u652f\u4ed8\u65b9\u5f0f\uff0c\u65e0\u6cd5\u5220\u9664\uff01");
    }
    List<RpUserPayConfig> payConfigList = rpUserPayConfigService.listByProductCode(productCode);
    if (!payConfigList.isEmpty()) {
      throw new PayBizException(PayBizException.PAY_PRODUCT_HAS_DATA, "\u652f\u4ed8\u4ea7\u54c1\u5df2\u5173\u8054\u7528\u6237\uff0c\u65e0\u6cd5\u5220\u9664\uff01");
    }
    RpPayProduct rpPayProduct = getByProductCode(productCode, null);
    if (rpPayProduct.getAuditStatus().equals(PublicEnum.YES.name())) {
      throw new PayBizException(PayBizException.PAY_PRODUCT_IS_EFFECTIVE, "\u652f\u4ed8\u4ea7\u54c1\u5df2\u751f\u6548\uff0c\u65e0\u6cd5\u5220\u9664\uff01");
    }
    rpPayProduct.setStatus(PublicStatusEnum.UNACTIVE.name());
    updateData(rpPayProduct);
  }

  /**
	 * 获取所有支付产品
	 * @param productCode
	 */
  @Override public List<RpPayProduct> listAll() {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("status", PublicStatusEnum.ACTIVE.name());
    return rpPayProductDao.listBy(paramMap);
  }

  /**
	 * 审核
	 * @param productCode
	 * @param auditStatus
	 */
  @Override public void audit(String productCode, String auditStatus) throws PayBizException {
    RpPayProduct rpPayProduct = getByProductCode(productCode, null);
    if (rpPayProduct == null) {
      throw new PayBizException(PayBizException.PAY_PRODUCT_IS_NOT_EXIST, "\u652f\u4ed8\u4ea7\u54c1\u4e0d\u5b58\u5728\uff01");
    }
    if (auditStatus.equals(PublicEnum.YES.name())) {
      List<RpPayWay> payWayList = rpPayWayService.listByProductCode(productCode);
      if (payWayList.isEmpty()) {
        throw new PayBizException(PayBizException.PAY_TYPE_IS_NOT_EXIST, "\u652f\u4ed8\u65b9\u5f0f\u672a\u8bbe\u7f6e\uff0c\u65e0\u6cd5\u64cd\u4f5c\uff01");
      }
    } else {
      if (auditStatus.equals(PublicEnum.NO.name())) {
        List<RpUserPayConfig> payConfigList = rpUserPayConfigService.listByProductCode(productCode);
        if (!payConfigList.isEmpty()) {
          throw new PayBizException(PayBizException.USER_PAY_CONFIG_IS_EXIST, "\u652f\u4ed8\u4ea7\u54c1\u5df2\u5173\u8054\u7528\u6237\u652f\u4ed8\u914d\u7f6e\uff0c\u65e0\u6cd5\u64cd\u4f5c\uff01");
        }
      }
    }
    rpPayProduct.setAuditStatus(auditStatus);
    rpPayProduct.setEditTime(new Date());
    updateData(rpPayProduct);
  }
}