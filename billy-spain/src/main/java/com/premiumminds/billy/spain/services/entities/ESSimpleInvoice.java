package com.premiumminds.billy.spain.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.spain.persistence.dao.DAOESBusiness;
import com.premiumminds.billy.spain.persistence.dao.DAOESCustomer;
import com.premiumminds.billy.spain.persistence.dao.DAOESSimpleInvoice;
import com.premiumminds.billy.spain.persistence.dao.DAOESSupplier;
import com.premiumminds.billy.spain.services.builders.impl.ESSimpleInvoiceBuilderImpl;

public interface ESSimpleInvoice extends ESInvoice {
  public static enum CLIENTTYPE {
    CUSTOMER,
    BUSINESS
  }

  public static class Builder extends ESSimpleInvoiceBuilderImpl<Builder, ESInvoiceEntry, ESSimpleInvoice> {
    @Inject public Builder(DAOESSimpleInvoice daoESSimpleInvoice, DAOESBusiness daoESBusiness, DAOESCustomer daoESCustomer, DAOESSupplier daoESSupplier) {
      super(daoESSimpleInvoice, daoESBusiness, daoESCustomer, daoESSupplier);
    }
  }

  public CLIENTTYPE getClientType();
}