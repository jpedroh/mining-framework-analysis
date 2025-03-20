package com.premiumminds.billy.spain.services.entities;
import java.util.List;
import javax.inject.Inject;
import com.premiumminds.billy.core.services.entities.Payment;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoice;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoiceEntry;
import com.premiumminds.billy.spain.persistence.dao.DAOESBusiness;
import com.premiumminds.billy.spain.persistence.dao.DAOESCustomer;
import com.premiumminds.billy.spain.persistence.dao.DAOESGenericInvoice;
import com.premiumminds.billy.spain.persistence.dao.DAOESSupplier;
import com.premiumminds.billy.spain.services.builders.impl.ESGenericInvoiceBuilderImpl;

public interface ESGenericInvoice extends GenericInvoice {
  public static class Builder extends ESGenericInvoiceBuilderImpl<Builder, ESGenericInvoiceEntry, ESGenericInvoice> {
    @Inject public Builder(DAOESGenericInvoice daoESGenericInvoice, DAOESBusiness daoESBusiness, DAOESCustomer daoESCustomer, DAOESSupplier daoESSupplier) {
      super(daoESGenericInvoice, daoESBusiness, daoESCustomer, daoESSupplier);
    }
  }

  public boolean isCancelled();

  public boolean isBilled();

  public String getEACCode();

  @Override public <T extends GenericInvoiceEntry> List<T> getEntries();

  @Override public <T extends Payment> List<T> getPayments();
}