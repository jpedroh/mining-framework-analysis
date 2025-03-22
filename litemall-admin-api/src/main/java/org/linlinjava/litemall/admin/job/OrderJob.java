package org.linlinjava.litemall.admin.job;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 检测订单状态
 */
@Component public class OrderJob {
  private final Log logger = LogFactory.getLog(OrderJob.class);

  @Autowired private LitemallOrderGoodsService orderGoodsService;

  @Autowired private LitemallOrderService orderService;

  @Autowired private LitemallGoodsProductService productService;

  @Autowired private LitemallGrouponService grouponService;

  @Autowired private LitemallGrouponRulesService rulesService;


<<<<<<< /usr/src/app/output/linlinjava/litemall/f16b2f35fc8aa4e77ceb428cabc946c330c7c05e/litemall-admin-api/src/main/java/org/linlinjava/litemall/admin/job/OrderJob.java/left.java
  /**
     * 自动取消订单
     * <p>
     * 定时检查订单未付款情况，如果超时 LITEMALL_ORDER_UNPAID 分钟则自动取消订单
     * 定时时间是每次相隔半个小时。
     * <p>
     * TODO
     * 注意，因为是相隔半小时检查，因此导致订单真正超时时间是 [LITEMALL_ORDER_UNPAID, 30 + LITEMALL_ORDER_UNPAID]
     */
  @Scheduled(fixedDelay = 30 * 60 * 1000) @Transactional(rollbackFor = Exception.class) public void checkOrderUnpaid() {
    logger.info("\u7cfb\u7edf\u5f00\u542f\u4efb\u52a1\u68c0\u67e5\u8ba2\u5355\u662f\u5426\u5df2\u7ecf\u8d85\u671f\u81ea\u52a8\u53d6\u6d88\u8ba2\u5355");
    List<LitemallOrder> orderList = orderService.queryUnpaid(SystemConfig.getOrderUnpaid());
    for (LitemallOrder order : orderList) {
      order.setOrderStatus(OrderUtil.STATUS_AUTO_CANCEL);
      order.setEndTime(LocalDateTime.now());
      cancelOrderScope(order);
      logger.info("\u8ba2\u5355 ID" + order.getId() + " \u5df2\u7ecf\u8d85\u671f\u81ea\u52a8\u53d6\u6d88\u8ba2\u5355");
    }
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  /**
     * 自动确认订单
     * <p>
     * 定时检查订单未确认情况，如果超时 LITEMALL_ORDER_UNCONFIRM 天则自动确认订单
     * 定时时间是每天凌晨3点。
     * <p>
     * TODO
     * 注意，因为是相隔一天检查，因此导致订单真正超时时间是 [LITEMALL_ORDER_UNCONFIRM, 1 + LITEMALL_ORDER_UNCONFIRM]
     */
  @Scheduled(cron = "0 0 3 * * ?") public void checkOrderUnconfirm() {
    logger.info("\u7cfb\u7edf\u5f00\u542f\u4efb\u52a1\u68c0\u67e5\u8ba2\u5355\u662f\u5426\u5df2\u7ecf\u8d85\u671f\u81ea\u52a8\u786e\u8ba4\u6536\u8d27");
    List<LitemallOrder> orderList = orderService.queryUnconfirm(SystemConfig.getOrderUnconfirm());
    for (LitemallOrder order : orderList) {
      order.setOrderStatus(OrderUtil.STATUS_AUTO_CONFIRM);
      order.setConfirmTime(LocalDateTime.now());
      if (orderService.updateWithOptimisticLocker(order) == 0) {
        logger.info("\u8ba2\u5355 ID=" + order.getId() + " \u6570\u636e\u5df2\u7ecf\u66f4\u65b0\uff0c\u653e\u5f03\u81ea\u52a8\u786e\u8ba4\u6536\u8d27");
      } else {
        logger.info("\u8ba2\u5355 ID=" + order.getId() + " \u5df2\u7ecf\u8d85\u671f\u81ea\u52a8\u786e\u8ba4\u6536\u8d27");
      }
    }
  }

  /**
     * 可评价订单商品超期
     * <p>
     * 定时检查订单商品评价情况，如果确认商品超时 LITEMALL_ORDER_COMMENT 天则取消可评价状态
     * 定时时间是每天凌晨4点。
     * <p>
     * TODO
     * 注意，因为是相隔一天检查，因此导致订单真正超时时间是 [LITEMALL_ORDER_COMMENT, 1 + LITEMALL_ORDER_COMMENT]
     */
  @Scheduled(cron = "0 0 4 * * ?") public void checkOrderComment() {
    logger.info("\u7cfb\u7edf\u5f00\u542f\u4efb\u52a1\u68c0\u67e5\u8ba2\u5355\u662f\u5426\u5df2\u7ecf\u8d85\u671f\u672a\u8bc4\u4ef7");
    List<LitemallOrder> orderList = orderService.queryComment(SystemConfig.getOrderComment());
    for (LitemallOrder order : orderList) {
      order.setComments((short) 0);
      orderService.updateWithOptimisticLocker(order);
      List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
      for (LitemallOrderGoods orderGoods : orderGoodsList) {
        orderGoods.setComment(-1);
        orderGoodsService.updateById(orderGoods);
      }
    }
  }

  /**
     * 团购订单拼团超期自动取消
     */
  @Scheduled(initialDelay = 5000, fixedDelay = 10 * 60 * 1000) @Transactional(rollbackFor = Exception.class) public void checkGrouponOrderTimeout() {
    logger.info("\u7cfb\u7edf\u5f00\u542f\u4efb\u52a1\u68c0\u67e5\u56e2\u8d2d\u8ba2\u5355\u662f\u5426\u5df2\u7ecf\u62fc\u56e2\u8d85\u671f\u81ea\u52a8\u53d6\u6d88\u8ba2\u5355");
    List<LitemallGroupon> grouponList = grouponService.queryJoinRecord(0);
    for (LitemallGroupon groupon : grouponList) {
      LitemallGrouponRules rules = rulesService.queryById(groupon.getRulesId());
      if (rulesService.isExpired(rules)) {
        List<LitemallGroupon> subGrouponList = grouponService.queryJoinRecord(groupon.getId());
        for (LitemallGroupon subGroupon : subGrouponList) {
          cancelGrouponScope(subGroupon);
        }
        cancelGrouponScope(groupon);
      }
    }
  }

  private void cancelGrouponScope(LitemallGroupon groupon) {
    LitemallOrder order = orderService.findById(groupon.getOrderId());
    if (order.getOrderStatus().equals(OrderUtil.STATUS_PAY_GROUPON)) {
      order.setOrderStatus(OrderUtil.STATUS_TIMEOUT_GROUPON);
      order.setEndTime(LocalDateTime.now());
      cancelOrderScope(order);
      logger.info("\u56e2\u8d2d\u8ba2\u5355 ID" + order.getId() + " \u5df2\u7ecf\u62fc\u56e2\u8d85\u671f\u81ea\u52a8\u53d6\u6d88\u8ba2\u5355");
    }
  }

  private void cancelOrderScope(LitemallOrder order) {
    if (orderService.updateWithOptimisticLocker(order) == 0) {
      throw new RuntimeException("\u66f4\u65b0\u6570\u636e\u5df2\u5931\u6548");
    }
    Integer orderId = order.getId();
    List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
    for (LitemallOrderGoods orderGoods : orderGoodsList) {
      Integer productId = orderGoods.getProductId();
      Short number = orderGoods.getNumber();
      if (productService.addStock(productId, number) == 0) {
        throw new RuntimeException("\u5546\u54c1\u8d27\u54c1\u5e93\u5b58\u589e\u52a0\u5931\u8d25");
      }
    }
  }
}