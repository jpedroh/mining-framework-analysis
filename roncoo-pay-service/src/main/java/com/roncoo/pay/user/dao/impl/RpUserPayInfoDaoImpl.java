package com.roncoo.pay.user.dao.impl;
import com.roncoo.pay.user.entity.RpUserPayInfo;
import com.roncoo.pay.common.core.dao.impl.BaseDaoImpl;
import org.springframework.stereotype.Repository;
import com.roncoo.pay.user.dao.RpUserPayInfoDao;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户第三方支付信息dao实现类
 * 龙果学院：www.roncoo.com
 * @author：zenghao
 */
@Repository public class RpUserPayInfoDaoImpl extends BaseDaoImpl<RpUserPayInfo> implements RpUserPayInfoDao {
  /**
     * 通过商户编号获取商户第三方支付信息
     *
     * @param userNo
     * @param payWayCode
     * @return
     */
  @Override public RpUserPayInfo getByUserNo(String userNo, String payWayCode) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("userNo", userNo);
    paramMap.put("payWayCode", payWayCode);
    return super.getBy(paramMap);
  }
}