package com.roncoo.pay.permission.service.impl;
import java.util.HashMap;
import com.roncoo.pay.common.core.page.PageBean;
import java.util.Map;
import com.roncoo.pay.common.core.page.PageParam;
import com.roncoo.pay.permission.dao.PmsOperatorLogDao;
import com.roncoo.pay.permission.entity.PmsOperatorLog;
import com.roncoo.pay.permission.service.PmsOperatorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 操作员service接口实现
 *
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
@Service(value = "pmsOperatorLogService") public class PmsOperatorLogServiceImpl implements PmsOperatorLogService {
  @Autowired private PmsOperatorLogDao pmsOperatorLogDao;

  /**
	 * 创建pmsOperator
	 */
  public void saveData(PmsOperatorLog pmsOperatorLog) {
    pmsOperatorLogDao.insert(pmsOperatorLog);
  }

  /**
	 * 修改pmsOperator
	 */
  public void updateData(PmsOperatorLog pmsOperatorLog) {
    pmsOperatorLogDao.update(pmsOperatorLog);
  }

  /**
	 * 根据id获取数据pmsOperator
	 * 
	 * @param id
	 * @return
	 */
  public PmsOperatorLog getDataById(Long id) {
    return pmsOperatorLogDao.getById(id);
  }

  /**
	 * 分页查询pmsOperator
	 * 
	 * @param pageParam
	 * @param ActivityVo
	 *            PmsOperator
	 * @return
	 */
  public PageBean listPage(PageParam pageParam, PmsOperatorLog pmsOperatorLog) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    return pmsOperatorLogDao.listPage(pageParam, paramMap);
  }
}