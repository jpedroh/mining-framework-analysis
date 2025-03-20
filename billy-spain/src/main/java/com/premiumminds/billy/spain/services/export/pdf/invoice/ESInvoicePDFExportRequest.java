package com.premiumminds.billy.spain.services.export.pdf.invoice;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.gin.services.impl.pdf.AbstractExportRequest;

public class ESInvoicePDFExportRequest extends AbstractExportRequest {
  public ESInvoicePDFExportRequest(UID uid, ESInvoiceTemplateBundle bundle) {
    super(uid, bundle);
  }

  public ESInvoicePDFExportRequest(UID uid, ESInvoiceTemplateBundle bundle, String resultPath) {
    super(uid, bundle, resultPath);
  }

  @Override public ESInvoiceTemplateBundle getBundle() {
    return (ESInvoiceTemplateBundle) this.bundle;
  }
}