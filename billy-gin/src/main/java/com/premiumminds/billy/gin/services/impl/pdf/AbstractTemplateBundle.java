package com.premiumminds.billy.gin.services.impl.pdf;
import java.io.InputStream;
import com.premiumminds.billy.gin.services.export.BillyTemplateBundle;

public abstract class AbstractTemplateBundle implements BillyTemplateBundle {
  protected final String logoImagePath;

  protected final InputStream xsltFileStream;

  public AbstractTemplateBundle(String logoImagePath, InputStream xsltFileStream) {
    this.logoImagePath = logoImagePath;
    this.xsltFileStream = xsltFileStream;
  }

  @Override public String getLogoImagePath() {
    return this.logoImagePath;
  }

  @Override public InputStream getXSLTFileStream() {
    return this.xsltFileStream;
  }

  @Override public abstract String getPaymentMechanismTranslation(Enum<?> pmc);
}