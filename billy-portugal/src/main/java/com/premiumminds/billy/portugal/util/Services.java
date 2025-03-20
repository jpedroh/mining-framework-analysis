package com.premiumminds.billy.portugal.util;
import com.google.inject.Injector;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.documents.DocumentIssuingService;
import com.premiumminds.billy.core.services.documents.IssuingParams;
import com.premiumminds.billy.core.services.documents.impl.DocumentIssuingServiceImpl;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.portugal.persistence.entities.PTCreditNoteEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTInvoiceEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTReceiptInvoiceEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTSimpleInvoiceEntity;
import com.premiumminds.billy.portugal.services.documents.PTCreditNoteIssuingHandler;
import com.premiumminds.billy.portugal.services.documents.PTInvoiceIssuingHandler;
import com.premiumminds.billy.portugal.services.documents.PTReceiptInvoiceIssuingHandler;
import com.premiumminds.billy.portugal.services.documents.PTSimpleInvoiceIssuingHandler;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParams;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice;

public class Services {
  private final Injector injector;

  private DocumentIssuingService issuingService;

  private PersistenceServices persistenceService;

  public Services(Injector injector) {
    this.injector = injector;
    this.issuingService = injector.getInstance(DocumentIssuingServiceImpl.class);
    this.persistenceService = new PersistenceServices(injector);
    this.setupServices();
  }

  private void setupServices() {
    this.issuingService.addHandler(PTInvoiceEntity.class, this.injector.getInstance(PTInvoiceIssuingHandler.class));
    this.issuingService.addHandler(PTCreditNoteEntity.class, this.injector.getInstance(PTCreditNoteIssuingHandler.class));
    this.issuingService.addHandler(PTSimpleInvoiceEntity.class, this.injector.getInstance(PTSimpleInvoiceIssuingHandler.class));
    this.issuingService.addHandler(PTReceiptInvoiceEntity.class, this.injector.getInstance(PTReceiptInvoiceIssuingHandler.class));
  }

  /**
     * @return {@link PersistenceServices}
     */
  public PersistenceServices entities() {
    return this.persistenceService;
  }

  /**
     * Issue a new document and store it in the database.
     *
     * @param {@link
     *        Builder} of the document to issue.
     * @param {@link
     *        IssuingParams} required to issue the document.
     * @return The newly issued document
     * @throws DocumentIssuingException
     */
  public <T extends PTGenericInvoice> T issueDocument(Builder<T> builder, PTIssuingParams issuingParameters) throws DocumentIssuingException {
    return this.issuingService.issue(builder, issuingParameters);
  }

  public <T extends PTGenericInvoice> T issueDocument(Builder<T> builder, PTIssuingParams issuingParameters, String ticketUID) throws DocumentIssuingException {
    return this.issuingService.issue(builder, issuingParameters, ticketUID);
  }
}