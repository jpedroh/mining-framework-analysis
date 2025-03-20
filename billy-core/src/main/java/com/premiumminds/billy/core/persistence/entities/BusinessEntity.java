package com.premiumminds.billy.core.persistence.entities;
import java.util.List;
import com.premiumminds.billy.core.services.entities.Application;
import com.premiumminds.billy.core.services.entities.Business;
import com.premiumminds.billy.core.services.entities.Contact;

/**
 * @author Francisco Vargas
 *
 *         The definition of a Billy persistence business entity. A business is
 *         an owner of financial documents.
 */
public interface BusinessEntity extends Business, BaseEntity {
  public <T extends ContextEntity> void setOperationalContext(T context);

  public void setFinancialID(String id);

  public void setName(String name);

  public void setCommercialName(String name);

  public void setWebsiteAddress(String address);

  public <T extends AddressEntity> void setAddress(T address);

  public <T extends AddressEntity> void setBillingAddress(T address);

  public <T extends AddressEntity> void setShippingAddress(T address);

  @Override public <T extends Contact> List<T> getContacts();

  public <T extends ContactEntity> void setMainContact(T contact);

  @Override public <T extends Application> List<T> getApplications();
}