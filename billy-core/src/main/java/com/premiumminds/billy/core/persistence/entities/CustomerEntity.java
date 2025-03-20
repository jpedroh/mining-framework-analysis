package com.premiumminds.billy.core.persistence.entities;
import java.util.List;
import com.premiumminds.billy.core.services.entities.Address;
import com.premiumminds.billy.core.services.entities.BankAccount;
import com.premiumminds.billy.core.services.entities.Contact;
import com.premiumminds.billy.core.services.entities.Customer;

public interface CustomerEntity extends Customer, BaseEntity {
  public void setName(String name);

  public void setTaxRegistrationNumber(String number);

  @Override public <T extends Address> List<T> getAddresses();

  public <T extends AddressEntity> void setMainAddress(T address);

  public <T extends AddressEntity> void setBillingAddress(T address);

  public <T extends AddressEntity> void setShippingAddress(T address);

  @Override public <T extends Contact> List<T> getContacts();

  public <T extends ContactEntity> void setMainContact(T contact);

  @Override public <T extends BankAccount> List<T> getBankAccounts();

  public void setHasSelfBillingAgreement(boolean selfBiling);
}