package org.broadleafcommerce.core.offer.domain;
import org.broadleafcommerce.core.order.domain.Order;

public interface OrderAdjustment extends Adjustment {
  public Order getOrder();

  public void init(Order order, Offer offer, String reason);

  public void setOrder(Order order);

  /**
     * Future credit means that the associated adjustment will be discounted at a later time to the customer 
     * via a credit. It is up to the implementor to decide how to achieve this. This field is used to determine 
     * if the adjustment originated from an offer marked as FUTURE_CREDIT.
     *
     * See {@link Offer#getAdjustmentType()} for more info
     *
     * @return
     */
  Boolean isFutureCredit();

  /**
     * Future credit means that the associated adjustment will be discounted at a later time to the customer 
     * via a credit. It is up to the implementor to decide how to achieve this. This field is used to determine 
     * if the adjustment originated from an offer marked as FUTURE_CREDIT.
     *
     * See {@link Offer#getAdjustmentType()} for more info
     *
     * @param futureCredit
     */
  void setFutureCredit(Boolean futureCredit);
}