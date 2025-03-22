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
 * This activity handles both adds and updates. In both cases, this will check the availability and quantities (if applicable)
 * of the passed in request. If this is an update request, this will use the {@link Sku} from {@link OrderItemRequestDTO#getOrderItemId()}.
 * If this is an add request, there is no order item yet so the {@link Sku} is looked up via the {@link OrderItemRequestDTO#getSkuId()}.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component(value = "blCheckAddAvailabilityActivity") public class CheckAddAvailabilityActivity extends AbstractCheckAvailabilityActivity {
  private static final Log LOG = LogFactory.getLog(CheckAddAvailabilityActivity.class);

  public static final int ORDER = 2000;

  @Resource(name = "blCatalogService") protected CatalogService catalogService;

  @Resource(name = "blOrderItemService") protected OrderItemService orderItemService;

  @Value(value = "${enable.weave.use.default.sku.inventory:false}") protected boolean enableUseDefaultSkuInventory = false;

  public CheckAddAvailabilityActivity() {
    setOrder(ORDER);
  }

  @Override public ProcessContext<CartOperationRequest> execute(ProcessContext<CartOperationRequest> context) throws Exception {
    CartOperationRequest request = context.getSeedData();
    OrderItemRequestDTO orderItemRequestDTO = request.getItemRequest();
    if (orderItemRequestDTO instanceof NonDiscreteOrderItemRequestDTO) {
      return context;
    }
    Long skuId = request.getItemRequest().getSkuId();
    Sku sku = catalogService.findSkuById(skuId);

<<<<<<< /usr/src/app/output/broadleafcommerce/broadleafcommerce/09e0d493ef8f08b241da892df08f4751799082a3/core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/order/service/workflow/CheckAddAvailabilityActivity.java/left.java
    if (sku.getProduct().getEnableDefaultSkuInInventory()) {
      sku = sku.getProduct().getDefaultSku();
    }
=======
    if (enableUseDefaultSkuInventory && ((ProductSkuUsage) sku.getProduct()).getUseDefaultSkuInInventory()) {
      sku = sku.getProduct().getDefaultSku();
    }
>>>>>>> /usr/src/app/output/broadleafcommerce/broadleafcommerce/09e0d493ef8f08b241da892df08f4751799082a3/core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/order/service/workflow/CheckAddAvailabilityActivity.java/right.java

    Order order = context.getSeedData().getOrder();
    Integer requestedQuantity = request.getItemRequest().getQuantity();
    Map<Sku, Integer> skuItems = new HashMap<>();
    for (OrderItem orderItem : order.getOrderItems()) {
      Sku skuFromOrder = null;
      if (orderItem instanceof DiscreteOrderItem) {
        skuFromOrder = ((DiscreteOrderItem) orderItem).getSku();
      } else {
        if (orderItem instanceof BundleOrderItem) {
          skuFromOrder = ((BundleOrderItem) orderItem).getSku();
        }
      }
      if (
<<<<<<< /usr/src/app/output/broadleafcommerce/broadleafcommerce/09e0d493ef8f08b241da892df08f4751799082a3/core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/order/service/workflow/CheckAddAvailabilityActivity.java/left.java
      skuFromOrder != null && skuFromOrder.getProduct().getEnableDefaultSkuInInventory()
=======
      skuFromOrder != null && enableUseDefaultSkuInventory && ((ProductSkuUsage) skuFromOrder.getProduct()).getUseDefaultSkuInInventory()
>>>>>>> /usr/src/app/output/broadleafcommerce/broadleafcommerce/09e0d493ef8f08b241da892df08f4751799082a3/core/broadleaf-framework/src/main/java/org/broadleafcommerce/core/order/service/workflow/CheckAddAvailabilityActivity.java/right.java
      ) {
        skuFromOrder = skuFromOrder.getProduct().getDefaultSku();
      }
      if (skuFromOrder != null && skuFromOrder.equals(sku)) {
        skuItems.merge(sku, orderItem.getQuantity(), (oldVal, newVal) -> oldVal + newVal);
      }
    }
    skuItems.merge(sku, requestedQuantity, (oldVal, newVal) -> oldVal + newVal);
    for (Map.Entry<Sku, Integer> entry : skuItems.entrySet()) {
      checkSkuAvailability(order, entry.getKey(), entry.getValue());
    }
    return context;
  }
}