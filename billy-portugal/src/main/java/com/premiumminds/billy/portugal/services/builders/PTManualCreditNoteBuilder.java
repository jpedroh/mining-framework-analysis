package com.premiumminds.billy.portugal.services.builders;
import com.premiumminds.billy.portugal.services.entities.PTCreditNote;
import com.premiumminds.billy.portugal.services.entities.PTCreditNoteEntry;

public interface PTManualCreditNoteBuilder<TBuilder extends PTManualCreditNoteBuilder<TBuilder, TEntry, TDocument>, TEntry extends PTCreditNoteEntry, TDocument extends PTCreditNote> extends PTManualInvoiceBuilder<TBuilder, TEntry, TDocument> {
}