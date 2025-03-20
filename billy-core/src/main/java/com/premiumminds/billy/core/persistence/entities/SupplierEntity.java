package com.premiumminds.billy.core.persistence.entities;
import java.util.List;
import com.premiumminds.billy.core.services.entities.Address;
import com.premiumminds.billy.core.services.entities.BankAccount;
import com.premiumminds.billy.core.services.entities.Contact;
import com.premiumminds.billy.core.services.entities.Supplier;

public interface SupplierEntity extends Supplier, BaseEntity {
  public void setName(String name);

  public void setTaxRegistrationNumber(String number);

  @Override public List<Address> getAddresses();

  public <T extends AddressEntity> void setMainAddress(T address);

  public <T extends AddressEntity> void setBillingAddress(T address);

  public <T extends AddressEntity> void setShippingAddress(T address);

  @Override public <T extends Contact> List<T> getContacts();

  public <T extends ContactEntity> void setMainContact(T contact);

  @Override public List<BankAccount> getBankAccounts();

  public void setSelfBillingAgreement(boolean selfBilling);
}