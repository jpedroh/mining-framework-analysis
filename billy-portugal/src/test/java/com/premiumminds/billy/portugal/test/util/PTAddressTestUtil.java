package com.premiumminds.billy.portugal.test.util;
import com.google.inject.Injector;
import com.premiumminds.billy.portugal.services.entities.PTAddress;

public class PTAddressTestUtil {
  private static final String NUMBER = "1";

  private static final String STREET = "street";

  private static final String BUILDING = "building";

  private static final String CITY = "city";

  private static final String REGION = "region";

  private static final String ISOCODE = "PT";

  private static final String DETAILS = "details";

  private static final String POSTAL_CODE = "1000-000";

  private Injector injector;

  public PTAddressTestUtil(Injector injector) {
    this.injector = injector;
  }

  public PTAddress.Builder getAddressBuilder(String streetName, String number, String details, String building, String city, String postalCode, String region, String isoCountry) {
    PTAddress.Builder addressBuilder = this.injector.getInstance(PTAddress.Builder.class);
    addressBuilder.clear();
    addressBuilder.setBuilding(building).setCity(city).setDetails(details).setISOCountry(isoCountry).setNumber(number).setRegion(region).setStreetName(streetName).setPostalCode(postalCode);
    return addressBuilder;
  }

  public PTAddress.Builder getAddressBuilder() {
    return this.getAddressBuilder(PTAddressTestUtil.STREET, PTAddressTestUtil.NUMBER, PTAddressTestUtil.DETAILS, PTAddressTestUtil.BUILDING, PTAddressTestUtil.CITY, PTAddressTestUtil.POSTAL_CODE, PTAddressTestUtil.REGION, PTAddressTestUtil.ISOCODE);
  }
}