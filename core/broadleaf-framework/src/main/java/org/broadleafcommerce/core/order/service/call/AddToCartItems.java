package org.broadleafcommerce.core.order.service.call;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import java.util.ArrayList;
import java.util.List;

public class AddToCartItems {
  @SuppressWarnings(value = { "unchecked" }) private List<OrderItemRequestDTO> addToCartItems = LazyList.decorate(new ArrayList<OrderItemRequestDTO>(), FactoryUtils.instantiateFactory(OrderItemRequestDTO.class));

  private long productId;

  private long categoryId;

  public void setProductId(long productId) {
    this.productId = productId;
    for (OrderItemRequestDTO addToCartItem : addToCartItems) {
      addToCartItem.setProductId(productId);
    }
  }

  public void setCategoryId(long categoryId) {
    this.categoryId = categoryId;
    for (OrderItemRequestDTO addToCartItem : addToCartItems) {
      addToCartItem.setCategoryId(categoryId);
    }
  }

  public List<OrderItemRequestDTO> getAddToCartItems() {
    return addToCartItems;
  }

  public void setAddToCartItem(List<OrderItemRequestDTO> addToCartItems) {
    this.addToCartItems = addToCartItems;
  }

  public long getProductId() {
    return productId;
  }

  public long getCategoryId() {
    return categoryId;
  }
}