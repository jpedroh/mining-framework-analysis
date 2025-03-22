package org.broadleafcommerce.core.offer.dao;
import org.broadleafcommerce.core.offer.domain.OfferAudit;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.offer.service.workflow.RecordOfferUsageActivity;
import org.broadleafcommerce.core.offer.service.workflow.VerifyCustomerMaxOfferUsesActivity;
import org.broadleafcommerce.core.order.domain.Order;
import java.util.List;

/**
 * DAO for auditing what went on with offers being added to an order
 *
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link VerifyCustomerMaxOfferUsesActivity}, {@link RecordOfferUsageActivity},
 * {@link OfferService#verifyMaxCustomerUsageThreshold(Order, OfferCode)}, 
 * {@link OfferService#verifyMaxCustomerUsageThreshold(Order, OfferCode)}
 */
public interface OfferAuditDao {
  OfferAudit readAuditById(Long offerAuditId);

  /**
     * Persists an audit record to the database
     */
  OfferAudit save(OfferAudit offerAudit);

  void delete(OfferAudit offerAudit);

  /**
     * Creates a new offer audit
     */
  OfferAudit create();

  /**
     * Counts how many times the an offer has been used by a customer. 
     * This method will take into account if the Offer has already been 
     * applied to the Order so as not to prevent the Offer from applying 
     * to new items added to the Order by a CRS.
     * 
     * @param order 
     * @param customerId
     * @param offerId
     * @return number of times and offer has been used by a customer
     */
  Long countUsesByCustomer(Order order, Long customerId, Long offerId);

  /**
     * Counts how many times the an offer has been used by a customer (within the number of passed in days if provided). 
     * This method will take into account if the Offer has already been 
     * applied to the Order so as not to prevent the Offer from applying 
     * to new items added to the Order by a CRS.
     *
     * @param order
     * @param customerId
     * @param offerId
     * @return number of times and offer has been used by a customer
     */
  Long countUsesByCustomer(Order order, Long customerId, Long offerId, Long minimumDaysPerUsage);

  /**
     * Counts how many times the an offer has been used by an account. 
     * This method will take into account if the Offer has already been 
     * applied to the Order so as not to prevent the Offer from applying 
     * to new items added to the Order by a CRS.
     * 
     * @param order 
     * @param accountId
     * @param offerId
     * @return number of times and offer has been used by a customer
     */
  Long countUsesByAccount(Order order, Long accountId, Long offerId);

  /**
     * Counts how many times the an offer has been used by an account (within the number of passed in days if provided). 
     * This method will take into account if the Offer has already been 
     * applied to the Order so as not to prevent the Offer from applying 
     * to new items added to the Order by a CRS.
     *
     * @param order
     * @param accountId
     * @param offerId
     * @return number of times and offer has been used by a customer
     */
  Long countUsesByAccount(Order order, Long accountId, Long offerId, Long minimumDaysPerUsage);

  /**
     * Counts how many times the an offer has been used by a customer
     *
     * @param customerId
     * @param offerId
     * @return number of times and offer has been used by a customer
     * @deprecated use {@link #countUsesByCustomer(Order, Long, Long)}
     */
  Long countUsesByCustomer(Long customerId, Long offerId);

  /**
     * Counts how many times the given offer code has been used in the system. 
     * This method will take into account if the OfferCode has already been 
     * applied to the Order so as not to prevent the OfferCODE from applying 
     * to new items added to the Order by a CRS.
     * 
     * @param order 
     * @param offerCodeId
     * @return number of times the offer code has been used
     */
  Long countOfferCodeUses(Order order, Long offerCodeId);

  /**
     * Counts how many times the given offer code has been used in the system
     *
     * @param offerCodeId
     * @return number of times the offer code has been used
     * @deprecated use {@link #countOfferCodeUses(Order, Long)}
     */
  @Deprecated Long countOfferCodeUses(Long offerCodeId);

  /**
     * Return all offer audits for a particular order
     * @param orderId
     * @return
     */
  List<OfferAudit> readOfferAuditsByOrderId(Long orderId);

  Long getCurrentDateResolution();

  void setCurrentDateResolution(Long currentDateResolution);
}