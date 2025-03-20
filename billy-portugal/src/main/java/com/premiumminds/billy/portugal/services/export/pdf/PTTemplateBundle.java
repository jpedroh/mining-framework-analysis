package com.premiumminds.billy.portugal.services.export.pdf;
import com.premiumminds.billy.gin.services.export.BillyTemplateBundle;

public interface PTTemplateBundle extends BillyTemplateBundle {
  public String getGenericCustomer();

  public String getSoftwareCertificationId();
}