package com.premiumminds.billy.spain.services.builders;
import com.premiumminds.billy.spain.services.entities.ESReceiptEntry;

public interface ESReceiptEntryBuilder<TBuilder extends ESReceiptEntryBuilder<TBuilder, TEntry>, TEntry extends ESReceiptEntry> extends ESGenericInvoiceEntryBuilder<TBuilder, TEntry> {
}