package com.premiumminds.billy.spain.services.export.pdf.simpleinvoice;
import java.io.InputStream;
import com.premiumminds.billy.spain.services.export.pdf.ESAbstractTemplateBundle;

public class ESSimpleInvoiceTemplateBundle extends ESAbstractTemplateBundle {
  public ESSimpleInvoiceTemplateBundle(String logoImagePath, InputStream xsltFileStream) {
    super(logoImagePath, xsltFileStream);
  }
}