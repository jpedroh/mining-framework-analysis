package com.premiumminds.billy.portugal.services.builders;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.portugal.services.entities.PTCreditNoteEntry;

public interface PTCreditNoteEntryBuilder<TBuilder extends PTCreditNoteEntryBuilder<TBuilder, TEntry>, TEntry extends PTCreditNoteEntry> extends PTGenericInvoiceEntryBuilder<TBuilder, TEntry> {
  public TBuilder setReferenceUID(UID reference);

  public TBuilder setReason(String reason);
}