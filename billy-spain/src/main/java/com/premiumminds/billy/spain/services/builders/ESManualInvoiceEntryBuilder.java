package com.premiumminds.billy.spain.services.builders;
import java.math.BigDecimal;
import com.premiumminds.billy.spain.services.entities.ESGenericInvoiceEntry;

public interface ESManualInvoiceEntryBuilder<TBuilder extends ESManualInvoiceEntryBuilder<TBuilder, TEntry>, TEntry extends ESGenericInvoiceEntry> extends ESGenericInvoiceEntryBuilder<TBuilder, TEntry> {
  public TBuilder setUnitTaxAmount(BigDecimal taxAmount);

  public TBuilder setAmount(AmountType type, BigDecimal amount);

  public TBuilder setTaxAmount(BigDecimal taxAmount);
}