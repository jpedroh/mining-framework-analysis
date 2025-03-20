package com.premiumminds.billy.portugal.services.export.pdf.creditnote;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.gin.services.impl.pdf.AbstractExportRequest;

public class PTCreditNotePDFExportRequest extends AbstractExportRequest {
  public PTCreditNotePDFExportRequest(UID uid, PTCreditNoteTemplateBundle bundle) {
    super(uid, bundle);
  }

  public PTCreditNotePDFExportRequest(UID uid, PTCreditNoteTemplateBundle bundle, String resultPath) {
    super(uid, bundle, resultPath);
  }

  @Override public PTCreditNoteTemplateBundle getBundle() {
    return (PTCreditNoteTemplateBundle) this.bundle;
  }
}