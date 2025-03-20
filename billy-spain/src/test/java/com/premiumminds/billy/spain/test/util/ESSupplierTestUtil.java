package com.premiumminds.billy.spain.test.util;
import com.google.inject.Injector;
import com.premiumminds.billy.spain.persistence.entities.ESSupplierEntity;
import com.premiumminds.billy.spain.services.entities.ESAddress;
import com.premiumminds.billy.spain.services.entities.ESContact;
import com.premiumminds.billy.spain.services.entities.ESSupplier;

public class ESSupplierTestUtil {
  private static final Boolean SELF_BILLING = false;

  private static final String NUMBER = "11111111H";

  private static final String NAME = "Supplier";

  protected static final String ES_COUNTRY_CODE = "ES";

  private Injector injector;

  private ESAddressTestUtil address;

  private ESContactTestUtil contact;

  public ESSupplierTestUtil(Injector injector) {
    this.injector = injector;
    this.address = new ESAddressTestUtil(injector);
    this.contact = new ESContactTestUtil(injector);
  }

  public ESSupplierEntity getSupplierEntity() {
    ESAddress.Builder addressBuilder = this.address.getAddressBuilder();
    ESContact.Builder contactBuilder = this.contact.getContactBuilder();
    return this.getSupplierEntity(ESSupplierTestUtil.NAME, ESSupplierTestUtil.NUMBER, ESSupplierTestUtil.SELF_BILLING, addressBuilder, contactBuilder);
  }

  public ESSupplierEntity getSupplierEntity(String name, String taxNumber, boolean selfBillingAgree, ESAddress.Builder addressBuilder, ESContact.Builder contactBuilder) {
    return (ESSupplierEntity) this.getSupplierBuilder(name, taxNumber, selfBillingAgree, addressBuilder, contactBuilder).build();
  }

  public ESSupplier.Builder getSupplierBuilder(String name, String taxNumber, boolean selfBillingAgree, ESAddress.Builder addressBuilder, ESContact.Builder contactBuilder) {
    ESSupplier.Builder supplierBuilder = this.injector.getInstance(ESSupplier.Builder.class);
    supplierBuilder.addAddress(addressBuilder).addContact(contactBuilder).setBillingAddress(addressBuilder).setMainContact(contactBuilder).setSelfBillingAgreement(selfBillingAgree).setTaxRegistrationNumber(taxNumber, ESSupplierTestUtil.ES_COUNTRY_CODE).setName(name).setMainAddress(addressBuilder);
    return supplierBuilder;
  }
}