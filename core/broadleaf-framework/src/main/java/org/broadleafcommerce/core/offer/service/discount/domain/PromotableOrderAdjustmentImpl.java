package org.broadleafcommerce.core.offer.service.discount.domain;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PromotableOrderAdjustmentImpl implements PromotableOrderAdjustment {
  private static final long serialVersionUID = 1L;

  protected PromotableCandidateOrderOffer promotableCandidateOrderOffer;

  protected PromotableOrder promotableOrder;

  protected Money adjustmentValue;

  protected Offer offer;

  protected boolean roundOfferValues = true;

  protected int roundingScale = 2;

  protected RoundingMode roundingMode = RoundingMode.HALF_EVEN;

  protected boolean isFutureCredit = false;

  public PromotableOrderAdjustmentImpl(PromotableCandidateOrderOffer promotableCandidateOrderOffer, PromotableOrder promotableOrder) {
    assert (promotableOrder != null);
    assert (promotableCandidateOrderOffer != null);
    this.promotableCandidateOrderOffer = promotableCandidateOrderOffer;
    this.promotableOrder = promotableOrder;
    this.offer = promotableCandidateOrderOffer.getOffer();
    if (this.offer != null) {
      this.setFutureCredit(this.offer.isFutureCredit());
    }
    computeAdjustmentValue();
  }

  public PromotableOrderAdjustmentImpl(PromotableCandidateOrderOffer promotableCandidateOrderOffer, PromotableOrder promotableOrder, Money adjustmentValue) {
    this(promotableCandidateOrderOffer, promotableOrder);
    if (promotableOrder.isIncludeOrderAndItemAdjustments()) {
      this.adjustmentValue = adjustmentValue;
    }
  }

  @Override public PromotableOrder getPromotableOrder() {
    return promotableOrder;
  }

  @Override public Offer getOffer() {
    return offer;
  }

  protected void computeAdjustmentValue() {
    adjustmentValue = new Money(promotableOrder.getOrderCurrency());
    Money currentOrderValue = promotableOrder.calculateSubtotalWithAdjustments();
    currentOrderValue = currentOrderValue.subtract(promotableOrder.calculateOrderAdjustmentTotal());
    if (offer.getDiscountType().equals(OfferDiscountType.AMOUNT_OFF)) {
      adjustmentValue = new Money(offer.getValue(), promotableOrder.getOrderCurrency());
    } else {
      if (offer.getDiscountType().equals(OfferDiscountType.PERCENT_OFF)) {
        BigDecimal offerValue = currentOrderValue.getAmount().multiply(offer.getValue().divide(new BigDecimal("100"), 5, RoundingMode.HALF_EVEN));
        if (isRoundOfferValues()) {
          offerValue = offerValue.setScale(roundingScale, roundingMode);
        }
        adjustmentValue = new Money(offerValue, promotableOrder.getOrderCurrency(), 5);
      }
    }
    if (currentOrderValue.lessThan(adjustmentValue)) {
      adjustmentValue = currentOrderValue;
    }
  }

  @Override public Money getAdjustmentValue() {
    return adjustmentValue;
  }

  /**
     * It is sometimes problematic to offer percentage-off offers with regards to rounding. For example,
     * consider an item that costs 9.99 and has a 50% promotion. To be precise, the offer value is 4.995,
     * but this may be a strange value to display to the user depending on the currency being used.
     */
  public boolean isRoundOfferValues() {
    return roundOfferValues;
  }

  /**
     * @see #isRoundOfferValues()
     * 
     * @param roundingScale
     */
  public void setRoundingScale(int roundingScale) {
    this.roundingScale = roundingScale;
  }

  public int getRoundingScale() {
    return roundingScale;
  }

  /**
     * @see #isRoundOfferValues()
     * 
     * @param roundingMode
     */
  public void setRoundingMode(RoundingMode roundingMode) {
    this.roundingMode = roundingMode;
  }

  public RoundingMode getRoundingMode() {
    return roundingMode;
  }

  @Override public boolean isCombinable() {
    Boolean combinable = offer.isCombinableWithOtherOffers();
    return (combinable != null && combinable);
  }

  @Override public boolean isTotalitarian() {
    Boolean totalitarian = offer.isTotalitarianOffer();
    return (totalitarian != null && totalitarian.booleanValue());
  }

  @Override public boolean isFutureCredit() {
    return isFutureCredit;
  }

  @Override public void setFutureCredit(boolean futureCredit) {
    isFutureCredit = futureCredit;
  }
}