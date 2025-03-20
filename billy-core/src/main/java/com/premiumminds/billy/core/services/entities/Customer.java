package com.premiumminds.billy.core.services.entities;
import java.util.Collection;
import javax.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOContact;
import com.premiumminds.billy.core.persistence.dao.DAOCustomer;
import com.premiumminds.billy.core.services.builders.impl.CustomerBuilderImpl;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoice;

/**
 * @author Francisco Vargas
 *
 *         The Billy services entity for a customer. A customer is someone for
 *         which a {@link FinancialDocument} is issued.
 */
public interface Customer extends Entity {
  public static class Builder extends CustomerBuilderImpl<Builder, Customer> {
    @Inject public Builder(DAOCustomer daoCustomer, DAOContact daoContact) {
      super(daoCustomer, daoContact);
    }
  }

  public String getName();

  public String getTaxRegistrationNumber();

  public <T extends Address> Collection<T> getAddresses();

  public <T extends Address> T getMainAddress();

  public <T extends Address> T getBillingAddress();

  public <T extends Address> T getShippingAddress();

  public <T extends Contact> Collection<T> getContacts();

  public <T extends Contact> T getMainContact();

  public <T extends BankAccount> Collection<T> getBankAccounts();

  public boolean hasSelfBillingAgreement();
}