package com.premiumminds.billy.core.services.builders;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.entities.Address;
import com.premiumminds.billy.core.services.entities.BankAccount;
import com.premiumminds.billy.core.services.entities.Contact;
import com.premiumminds.billy.core.services.entities.Supplier;

public interface SupplierBuilder<TBuilder extends SupplierBuilder<TBuilder, TSupplier>, TSupplier extends Supplier> extends Builder<TSupplier> {
  public TBuilder setName(String name);

  public TBuilder setTaxRegistrationNumber(String number, String countryCode);

  public <T extends Address> TBuilder addAddress(Builder<T> addressBuilder);

  public <T extends Address> TBuilder setMainAddress(Builder<T> addressBuilder);

  public <T extends Address> TBuilder setBillingAddress(Builder<T> addressBuilder);

  public <T extends Address> TBuilder setShippingAddress(Builder<T> addressBuilder);

  public <T extends Contact> TBuilder addContact(Builder<T> contactBuilder);

  public <T extends Contact> TBuilder setMainContact(Builder<T> contactBuilder);

  public <T extends BankAccount> TBuilder addBankAccount(Builder<T> accountBuilder);

  public TBuilder setSelfBillingAgreement(boolean selfBilling);
}