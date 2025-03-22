package org.broadleafcommerce.core.offer.service.discount.domain;
import org.broadleafcommerce.common.money.Money;
import java.io.Serializable;

/**
 * This class holds adjustment records during the discount calculation 
 * processing.  This and other disposable objects avoid churn on the database while the 
 * offer engine determines the best offer(s) for the order being priced.
 * 
 * @author bpolster
 */
public interface PromotableFulfillmentGroupAdjustment extends PromotionRounding, Serializable {
  /**
     * Returns the associated promotableFulfillmentGroup
     * @return
     */
  public PromotableFulfillmentGroup getPromotableFulfillmentGroup();

  /**
     * Returns the associated promotableCandidateOrderOffer
     * @return
     */
  public PromotableCandidateFulfillmentGroupOffer getPromotableCandidateFulfillmentGroupOffer();

  /**
     * Returns the value of this adjustment 
     * @return
     */
  public Money getSaleAdjustmentValue();

  /**
     * Returns the value of this adjustment 
     * @return
     */
  public Money getRetailAdjustmentValue();

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
     * Updates the adjustmentValue to the sales or retail value based on the passed in param
     */
  void finalizeAdjustment(boolean useSaleAdjustments);

  boolean isAppliedToSalePrice();
}