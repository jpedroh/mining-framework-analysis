package com.premiumminds.billy.spain.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.spain.persistence.dao.DAOESInvoice;
import com.premiumminds.billy.spain.persistence.dao.DAOESInvoiceEntry;
import com.premiumminds.billy.spain.persistence.dao.DAOESProduct;
import com.premiumminds.billy.spain.persistence.dao.DAOESRegionContext;
import com.premiumminds.billy.spain.persistence.dao.DAOESTax;
import com.premiumminds.billy.spain.services.builders.impl.ESInvoiceEntryBuilderImpl;
import com.premiumminds.billy.spain.services.builders.impl.ESManualInvoiceEntryBuilderImpl;

public interface ESInvoiceEntry extends ESGenericInvoiceEntry {
  public static class Builder extends ESInvoiceEntryBuilderImpl<Builder, ESInvoiceEntry> {
    @Inject public Builder(DAOESInvoiceEntry daoESEntry, DAOESInvoice daoESInvoice, DAOESTax daoESTax, DAOESProduct daoESProduct, DAOESRegionContext daoESRegionContext) {
      super(daoESEntry, daoESInvoice, daoESTax, daoESProduct, daoESRegionContext);
    }
  }

  public static class ManualBuilder extends ESManualInvoiceEntryBuilderImpl<ManualBuilder, ESInvoiceEntry> {
    @Inject public ManualBuilder(DAOESInvoiceEntry daoESEntry, DAOESInvoice daoESInvoice, DAOESTax daoESTax, DAOESProduct daoESProduct, DAOESRegionContext daoESRegionContext) {
      super(daoESEntry, daoESInvoice, daoESTax, daoESProduct, daoESRegionContext);
    }
  }
}