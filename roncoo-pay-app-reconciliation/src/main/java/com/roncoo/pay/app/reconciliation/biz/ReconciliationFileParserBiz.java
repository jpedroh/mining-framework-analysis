package com.roncoo.pay.app.reconciliation.biz;
import java.io.File;
import com.roncoo.pay.app.reconciliation.parser.ParserInterface;
import java.io.IOException;
import com.roncoo.pay.reconciliation.entity.RpAccountCheckBatch;
import java.util.Date;
import com.roncoo.pay.reconciliation.vo.ReconciliationEntityVo;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;

/**
 * 对账文件解析业务逻辑.
 *
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
@Component(value = "reconciliationFileParserBiz") public class ReconciliationFileParserBiz implements BeanFactoryAware {
  private BeanFactory beanFactory;

  public Object getService(String payInterface) {
    return beanFactory.getBean(payInterface);
  }

  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  private static final Log LOG = LogFactory.getLog(ReconciliationFileParserBiz.class);

  /**
	 * 解析file文件
	 * 
	 * @param batch
	 *            对账批次实体
	 * @param file
	 *            下载的对账文件
	 * @param billDate
	 *            下载对账单的日期
	 * 
	 * @param interfaceCode
	 *            具体的支付方式
	 * 
	 * @return 转换之后的vo对象
	 * @throws IOException
	 */
  public List<ReconciliationEntityVo> parser(RpAccountCheckBatch batch, File file, Date billDate, String interfaceCode) throws IOException {
    List<ReconciliationEntityVo> rcVoList = null;
    String parserClassName = interfaceCode + "Parser";
    LOG.info("\u6839\u636e\u652f\u4ed8\u65b9\u5f0f\u5f97\u5230\u89e3\u6790\u5668\u7684\u540d\u5b57[" + parserClassName + "]");
    ParserInterface service = null;
    try {
      service = (ParserInterface) this.getService(parserClassName);
    } catch (NoSuchBeanDefinitionException e) {
      LOG.error("\u6839\u636e\u89e3\u6790\u5668\u7684\u540d\u5b57[" + parserClassName + "]\uff0c\u6ca1\u6709\u627e\u5230\u76f8\u5e94\u7684\u89e3\u6790\u5668");
      return null;
    }
    rcVoList = service.parser(file, billDate, batch);
    return rcVoList;
  }
}