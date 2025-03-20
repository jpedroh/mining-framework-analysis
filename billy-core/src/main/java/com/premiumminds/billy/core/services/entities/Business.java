package com.premiumminds.billy.core.services.entities;
import java.util.Collection;
import com.google.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOBusiness;
import com.premiumminds.billy.core.persistence.dao.DAOContext;
import com.premiumminds.billy.core.services.builders.impl.BusinessBuilderImpl;

/**
 * @author Francisco Vargas
 *
 *         The Billy services entity for a Business. The business is the entity
 *         for which the financial data is being managed.
 */
public interface Business extends Entity {
  public static class Builder extends BusinessBuilderImpl<Builder, Business> {
    @Inject public Builder(DAOBusiness daoBusiness, DAOContext daoContext) {
      super(daoBusiness, daoContext);
    }
  }

  public <T extends Context> T getOperationalContext();

  public String getFinancialID();

  public String getName();

  public String getCommercialName();

  public <T extends Address> T getAddress();

  public <T extends Address> T getBillingAddress();

  public <T extends Address> T getShippingAddress();

  public <T extends Contact> Collection<T> getContacts();

  public <T extends Contact> T getMainContact();

  public String getWebsiteAddress();

  public <T extends Application> Collection<T> getApplications();
}