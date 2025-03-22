package org.broadleafcommerce.core.order.domain;
import junit.framework.TestCase;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderAdjustmentImpl;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOfferImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactoryImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOfferUtility;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOfferUtilityImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemPriceDetail;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemPriceDetailImpl;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class OrderItemTest extends TestCase {
  private PromotableOrderItemPriceDetail priceDetail1;

  private PromotableCandidateItemOffer candidateOffer;

  private Offer offer;

  @Override protected void setUp() throws Exception {
    PromotableOfferUtility promotableOfferUtility = new PromotableOfferUtilityImpl();
    PromotableOrder promotableOrder = new PromotableOrderImpl(new OrderImpl(), new PromotableItemFactoryImpl(promotableOfferUtility), false);
    DiscreteOrderItemImpl discreteOrderItem1 = new DiscreteOrderItemImpl();
    discreteOrderItem1.setName("test1");
    discreteOrderItem1.setOrderItemType(OrderItemType.DISCRETE);
    discreteOrderItem1.setQuantity(2);
    discreteOrderItem1.setRetailPrice(new Money(19.99D));
    OrderItemPriceDetail pdetail = new OrderItemPriceDetailImpl();
    pdetail.setOrderItem(discreteOrderItem1);
    pdetail.setQuantity(2);
    PromotableOrderItem orderItem1 = new PromotableOrderItemImpl(discreteOrderItem1, null, new PromotableItemFactoryImpl(promotableOfferUtility), false);
    priceDetail1 = new PromotableOrderItemPriceDetailImpl(orderItem1, 2);
    OfferDataItemProvider dataProvider = new OfferDataItemProvider();
    offer = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", OfferDiscountType.PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))").get(0);
    candidateOffer = new PromotableCandidateItemOfferImpl(promotableOrder, offer);
  }

  public void testGetQuantityAvailableToBeUsedAsQualifier() throws Exception {
    int quantity = priceDetail1.getQuantityAvailableToBeUsedAsQualifier(candidateOffer);
    assertTrue(quantity == 2);
    PromotionDiscount discount = new PromotionDiscount();
    discount.setPromotion(offer);
    discount.setQuantity(1);
    priceDetail1.getPromotionDiscounts().add(discount);
    quantity = priceDetail1.getQuantityAvailableToBeUsedAsQualifier(candidateOffer);
    assertTrue(quantity == 1);
    Offer testOffer = new OfferImpl();
    testOffer.setOfferItemQualifierRuleType(OfferItemRestrictionRuleType.NONE);
    testOffer.setOfferItemTargetRuleType(OfferItemRestrictionRuleType.NONE);
    discount.setPromotion(testOffer);
    quantity = priceDetail1.getQuantityAvailableToBeUsedAsQualifier(candidateOffer);
    assertTrue(quantity == 1);
    testOffer.setOfferItemTargetRuleType(OfferItemRestrictionRuleType.QUALIFIER);
    candidateOffer.getOffer().setOfferItemQualifierRuleType(OfferItemRestrictionRuleType.TARGET);
    quantity = priceDetail1.getQuantityAvailableToBeUsedAsQualifier(candidateOffer);
    priceDetail1.getPromotionDiscounts().clear();
    PromotionQualifier qualifier = new PromotionQualifier();
    qualifier.setPromotion(offer);
    qualifier.setQuantity(1);
    priceDetail1.getPromotionQualifiers().add(qualifier);
    quantity = priceDetail1.getQuantityAvailableToBeUsedAsQualifier(candidateOffer);
    assertTrue(quantity == 1);
    qualifier.setPromotion(testOffer);
    quantity = priceDetail1.getQuantityAvailableToBeUsedAsQualifier(candidateOffer);
    assertTrue(quantity == 1);
    testOffer.setOfferItemQualifierRuleType(OfferItemRestrictionRuleType.QUALIFIER);
    candidateOffer.getOffer().setOfferItemQualifierRuleType(OfferItemRestrictionRuleType.QUALIFIER);
    quantity = priceDetail1.getQuantityAvailableToBeUsedAsQualifier(candidateOffer);
  }

  public void testGetQuantityAvailableToBeUsedAsTarget() throws Exception {
    int quantity = priceDetail1.getQuantityAvailableToBeUsedAsTarget(candidateOffer);
    assertTrue(quantity == 2);
    PromotionDiscount discount = new PromotionDiscount();
    discount.setPromotion(offer);
    discount.setQuantity(1);
    priceDetail1.getPromotionDiscounts().add(discount);
    quantity = priceDetail1.getQuantityAvailableToBeUsedAsTarget(candidateOffer);
    assertTrue(quantity == 1);
    Offer tempOffer = new OfferImpl();
    tempOffer.setCombinableWithOtherOffers(true);
    tempOffer.setOfferItemQualifierRuleType(OfferItemRestrictionRuleType.NONE);
    tempOffer.setOfferItemTargetRuleType(OfferItemRestrictionRuleType.NONE);
    discount.setPromotion(tempOffer);
    quantity = priceDetail1.getQuantityAvailableToBeUsedAsTarget(candidateOffer);
    assertTrue(quantity == 1);
    tempOffer.setOfferItemTargetRuleType(OfferItemRestrictionRuleType.TARGET);
    quantity = priceDetail1.getQuantityAvailableToBeUsedAsTarget(candidateOffer);
    assertTrue(quantity == 1);
    candidateOffer.getOffer().setOfferItemTargetRuleType(OfferItemRestrictionRuleType.TARGET);
    quantity = priceDetail1.getQuantityAvailableToBeUsedAsTarget(candidateOffer);
    assertTrue(quantity == 2);
    priceDetail1.getPromotionDiscounts().clear();
    candidateOffer.getOffer().setOfferItemTargetRuleType(OfferItemRestrictionRuleType.NONE);
    PromotionQualifier qualifier = new PromotionQualifier();
    qualifier.setPromotion(offer);
    qualifier.setQuantity(1);
    priceDetail1.getPromotionQualifiers().add(qualifier);
    quantity = priceDetail1.getQuantityAvailableToBeUsedAsTarget(candidateOffer);
    assertTrue(quantity == 1);
    qualifier.setPromotion(tempOffer);
    quantity = priceDetail1.getQuantityAvailableToBeUsedAsTarget(candidateOffer);
    assertTrue(quantity == 1);
    tempOffer.setOfferItemQualifierRuleType(OfferItemRestrictionRuleType.TARGET);
    candidateOffer.getOffer().setOfferItemTargetRuleType(OfferItemRestrictionRuleType.QUALIFIER);
    quantity = priceDetail1.getQuantityAvailableToBeUsedAsTarget(candidateOffer);
  }

  /**
     * This test checks the return value of OrderImpl.getHasOrderAdjustments()
     * By default (when there is no order adjustment), the method should return false
     * If an adjustment is given, the method should return true
     */
  public void testGetHasOrderAdjustments() {
    OrderImpl order = new OrderImpl();
    assertFalse(order.getHasOrderAdjustments());
    OrderAdjustmentImpl adjustment = new OrderAdjustmentImpl();
    List<OrderAdjustment> adjustmentList = new ArrayList<OrderAdjustment>();
    adjustment.setOrder(order);
    adjustment.setValue(new Money(19.99D));
    adjustmentList.add(adjustment);
    order.setOrderAdjustments(adjustmentList);
    assertTrue(order.getHasOrderAdjustments());
  }
}