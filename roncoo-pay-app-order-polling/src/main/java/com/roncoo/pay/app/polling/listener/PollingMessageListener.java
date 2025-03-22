package com.roncoo.pay.app.polling.listener;
import com.alibaba.fastjson.JSONObject;
import com.roncoo.pay.app.polling.core.PollingQueue;
import com.roncoo.pay.app.polling.entity.PollingParam;
import com.roncoo.pay.common.core.exception.BizException;
import com.roncoo.pay.notify.entity.RpOrderResultQueryVo;
import com.roncoo.pay.notify.enums.NotifyStatusEnum;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Date;
import java.util.Map;

/**
 * @author wujing
 */
@Component(value = "pollingMessageListener") public class PollingMessageListener implements MessageListener {
  private static final Log log = LogFactory.getLog(PollingMessageListener.class);

  @Autowired private PollingQueue pollingQueue;

  @Autowired private PollingParam pollingParam;

  @Override public void onMessage(Message message) {
    try {
      ActiveMQTextMessage msg = (ActiveMQTextMessage) message;
      final String msgText = msg.getText();
      log.info("== receive bankOrderNo :" + msgText);
      RpOrderResultQueryVo rpOrderResultQueryVo = new RpOrderResultQueryVo();
      rpOrderResultQueryVo.setBankOrderNo(msgText);
      rpOrderResultQueryVo.setStatus(NotifyStatusEnum.CREATED.name());
      rpOrderResultQueryVo.setCreateTime(new Date());
      rpOrderResultQueryVo.setEditTime(new Date());
      rpOrderResultQueryVo.setLastNotifyTime(new Date());
      rpOrderResultQueryVo.setNotifyTimes(0);
      rpOrderResultQueryVo.setLimitNotifyTimes(pollingParam.getMaxNotifyTimes());
      Map<Integer, Integer> notifyParams = pollingParam.getNotifyParams();
      rpOrderResultQueryVo.setNotifyRule(JSONObject.toJSONString(notifyParams));
      try {
        pollingQueue.addToNotifyTaskDelayQueue(rpOrderResultQueryVo);
      } catch (BizException e) {
        log.error("BizException :", e);
      } catch (Exception e) {
        log.error(e);
      }
    } catch (Exception e) {
      log.error(e);
    }
  }
}