package com.premiumminds.billy.core.services.builders;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.entities.Address;

public interface AddressBuilder<TBuilder extends AddressBuilder<TBuilder, TAddress>, TAddress extends Address> extends Builder<TAddress> {
  public TBuilder setStreetName(String streetName);

  public TBuilder setNumber(String number);

  public TBuilder setDetails(String details);

  public TBuilder setBuilding(String building);

  public TBuilder setCity(String city);

  public TBuilder setPostalCode(String postalCode);

  /**
     * Gets the ISO 3166-2 code for the country region
     *
     * @return The region ISO code
     */
  public TBuilder setRegion(String region);

  /**
     * Gets the address country ISO 3166-1 code.
     *
     * @return The country iso code.
     */
  public TBuilder setISOCountry(String country);
}