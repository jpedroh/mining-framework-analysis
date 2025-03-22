package org.broadleafcommerce.core.offer.service.discount.domain;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Offer;
import java.io.Serializable;

public interface PromotableOrderAdjustment extends Serializable {
  /**
     * Returns the associated promotableOrder
     * @return
     */
  public PromotableOrder getPromotableOrder();

  /**
     * Returns the associated promotableCandidateOrderOffer
     * @return
     */
  public Offer getOffer();

  /**
     * Returns the value of this adjustment
     * @return
     */
  public Money getAdjustmentValue();

  /**
     * Returns true if this adjustment represents a combinable offer.
     */
  boolean isCombinable();

  /**
     * Returns true if this adjustment represents a totalitarian offer.
     */
  boolean isTotalitarian();

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