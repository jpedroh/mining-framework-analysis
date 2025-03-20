package com.premiumminds.billy.spain.services.builders;
import com.premiumminds.billy.core.services.builders.GenericInvoiceBuilder;
import com.premiumminds.billy.spain.services.entities.ESGenericInvoice;
import com.premiumminds.billy.spain.services.entities.ESGenericInvoiceEntry;

public interface ESGenericInvoiceBuilder<TBuilder extends ESGenericInvoiceBuilder<TBuilder, TEntry, TDocument>, TEntry extends ESGenericInvoiceEntry, TDocument extends ESGenericInvoice> extends GenericInvoiceBuilder<TBuilder, TEntry, TDocument> {
  public TBuilder setCancelled(boolean cancelled);

  public TBuilder setBilled(boolean billed);
}