package com.premiumminds.billy.spain.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.spain.persistence.dao.DAOESProduct;
import com.premiumminds.billy.spain.persistence.dao.DAOESReceipt;
import com.premiumminds.billy.spain.persistence.dao.DAOESReceiptEntry;
import com.premiumminds.billy.spain.persistence.dao.DAOESRegionContext;
import com.premiumminds.billy.spain.persistence.dao.DAOESTax;
import com.premiumminds.billy.spain.services.builders.impl.ESReceiptEntryBuilderImpl;

public interface ESReceiptEntry extends ESGenericInvoiceEntry {
  public static class Builder extends ESReceiptEntryBuilderImpl<Builder, ESReceiptEntry> {
    @Inject public Builder(DAOESReceiptEntry daoESReceiptEntry, DAOESReceipt daoESReceipt, DAOESTax daoESTax, DAOESProduct daoESProduct, DAOESRegionContext daoESRegionContext) {
      super(daoESReceiptEntry, daoESReceipt, daoESTax, daoESProduct, daoESRegionContext);
    }
  }
}