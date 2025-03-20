package com.premiumminds.billy.portugal.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTInvoice;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTInvoiceEntry;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTProduct;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTRegionContext;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTTax;
import com.premiumminds.billy.portugal.services.builders.impl.PTInvoiceEntryBuilderImpl;
import com.premiumminds.billy.portugal.services.builders.impl.PTManualInvoiceEntryBuilderImpl;

public interface PTInvoiceEntry extends PTGenericInvoiceEntry {
  public static class Builder extends PTInvoiceEntryBuilderImpl<Builder, PTInvoiceEntry> {
    @Inject public Builder(DAOPTInvoiceEntry daoPTEntry, DAOPTInvoice daoPTInvoice, DAOPTTax daoPTTax, DAOPTProduct daoPTProduct, DAOPTRegionContext daoPTRegionContext) {
      super(daoPTEntry, daoPTInvoice, daoPTTax, daoPTProduct, daoPTRegionContext);
    }
  }

  public static class ManualBuilder extends PTManualInvoiceEntryBuilderImpl<ManualBuilder, PTInvoiceEntry> {
    @Inject public ManualBuilder(DAOPTInvoiceEntry daoPTEntry, DAOPTInvoice daoPTInvoice, DAOPTTax daoPTTax, DAOPTProduct daoPTProduct, DAOPTRegionContext daoPTRegionContext) {
      super(daoPTEntry, daoPTInvoice, daoPTTax, daoPTProduct, daoPTRegionContext);
    }
  }
}