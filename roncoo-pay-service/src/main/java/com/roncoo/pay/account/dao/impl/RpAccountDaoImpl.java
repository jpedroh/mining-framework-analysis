package com.roncoo.pay.account.dao.impl;
import com.roncoo.pay.account.dao.RpAccountDao;
import com.roncoo.pay.account.entity.RpAccount;
import com.roncoo.pay.common.core.dao.impl.BaseDaoImpl;
import com.roncoo.pay.common.core.enums.PublicStatusEnum;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

/**
 * 账户dao实现类
 * 龙果学院：www.roncoo.com
 * @author：zenghao
 */
@Repository public class RpAccountDaoImpl extends BaseDaoImpl<RpAccount> implements RpAccountDao {
  public RpAccount getByAccountNo(String accountNo) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("accountNo", accountNo);
    paramMap.put("status", PublicStatusEnum.ACTIVE.name());
    return this.getBy(paramMap);
  }

  public RpAccount getByUserNo(Map<String, Object> map) {
    return this.getSessionTemplate().selectOne(getStatement("getByUserNo"), map);
  }
}