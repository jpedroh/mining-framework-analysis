package com.premiumminds.billy.portugal.services.builders;
import com.premiumminds.billy.portugal.services.entities.PTCreditNote;
import com.premiumminds.billy.portugal.services.entities.PTCreditNoteEntry;

public interface PTCreditNoteBuilder<TBuilder extends PTCreditNoteBuilder<TBuilder, TEntry, TDocument>, TEntry extends PTCreditNoteEntry, TDocument extends PTCreditNote> extends PTGenericInvoiceBuilder<TBuilder, TEntry, TDocument> {
}