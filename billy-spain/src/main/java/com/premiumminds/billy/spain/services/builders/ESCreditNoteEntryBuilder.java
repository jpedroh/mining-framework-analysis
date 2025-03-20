package com.premiumminds.billy.spain.services.builders;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.spain.services.entities.ESCreditNoteEntry;

public interface ESCreditNoteEntryBuilder<TBuilder extends ESCreditNoteEntryBuilder<TBuilder, TEntry>, TEntry extends ESCreditNoteEntry> extends ESGenericInvoiceEntryBuilder<TBuilder, TEntry> {
  public TBuilder setReferenceUID(UID reference);

  public TBuilder setReason(String reason);
}