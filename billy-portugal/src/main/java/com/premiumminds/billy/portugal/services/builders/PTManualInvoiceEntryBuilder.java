package com.premiumminds.billy.portugal.services.builders;
import java.math.BigDecimal;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoiceEntry;

public interface PTManualInvoiceEntryBuilder<TBuilder extends PTManualInvoiceEntryBuilder<TBuilder, TEntry>, TEntry extends PTGenericInvoiceEntry> extends PTGenericInvoiceEntryBuilder<TBuilder, TEntry> {
  public TBuilder setUnitTaxAmount(BigDecimal taxAmount);

  public TBuilder setAmount(AmountType type, BigDecimal amount);

  public TBuilder setTaxAmount(BigDecimal taxAmount);
}