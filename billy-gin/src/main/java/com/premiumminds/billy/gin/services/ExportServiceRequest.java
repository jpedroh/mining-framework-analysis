package com.premiumminds.billy.gin.services;
import com.premiumminds.billy.gin.services.export.BillyTemplateBundle;

public interface ExportServiceRequest {
  /**
     * returns the bundle associated with this request.
     *
     * @return returns null if there is no bundle
     */
  public BillyTemplateBundle getBundle();

  public String getResultPath();
}