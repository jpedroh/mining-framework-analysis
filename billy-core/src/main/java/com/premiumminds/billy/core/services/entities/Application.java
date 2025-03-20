package com.premiumminds.billy.core.services.entities;
import java.util.Collection;
import javax.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOApplication;
import com.premiumminds.billy.core.services.builders.impl.ApplicationBuilderImpl;

/**
 * @author Francisco Vargas
 *
 *         The Billy services entity for an application which is a client of
 *         Billy.
 */
public interface Application extends Entity {
  public static class Builder extends ApplicationBuilderImpl<Builder, Application> {
    @Inject public Builder(DAOApplication daoApplication) {
      super(daoApplication);
    }
  }

  /**
     * Gets the name of the application.
     *
     * @return The application name.
     */
  public String getName();

  /**
     * Gets the application version.
     *
     * @return The application version.
     */
  public String getVersion();

  /**
     * Gets the name of the application developer company.
     *
     * @return The name of the developer company.
     */
  public String getDeveloperCompanyName();

  /**
     * Gets the tax identifier for the application developer company.
     *
     * @return The company tax identifier.
     */
  public String getDeveloperCompanyTaxIdentifier();

  /**
     * Gets the application developer company website address.
     *
     * @return The website address.
     */
  public String getWebsiteAddress();

  /**
     * Gets the application developer company collection of {@link Contact}
     * contacts.
     *
     * @return The list of contacts.
     */
  public <T extends Contact> Collection<T> getContacts();

  public <T extends Contact> Contact getMainContact();
}