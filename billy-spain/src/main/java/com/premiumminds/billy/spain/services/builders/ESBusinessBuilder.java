package com.premiumminds.billy.spain.services.builders;
import com.premiumminds.billy.core.services.builders.BusinessBuilder;
import com.premiumminds.billy.spain.services.entities.ESBusiness;

public interface ESBusinessBuilder<TBuilder extends ESBusinessBuilder<TBuilder, TBusiness>, TBusiness extends ESBusiness> extends BusinessBuilder<TBuilder, TBusiness> {
  @Override public TBuilder setFinancialID(String id, String countryCode);
}