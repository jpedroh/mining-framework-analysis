package com.premiumminds.billy.spain.services.builders;
import com.premiumminds.billy.core.services.builders.SupplierBuilder;
import com.premiumminds.billy.spain.services.entities.ESSupplier;

public interface ESSupplierBuilder<TBuilder extends ESSupplierBuilder<TBuilder, TSupplier>, TSupplier extends ESSupplier> extends SupplierBuilder<TBuilder, TSupplier> {
  public TBuilder setReferralName(String referralName);
}