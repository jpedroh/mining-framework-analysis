package org.broadleafcommerce.core.offer.service.discount.domain;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class PromotableCandidateFulfillmentGroupOfferImpl extends AbstractPromotionRounding implements PromotableCandidateFulfillmentGroupOffer {
  private static final long serialVersionUID = 1L;

  protected HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateQualifiersMap = new HashMap<OfferItemCriteria, List<PromotableOrderItem>>();

  protected Offer offer;

  protected PromotableFulfillmentGroup promotableFulfillmentGroup;

  public PromotableCandidateFulfillmentGroupOfferImpl(PromotableFulfillmentGroup promotableFulfillmentGroup, Offer offer) {
    assert (offer != null);
    assert (promotableFulfillmentGroup != null);
    this.offer = offer;
    this.promotableFulfillmentGroup = promotableFulfillmentGroup;
  }

  @Override public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateQualifiersMap() {
    return candidateQualifiersMap;
  }

  @Override public void setCandidateQualifiersMap(HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateItemsMap) {
    this.candidateQualifiersMap = candidateItemsMap;
  }

  protected Money getBasePrice() {
    Money priceToUse = null;
    if (promotableFulfillmentGroup.getFulfillmentGroup().getRetailFulfillmentPrice() != null) {
      priceToUse = promotableFulfillmentGroup.getFulfillmentGroup().getRetailFulfillmentPrice();
      if ((offer.getApplyDiscountToSalePrice()) && (promotableFulfillmentGroup.getFulfillmentGroup().getSaleFulfillmentPrice() != null)) {
        priceToUse = promotableFulfillmentGroup.getFulfillmentGroup().getSaleFulfillmentPrice();
      }
    }
    return priceToUse;
  }

  @Override public Money computeDiscountedAmount() {
    Money discountedAmount = new Money(0);
    Money priceToUse = getBasePrice();
    if (priceToUse != null) {
      if (offer.getDiscountType().equals(OfferDiscountType.AMOUNT_OFF)) {
        discountedAmount = BroadleafCurrencyUtils.getMoney(offer.getValue(), promotableFulfillmentGroup.getFulfillmentGroup().getOrder().getCurrency());
      } else {
        if (offer.getDiscountType().equals(OfferDiscountType.FIX_PRICE)) {
          discountedAmount = priceToUse.subtract(BroadleafCurrencyUtils.getMoney(offer.getValue(), promotableFulfillmentGroup.getFulfillmentGroup().getOrder().getCurrency()));
        } else {
          if (offer.getDiscountType().equals(OfferDiscountType.PERCENT_OFF)) {
            discountedAmount = priceToUse.multiply(offer.getValue().divide(new BigDecimal("100")));
          }
        }
      }
      if (discountedAmount.greaterThan(priceToUse)) {
        discountedAmount = priceToUse;
      }
    }
    return discountedAmount;
  }

  @Override public Money getDiscountedPrice() {
    return getBasePrice().subtract(computeDiscountedAmount());
  }

  @Override public Money getDiscountedAmount() {
    return computeDiscountedAmount();
  }

  @Override public Offer getOffer() {
    return offer;
  }

  @Override public PromotableFulfillmentGroup getFulfillmentGroup() {
    return promotableFulfillmentGroup;
  }

  public int getPriority() {
    return offer.getPriority();
  }
}