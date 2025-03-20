package com.premiumminds.billy.gin.services.export;
import java.io.InputStream;

public interface BillyTemplateBundle {
  /**
     * returns the path to the image file to be used as a logo
     *
     * @return The path of the logo image file
     */
  public String getLogoImagePath();

  /**
     * returns the path to the xslt file to be used as the pdf template
     * generator
     *
     * @return The path of the xslt template file.
     */
  public InputStream getXSLTFileStream();

  public String getPaymentMechanismTranslation(Enum<?> pmc);
}