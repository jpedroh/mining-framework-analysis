package org.broadleafcommerce.core.offer.domain;
import java.io.Serializable;
import java.util.Date;

/**
 * Captures when an offer was applied to a customer.
 *
 * Utilized by the offer process to enforce max use by customer rules and as
 * a high-level audit of what orders and customers have used an offer.
 *
 */
public interface OfferAudit extends Serializable {
  /**
     * System generated unique id for this audit record.
     * @return
     */
  public Long getId();

  /**
     * Sets the id.
     * @param id
     */
  public void setId(Long id);

  /**
     * The associated offer id.
     * @return
     */
  public Long getOfferId();

  /**
     * Sets the associated offer id.
     * @param offerId
     */
  public void setOfferId(Long offerId);

  /**
     * <p>The offer code that was used to retrieve the offer. This will be null if the offer was automatically applied
     * and not obtained by an {@link OfferCode}.</p>
     */
  public Long getOfferCodeId();

  /**
     * <p>Sets the offer code that was used to retrieve the offer. This should be null if the offer was automatically applied
     * and not obtained by an {@link OfferCode}.</p>
     */
  public void setOfferCodeId(Long offerCodeId);

  /**
     * The associated order id.
     * @return
     */
  public Long getOrderId();

  /**
     * Sets the associated order id.
     * @param orderId
     */
  public void setOrderId(Long orderId);

  /**
     * The id of the associated customer.
     * @return
     */
  public Long getCustomerId();

  /**
     * Sets the customer id.
     * @param customerId
     */
  public void setCustomerId(Long customerId);

  /**
     * The id of the associated account.
     * @return
     */
  Long getAccountId();

  /**
     * Sets the associated account id.
     * @param accountId
     */
  void setAccountId(Long customerId);

  /**
     * The date the offer was applied to the order.
     * @return
     */
  public Date getRedeemedDate();

  /**
     * Sets the offer redeemed date.
     * @param redeemedDate
     */
  public void setRedeemedDate(Date redeemedDate);
}