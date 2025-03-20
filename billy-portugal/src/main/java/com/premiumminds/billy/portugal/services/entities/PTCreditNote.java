package com.premiumminds.billy.portugal.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTBusiness;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTCreditNote;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTCustomer;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTSupplier;
import com.premiumminds.billy.portugal.services.builders.impl.PTCreditNoteBuilderImpl;
import com.premiumminds.billy.portugal.services.builders.impl.PTManualCreditNoteBuilderImpl;

public interface PTCreditNote extends PTGenericInvoice {
  public static class Builder extends PTCreditNoteBuilderImpl<Builder, PTCreditNoteEntry, PTCreditNote> {
    @Inject public Builder(DAOPTCreditNote daoPTCreditNote, DAOPTBusiness daoPTBusiness, DAOPTCustomer daoPTCustomer, DAOPTSupplier daoPTSupplier) {
      super(daoPTCreditNote, daoPTBusiness, daoPTCustomer, daoPTSupplier);
    }
  }

  public static class ManualBuilder extends PTManualCreditNoteBuilderImpl<ManualBuilder, PTCreditNoteEntry, PTCreditNote> {
    @Inject public ManualBuilder(DAOPTCreditNote daoPTCreditNote, DAOPTBusiness daoPTBusiness, DAOPTCustomer daoPTCustomer, DAOPTSupplier daoPTSupplier) {
      super(daoPTCreditNote, daoPTBusiness, daoPTCustomer, daoPTSupplier);
    }
  }
}