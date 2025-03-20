package com.premiumminds.billy.portugal.util;
import java.io.InputStream;
import com.google.inject.Injector;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.builders.impl.BuilderManager;
import com.premiumminds.billy.core.services.documents.DocumentIssuingService;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.gin.services.ExportService;
import com.premiumminds.billy.gin.services.exceptions.ExportServiceException;
import com.premiumminds.billy.gin.services.export.BillyExportTransformer;
import com.premiumminds.billy.portugal.persistence.entities.PTSimpleInvoiceEntity;
import com.premiumminds.billy.portugal.services.documents.PTSimpleInvoiceIssuingHandler;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParams;
import com.premiumminds.billy.portugal.services.entities.PTSimpleInvoice;
import com.premiumminds.billy.portugal.services.export.PTSimpleInvoiceData;
import com.premiumminds.billy.portugal.services.export.PTSimpleInvoiceDataExtractor;
import com.premiumminds.billy.portugal.services.export.pdf.simpleinvoice.PTSimpleInvoicePDFExportRequest;
import com.premiumminds.billy.portugal.services.export.pdf.simpleinvoice.PTSimpleInvoicePDFFOPTransformer;
import com.premiumminds.billy.portugal.services.persistence.PTSimpleInvoicePersistenceService;

public class SimpleInvoices {
  private final Injector injector;

  private final PTSimpleInvoicePersistenceService persistenceService;

  private final DocumentIssuingService issuingService;

  private final ExportService exportService;

  public SimpleInvoices(Injector injector) {
    this.injector = injector;
    this.persistenceService = this.getInstance(PTSimpleInvoicePersistenceService.class);
    this.issuingService = injector.getInstance(DocumentIssuingService.class);
    this.issuingService.addHandler(PTSimpleInvoiceEntity.class, this.injector.getInstance(PTSimpleInvoiceIssuingHandler.class));
    this.exportService = this.getInstance(ExportService.class);
    this.exportService.addDataExtractor(PTSimpleInvoiceData.class, this.getInstance(PTSimpleInvoiceDataExtractor.class));
    this.exportService.addTransformerMapper(PTSimpleInvoicePDFExportRequest.class, PTSimpleInvoicePDFFOPTransformer.class);
  }

  public PTSimpleInvoice.Builder builder() {
    return this.getInstance(PTSimpleInvoice.Builder.class);
  }

  public PTSimpleInvoice.Builder builder(PTSimpleInvoice customer) {
    PTSimpleInvoice.Builder builder = this.getInstance(PTSimpleInvoice.Builder.class);
    BuilderManager.setTypeInstance(builder, customer);
    return builder;
  }

  public PTSimpleInvoicePersistenceService persistence() {
    return this.persistenceService;
  }

  public PTSimpleInvoice issue(PTSimpleInvoice.Builder builder, PTIssuingParams params) throws DocumentIssuingException {
    return this.issuingService.issue(builder, params);
  }

  public InputStream pdfExport(PTSimpleInvoicePDFExportRequest request) throws ExportServiceException {
    return this.exportService.exportToStream(request);
  }

  public <O extends java.lang.Object> void pdfExport(UID uidDoc, BillyExportTransformer<PTSimpleInvoiceData, O> dataTransformer, O output) throws ExportServiceException {
    this.exportService.export(uidDoc, dataTransformer, output);
  }

  private <T extends java.lang.Object> T getInstance(Class<T> clazz) {
    return this.injector.getInstance(clazz);
  }
}