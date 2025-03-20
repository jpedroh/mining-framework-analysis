package com.premiumminds.billy.spain.persistence.entities;
import java.util.List;
import com.premiumminds.billy.core.persistence.entities.GenericInvoiceEntity;
import com.premiumminds.billy.core.services.entities.Payment;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoiceEntry;
import com.premiumminds.billy.spain.services.entities.ESGenericInvoice;

public interface ESGenericInvoiceEntity extends GenericInvoiceEntity, ESGenericInvoice {
  public void setCancelled(boolean cancelled);

  public void setBilled(boolean billed);

  public void setEACCode(String eacCode);

  @Override public <T extends GenericInvoiceEntry> List<T> getEntries();

  @Override public <T extends Payment> List<T> getPayments();
}