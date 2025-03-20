package com.premiumminds.billy.core.services.builders.impl;
import javax.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOContact;
import com.premiumminds.billy.core.persistence.dao.DAOCustomer;
import com.premiumminds.billy.core.persistence.entities.AddressEntity;
import com.premiumminds.billy.core.persistence.entities.ContactEntity;
import com.premiumminds.billy.core.persistence.entities.CustomerEntity;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.builders.CustomerBuilder;
import com.premiumminds.billy.core.services.entities.Address;
import com.premiumminds.billy.core.services.entities.BankAccount;
import com.premiumminds.billy.core.services.entities.Contact;
import com.premiumminds.billy.core.services.entities.Customer;
import com.premiumminds.billy.core.util.BillyValidator;
import com.premiumminds.billy.core.util.Localizer;
import com.premiumminds.billy.core.util.NotOnUpdate;

public class CustomerBuilderImpl<TBuilder extends CustomerBuilderImpl<TBuilder, TCustomer>, TCustomer extends Customer> extends AbstractBuilder<TBuilder, TCustomer> implements CustomerBuilder<TBuilder, TCustomer> {
  protected static final Localizer LOCALIZER = new Localizer("com/premiumminds/billy/core/i18n/FieldNames");

  protected DAOCustomer daoCustomer;

  protected DAOContact daoContact;

  @Inject public CustomerBuilderImpl(DAOCustomer daoCustomer, DAOContact daoContact) {
    super(daoCustomer);
    this.daoCustomer = daoCustomer;
    this.daoContact = daoContact;
  }

  @Override public TBuilder setName(String name) {
    BillyValidator.mandatory(name, CustomerBuilderImpl.LOCALIZER.getString("field.customer_name"));
    this.getTypeInstance().setName(name);
    return this.getBuilder();
  }

  @Override @NotOnUpdate public TBuilder setTaxRegistrationNumber(String number, String countryCode) {
    BillyValidator.mandatory(number, CustomerBuilderImpl.LOCALIZER.getString("field.customer_tax_number"));
    this.getTypeInstance().setTaxRegistrationNumber(number);
    return this.getBuilder();
  }

  @Override public <T extends Address> TBuilder addAddress(Builder<T> addressBuilder, boolean mainAddress) {
    BillyValidator.notNull(addressBuilder, CustomerBuilderImpl.LOCALIZER.getString("field.customer_address"));
    Address address = addressBuilder.build();
    if (mainAddress) {
      this.getTypeInstance().setMainAddress((AddressEntity) address);
    }
    this.getTypeInstance().getAddresses().add(address);
    return this.getBuilder();
  }

  @Override public <T extends Address> TBuilder setBillingAddress(Builder<T> addressBuilder) {
    BillyValidator.notNull(addressBuilder, CustomerBuilderImpl.LOCALIZER.getString("field.customer_billing_address"));
    this.getTypeInstance().setBillingAddress((AddressEntity) addressBuilder.build());
    return this.getBuilder();
  }

  @Override public <T extends Address> TBuilder setShippingAddress(Builder<T> addressBuilder) {
    BillyValidator.notNull(addressBuilder, CustomerBuilderImpl.LOCALIZER.getString("field.customer_shipping_address"));
    this.getTypeInstance().setShippingAddress((AddressEntity) addressBuilder.build());
    return this.getBuilder();
  }

  @Override public <T extends Contact> TBuilder addContact(Builder<T> contactBuilder) {
    BillyValidator.notNull(contactBuilder, CustomerBuilderImpl.LOCALIZER.getString("field.customer_contact"));
    this.getTypeInstance().getContacts().add(contactBuilder.build());
    return this.getBuilder();
  }

  @Override public TBuilder setMainContactUID(UID contactUID) {
    BillyValidator.notNull(contactUID, CustomerBuilderImpl.LOCALIZER.getString("field.customer_main_contact"));
    Contact c = null;
    for (Contact contact : this.getTypeInstance().getContacts()) {
      if (contact.getUID().equals(contactUID)) {
        this.getTypeInstance().setMainContact((ContactEntity) contact);
        c = contact;
        break;
      }
    }
    BillyValidator.found(c, CustomerBuilderImpl.LOCALIZER.getString("field.customer_main_contact"));
    return this.getBuilder();
  }

  @Override public <T extends BankAccount> TBuilder addBankAccount(Builder<T> accountBuilder) {
    BillyValidator.notNull(accountBuilder, CustomerBuilderImpl.LOCALIZER.getString("field.customer_bank_account"));
    this.getTypeInstance().getBankAccounts().add(accountBuilder.build());
    return this.getBuilder();
  }

  @Override public TBuilder setHasSelfBillingAgreement(boolean selfBiling) {
    this.getTypeInstance().setHasSelfBillingAgreement(selfBiling);
    return this.getBuilder();
  }

  @Override protected void validateInstance() throws javax.validation.ValidationException {
    CustomerEntity c = this.getTypeInstance();
    BillyValidator.mandatory(c.getName(), CustomerBuilderImpl.LOCALIZER.getString("field.customer_name"));
    BillyValidator.mandatory(c.getTaxRegistrationNumber(), CustomerBuilderImpl.LOCALIZER.getString("field.customer_tax_number"));
    BillyValidator.notEmpty(c.getAddresses(), CustomerBuilderImpl.LOCALIZER.getString("field.customer_address"));
  }

  @SuppressWarnings(value = { "unchecked" }) @Override protected CustomerEntity getTypeInstance() {
    return (CustomerEntity) super.getTypeInstance();
  }
}