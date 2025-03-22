package org.broadleafcommerce.core.offer.service.discount.domain;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustment;

public class PromotableOrderItemPriceDetailAdjustmentImpl extends AbstractPromotionRounding implements PromotableOrderItemPriceDetailAdjustment, OfferHolder {
  private static final long serialVersionUID = 1L;

  protected PromotableCandidateItemOffer promotableCandidateItemOffer;

  protected PromotableOrderItemPriceDetail promotableOrderItemPriceDetail;

  protected Money saleAdjustmentValue;

  protected Money retailAdjustmentValue;

  protected Money adjustmentValue;

  protected boolean appliedToSalePrice;

  protected Offer offer;

  public PromotableOrderItemPriceDetailAdjustmentImpl(PromotableCandidateItemOffer promotableCandidateItemOffer, PromotableOrderItemPriceDetail orderItemPriceDetail, Money retailAdjustmentValue, Money saleAdjustmentValue) {
    assert (promotableCandidateItemOffer != null);
    assert (orderItemPriceDetail != null);
    this.promotableCandidateItemOffer = promotableCandidateItemOffer;
    this.promotableOrderItemPriceDetail = orderItemPriceDetail;
    this.offer = promotableCandidateItemOffer.getOffer();
    this.retailAdjustmentValue = retailAdjustmentValue;
    this.saleAdjustmentValue = saleAdjustmentValue;
  }

  public PromotableOrderItemPriceDetailAdjustmentImpl(OrderItemPriceDetailAdjustment itemAdjustment, PromotableOrderItemPriceDetail orderItemPriceDetail) {
    assert (orderItemPriceDetail != null);
    adjustmentValue = itemAdjustment.getValue();
    if (itemAdjustment.isAppliedToSalePrice()) {
      saleAdjustmentValue = itemAdjustment.getValue();
      retailAdjustmentValue = itemAdjustment.getRetailPriceValue();
    } else {
      retailAdjustmentValue = itemAdjustment.getValue();
      saleAdjustmentValue = itemAdjustment.getSalesPriceValue();
    }
    appliedToSalePrice = itemAdjustment.isAppliedToSalePrice();
    promotableOrderItemPriceDetail = orderItemPriceDetail;
    offer = itemAdjustment.getOffer();
  }

  @Override public Money getRetailAdjustmentValue() {
    return retailAdjustmentValue;
  }

  @Override public Money getSaleAdjustmentValue() {
    return saleAdjustmentValue;
  }

  @Override public BroadleafCurrency getCurrency() {
    return promotableOrderItemPriceDetail.getPromotableOrderItem().getCurrency();
  }

  @Override public PromotableOrderItemPriceDetail getPromotableOrderItemPriceDetail() {
    return promotableOrderItemPriceDetail;
  }

  @Override public Offer getOffer() {
    return offer;
  }

  @Override public boolean isCombinable() {
    Boolean combinable = offer.isCombinableWithOtherOffers();
    return (combinable != null && combinable);
  }

  @Override public boolean isTotalitarian() {
    Boolean totalitarian = offer.isTotalitarianOffer();
    return (totalitarian != null && totalitarian.booleanValue());
  }

  @Override public Long getOfferId() {
    return offer.getId();
  }

  @Override public Money getAdjustmentValue() {
    return adjustmentValue;
  }

  @Override public boolean isAppliedToSalePrice() {
    return appliedToSalePrice;
  }

  @Override public void finalizeAdjustment(boolean useSalePrice) {
    appliedToSalePrice = useSalePrice;
    if (useSalePrice) {
      adjustmentValue = saleAdjustmentValue;
    } else {
      adjustmentValue = retailAdjustmentValue;
    }
  }

  @Override public PromotableOrderItemPriceDetailAdjustment copy() {
    PromotableOrderItemPriceDetailAdjustmentImpl newAdjustment = new PromotableOrderItemPriceDetailAdjustmentImpl(promotableCandidateItemOffer, promotableOrderItemPriceDetail, retailAdjustmentValue, saleAdjustmentValue);
    newAdjustment.adjustmentValue = adjustmentValue;
    newAdjustment.saleAdjustmentValue = saleAdjustmentValue;
    newAdjustment.retailAdjustmentValue = retailAdjustmentValue;
    newAdjustment.appliedToSalePrice = appliedToSalePrice;
    return newAdjustment;
  }
}