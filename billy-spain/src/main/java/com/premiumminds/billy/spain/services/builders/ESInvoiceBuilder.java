package com.premiumminds.billy.spain.services.builders;
import com.premiumminds.billy.spain.services.entities.ESInvoice;
import com.premiumminds.billy.spain.services.entities.ESInvoiceEntry;

public interface ESInvoiceBuilder<TBuilder extends ESInvoiceBuilder<TBuilder, TEntry, TDocument>, TEntry extends ESInvoiceEntry, TDocument extends ESInvoice> extends ESGenericInvoiceBuilder<TBuilder, TEntry, TDocument> {
}