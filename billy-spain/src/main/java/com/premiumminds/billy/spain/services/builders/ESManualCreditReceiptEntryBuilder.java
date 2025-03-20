package com.premiumminds.billy.spain.services.builders;
import com.premiumminds.billy.spain.services.entities.ESCreditReceiptEntry;

public interface ESManualCreditReceiptEntryBuilder<TBuilder extends ESManualCreditReceiptEntryBuilder<TBuilder, TEntry>, TEntry extends ESCreditReceiptEntry> extends ESManualInvoiceEntryBuilder<TBuilder, TEntry>, ESCreditReceiptEntryBuilder<TBuilder, TEntry> {
}