package com.premiumminds.billy.portugal.util;
import com.google.inject.Injector;
import com.premiumminds.billy.portugal.services.persistence.PTBusinessPersistenceService;
import com.premiumminds.billy.portugal.services.persistence.PTCreditNotePersistenceService;
import com.premiumminds.billy.portugal.services.persistence.PTCustomerPersistenceService;
import com.premiumminds.billy.portugal.services.persistence.PTInvoicePersistenceService;
import com.premiumminds.billy.portugal.services.persistence.PTProductPersistenceService;
import com.premiumminds.billy.portugal.services.persistence.PTRegionContextPersistenceService;
import com.premiumminds.billy.portugal.services.persistence.PTSimpleInvoicePersistenceService;
import com.premiumminds.billy.portugal.services.persistence.PTSupplierPersistenceService;
import com.premiumminds.billy.portugal.services.persistence.PTTaxPersistenceService;

/**
 * {@link PersistenceServices} provides persistence of Billy's entities.
 */
public class PersistenceServices {
  private Injector injector;

  public PersistenceServices(Injector injector) {
    this.injector = injector;
  }

  /**
     * @return {@link PTBusinessPersistenceService}.
     */
  public PTBusinessPersistenceService business() {
    return this.injector.getInstance(PTBusinessPersistenceService.class);
  }

  /**
     * @return {@link PTCustomerPersistenceService}.
     */
  public PTCustomerPersistenceService customer() {
    return this.injector.getInstance(PTCustomerPersistenceService.class);
  }

  /**
     * @return {@link PTProductPersistenceService}.
     */
  public PTProductPersistenceService product() {
    return this.injector.getInstance(PTProductPersistenceService.class);
  }

  /**
     * @return {@link PTRegionContextPersistenceService}.
     */
  public PTRegionContextPersistenceService context() {
    return this.injector.getInstance(PTRegionContextPersistenceService.class);
  }

  /**
     * @return {@link PTSupplierPersistenceService}.
     */
  public PTSupplierPersistenceService supplier() {
    return this.injector.getInstance(PTSupplierPersistenceService.class);
  }

  /**
     * @return {@link PTTaxPersistenceService}.
     */
  public PTTaxPersistenceService tax() {
    return this.injector.getInstance(PTTaxPersistenceService.class);
  }

  /**
     * @return {@link PTSimpleInvoicePersistenceService}.
     */
  public PTInvoicePersistenceService invoice() {
    return this.injector.getInstance(PTInvoicePersistenceService.class);
  }

  /**
     * @return {@link PTSimpleInvoicePersistenceService}.
     */
  public PTSimpleInvoicePersistenceService simpleInvoice() {
    return this.injector.getInstance(PTSimpleInvoicePersistenceService.class);
  }

  /**
     * @return {@link PTCreditNotePersistenceService}.
     */
  public PTCreditNotePersistenceService creditNote() {
    return this.injector.getInstance(PTCreditNotePersistenceService.class);
  }
}