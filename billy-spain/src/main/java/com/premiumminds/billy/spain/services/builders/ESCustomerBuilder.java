package com.premiumminds.billy.spain.services.builders;
import com.premiumminds.billy.core.services.builders.CustomerBuilder;
import com.premiumminds.billy.spain.services.entities.ESCustomer;

public interface ESCustomerBuilder<TBuilder extends ESCustomerBuilder<TBuilder, TCustomer>, TCustomer extends ESCustomer> extends CustomerBuilder<TBuilder, TCustomer> {
  public TBuilder setReferralName(String referralName);

  @Override public TBuilder setTaxRegistrationNumber(String number, String countryCode);
}