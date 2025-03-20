package com.premiumminds.billy.core.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOContact;
import com.premiumminds.billy.core.services.builders.impl.ContactBuilderImpl;

/**
 * @author Francisco Vargas
 *
 *         The Billy services entity for a contact.
 */
public interface Contact extends Entity {
  public static class Builder extends ContactBuilderImpl<Builder, Contact> {
    @Inject public Builder(DAOContact daoContact) {
      super(daoContact);
    }
  }

  public String getName();

  public String getTelephone();

  public String getMobile();

  public String getFax();

  public String getEmail();

  public String getWebsite();
}