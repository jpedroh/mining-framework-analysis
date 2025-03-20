package com.premiumminds.billy.portugal.services.export.pdf.receiptinvoice;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.gin.services.impl.pdf.AbstractExportRequest;

public class PTReceiptInvoicePDFExportRequest extends AbstractExportRequest {
  public PTReceiptInvoicePDFExportRequest(UID uid, PTReceiptInvoiceTemplateBundle bundle) {
    super(uid, bundle);
  }

  @Override public PTReceiptInvoiceTemplateBundle getBundle() {
    return (PTReceiptInvoiceTemplateBundle) this.bundle;
  }
}