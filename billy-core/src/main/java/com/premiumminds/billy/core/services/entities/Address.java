package com.premiumminds.billy.core.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOAddress;
import com.premiumminds.billy.core.services.builders.impl.AddressBuilderImpl;

/**
 * @author Francisco Vargas
 *
 *         The Billy services entity for an address
 */
public interface Address extends Entity {
  public static class Builder extends AddressBuilderImpl<Builder, Address> {
    @Inject public Builder(DAOAddress daoAddress) {
      super(daoAddress);
    }
  }

  public String getStreetName();

  public String getNumber();

  public String getDetails();

  public String getBuilding();

  public String getCity();

  public String getPostalCode();

  /**
     * Gets the ISO 3166-2 code for the country region
     *
     * @return The region ISO code
     */
  public String getRegion();

  /**
     * Gets the address country ISO 3166-1 code.
     *
     * @return The country iso code.
     */
  public String getISOCountry();
}