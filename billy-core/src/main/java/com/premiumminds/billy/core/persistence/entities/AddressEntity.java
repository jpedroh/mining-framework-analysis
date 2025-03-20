package com.premiumminds.billy.core.persistence.entities;
import com.premiumminds.billy.core.services.entities.Address;

/**
 * @author Francisco Vargas
 *
 *         The definition of a Billy persistence Address entity
 */
public interface AddressEntity extends Address, BaseEntity {
  public void setStreetName(String streetName);

  public void setNumber(String number);

  public void setDetails(String details);

  public void setBuilding(String building);

  public void setCity(String city);

  public void setPostalCode(String postalCode);

  /**
     * Gets the ISO 3166-2 code for the country region
     *
     * @return The region ISO code
     */
  public void setRegion(String region);

  /**
     * Gets the address country ISO 3166-1 code.
     *
     * @return The country iso code.
     */
  public void setISOCountry(String country);
}