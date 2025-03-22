package com.roncoo.pay.reconciliation.service.impl;
import java.util.List;
import com.roncoo.pay.common.core.page.PageBean;
import java.util.Map;
import com.roncoo.pay.common.core.page.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import com.roncoo.pay.reconciliation.dao.RpAccountCheckMistakeDao;
import org.springframework.stereotype.Service;
import com.roncoo.pay.reconciliation.entity.RpAccountCheckMistake;
import com.roncoo.pay.reconciliation.service.RpAccountCheckMistakeService;

/**
 * 对账批次接口实现 .
 * 
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
@Service(value = "rpAccountCheckMistakeService") public class RpAccountCheckMistakeServiceImpl implements RpAccountCheckMistakeService {
  @Autowired private RpAccountCheckMistakeDao rpAccountCheckMistakeDao;

  @Override public void saveData(RpAccountCheckMistake rpAccountCheckMistake) {
    rpAccountCheckMistakeDao.insert(rpAccountCheckMistake);
  }

  @Override public void updateData(RpAccountCheckMistake rpAccountCheckMistake) {
    rpAccountCheckMistakeDao.update(rpAccountCheckMistake);
  }

  @Override public RpAccountCheckMistake getDataById(String id) {
    return rpAccountCheckMistakeDao.getById(id);
  }

  @Override public PageBean listPage(PageParam pageParam, Map<String, Object> paramMap) {
    return rpAccountCheckMistakeDao.listPage(pageParam, paramMap);
  }

  /**
	 * 批量保存差错记录
	 * 
	 * @param mistakeList
	 */
  public void saveListDate(List<RpAccountCheckMistake> mistakeList) {
    for (RpAccountCheckMistake mistake : mistakeList) {
      rpAccountCheckMistakeDao.insert(mistake);
    }
  }
}