package com.premiumminds.billy.portugal.services.builders;
import java.math.BigDecimal;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.portugal.services.entities.PTPayment;

public interface PTPaymentBuilder<TBuilder extends PTPaymentBuilder<TBuilder, TPayment>, TPayment extends PTPayment> extends Builder<TPayment> {
  public TBuilder setPaymentAmount(BigDecimal amount);
}