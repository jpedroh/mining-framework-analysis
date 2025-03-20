package com.premiumminds.billy.portugal.services.builders;
import com.premiumminds.billy.core.services.builders.SupplierBuilder;
import com.premiumminds.billy.portugal.services.entities.PTSupplier;

public interface PTSupplierBuilder<TBuilder extends PTSupplierBuilder<TBuilder, TSupplier>, TSupplier extends PTSupplier> extends SupplierBuilder<TBuilder, TSupplier> {
  public TBuilder setReferralName(String referralName);
}