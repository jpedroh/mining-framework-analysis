package com.premiumminds.billy.portugal.services.builders;
import java.math.BigDecimal;
import com.premiumminds.billy.core.services.builders.GenericInvoiceEntryBuilder.AmountType;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoiceEntry;

public interface PTManualInvoiceBuilder<TBuilder extends PTManualInvoiceBuilder<TBuilder, TEntry, TDocument>, TEntry extends PTGenericInvoiceEntry, TDocument extends PTGenericInvoice> extends PTGenericInvoiceBuilder<TBuilder, TEntry, TDocument> {
  public TBuilder setAmount(AmountType type, BigDecimal amount);

  public TBuilder setTaxAmount(BigDecimal taxAmount);
}