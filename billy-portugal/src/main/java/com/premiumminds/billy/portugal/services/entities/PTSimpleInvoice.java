package com.premiumminds.billy.portugal.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTBusiness;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTCustomer;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTSimpleInvoice;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTSupplier;
import com.premiumminds.billy.portugal.services.builders.impl.PTSimpleInvoiceBuilderImpl;

public interface PTSimpleInvoice extends PTInvoice {
  public static enum CLIENTTYPE {
    CUSTOMER,
    BUSINESS
  }

  public static class Builder extends PTSimpleInvoiceBuilderImpl<Builder, PTInvoiceEntry, PTSimpleInvoice> {
    @Inject public Builder(DAOPTSimpleInvoice daoPTSimpleInvoice, DAOPTBusiness daoPTBusiness, DAOPTCustomer daoPTCustomer, DAOPTSupplier daoPTSupplier) {
      super(daoPTSimpleInvoice, daoPTBusiness, daoPTCustomer, daoPTSupplier);
    }
  }

  public CLIENTTYPE getClientType();
}