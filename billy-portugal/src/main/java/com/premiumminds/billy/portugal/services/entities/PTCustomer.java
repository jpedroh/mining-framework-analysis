package com.premiumminds.billy.portugal.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.core.services.entities.Customer;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTContact;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTCustomer;
import com.premiumminds.billy.portugal.services.builders.impl.PTCustomerBuilderImpl;

public interface PTCustomer extends Customer {
  public static class Builder extends PTCustomerBuilderImpl<Builder, PTCustomer> {
    @Inject public Builder(DAOPTCustomer daoPTCustomer, DAOPTContact daoPTContact) {
      super(daoPTCustomer, daoPTContact);
    }
  }

  public String getReferralName();
}