package com.premiumminds.billy.core.persistence.entities;
import java.util.List;
import com.premiumminds.billy.core.services.entities.Application;
import com.premiumminds.billy.core.services.entities.Contact;

/**
 * @author Francisco Vargas
 *
 *         The definition of a Billy persistence application entity. An
 *         application is the piece of software that uses Billy. This is needed
 *         to identity the origin of the billing data.
 */
public interface ApplicationEntity extends Application, BaseEntity {
  public void setName(String name);

  public void setVersion(String version);

  public void setDeveloperCompanyName(String name);

  public void setDeveloperCompanyTaxIdentifier(String id);

  @Override public <T extends Contact> List<T> getContacts();

  public <T extends ContactEntity> void setMainContact(T contact);

  public void setWebsiteAddress(String website);
}