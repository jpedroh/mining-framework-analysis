package com.premiumminds.billy.spain.services.builders;
import com.premiumminds.billy.spain.services.entities.ESInvoiceEntry;
import com.premiumminds.billy.spain.services.entities.ESSimpleInvoice;
import com.premiumminds.billy.spain.services.entities.ESSimpleInvoice.CLIENTTYPE;

public interface ESSimpleInvoiceBuilder<TBuilder extends ESSimpleInvoiceBuilder<TBuilder, TEntry, TDocument>, TEntry extends ESInvoiceEntry, TDocument extends ESSimpleInvoice> extends ESInvoiceBuilder<TBuilder, TEntry, TDocument> {
  public TBuilder setClientType(CLIENTTYPE type);
}