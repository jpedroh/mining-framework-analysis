package com.premiumminds.billy.portugal.services.builders;
import com.premiumminds.billy.core.services.builders.GenericInvoiceEntryBuilder;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoiceEntry;

public interface PTGenericInvoiceEntryBuilder<TBuilder extends PTGenericInvoiceEntryBuilder<TBuilder, TEntry>, TEntry extends PTGenericInvoiceEntry> extends GenericInvoiceEntryBuilder<TBuilder, TEntry> {
}