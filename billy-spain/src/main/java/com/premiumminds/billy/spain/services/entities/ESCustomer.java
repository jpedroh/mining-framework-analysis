package com.premiumminds.billy.spain.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.core.services.entities.Customer;
import com.premiumminds.billy.spain.persistence.dao.DAOESContact;
import com.premiumminds.billy.spain.persistence.dao.DAOESCustomer;
import com.premiumminds.billy.spain.services.builders.impl.ESCustomerBuilderImpl;

public interface ESCustomer extends Customer {
  public static class Builder extends ESCustomerBuilderImpl<Builder, ESCustomer> {
    @Inject public Builder(DAOESCustomer daoESCustomer, DAOESContact daoESContact) {
      super(daoESCustomer, daoESContact);
    }
  }

  public String getReferralName();
}