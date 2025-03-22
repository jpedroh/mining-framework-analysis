package org.broadleafcommerce.core.offer.domain;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;

/**
 * Records the actual adjustments that were made to an OrderItemPriceDetail.
 * 
 * @author bpolster
 *
 */
public interface OrderItemPriceDetailAdjustment extends Adjustment, MultiTenantCloneable<OrderItemPriceDetailAdjustment> {
  /**
     * Stores the offer name at the time the adjustment was made.   Primarily to simplify display 
     * within the admin.
     * 
     * @return
     */
  public String getOfferName();

  /**
     * Returns the name of the offer at the time the adjustment was made.
     * @param offerName
     */
  public void setOfferName(String offerName);

  public OrderItemPriceDetail getOrderItemPriceDetail();

  public void init(OrderItemPriceDetail orderItemPriceDetail, Offer offer, String reason);

  public void setOrderItemPriceDetail(OrderItemPriceDetail orderItemPriceDetail);

  /**
     * Even for items that are on sale, it is possible that an adjustment was made
     * to the retail price that gave the customer a better offer.
     *
     * Since some offers can be applied to the sale price and some only to the
     * retail price, this setting provides the required value.
     *
     * @return true if this adjustment was applied to the sale price
     */
  public boolean isAppliedToSalePrice();

  public void setAppliedToSalePrice(boolean appliedToSalePrice);

  /**
     * Value of this adjustment relative to the retail price.
     * @return
     */
  public Money getRetailPriceValue();

  public void setRetailPriceValue(Money retailPriceValue);

  /**
     * Value of this adjustment relative to the sale price.
     *
     * @return
     */
  public Money getSalesPriceValue();

  public void setSalesPriceValue(Money salesPriceValue);

  /**
     * Future credit means that the associated adjustment will be discounted at a later time to the customer 
     * via a credit. It is up to the implementor to decide how to achieve this. This field is used to determine 
     * if the adjustment originated from an offer marked as FUTURE_CREDIT.
     *
     * See {@link Offer#getAdjustmentType()} for more info
     *
     * @return 
     */
  boolean isFutureCredit();

  /**
     * Future credit means that the associated adjustment will be discounted at a later time to the customer 
     * via a credit. It is up to the implementor to decide how to achieve this. This field is used to determine 
     * if the adjustment originated from an offer marked as FUTURE_CREDIT.
     *
     * See {@link Offer#getAdjustmentType()} for more info
     *
     * @param futureCredit
     */
  void setFutureCredit(boolean futureCredit);
}