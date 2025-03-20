package com.premiumminds.billy.core.services.entities;

public interface InvoiceSeries extends Entity {
  public String getSeries();

  public <T extends Business> T getBusiness();
}