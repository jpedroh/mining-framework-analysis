package com.premiumminds.billy.spain.test.util;
import com.google.inject.Injector;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.spain.persistence.entities.ESCustomerEntity;
import com.premiumminds.billy.spain.services.entities.ESAddress;
import com.premiumminds.billy.spain.services.entities.ESContact;
import com.premiumminds.billy.spain.services.entities.ESCustomer;

public class ESCustomerTestUtil {
  private static final String NAME = "Name";

  private static final String TAX_NUMBER = "11111111H";

  private static final Boolean SELF_BILLING_AGREE = false;

  protected static final String ES_COUNTRY_CODE = "ES";

  private Injector injector;

  private ESAddressTestUtil address;

  private ESContactTestUtil contact;

  public ESCustomerTestUtil(Injector injector) {
    this.injector = injector;
    this.address = new ESAddressTestUtil(injector);
    this.contact = new ESContactTestUtil(injector);
  }

  public ESCustomerEntity getCustomerEntity(String uid) {
    ESCustomerEntity customer = (ESCustomerEntity) this.getCustomerBuilder().build();
    customer.setUID(new UID(uid));
    return customer;
  }

  public ESCustomerEntity getCustomerEntity() {
    return (ESCustomerEntity) this.getCustomerBuilder().build();
  }

  public ESCustomer.Builder getCustomerBuilder() {
    ESAddress.Builder addressBuilder = this.address.getAddressBuilder();
    ESContact.Builder contactBuilder = this.contact.getContactBuilder();
    return this.getCustomerBuilder(ESCustomerTestUtil.NAME, ESCustomerTestUtil.TAX_NUMBER, ESCustomerTestUtil.SELF_BILLING_AGREE, addressBuilder, contactBuilder);
  }

  public ESCustomer.Builder getCustomerBuilder(String name, String taxNumber, Boolean selfBilling, ESAddress.Builder addressBuilder, ESContact.Builder contactBuilder) {
    ESCustomer.Builder customerBuilder = this.injector.getInstance(ESCustomer.Builder.class);
    return customerBuilder.addAddress(addressBuilder, true).addContact(contactBuilder).setBillingAddress(addressBuilder).setName(name).setHasSelfBillingAgreement(selfBilling).setTaxRegistrationNumber(taxNumber, ESCustomerTestUtil.ES_COUNTRY_CODE);
  }

  public ESCustomerEntity getCustomerEntity(String name, String taxNumber, boolean selfBilling, ESAddress.Builder addressBuilder, ESContact.Builder contactBuilder) {
    ESCustomerEntity customer = (ESCustomerEntity) this.getCustomerBuilder().build();
    return customer;
  }
}