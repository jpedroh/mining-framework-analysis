package org.broadleafcommerce.core.order.service.workflow;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.ProductSkuUsage;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.call.NonDiscreteOrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;

/**
 * Very similar to the {@link CheckAddAvailabilityActivity} but in the blUpdateItemWorkflow instead
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component(value = "blCheckUpdateAvailabilityActivity") public class CheckUpdateAvailabilityActivity extends AbstractCheckAvailabilityActivity {
  private static final Log LOG = LogFactory.getLog(CheckUpdateAvailabilityActivity.class);

  public static final int ORDER = 2000;

  @Resource(name = "blCatalogService") protected CatalogService catalogService;

  @Resource(name = "blOrderItemService") protected OrderItemService orderItemService;

  @Value(value = "${enable.weave.use.default.sku.inventory:false}") protected boolean enableUseDefaultSkuInventory = false;

  public CheckUpdateAvailabilityActivity() {
    setOrder(ORDER);
  }

  @Override public ProcessContext<CartOperationRequest> execute(ProcessContext<CartOperationRequest> context) throws Exception {
    CartOperationRequest request = context.getSeedData();
    OrderItemRequestDTO orderItemRequestDTO = request.getItemRequest();
    if (orderItemRequestDTO instanceof NonDiscreteOrderItemRequestDTO) {
      return context;
    }
    Sku sku;
    Long orderItemId = request.getItemRequest().getOrderItemId();
    OrderItem orderItem = orderItemService.readOrderItemById(orderItemId);
    if (orderItem instanceof DiscreteOrderItem) {
      sku = ((DiscreteOrderItem) orderItem).getSku();
    } else {
      if (orderItem instanceof BundleOrderItem) {
        sku = ((BundleOrderItem) orderItem).getSku();
      } else {
        LOG.warn("Could not check availability; did not recognize passed-in item " + orderItem.getClass().getName());
        return context;
      }
    }

<<<<<<< /usr/src/app/output/broadleafcommerce/broadleafcommerce/09e0d493ef8f08b241da892df08f4751799082a3/core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/order/service/workflow/CheckUpdateAvailabilityActivity.java/left.java
    if (sku.getProduct().getEnableDefaultSkuInInventory()) {
      sku = sku.getProduct().getDefaultSku();
    }
=======
    if (enableUseDefaultSkuInventory && ((ProductSkuUsage) sku.getProduct()).getUseDefaultSkuInInventory()) {
      sku = sku.getProduct().getDefaultSku();
    }
>>>>>>> /usr/src/app/output/broadleafcommerce/broadleafcommerce/09e0d493ef8f08b241da892df08f4751799082a3/core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/order/service/workflow/CheckUpdateAvailabilityActivity.java/right.java

    Order order = context.getSeedData().getOrder();
    Integer requestedQuantity = request.getItemRequest().getQuantity();
    Map<Sku, Integer> skuItems = new HashMap<>();
    for (OrderItem orderItemFromOrder : order.getOrderItems()) {
      Sku skuFromOrder = null;
      if (orderItemFromOrder instanceof DiscreteOrderItem) {
        skuFromOrder = ((DiscreteOrderItem) orderItemFromOrder).getSku();
      } else {
        if (orderItemFromOrder instanceof BundleOrderItem) {
          skuFromOrder = ((BundleOrderItem) orderItemFromOrder).getSku();
        }
      }
      if (
<<<<<<< /usr/src/app/output/broadleafcommerce/broadleafcommerce/09e0d493ef8f08b241da892df08f4751799082a3/core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/order/service/workflow/CheckUpdateAvailabilityActivity.java/left.java
      skuFromOrder != null && skuFromOrder.getProduct().getEnableDefaultSkuInInventory()
=======
      skuFromOrder != null && enableUseDefaultSkuInventory && ((ProductSkuUsage) skuFromOrder.getProduct()).getUseDefaultSkuInInventory()
>>>>>>> /usr/src/app/output/broadleafcommerce/broadleafcommerce/09e0d493ef8f08b241da892df08f4751799082a3/core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/order/service/workflow/CheckUpdateAvailabilityActivity.java/right.java
      ) {
        skuFromOrder = skuFromOrder.getProduct().getDefaultSku();
      }
      if (skuFromOrder != null && skuFromOrder.equals(sku) && !orderItemFromOrder.equals(orderItem)) {
        skuItems.merge(sku, orderItemFromOrder.getQuantity(), (oldVal, newVal) -> oldVal + newVal);
      }
    }
    skuItems.merge(sku, requestedQuantity, (oldVal, newVal) -> oldVal + newVal);
    for (Map.Entry<Sku, Integer> entry : skuItems.entrySet()) {
      checkSkuAvailability(order, entry.getKey(), entry.getValue());
    }
    Integer previousQty = orderItem.getQuantity();
    for (OrderItem child : orderItem.getChildOrderItems()) {
      Sku childSku = ((DiscreteOrderItem) child).getSku();

<<<<<<< /usr/src/app/output/broadleafcommerce/broadleafcommerce/09e0d493ef8f08b241da892df08f4751799082a3/core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/order/service/workflow/CheckUpdateAvailabilityActivity.java/left.java
      if (childSku.getProduct().getEnableDefaultSkuInInventory()) {
        childSku = childSku.getProduct().getDefaultSku();
      }
=======
      if (enableUseDefaultSkuInventory && ((ProductSkuUsage) childSku.getProduct()).getUseDefaultSkuInInventory()) {
        childSku = childSku.getProduct().getDefaultSku();
      }
>>>>>>> /usr/src/app/output/broadleafcommerce/broadleafcommerce/09e0d493ef8f08b241da892df08f4751799082a3/core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/order/service/workflow/CheckUpdateAvailabilityActivity.java/right.java

      Integer childQuantity = child.getQuantity();
      childQuantity = childQuantity / previousQty;
      checkSkuAvailability(order, childSku, childQuantity * requestedQuantity);
    }
    return context;
  }
}