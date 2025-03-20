package com.premiumminds.billy.spain.util;
import com.google.inject.Injector;
import com.premiumminds.billy.spain.services.persistence.ESBusinessPersistenceService;
import com.premiumminds.billy.spain.services.persistence.ESCreditNotePersistenceService;
import com.premiumminds.billy.spain.services.persistence.ESCustomerPersistenceService;
import com.premiumminds.billy.spain.services.persistence.ESInvoicePersistenceService;
import com.premiumminds.billy.spain.services.persistence.ESProductPersistenceService;
import com.premiumminds.billy.spain.services.persistence.ESRegionContextPersistenceService;
import com.premiumminds.billy.spain.services.persistence.ESSimpleInvoicePersistenceService;
import com.premiumminds.billy.spain.services.persistence.ESSupplierPersistenceService;
import com.premiumminds.billy.spain.services.persistence.ESTaxPersistenceService;

/**
 * {@link PersistenceServices} provides persistence of Billy's entities.
 */
public class PersistenceServices {
  private Injector injector;

  public PersistenceServices(Injector injector) {
    this.injector = injector;
  }

  /**
     * @return {@link ESBusinessPersistenceService}.
     */
  public ESBusinessPersistenceService business() {
    return this.injector.getInstance(ESBusinessPersistenceService.class);
  }

  /**
     * @return {@link ESCustomerPersistenceService}.
     */
  public ESCustomerPersistenceService customer() {
    return this.injector.getInstance(ESCustomerPersistenceService.class);
  }

  /**
     * @return {@link ESProductPersistenceService}.
     */
  public ESProductPersistenceService product() {
    return this.injector.getInstance(ESProductPersistenceService.class);
  }

  /**
     * @return {@link ESRegionContextPersistenceService}.
     */
  public ESRegionContextPersistenceService context() {
    return this.injector.getInstance(ESRegionContextPersistenceService.class);
  }

  /**
     * @return {@link ESSupplierPersistenceService}.
     */
  public ESSupplierPersistenceService supplier() {
    return this.injector.getInstance(ESSupplierPersistenceService.class);
  }

  /**
     * @return {@link ESTaxPersistenceService}.
     */
  public ESTaxPersistenceService tax() {
    return this.injector.getInstance(ESTaxPersistenceService.class);
  }

  /**
     * @return {@link ESSimpleInvoicePersistenceService}.
     */
  public ESInvoicePersistenceService invoice() {
    return this.injector.getInstance(ESInvoicePersistenceService.class);
  }

  /**
     * @return {@link ESSimpleInvoicePersistenceService}.
     */
  public ESSimpleInvoicePersistenceService simpleInvoice() {
    return this.injector.getInstance(ESSimpleInvoicePersistenceService.class);
  }

  /**
     * @return {@link ESCreditNotePersistenceService}.
     */
  public ESCreditNotePersistenceService creditNote() {
    return this.injector.getInstance(ESCreditNotePersistenceService.class);
  }
}