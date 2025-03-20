package com.premiumminds.billy.spain.util;
import java.io.InputStream;
import com.google.inject.Injector;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.builders.impl.BuilderManager;
import com.premiumminds.billy.core.services.documents.DocumentIssuingService;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.gin.services.ExportService;
import com.premiumminds.billy.gin.services.exceptions.ExportServiceException;
import com.premiumminds.billy.gin.services.export.BillyExportTransformer;
import com.premiumminds.billy.spain.persistence.entities.ESInvoiceEntity;
import com.premiumminds.billy.spain.services.documents.ESInvoiceIssuingHandler;
import com.premiumminds.billy.spain.services.documents.util.ESIssuingParams;
import com.premiumminds.billy.spain.services.entities.ESInvoice;
import com.premiumminds.billy.spain.services.entities.ESInvoiceEntry;
import com.premiumminds.billy.spain.services.export.ESInvoiceData;
import com.premiumminds.billy.spain.services.export.ESInvoiceDataExtractor;
import com.premiumminds.billy.spain.services.export.pdf.invoice.ESInvoicePDFExportRequest;
import com.premiumminds.billy.spain.services.export.pdf.invoice.ESInvoicePDFFOPTransformer;
import com.premiumminds.billy.spain.services.persistence.ESInvoicePersistenceService;

public class Invoices {
  private final Injector injector;

  private final ESInvoicePersistenceService persistenceService;

  private final DocumentIssuingService issuingService;

  private final ExportService exportService;

  public Invoices(Injector injector) {
    this.injector = injector;
    this.persistenceService = this.getInstance(ESInvoicePersistenceService.class);
    this.issuingService = injector.getInstance(DocumentIssuingService.class);
    this.issuingService.addHandler(ESInvoiceEntity.class, this.injector.getInstance(ESInvoiceIssuingHandler.class));
    this.exportService = this.getInstance(ExportService.class);
    this.exportService.addDataExtractor(ESInvoiceData.class, this.getInstance(ESInvoiceDataExtractor.class));
    this.exportService.addTransformerMapper(ESInvoicePDFExportRequest.class, ESInvoicePDFFOPTransformer.class);
  }

  public ESInvoice.Builder builder() {
    return this.getInstance(ESInvoice.Builder.class);
  }

  public ESInvoice.Builder builder(ESInvoice invoice) {
    ESInvoice.Builder builder = this.getInstance(ESInvoice.Builder.class);
    BuilderManager.setTypeInstance(builder, invoice);
    return builder;
  }

  public ESInvoiceEntry.Builder entryBuilder() {
    return this.getInstance(ESInvoiceEntry.Builder.class);
  }

  public ESInvoiceEntry.Builder entrybuilder(ESInvoiceEntry entry) {
    ESInvoiceEntry.Builder builder = this.getInstance(ESInvoiceEntry.Builder.class);
    BuilderManager.setTypeInstance(builder, entry);
    return builder;
  }

  public ESInvoicePersistenceService persistence() {
    return this.persistenceService;
  }

  public ESInvoice issue(ESInvoice.Builder builder, ESIssuingParams params) throws DocumentIssuingException {
    return this.issuingService.issue(builder, params);
  }

  public InputStream pdfExport(ESInvoicePDFExportRequest request) throws ExportServiceException {
    return this.exportService.exportToStream(request);
  }

  public <O extends java.lang.Object> void pdfExport(UID uidDoc, BillyExportTransformer<ESInvoiceData, O> dataTransformer, O output) throws ExportServiceException {
    this.exportService.export(uidDoc, dataTransformer, output);
  }

  public ESInvoice.ManualBuilder manualBuilder() {
    return this.getInstance(ESInvoice.ManualBuilder.class);
  }

  public ESInvoice.ManualBuilder manualBuilder(ESInvoice invoice) {
    ESInvoice.ManualBuilder builder = this.getInstance(ESInvoice.ManualBuilder.class);
    BuilderManager.setTypeInstance(builder, invoice);
    return builder;
  }

  public ESInvoiceEntry.ManualBuilder manualEntryBuilder() {
    return this.getInstance(ESInvoiceEntry.ManualBuilder.class);
  }

  public ESInvoiceEntry.ManualBuilder manualEntrybuilder(ESInvoiceEntry entry) {
    ESInvoiceEntry.ManualBuilder builder = this.getInstance(ESInvoiceEntry.ManualBuilder.class);
    BuilderManager.setTypeInstance(builder, entry);
    return builder;
  }

  public ESInvoice issue(ESInvoice.ManualBuilder builder, ESIssuingParams params) throws DocumentIssuingException {
    return this.issuingService.issue(builder, params);
  }

  private <T extends java.lang.Object> T getInstance(Class<T> clazz) {
    return this.injector.getInstance(clazz);
  }
}