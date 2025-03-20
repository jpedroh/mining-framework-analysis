package com.premiumminds.billy.spain.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.spain.persistence.dao.DAOESBusiness;
import com.premiumminds.billy.spain.persistence.dao.DAOESCreditNote;
import com.premiumminds.billy.spain.persistence.dao.DAOESCustomer;
import com.premiumminds.billy.spain.persistence.dao.DAOESSupplier;
import com.premiumminds.billy.spain.services.builders.impl.ESCreditNoteBuilderImpl;
import com.premiumminds.billy.spain.services.builders.impl.ESManualCreditNoteBuilderImpl;

public interface ESCreditNote extends ESGenericInvoice {
  public static class Builder extends ESCreditNoteBuilderImpl<Builder, ESCreditNoteEntry, ESCreditNote> {
    @Inject public Builder(DAOESCreditNote daoESCreditNote, DAOESBusiness daoESBusiness, DAOESCustomer daoESCustomer, DAOESSupplier daoESSupplier) {
      super(daoESCreditNote, daoESBusiness, daoESCustomer, daoESSupplier);
    }
  }

  public static class ManualBuilder extends ESManualCreditNoteBuilderImpl<ManualBuilder, ESCreditNoteEntry, ESCreditNote> {
    @Inject public ManualBuilder(DAOESCreditNote daoESCreditNote, DAOESBusiness daoESBusiness, DAOESCustomer daoESCustomer, DAOESSupplier daoESSupplier) {
      super(daoESCreditNote, daoESBusiness, daoESCustomer, daoESSupplier);
    }
  }
}