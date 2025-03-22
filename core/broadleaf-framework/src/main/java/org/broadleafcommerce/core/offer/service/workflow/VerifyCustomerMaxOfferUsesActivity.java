package org.broadleafcommerce.core.offer.service.workflow;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.OfferAuditService;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.offer.service.type.CustomerMaxUsesStrategyType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Component;
import java.util.Set;
import javax.annotation.Resource;

/**
 * <p>Checks the offers being used in the order to make sure that the customer
 * has not exceeded the max uses for the {@link Offer}.</p>
 * 
 * This will also verify that max uses for any {@link OfferCode}s that were used to retrieve the {@link Offer}s.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component(value = "blVerifyCustomerMaxOfferUsesActivity") public class VerifyCustomerMaxOfferUsesActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {
  public static final int ORDER = 1000;

  @Resource(name = "blOfferAuditService") protected OfferAuditService offerAuditService;

  @Resource(name = "blOfferService") protected OfferService offerService;

  public VerifyCustomerMaxOfferUsesActivity() {
    setOrder(ORDER);
  }

  @Override public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
    Order order = context.getSeedData().getOrder();
    Set<Offer> appliedOffers = offerService.getUniqueOffersFromOrder(order);
    for (Offer offer : appliedOffers) {
      if (offer.isLimitedUsePerCustomer()) {
        CustomerMaxUsesStrategyType strategy = offer.getMaxUsesStrategyType();
        boolean checkUsingCustomer = (strategy == null || strategy.equals(CustomerMaxUsesStrategyType.CUSTOMER));
        Long currentUses;
        if (checkUsingCustomer) {
          currentUses = offerAuditService.countUsesByCustomer(order, order.getCustomer().getId(), offer.getId(), offer.getMinimumDaysPerUsage());
        } else {
          currentUses = offerAuditService.countUsesByAccount(order, order.getBroadleafAccountId(), offer.getId(), offer.getMinimumDaysPerUsage());
        }
        if (currentUses >= offer.getMaxUsesPerCustomer()) {
          throw new OfferMaxUseExceededException("The customer has used this offer more than the maximum allowed number of times.");
        }
      }
    }
    for (OfferCode code : order.getAddedOfferCodes()) {
      if (code.isLimitedUse()) {
        Long currentCodeUses = offerAuditService.countOfferCodeUses(order, code.getId());
        if (currentCodeUses >= code.getMaxUses()) {
          throw new OfferMaxUseExceededException(" Offer code " + code.getOfferCode() + " has been used more than the maximum allowed number of times. Please remove it and add another code to proceed.");
        }
      }
    }
    return context;
  }
}