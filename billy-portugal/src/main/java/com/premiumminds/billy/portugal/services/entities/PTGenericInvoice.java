package com.premiumminds.billy.portugal.services.entities;
import java.util.List;
import javax.inject.Inject;
import com.premiumminds.billy.core.services.entities.Payment;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoice;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoiceEntry;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTBusiness;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTCustomer;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTGenericInvoice;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTSupplier;
import com.premiumminds.billy.portugal.services.builders.impl.PTGenericInvoiceBuilderImpl;

public interface PTGenericInvoice extends GenericInvoice {
  public static enum TYPE {
    FT,
    FS,
    FR,
    NC,
    ND
  }

  public static enum SourceBilling {
    P,
    M
  }

  public static class Builder extends PTGenericInvoiceBuilderImpl<Builder, PTGenericInvoiceEntry, PTGenericInvoice> {
    @Inject public Builder(DAOPTGenericInvoice daoPTGenericInvoice, DAOPTBusiness daoPTBusiness, DAOPTCustomer daoPTCustomer, DAOPTSupplier daoPTSupplier) {
      super(daoPTGenericInvoice, daoPTBusiness, daoPTCustomer, daoPTSupplier);
    }
  }

  public TYPE getType();

  public boolean isCancelled();

  public boolean isBilled();

  public String getHash();

  public String getSourceHash();

  public String getHashControl();

  public String getEACCode();

  public SourceBilling getSourceBilling();

  public String getChangeReason();

  @Override public <T extends GenericInvoiceEntry> List<T> getEntries();

  @Override public <T extends Payment> List<T> getPayments();
}