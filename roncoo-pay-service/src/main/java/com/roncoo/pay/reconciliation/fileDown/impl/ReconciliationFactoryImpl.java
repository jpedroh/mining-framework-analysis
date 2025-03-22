package com.roncoo.pay.reconciliation.fileDown.impl;
import java.io.File;
import com.roncoo.pay.reconciliation.fileDown.service.FileDown;
import java.util.Date;
import com.roncoo.pay.reconciliation.fileDown.service.ReconciliationFactory;
import org.springframework.beans.factory.BeanFactory;
import com.roncoo.pay.reconciliation.utils.ReconciliationConfigUtil;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Service;

/**
 * 文件下载factory
 *
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
@Service(value = "reconciliationFactory") public class ReconciliationFactoryImpl implements ReconciliationFactory, BeanFactoryAware {
  private BeanFactory beanFactory;

  /**
	 * 去Spring容器中根据beanName获取对象（也可以直接根据名字创建实例，可以参考后面流程中的parser）
	 * 
	 * @param payInterface
	 * @return
	 */
  public Object getService(String payInterface) {
    return beanFactory.getBean(payInterface);
  }

  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  /**
	 * 账单下载
	 * 
	 * @param payInterface
	 *            支付渠道
	 * 
	 * @param billDate
	 *            账单日
	 */
  public File fileDown(String payInterface, Date billDate) throws Exception {
    FileDown fileDown = (FileDown) this.getService(payInterface);
    String dir = ReconciliationConfigUtil.readConfig("dir") + payInterface.toLowerCase();
    return fileDown.fileDown(billDate, dir);
  }
}