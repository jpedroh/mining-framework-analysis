package com.roncoo.pay.user.dao.impl;
import java.util.HashMap;
import com.roncoo.pay.common.core.dao.impl.BaseDaoImpl;
import java.util.Map;
import com.roncoo.pay.common.core.enums.PublicStatusEnum;
import com.roncoo.pay.user.entity.RpUserPayConfig;
import com.roncoo.pay.user.dao.RpUserPayConfigDao;
import org.springframework.stereotype.Repository;

/**
 * 用户支付配置dao实现类
 * 龙果学院：www.roncoo.com
 * @author：zenghao
 */
@Repository public class RpUserPayConfigDaoImpl extends BaseDaoImpl<RpUserPayConfig> implements RpUserPayConfigDao {
  @Override public RpUserPayConfig getByUserNo(String userNo, String auditStatus) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("userNo", userNo);
    paramMap.put("status", PublicStatusEnum.ACTIVE.name());
    paramMap.put("auditStatus", auditStatus);
    return super.getBy(paramMap);
  }
}