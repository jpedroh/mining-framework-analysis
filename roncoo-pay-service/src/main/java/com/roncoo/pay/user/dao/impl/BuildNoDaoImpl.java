package com.roncoo.pay.user.dao.impl;
import com.roncoo.pay.user.entity.SeqBuild;
import com.roncoo.pay.common.core.dao.impl.BaseDaoImpl;
import org.springframework.stereotype.Repository;
import com.roncoo.pay.user.dao.BuildNoDao;

/**
 *  生成编号dao实现类
 * 龙果学院：www.roncoo.com
 * @author：zenghao
 */
@Repository public class BuildNoDaoImpl extends BaseDaoImpl<SeqBuild> implements BuildNoDao {
  @Override public String getSeqNextValue(SeqBuild seqBuild) {
    return super.getSessionTemplate().selectOne(getStatement("getSeqNextValue"), seqBuild);
  }
}